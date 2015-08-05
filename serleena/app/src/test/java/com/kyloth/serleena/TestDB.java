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


package com.kyloth.serleena;

import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.util.Date;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.common.TelemetryEventType;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
@Ignore
public class TestDB {

    public static void experienceQuery(SQLiteDatabase db, int id, String name) {
        String query = "INSERT INTO experiences (experience_id, experience_name) VALUES (" +
                       String.valueOf(id) + ", '" +  name + "')";
        db.execSQL(query);
    }

    public static void trackQuery(SQLiteDatabase db, int id, String name,
                                  int experience) {
        String query = "INSERT INTO tracks (track_id, track_name, track_experience) " +
                       "VALUES (" + String.valueOf(id) + ", '" + name + "', " +
                       String.valueOf(experience) + ")";
        db.execSQL(query);
    }

    public static void telemetryQuery(SQLiteDatabase db, int id, int track) {
        String query = "INSERT INTO telemetries (telem_id, telem_track) VALUES (" +
                       String.valueOf(id) + ", " + String.valueOf(track) + ")";
        db.execSQL(query);
    }

    public static void locationTelemetryEventQuery(SQLiteDatabase db, int id,
           long timestamp, double lat, double lon, int telemetry) {
        String query = "INSERT INTO telemetry_events_location (eventl_id, " +
                       "eventl_timestamp, eventl_latitude, eventl_longitude, " +
                       "eventl_telem) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(timestamp) +
                       ", " + String.valueOf(lat) + ", " + String.valueOf(lon) + ", " +
                       String.valueOf(telemetry) + ")";
        db.execSQL(query);
    }

    public static void heartTelemetryEventQuery(SQLiteDatabase db, int id,
            long timestamp, int value, String type, int telemetry) {
        String query = "INSERT INTO telemetry_events_heart_checkp (eventhc_id, " +
                       "eventhc_timestamp, eventhc_value, eventhc_type, eventhc_telem) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(timestamp) +
                       ", " + String.valueOf(value) + ", '" + type + "', " +
                       String.valueOf(telemetry) + ")";
        db.execSQL(query);
    }

    public static void checkpointQuery(SQLiteDatabase db, int id, int num,
           double lat, double lon, int track) {
        String query = "INSERT INTO checkpoints (checkpoint_id, checkpoint_num, " +
                       "checkpoint_latitude, checkpoint_longitude, checkpoint_track) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(num) + ", " +
                       String.valueOf(lat) + ", " + String.valueOf(lon) + ", "
                       + String.valueOf(track) + ")";
        db.execSQL(query);
    }

    public static void userPointQuery(SQLiteDatabase db, int id, double lat,
          double lon, int experience) {
        String query = "INSERT INTO user_points (userpoint_id, userpoint_x, " +
                       "userpoint_y, " +
                       "userpoint_experience) VALUES (" + String.valueOf(id) + ", " +
                       String.valueOf(lat) + ", " + String.valueOf(lon) + ", " +
                       String.valueOf(experience) + ")";
        db.execSQL(query);
    }

    public static void contactQuery(SQLiteDatabase db, int id, String name,
            String value, double nwLat, double nwLon, double seLat, double seLon) {
        String query = "INSERT INTO contacts (contact_id, contact_name, " +
                       "contact_value, contact_nw_corner_latitude, contact_nw_corner_longitude, " +
                       "contact_se_corner_latitude, contact_se_corner_longitude) VALUES " +
                       "(" + String.valueOf(id) + ", '" + name + "', '" + value + "', " +
                       String.valueOf(nwLat) + ", " + String.valueOf(nwLon) + ", " + String.valueOf(seLat) + ", " + String.valueOf(seLon) + ")";
        db.execSQL(query);
    }

    public static void forecastQuery(SQLiteDatabase db, int id, long start,
         long end, String condition, int temperature, double neLat, double neLon, double swLat, double swLon) {
        String query = "INSERT INTO weather_forecasts (weather_id, weather_start, " +
                       "weather_end, weather_condition, weather_temperature, " +
                       "weather_nw_corner_latitude, weather_nw_corner_longitude, " +
                       "weather_se_corner_latitude, weather_se_corner_longitude) VALUES " +
                       "(" + String.valueOf(id) + ", " + String.valueOf(start) + ", " +
                       String.valueOf(end) + ", '" + condition + "', " +
                       String.valueOf(temperature) + ", " + String.valueOf(neLat) + ", " +
                       String.valueOf(neLon) + ", " + String.valueOf(swLat) + ", " +
                       String.valueOf(swLon) + ")";
        db.execSQL(query);
    }

