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
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 * 1.0.1    Tobia Tesan      Aggiunta di getIJ
 * 1.0.2    Tobia Tesan      Aggiunta di getPath
 * 1.0.3    Tobia Tesan      Riscrittura di getForecast con GregorianCalendar
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.Quadrant;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Math.floor;

/**
 * Classe concreta contenente l’implementazione del data source per l’accesso al
 * database SQLite dell’applicazione.
 *
 * @use Viene istanziato dall'activity, che lo utilizza nella creazione di un DAO SerleenaDataSource. Viene inoltre utilizzato come datasource interno agli elementi del package persistence.sqlite, dietro interfaccia ISerleenaSQLiteDataSource.
 * @field dbHelper : SerleenaDatabase Oggetto rappresentante il punto di accesso al database SQLite utilizzato dall'applicazione
 * @field context : Context Contesto dell'applicazione
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SerleenaSQLiteDataSource implements ISerleenaSQLiteDataSource {
    final static int QUADRANT_LATSIZE = 10;
    final static int QUADRANT_LONGSIZE = 10;
    final static int TOT_LAT_QUADRANTS = 180 / QUADRANT_LATSIZE;
    final static int TOT_LONG_QUADRANTS = 360 / QUADRANT_LONGSIZE;
    final static String RASTER_PATH = "raster/";

    private SerleenaDatabase dbHelper;
    private Context context;

    public SerleenaSQLiteDataSource(Context context, SerleenaDatabase dbHelper) {
        this.dbHelper = dbHelper;
        this.context = context;
    }

    /**
     * Ritorna il percorso del file per il quadrante i,j-esimo.
     *
     * @return Il path
     */
    public static String getRasterPath(int i, int j) {
        return RASTER_PATH + i + "_" + j;
    }

    /**
     * Ritorna la coppia i,j che identifica il quadrante a cui appartiene un punto.
     *
     * @param p Il punto geografico
     * @return Un array int [2] di due punti i,j che identifica il quadrante.
     */
    public static int[] getIJ(GeoPoint p) {
        assert(p.latitude() >= -90.0);
        assert(p.latitude() <= 90.0);
        assert(p.longitude() >= -180.0);
        assert(p.longitude() < 180.0);
        int ij[] = new int[2];
        if (p.latitude() == 90.0) {
            ij[0] = TOT_LAT_QUADRANTS - 1;
        } else {
            ij[0] = (int) (floor((p.latitude() + 90.0) / QUADRANT_LATSIZE));
        }
        assert(ij[0] < TOT_LAT_QUADRANTS);
        ij[1] = (int)(floor((p.longitude() + 180.0) / QUADRANT_LONGSIZE) %  TOT_LONG_QUADRANTS);
        assert(ij[1] < TOT_LONG_QUADRANTS);
        return ij;
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
        Cursor result = db.query(SerleenaDatabase.TABLE_TRACKS,
                new String[] { "track_id" }, where, null, null, null, null);

        ArrayList<SQLiteDAOTrack> list = new ArrayList<SQLiteDAOTrack>();
        int columnIndex = result.getColumnIndexOrThrow("track_id");

        while (result.moveToNext()) {
            int trackId = result.getInt(columnIndex);
            list.add(new SQLiteDAOTrack(getCheckpoints(trackId) ,trackId,
                    this));
        }

        result.close();
        return list;
    }

    /**
     * Restituisce i checkpoint di un Percorso.
     *
     * @param trackId ID del percorso di cui si vogliono ottenere i Checkpoint.
     * @return Elenco di checkpoint del Percorso specificato.
     */
    private ImmutableList<Checkpoint> getCheckpoints(int trackId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "checkpoint_track = " + trackId;
        String orderBy = "checkpoint_num ASC";

        Cursor result = db.query(SerleenaDatabase.TABLE_CHECKPOINTS,
                new String[] { "checkpoint_latitude", "checkpoint_longitude"},
                where, null, null, null, orderBy);

        ArrayList<Checkpoint> list = new ArrayList<Checkpoint>();
        int latIndex = result.getColumnIndexOrThrow("checkpoint_latitude");
        int lonIndex = result.getColumnIndexOrThrow("checkpoint_longitude");

        while (result.moveToNext()) {
            double lat = result.getDouble(latIndex);
            double lon = result.getDouble(lonIndex);
            list.add(new Checkpoint(lat, lon));
        }

        result.close();
        return new ListAdapter<Checkpoint>(list);
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
        Cursor result = db.query(SerleenaDatabase.TABLE_TELEMETRIES,
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
     * Implementazione di ISerleenaSQLiteDataSource.getUserPoints().
     *
     * Viene eseguita una query sul database per ottenere i Punti Utente
     * associati all'Esperienza specificata.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Punti Utente.
     * @return Insieme enumerabile di Punti Utente.
     */
    @Override
    public Iterable<UserPoint> getUserPoints(SQLiteDAOExperience experience) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "userpoint_experience = " + experience.id();
        Cursor result = db.query(SerleenaDatabase.TABLE_USER_POINTS,
                new String[] { "userpoint_x", "userpoint_y" }, where, null,
                null, null, null);
        int latIndex = result.getColumnIndexOrThrow("userpoint_x");
        int lonIndex = result.getColumnIndexOrThrow("userpoint_y");

        ArrayList<UserPoint> list = new ArrayList<>();
        while (result.moveToNext()) {
            double latitude = result.getDouble(latIndex);
            double longitude = result.getDouble(lonIndex);
            list.add(new UserPoint(latitude, longitude));
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

        db.insert(SerleenaDatabase.TABLE_USER_POINTS, null, values);
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

        ContentValues values = new ContentValues();
        values.put("telem_track", track.id());
        long newId = db.insert(SerleenaDatabase.TABLE_TELEMETRIES, null, values);


        for (TelemetryEvent event : events) {
            values = new ContentValues();

            if (event instanceof LocationTelemetryEvent) {

                LocationTelemetryEvent eventl = (LocationTelemetryEvent) event;
                values.put("eventl_timestamp", eventl.timestamp());
                values.put("eventl_latitude", eventl.location().latitude());
                values.put("eventl_longitude", eventl.location().longitude());
                values.put("eventl_telem", newId);
                db.insert(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);

            } else if (event instanceof HeartRateTelemetryEvent) {

                HeartRateTelemetryEvent eventh =
                        (HeartRateTelemetryEvent) event;
                values.put("eventhc_timestamp", eventh.timestamp());
                values.put("eventhc_value", eventh.heartRate());
                values.put("eventhc_type", SerleenaDatabase.EVENT_TYPE_HEARTRATE);
                values.put("eventhc_telem", newId);
                db.insert(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null,
                        values);

            } else if (event instanceof CheckpointReachedTelemetryEvent) {

                CheckpointReachedTelemetryEvent eventc =
                        (CheckpointReachedTelemetryEvent) event;
                values.put("eventhc_timestamp", eventc.timestamp());
                values.put("eventhc_value", eventc.checkpointNumber());
                values.put("eventhc_type", SerleenaDatabase.EVENT_TYPE_CHECKPOINT);
                values.put("eventhc_telem", newId);
                db.insert(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null,
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
        Cursor result = db.query(SerleenaDatabase.TABLE_EXPERIENCES,
                new String[] { "experience_id", "experience_name" }, null,
                null, null, null, null);

        int idIndex = result.getColumnIndexOrThrow("experience_id");
        int nameIndex = result.getColumnIndexOrThrow("experience_name");

        ArrayList<IExperienceStorage> list =
                new ArrayList<IExperienceStorage>();

        while (result.moveToNext()) {
            int id = result.getInt(idIndex);
            String name = result.getString(nameIndex);
            list.add(new SQLiteDAOExperience(name, id, this));
        }

        result.close();
        return list;
    }

    /**
     * Implementazione di IPersistenceDataStorage.getWeatherInfo().
     *
     * @param location Posizione geografica di cui si vogliono ottenere le
     *                 previsioni.
     * @param date Data di cui si vogliono ottenere le previsioni.
     * @return Previsioni metereologiche.
     */
    @Override
    public IWeatherStorage getWeatherInfo(GeoPoint location, Date date) {
        if (date == null)
            return null;

        GregorianCalendar morning = new GregorianCalendar();
        morning.setTime(date);
        morning.set(Calendar.HOUR_OF_DAY, 6);
        morning.set(Calendar.MINUTE, 0);
        morning.set(Calendar.SECOND, 0);
        morning.set(Calendar.MILLISECOND, 0);

        GregorianCalendar morningAfter = new GregorianCalendar();
        morningAfter.setTime(date);
        morningAfter.set(Calendar.HOUR_OF_DAY, 6);
        morningAfter.set(Calendar.HOUR_OF_DAY, 6);
        morningAfter.set(Calendar.MINUTE, 0);
        morningAfter.set(Calendar.SECOND, 0);
        morningAfter.set(Calendar.MILLISECOND, 0);
        morningAfter.add(Calendar.HOUR_OF_DAY, 24);

        GregorianCalendar afternoon = new GregorianCalendar();
        afternoon.setTime(date);
        afternoon.set(Calendar.HOUR_OF_DAY, 14);
        afternoon.set(Calendar.MINUTE, 0);
        afternoon.set(Calendar.SECOND, 0);
        afternoon.set(Calendar.MILLISECOND, 0);

        GregorianCalendar night = new GregorianCalendar();
        night.setTime(date);
        night.set(Calendar.HOUR_OF_DAY, 21);
        night.set(Calendar.MINUTE, 0);
        night.set(Calendar.SECOND, 0);
        night.set(Calendar.MILLISECOND, 0);

        int morningStart = Math.round(morning.getTimeInMillis() / 1000);
        int morningEnd = Math.round(afternoon.getTimeInMillis() / 1000);

        int afternoonStart = Math.round(afternoon.getTimeInMillis() / 1000);
        int afternoonEnd = Math.round(night.getTimeInMillis() / 1000);

        int nightStart = Math.round(night.getTimeInMillis() / 1000);
        int nightEnd = Math.round(morningAfter.getTimeInMillis() / 1000);

        SimpleWeather morningWeather = getForecast(location, morningStart, morningEnd);
        SimpleWeather afternoonWeather =
                getForecast(location, afternoonStart, afternoonEnd);
        SimpleWeather nightWeather = getForecast(location, nightStart, nightEnd);

        if (morningWeather != null && morningWeather != null && morningWeather != null) {
            return new SQLiteDAOWeather(morningWeather.forecast(),
                    afternoonWeather.forecast(), nightWeather.forecast(),
                    morningWeather.temperature(), afternoonWeather.temperature(),
                    nightWeather.temperature(), date);
        }

        return null;
    }

    /**
     * Restituisce il quadrante i cui limiti comprendono la posizione
     * geografica specificata.
     *
     * @param location Posizione geografica che ricade nei limiti del quadrante.
     * @return Oggetto IQuadrant.
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint location) {

        int[] ij = getIJ(location);

        assert(ij[0] < TOT_LAT_QUADRANTS);
        assert(ij[1] < TOT_LONG_QUADRANTS);

        String fileName = getRasterPath(ij[0], ij[1]);
        GeoPoint p1 = new GeoPoint((ij[0] * QUADRANT_LATSIZE - 90.0),
                (ij[1] * QUADRANT_LONGSIZE - 180.0));
        // Vogliamo longitudine 180.0 espressa come -180.0 (=in mod 2pi)
        GeoPoint p2 = new GeoPoint(((ij[0] + 1) * QUADRANT_LATSIZE - 90.0),
                (((ij[1] + 1) * QUADRANT_LONGSIZE) % 360.0) - 180.0);

        Bitmap raster = null;
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists())
            raster = BitmapFactory.decodeFile(file.getAbsolutePath());
        return new Quadrant(p1, p2, raster);
    }

    /**
     * Implementazione di IPersistenceDataSource.getContacts().
     *
     * @param location Punto geografico del cui intorno si vogliono ottenere
     *                 i contatti di autorità locali.
     * @return Insieme enumerabile di contatti di emergenza.
     */
    @Override
    public Iterable<EmergencyContact> getContacts(GeoPoint location) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<EmergencyContact> list = new ArrayList<EmergencyContact>();

        String where = "(contact_ne_corner_latitude + 90) <= " +
                (location.latitude() + 90) + " AND " +
                "(contact_ne_corner_longitude + 180) <= " +
                (location.longitude() + 180) + " AND " +
                "(contact_sw_corner_latitude + 90) >= " +
                (location.latitude() + 90) + " AND " +
                "(contact_sw_corner_longitude + 180) >= " +
                (location.longitude() + 180);
        Cursor result = db.query(SerleenaDatabase.TABLE_CONTACTS,
                new String[]{"contact_name", "contact_value"}, where,
                null, null, null, null);

        int nameIndex = result.getColumnIndexOrThrow("contact_name");
        int valueIndex = result.getColumnIndexOrThrow("contact_value");

        while (result.moveToNext()) {
            String name = result.getString(nameIndex);
            String value = result.getString(valueIndex);
            list.add(new EmergencyContact(name, value));
        }

        result.close();
        return list;
    }

    /**
     * Restituisce gli eventi di Tracciamento associati al Tracciamento con ID
     * specificato, memorizzati nel database SQLite.
     *
     * @param id ID del Tracciamento di cui si vogliono ottenere gli eventi.
     * @return Insieme enumerabile di eventi di Tracciamento.
     */
    private Iterable<TelemetryEvent> getTelemetryEvents(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<TelemetryEvent> list = new ArrayList<TelemetryEvent>();

        String where = "eventhc_telem = " + id;
        Cursor result = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP,
                new String[] { "eventhc_timestamp", "eventhc_value",
                "eventhc_type" }, where, null, null, null, null);

        int timestampIndex = result.getColumnIndexOrThrow("eventhc_timestamp");
        int valueIndex = result.getColumnIndexOrThrow("eventhc_value");
        int typeIndex = result.getColumnIndexOrThrow("eventhc_type");

        while (result.moveToNext()) {
            int time = result.getInt(timestampIndex);
            int value = Integer.parseInt(result.getString(valueIndex));
            String type = result.getString(typeIndex);

            TelemetryEvent event = null;
            switch (type) {
                case SerleenaDatabase.EVENT_TYPE_CHECKPOINT:
                    event = new CheckpointReachedTelemetryEvent(time, value);
                    break;
                case SerleenaDatabase.EVENT_TYPE_HEARTRATE:
                    event = new HeartRateTelemetryEvent(time, value);
                    break;
                default:
                   /*throw new Exception();*/
            }

            list.add(event);
        }

        where = "eventl_telem = " + id;
        result = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION,
                new String[] { "eventl_timestamp", "eventl_latitude",
                        "eventl_longitude" }, where, null, null, null, null);

        timestampIndex = result.getColumnIndexOrThrow("eventl_timestamp");
        int latitudeIndex = result.getColumnIndexOrThrow("eventl_latitude");
        int longitudeIndex = result.getColumnIndexOrThrow("eventl_longitude");

        while (result.moveToNext()) {
            int time = result.getInt(timestampIndex);
            double latitude = result.getDouble(latitudeIndex);
            double longitude = result.getDouble(longitudeIndex);
            GeoPoint location = new GeoPoint(latitude, longitude);

            list.add(new LocationTelemetryEvent(time, location));
        }

        result.close();

        return list;
    }

    /**
     * Restituisce le previsioni, comprensive di condizione metereologica e
     * temperatura, per una posizione geografica e un intervallo di tempo
     * specificati.
     *
     * @param location Posizione geografica di cui si vogliono ottenere le
     *                 previsioni.
     * @param startTime Inizio dell'intervallo di tempo di cui si vogliono
     *                  ottenere le previsioni, in UNIX time.
     * @param endTime Fine dell'intervallo di tempo di cui si vogliono
     *                ottenere le previsioni, in UNIX time.
     * @return Previsioni metereologiche.
     */
    private SimpleWeather getForecast(GeoPoint location, int startTime,
                                      int endTime) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where =
                "weather_end >= " + startTime + " AND " +
                "weather_start <= " + endTime + " AND " +
                "(weather_ne_corner_latitude + 90) <= " +
                (location.latitude() + 90) + " AND " +
                "(weather_ne_corner_longitude + 180) <= " +
                (location.longitude() + 180) + " AND " +
                "(weather_sw_corner_latitude + 90) >= " +
                (location.latitude() + 90) + " AND " +
                "(weather_sw_corner_longitude + 180) >= " +
                (location.longitude() + 180);

        Cursor result = db.query(SerleenaDatabase.TABLE_WEATHER_FORECASTS,
                new String[] { "weather_condition", "weather_temperature" },
                where, null, null, null, null);

        int conditionIndex = result.getColumnIndex("weather_condition");
        int temperatureIndex = result.getColumnIndex("weather_temperature");

        if (result.moveToNext()) {
            WeatherForecastEnum forecast =
                    WeatherForecastEnum.values()[result.getInt(conditionIndex)];
            int temperature = result.getInt(temperatureIndex);
            return new SimpleWeather(forecast, temperature);
        } else
            return null;
    }

    /**
     * Rappresenta una previsione metereologica in un istante di tempo,
     * comprensiva di condizione e temperatura previste.
     */
    private class SimpleWeather {
        private WeatherForecastEnum forecast;
        private int temperature;

        public SimpleWeather(WeatherForecastEnum forecast, int temperature) {
            this.forecast = forecast;
            this.temperature = temperature;
        }
        public WeatherForecastEnum forecast() { return forecast; }
        public int temperature() { return temperature; }
    }

}
