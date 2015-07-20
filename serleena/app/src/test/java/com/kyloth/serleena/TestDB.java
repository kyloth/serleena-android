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

import org.junit.runner.RunWith;

import java.util.Date;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.common.TelemetryEventType;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class TestDB {

    public static SerleenaDatabase serleenaDB;
    public static SQLiteDatabase db;

    private static void experienceQuery(int id, String name) {
        String query = "INSERT INTO experiences (experience_id, experience_name) VALUES (" +
                       String.valueOf(id) + ", '" +  name + "')";
        db.execSQL(query);
    }

    private static void trackQuery(int id, String name, int experience) {
        String query = "INSERT INTO tracks (track_id, track_name, track_experience) " +
                       "VALUES (" + String.valueOf(id) + ", '" + name + "', " +
                       String.valueOf(experience) + ")";
        db.execSQL(query);
    }

    private static void telemetryQuery(int id, int track) {
        String query = "INSERT INTO telemetries (telem_id, telem_track) VALUES (" +
                       String.valueOf(id) + ", " + String.valueOf(track) + ")";
        db.execSQL(query);
    }

    private static void locationTelemetryEventQuery(int id, long timestamp,
            double lat, double lon, int telemetry) {
        String query = "INSERT INTO telemetry_events_location (eventl_id, " +
                       "eventl_timestamp, eventl_latitude, eventl_longitude, " +
                       "eventl_telem) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(timestamp) +
                       ", " + String.valueOf(lat) + ", " + String.valueOf(lon) + ", " +
                       String.valueOf(telemetry) + ")";
        db.execSQL(query);
    }

    private static void heartTelemetryEventQuery(int id, long timestamp, int value, String type, int telemetry) {
        String query = "INSERT INTO telemetry_events_heart_checkp (eventhc_id, " +
                       "eventhc_timestamp, eventhc_value, eventhc_type, eventhc_telem) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(timestamp) +
                       ", " + String.valueOf(value) + ", '" + type + "', " +
                       String.valueOf(telemetry) + ")";
        db.execSQL(query);
    }

    private static void checkpointQuery(int id, int num, double lat, double lon,
                                        int track) {
        String query = "INSERT INTO checkpoints (checkpoint_id, checkpoint_num, " +
                       "checkpoint_latitude, checkpoint_longitude, checkpoint_track) " +
                       "VALUES (" + String.valueOf(id) + ", " + String.valueOf(num) + ", " +
                       String.valueOf(lat) + ", " + String.valueOf(lon) + ", "
                       + String.valueOf(track) + ")";
        db.execSQL(query);
    }

    private static void userPointQuery(int id, double lat, double lon,
                                       int experience) {
        String query = "INSERT INTO user_points (userpoint_id, userpoint_x, " +
                       "userpoint_y, " +
                       "userpoint_experience) VALUES (" + String.valueOf(id) + ", " +
                       String.valueOf(lat) + ", " + String.valueOf(lon) + ", " +
                       String.valueOf(experience) + ")";
        db.execSQL(query);
    }

    private static void contactQuery(int id, String name, String value, double neLat, double neLon, double swLat, double swLon) {
        String query = "INSERT INTO contacts (contact_id, contact_name, " +
                       "contact_value, contact_ne_corner_latitude, contact_ne_corner_longitude, " +
                       "contact_sw_corner_latitude, contact_sw_corner_longitude) VALUES " +
                       "(" + String.valueOf(id) + ", '" + name + "', '" + value + "', " +
                       String.valueOf(neLat) + ", " + String.valueOf(neLon) + ", " + String.valueOf(swLat) + ", " + String.valueOf(swLon) + ")";
        db.execSQL(query);
    }

    private static void forecastQuery(int id, long start, long end, String condition, int temperature, double neLat, double neLon, double swLat, double swLon) {
        String query = "INSERT INTO weather_forecasts (weather_id, weather_start, " +
                       "weather_end, weather_condition, weather_temperature, " +
                       "weather_ne_corner_latitude, weather_ne_corner_longitude, " +
                       "weather_sw_corner_latitude, weather_sw_corner_longitude) VALUES " +
                       "(" + String.valueOf(id) + ", " + String.valueOf(start) + ", " +
                       String.valueOf(end) + ", '" + condition + "', " +
                       String.valueOf(temperature) + ", " + String.valueOf(neLat) + ", " +
                       String.valueOf(neLon) + ", " + String.valueOf(swLat) + ", " +
                       String.valueOf(swLon) + ")";
        db.execSQL(query);
    }

    public static SerleenaDatabase getDB() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);

        /* ESPERIENZE */
        experienceQuery(1, "Experience_1");
        experienceQuery(2, "Experience_2");

        /* PERCORSI */
        trackQuery(1, "Track_1", 1);
        trackQuery(2, "Track_2", 1);
        trackQuery(3, "Track_3", 2);
        trackQuery(4, "Track_4", 2);

        /* TRACCIAMENTI */
        telemetryQuery(1, 1);
        telemetryQuery(2, 1);
        telemetryQuery(3, 2);
        telemetryQuery(4, 2);

        /* EVENTI DI TRACCIAMENTO */
        locationTelemetryEventQuery(1, new Date(100).getTime(), 1.0, 1.0, 1);
        locationTelemetryEventQuery(2, new Date(200).getTime(), 1.0, 1.0, 2);
        locationTelemetryEventQuery(3, new Date(300).getTime(), 2.0, 2.0, 3);
        locationTelemetryEventQuery(4, new Date(400).getTime(), 2.0, 2.0, 4);
        heartTelemetryEventQuery(1, new Date(500).getTime(), 100, "event_heartrate", 1);
        heartTelemetryEventQuery(2, new Date(600).getTime(), 150, "event_heartrate", 2);
        heartTelemetryEventQuery(3, new Date(700).getTime(), 200, "event_heartrate", 3);
        heartTelemetryEventQuery(4, new Date(800).getTime(), 250, "event_heartrate", 4);

        /* CHECKPOINT */
        checkpointQuery(1, 1, 1.0, 1.0, 1);
        checkpointQuery(2, 2, 2.0, 2.0, 1);
        checkpointQuery(3, 1, 3.0, 3.0, 2);
        checkpointQuery(4, 2, 4.0, 4.0, 2);

        /* PUNTI UTENTE */
        userPointQuery(1, 1.0, 1.0, 1);
        userPointQuery(2, 2.0, 2.0, 1);
        userPointQuery(3, 3.0, 3.0, 2);
        userPointQuery(4, 4.0, 4.0, 2);

        /* CONTATTI DI EMERGENZA */
        contactQuery(1, "Contact_1", "001", 2.0, 2.0, 1.0, 1.0);
        contactQuery(2, "Contact_2", "002", 4.0, 4.0, 2.0, 2.0);

        /* PREVISIONI METEO */
        forecastQuery(1, new Date(100).getTime(), new Date(200).getTime(), "Sunny", 10, 2.0, 2.0, 1.0, 1.0);
        forecastQuery(2, new Date(200).getTime(), new Date(400).getTime(), "Cloudy", 20, 4.0, 4.0, 2.0, 2.0);

        return serleenaDB;



    }
}