    public static void checkPointEventQuery(SQLiteDatabase db, int id,
                                            int timestamp, int checkpointNum,
                                            int telemetryId) {
        String query = "INSERT INTO " + SerleenaDatabase
                .TABLE_TELEM_EVENTS_CHECKP + " (eventc_id, eventc_timestamp, " +
                "eventc_value, eventc_telem) VALUES (" +
                String.valueOf(id) + ", " +
                String.valueOf(timestamp) + ", " +
                String.valueOf(checkpointNum) + ", " +
                String.valueOf(telemetryId) + ")";
        db.execSQL(query);
    }

    public static SerleenaDatabase getEmptyDatabase() {
        SerleenaDatabase serleenaDB = new SerleenaDatabase(
                RuntimeEnvironment.application,
                "sample.db", null, 1);
        SQLiteDatabase db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);
        return serleenaDB;
    }

    public static SerleenaDatabase getDB() {
        SerleenaDatabase serleenaDB = getEmptyDatabase();
        SQLiteDatabase db = serleenaDB.getWritableDatabase();

        /* ESPERIENZE */
        experienceQuery(db, 1, "Experience_1");
        experienceQuery(db, 2, "Experience_2");

        /* PERCORSI */
        trackQuery(db, 1, "Track_1", 1);
        trackQuery(db, 2, "Track_2", 1);
        trackQuery(db, 3, "Track_3", 2);
        trackQuery(db, 4, "Track_4", 2);

        /* TRACCIAMENTI */
        telemetryQuery(db, 1, 1);
        telemetryQuery(db, 2, 1);
        telemetryQuery(db, 3, 2);
        telemetryQuery(db, 4, 2);

        /* EVENTI DI TRACCIAMENTO */
        locationTelemetryEventQuery(db, 1, new Date(100).getTime(), 1.0, 1.0, 1);
        locationTelemetryEventQuery(db, 2, new Date(200).getTime(), 1.0, 1.0, 2);
        locationTelemetryEventQuery(db, 3, new Date(300).getTime(), 2.0, 2.0, 3);
        locationTelemetryEventQuery(db, 4, new Date(400).getTime(), 2.0, 2.0, 4);
        heartTelemetryEventQuery(db, 1, new Date(500).getTime(), 100, "event_heartrate", 1);
        heartTelemetryEventQuery(db, 2, new Date(600).getTime(), 150, "event_heartrate", 2);
        heartTelemetryEventQuery(db, 3, new Date(700).getTime(), 200, "event_heartrate", 3);
        heartTelemetryEventQuery(db, 4, new Date(800).getTime(), 250, "event_heartrate", 4);

        /* CHECKPOINT */
        checkpointQuery(db, 1, 1, 1.0, 1.0, 1);
        checkpointQuery(db, 2, 2, 2.0, 2.0, 1);
        checkpointQuery(db, 3, 1, 3.0, 3.0, 2);
        checkpointQuery(db, 4, 2, 4.0, 4.0, 2);

        /* PUNTI UTENTE */
        userPointQuery(db, 1, 1.0, 1.0, 1);
        userPointQuery(db, 2, 2.0, 2.0, 1);
        userPointQuery(db, 3, 3.0, 3.0, 2);
        userPointQuery(db, 4, 4.0, 4.0, 2);

        /* CONTATTI DI EMERGENZA */
        contactQuery(db, 1, "Contact_1", "001", 2.0, 2.0, 1.0, 1.0);
        contactQuery(db, 2, "Contact_2", "002", 4.0, 4.0, 2.0, 2.0);

        /* PREVISIONI METEO */
        forecastQuery(db, 1, new Date(100).getTime(), new Date(200).getTime(), "Sunny", 10, 2.0, 2.0, 1.0, 1.0);
        forecastQuery(db, 2, new Date(200).getTime(), new Date(400).getTime(), "Cloudy", 20, 4.0, 4.0, 2.0, 2.0);

        return serleenaDB;
    }
}
