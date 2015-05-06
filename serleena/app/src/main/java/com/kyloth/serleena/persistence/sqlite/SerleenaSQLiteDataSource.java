///////////////////////////////////////////////////////////////////////////////
// 
// This file is part of Serleena.
// 
// The MIT License (MIT)
//
// Copyright (C) 2015 Antonio Cavestro, Gabriele Pozzan, Matteo Lisotto, 
//   Nicola Mometto, Filippo Sestini, Tobia Tesan, Sebastiano Valle.    
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to 
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
//
///////////////////////////////////////////////////////////////////////////////


/**
 * Name: SerleenaSQLiteDataSource.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-06  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.io.File;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe concreta contenente l’implementazione del data source per l’accesso al
 * database SQLite dell’applicazione.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-06
 */
public class SerleenaSQLiteDataSource implements ISerleenaSQLiteDataSource {

    SerleenaDatabase dbHelper;
    Context context;

    public SerleenaSQLiteDataSource(Context context, SerleenaDatabase dbHelper) {
        this.dbHelper = dbHelper;
        this.context = context;
    }

    /**
     * Implementazione di ISerleenaSQLiteDataSource.getTracks().
     *
     * Viene eseguita una query sul database per ottenere gli ID di tutti i
     * Percorsi associati all'Esperienza specificata, da cui vengono creati
     * rispettivi oggetti SQLiteDAOTrack.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Percorsi.
     * @return Insieme enumerabile di Percorsi.
     */
    @Override
    public Iterable<SQLiteDAOTrack> getTracks(SQLiteDAOExperience experience) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "track_experience = " + experience.id();
        Cursor result = db.query(dbHelper.TABLE_TRACKS,
                new String[] { "track_id" }, where, null, null, null, null);

        ArrayList<SQLiteDAOTrack> list = new ArrayList<SQLiteDAOTrack>();
        int columnIndex = result.getColumnIndexOrThrow("track_id");

        while (result.moveToNext()) {
            int trackId = result.getInt(columnIndex);
            list.add(new SQLiteDAOTrack(trackId, this));
        }

