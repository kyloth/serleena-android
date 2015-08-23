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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.common.Quadrant;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

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
    private SerleenaDatabase dbHelper;

    public SerleenaSQLiteDataSource(SerleenaDatabase dbHelper) {
        if (dbHelper == null)
            throw new IllegalArgumentException("Illegal null database");
        this.dbHelper = dbHelper;
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
        String where = "track_experience = \"" + experience.getUUID() + "\"";
        Cursor result = db.query(SerleenaDatabase.TABLE_TRACKS,
                new String[] { "track_uuid, track_name" }, where, null, null,
                null, null);

        ArrayList<SQLiteDAOTrack> list = new ArrayList<SQLiteDAOTrack>();
        int uuidIndex = result.getColumnIndexOrThrow("track_uuid");
        int nameIndex = result.getColumnIndexOrThrow("track_name");

        while (result.moveToNext()) {
            UUID trackUuid = UUID.fromString(result.getString(uuidIndex));
            String name = result.getString(nameIndex);
            list.add(new SQLiteDAOTrack(
                    getCheckpoints(trackUuid), trackUuid, name, this));
        }

        result.close();
        return list;
    }

    /**
     * Restituisce i checkpoint di un Percorso.
     *
     * @param trackUuid ID del percorso di cui si vogliono ottenere i Checkpoint.
     * @return Elenco di checkpoint del Percorso specificato.
     */
    private DirectAccessList<Checkpoint> getCheckpoints(UUID trackUuid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "checkpoint_track = \"" + trackUuid + '\"';
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
        return getTelemetries(track, true);
    }

    @Override
    public Iterable<SQLiteDAOTelemetry> getTelemetries(SQLiteDAOTrack track, boolean includeGhost) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "telem_track = \"" + track.getUUID() + "\"";

        if (includeGhost == false) {
            where = "telem_track = \"" + track.getUUID() + "\" AND telem_id != -1";
        }

        Cursor result = db.query(SerleenaDatabase.TABLE_TELEMETRIES,
                new String[]{"telem_id"}, where, null, null, null, null);

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
        return getUserPoints(experience, false);
    }

    @Override
    public Iterable<UserPoint> getUserPoints(SQLiteDAOExperience experience, boolean localOnly) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "userpoint_experience = \"" + experience.getUUID() + "\"";
        if (localOnly) {
            where = "userpoint_experience = \"" + experience.getUUID() + "\" AND userpoint_id > 0";
        }

        Cursor result = db.query(SerleenaDatabase.TABLE_USER_POINTS,
                new String[] { "userpoint_x", "userpoint_y" }, where, null,
                null, null, null);
        int latIndex = result.getColumnIndexOrThrow("userpoint_x");
        int lonIndex = result.getColumnIndexOrThrow("userpoint_y");

        ArrayList<UserPoint> list = new ArrayList<UserPoint>();
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
        values.put("userpoint_experience", experience.getUUID().toString());

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
        if (events == null || track == null)
            throw new IllegalArgumentException();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("telem_track", track.getUUID().toString());
        long newId = db.insert(SerleenaDatabase.TABLE_TELEMETRIES, null, values);

        for (TelemetryEvent event : events) {
            values = new ContentValues();

            if (event instanceof CheckpointReachedTelemetryEvent) {

                CheckpointReachedTelemetryEvent eventc =
                        (CheckpointReachedTelemetryEvent) event;
                values.put("eventc_timestamp", eventc.timestamp());
                values.put("eventc_value", eventc.checkpointNumber());
                values.put("eventc_telem", newId);
                db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null,values);

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
                new String[]{"experience_uuid", "experience_name"}, null,
                null, null, null, null);

        int uuidIndex = result.getColumnIndexOrThrow("experience_uuid");
        int nameIndex = result.getColumnIndexOrThrow("experience_name");

        ArrayList<IExperienceStorage> list =
                new ArrayList<IExperienceStorage>();

        while (result.moveToNext()) {
            UUID uuid = UUID.fromString(result.getString(uuidIndex));
            String name = result.getString(nameIndex);
            list.add(new SQLiteDAOExperience(name, uuid, this));
        }

        result.close();
        return list;
    }

    /**
     * Restituisce il quadrante contenente la posizione geografica
     * specificata, tra quelli associati all'Esperienza indicata.
     *
     * Se non vi sono quadranti contenenti la posizione specificata, viene
     * sollevata un'eccezione NosuchQuadrantException.
     *
     * @param location Posizione geografica contenuta dal quadrante richiesto.
     * @param exp Esperienza a cui il quadrante è associato.
     * @return Oggetto IQuadrant rappresentante il quadrante.
     * @throws NoSuchQuadrantException
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint location, SQLiteDAOExperience exp)
            throws NoSuchQuadrantException {
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");
        if (exp == null)
            throw new IllegalArgumentException("Illegal null experience");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where =
                "`raster_nw_corner_latitude` >= " +
                location.latitude() + " AND " +
                "`raster_nw_corner_longitude` <= " +
                location.longitude() + " AND " +
                "`raster_se_corner_latitude` <= " +
                location.latitude() + " AND " +
                "`raster_se_corner_longitude` >= " +
                location.longitude() + " AND " +
                "`raster_experience` = \"" +
                        exp.getUUID() + "\"";

        Cursor result = db.query(SerleenaDatabase.TABLE_RASTERS,
                new String[]{
                    "raster_nw_corner_latitude",
                    "raster_nw_corner_longitude",
                    "raster_se_corner_latitude",
                    "raster_se_corner_longitude",
                    "raster_base64"
                },
                where, null, null, null, null);

        int nwLatIndex =
                result.getColumnIndexOrThrow("raster_nw_corner_latitude");
        int nwLonIndex =
                result.getColumnIndexOrThrow("raster_nw_corner_longitude");
        int seLatIndex =
                result.getColumnIndexOrThrow("raster_se_corner_latitude");
        int seLonIndex =
                result.getColumnIndexOrThrow("raster_se_corner_longitude");
        int base64Index =
                result.getColumnIndexOrThrow("raster_base64");

        if (result.moveToNext()) {
            double nwLat = result.getDouble(nwLatIndex);
            double nwLon = result.getDouble(nwLonIndex);
            double seLat = result.getDouble(seLatIndex);
            double seLon = result.getDouble(seLonIndex);
            byte[] data = Base64.decode(
                    result.getString(base64Index), Base64.DEFAULT);
            result.close();
            return new Quadrant(
                    new GeoPoint(nwLat, nwLon),
                    new GeoPoint(seLat, seLon),
                    BitmapFactory.decodeByteArray(data, 0, data.length));
        } else {
            result.close();
            throw new NoSuchQuadrantException();
        }
    }

    /**
     * Implementazione di IPersistenceDataSource.getContacts().
     *
     * @param location Punto geografico del cui intorno si vogliono ottenere
     *                 i contatti di autorità locali.
     * @return Insieme enumerabile di contatti di emergenza.
     */
    @Override
    public DirectAccessList<EmergencyContact> getContacts(GeoPoint location) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<EmergencyContact> list = new ArrayList<EmergencyContact>();

        String where = "`contact_nw_corner_latitude` >= " +
                location.latitude() + " AND " +
                "`contact_nw_corner_longitude` <= " +
                location.longitude() + " AND " +
                "`contact_se_corner_latitude` <= " +
                location.latitude() + " AND " +
                "`contact_se_corner_longitude` >= " +
                location.longitude();
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
        return new ListAdapter<EmergencyContact>(list);
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

        String where = "eventc_telem = " + id;
        Cursor result = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP,
                new String[]{"eventc_timestamp", "eventc_value"},
                where, null, null, null, null);

        int timestampIndex = result.getColumnIndexOrThrow("eventc_timestamp");
        int valueIndex = result.getColumnIndexOrThrow("eventc_value");

        while (result.moveToNext()) {
            long time = result.getLong(timestampIndex);
            int value = Integer.parseInt(result.getString(valueIndex));
            TelemetryEvent event =
                    new CheckpointReachedTelemetryEvent(time, value);
            list.add(event);
        }

        result.close();
        return list;
    }

    /**
     * Implementazione di IPersistenceDataStorage.getWeatherInfo().
     * Ricerca le previsioni per tre specifiche ore, AFTERNOON_CENTRAL_HOUR,
     * MORNING_CENTRAL_HOUR, NIGHT_CENTRAL_HOUR, da visualizzare o altrimenti
     * consumare come rappresentative delle condizioni di mattino, pomeriggio,
     * sera.
     *
     * @param location  Posizione geografica di cui si vogliono ottenere le
     *                  previsioni. Se null, viene sollevata un'eccezione
     *                  IllegalArgumentException.
     * @param date      Data di cui si vogliono ottenere le previsioni. Se null,
     *                  viene sollevata un'eccezione IllegalArgumentException.
     * @return Previsioni metereologiche.
     */
    @Override
    public IWeatherStorage getWeatherInfo(GeoPoint location, Date date)
            throws IllegalArgumentException, NoSuchWeatherForecastException {


        if (date == null)
            throw new IllegalArgumentException("Illegal null date");
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");

        GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        c.setTimeInMillis(date.getTime());
        if (c.get(Calendar.HOUR_OF_DAY) != 00 ||
            c.get(Calendar.MINUTE) != 00 ||
            c.get(Calendar.SECOND) != 00 ||
            c.get(Calendar.MILLISECOND) != 00) {
            throw new IllegalArgumentException("Illegal date, 00:00:00.000 required");
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where =
                "weather_date = " + (date.getTime()/1000) + " AND " +
                "`weather_nw_corner_latitude` >= " +
                location.latitude() + " AND " +
                "`weather_nw_corner_longitude` <= " +
                location.longitude() + " AND " +
                "`weather_se_corner_latitude` <= " +
                location.latitude() + " AND " +
                "`weather_se_corner_longitude` >= " +
                location.longitude();

        Cursor result = db.query(SerleenaDatabase.TABLE_WEATHER_FORECASTS,
                new String[]{
                        "weather_condition_morning",
                        "weather_temperature_morning",
                        "weather_condition_afternoon",
                        "weather_temperature_afternoon",
                        "weather_condition_night",
                        "weather_temperature_night"
                },
                where, null, null, null, null);

        int conditionMorningIndex = result.getColumnIndex("weather_condition_morning");
        int temperatureMorningIndex = result.getColumnIndex("weather_temperature_morning");
        int conditionAfternoonIndex = result.getColumnIndex("weather_condition_afternoon");
        int temperatureAfternoonIndex = result.getColumnIndex("weather_temperature_afternoon");
        int conditionNightIndex = result.getColumnIndex("weather_condition_night");
        int temperatureNightIndex = result.getColumnIndex("weather_temperature_night");

        if (result.moveToNext()) {
            SQLiteDAOWeather res = new SQLiteDAOWeather(
                    WeatherForecastEnum.values()[result.getInt(conditionMorningIndex)],
                    WeatherForecastEnum.values()[result.getInt(conditionAfternoonIndex)],
                    WeatherForecastEnum.values()[result.getInt(conditionNightIndex)],
                    result.getInt(temperatureMorningIndex),
                    result.getInt(temperatureAfternoonIndex),
                    result.getInt(temperatureNightIndex),
                    date);
            result.close();
            return res;
        } else {
            result.close();
            throw new NoSuchWeatherForecastException();
        }
    }

}
