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

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;

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

    public static void quadrantQuery(SQLiteDatabase db, double nwLat,
                                     double nwLon, double seLat, double seLon,
                                     String uuid) {
        ContentValues values = new ContentValues();
        values.put("raster_nw_corner_latitude", nwLat);
        values.put("raster_nw_corner_longitude", nwLon);
        values.put("raster_se_corner_latitude", seLat);
        values.put("raster_se_corner_longitude", seLon);
        // TODO: deve linkare a un'esperienza valida, e' FK
        // values.put("raster_uuid", uuid);
        db.insertOrThrow(SerleenaDatabase.TABLE_RASTERS, null, values);
    }

    public static void forecastQuery(
            SQLiteDatabase db,
            int id,
            int date,
            WeatherForecastEnum conditionMorning,
            WeatherForecastEnum conditionAfternoon,
            WeatherForecastEnum conditionNight,
            int tempMorning,
            int tempAfternoon,
            int tempNight,
            double nwLat, double nwLon, double seLat, double seLon) {
        String query =
                "INSERT INTO " + SerleenaDatabase.TABLE_WEATHER_FORECASTS + " ("  +
                "weather_id, " +
                "weather_date, " +
                "weather_condition_morning, " +
                "weather_temperature_morning, " +
                "weather_condition_afternoon, " +
                "weather_temperature_afternoon, " +
                "weather_condition_night, " +
                "weather_temperature_night, " +
                "weather_nw_corner_latitude, " +
                "weather_nw_corner_longitude, " +
                "weather_se_corner_latitude, " +
                "weather_se_corner_longitude) VALUES (" +
                String.valueOf(id) + ", " +
                String.valueOf(date) + ", " +
                String.valueOf(conditionMorning.ordinal()) + ", " +
                String.valueOf(tempMorning) + ", " +
                String.valueOf(conditionAfternoon.ordinal()) + ", " +
                String.valueOf(tempAfternoon) + ", " +
                String.valueOf(conditionNight.ordinal()) + ", " +
                String.valueOf(tempNight) + ", " +
                String.valueOf(nwLat) + ", " +
                String.valueOf(nwLon) + ", " +
                String.valueOf(seLat) + ", " +
                String.valueOf(seLon) + ")";

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
}