        result.close();
        return list;
    }

    /**
     * Implementazione di ISerleenaSQLiteDataSource.getTelemetries().
     *
     * Viene eseguita una query sul database per ottenere gli ID di tutti i
     * Tracciamenti associati al Percorso specificato, da cui vengono creati
     * rispettivi oggetti SQLiteDAOTelemetry.
     *
     * @param track Percorso di cui si vogliono ottenere i Tracciamenti.
     * @return Insieme enumerabile di Tracciamenti.
     */
    @Override
    public Iterable<SQLiteDAOTelemetry> getTelemetries(SQLiteDAOTrack track) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "telem_track = " + track.id();
        Cursor result = db.query(dbHelper.TABLE_TELEMETRIES,
                new String[] { "telem_id" }, where, null, null, null, null);

        ArrayList<SQLiteDAOTelemetry> list = new
                ArrayList<SQLiteDAOTelemetry>();
        int columnIndex = result.getColumnIndexOrThrow("telem_id");

        while (result.moveToNext()) {
            int telemId = result.getInt(columnIndex);
            Iterable<TelemetryEvent> events = getTelemetryEvents(telemId);
            list.add(new SQLiteDAOTelemetry(telemId, events));
        }

        result.close();
        return list;
    }

    /**
     * Implementazione di ISerleenaSQLiteDataSource.addUserPoint().
     *
     * @param experience Esperienza a cui aggiungere il punto utente.
     * @param point Punto utente da aggiungere.
     */
    @Override
    public void addUserPoint(SQLiteDAOExperience experience, UserPoint point) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        values.put("userpoint_x", point.latitude());
        values.put("userpoint_y", point.longitude());
        values.put("userpoint_experience", experience.id());

        db.insert(dbHelper.TABLE_USER_POINTS, null, values);
    }

    /**
     * Implementazione di ISerleenaSQLiteDataSource.createTelemetry().
     *
     * @param events Eventi di tracciamento da cui costruire il Tracciamento.
     * @param track Percorso a cui associare il Tracciamento.
     */
    @Override
    public void createTelemetry(Iterable<TelemetryEvent> events,
                                SQLiteDAOTrack track) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SimpleDateFormat format = dbHelper.DATE_FORMAT;

        ContentValues values = new ContentValues();
        values.put("telem_track", track.id());
        long newId = db.insert(dbHelper.TABLE_TELEMETRIES, null, values);


        for (TelemetryEvent event : events) {
            values = new ContentValues();

            if (event instanceof LocationTelemetryEvent) {

                LocationTelemetryEvent eventl = (LocationTelemetryEvent) event;
                values.put("eventl_timestamp",
                        format.format(eventl.timestamp()));
                values.put("eventl_latitude", eventl.location().latitude());
                values.put("eventl_longitude", eventl.location().longitude());
                values.put("eventl_telem", newId);
                db.insert(dbHelper.TABLE_TELEM_EVENTS_LOCATION, null, values);

            } else if (event instanceof HeartRateTelemetryEvent) {

                HeartRateTelemetryEvent eventh =
                        (HeartRateTelemetryEvent) event;
                values.put("eventhc_timestamp",
                        format.format(eventh.timestamp()));
                values.put("eventhc_value", eventh.heartRate());
                values.put("eventhc_type", dbHelper.EVENT_TYPE_HEARTRATE);
                values.put("eventhc_telem", newId);
                db.insert(dbHelper.TABLE_TELEM_EVENTS_HEART_CHECKP, null,
                        values);

            } else if (event instanceof CheckpointReachedTelemetryEvent) {

                CheckpointReachedTelemetryEvent eventc =
                        (CheckpointReachedTelemetryEvent) event;
                values.put("eventhc_timestamp",
                        format.format(eventc.timestamp()));
                values.put("eventhc_value", eventc.checkpointNumber());
                values.put("eventhc_type", dbHelper.EVENT_TYPE_CHECKPOINT);
                values.put("eventhc_telem", newId);
                db.insert(dbHelper.TABLE_TELEM_EVENTS_HEART_CHECKP, null,
                        values);

            }

        }
    }

    /**
     * Implementazione di IPersistenceDataSource.getExperiences().
     *
     * Ritorna un'enumerazione di tutte le Esperienze presenti nel database,
     * dietro interfaccia IExperienceStorage.
     *
     * @return Insieme enumerabile di Esperienze.
     * @see com.kyloth.serleena.persistence.IPersistenceDataSource
     */
    @Override
    public Iterable<IExperienceStorage> getExperiences() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor result = db.query(dbHelper.TABLE_EXPERIENCES,
                new String[] { "experience_id" }, null, null, null, null, null);

        int idIndex = result.getColumnIndex("experience_id");
        ArrayList<IExperienceStorage> list =
                new ArrayList<IExperienceStorage>();

        while (result.moveToNext()) {
            int id = result.getInt(idIndex);
            list.add(new SQLiteDAOExperience(id, this));
        }

        result.close();
        return list;
    }

    /**
     * Restituisce gli eventi di Tracciamento associati al Tracciamento
     * specificato, memorizzati nel database SQLite.
     *
     * @param id ID del Tracciamento di cui si vogliono ottenere gli eventi.
     * @return Insieme enumerabile di eventi di Tracciamento.
     */
    private Iterable<TelemetryEvent> getTelemetryEvents(int id) {
        SimpleDateFormat parser = SerleenaDatabase.DATE_FORMAT;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<TelemetryEvent> list = new ArrayList<TelemetryEvent>();

        String where = "eventhc_telem = " + id;
        Cursor result = db.query(dbHelper.TABLE_TELEM_EVENTS_HEART_CHECKP,
                new String[] { "eventhc_timestamp", "eventhc_value",
                "eventhc_type" }, where, null, null, null, null);

        int timestampIndex = result.getColumnIndexOrThrow("eventhc_timestamp");
        int valueIndex = result.getColumnIndexOrThrow("eventhc_value");
        int typeIndex = result.getColumnIndexOrThrow("eventhc_type");

        while (result.moveToNext()) {
            Date d = parser.parse(result.getString(timestampIndex),
                    new ParsePosition(0));
            int value = Integer.parseInt(result.getString(valueIndex));
            String type = result.getString(typeIndex);

            TelemetryEvent event = null;
            switch (type) {
                case SerleenaDatabase.EVENT_TYPE_CHECKPOINT:
                    event = new CheckpointReachedTelemetryEvent(d, value);
                    break;
                case SerleenaDatabase.EVENT_TYPE_HEARTRATE:
                    event = new HeartRateTelemetryEvent(d, value);
                    break;
                default:
                   /*throw new Exception();*/
            }

            list.add(event);
        }

        where = "eventl_telem = " + id;
        result = db.query(dbHelper.TABLE_TELEM_EVENTS_LOCATION,
                new String[] { "eventl_timestamp", "eventl_latitude",
                        "eventhl_longitude" }, where, null, null, null, null);

        timestampIndex = result.getColumnIndexOrThrow("eventl_timestamp");
        int latitudeIndex = result.getColumnIndexOrThrow("eventl_latitude");
        int longitudeIndex = result.getColumnIndexOrThrow("eventl_longitude");

        while (result.moveToNext()) {
            Date d = parser.parse(result.getString(timestampIndex),
                    new ParsePosition(0));
            double latitude = result.getDouble(latitudeIndex);
            double longitude = result.getDouble(longitudeIndex);
            GeoPoint location = new GeoPoint(latitude, longitude);

            list.add(new LocationTelemetryEvent(d, location));
        }

        result.close();

        return list;
    }

}
