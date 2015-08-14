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


package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

public class TestFixtures {

    public static ContentValues pack(Map<String, Object> fixture) {
        ContentValues values = new ContentValues();

        for (Map.Entry<String, Object> entry : fixture.entrySet()) {
            String key = entry.getKey();

            Object value = entry.getValue();
            if (value.getClass() == Integer.class) {
                values.put(key, (Integer)value);
            } else if (value.getClass() == String.class) {
                values.put(key, (String)value);
            } else if (value.getClass() == Float.class) {
                values.put(key, (Float)value);
            } else if (value.getClass() == Double.class) {
                values.put(key, (Double)value);
            } else if (value.getClass() == Long.class) {
                values.put(key, (Long)value);
            } else {
                throw new RuntimeException(value.getClass().toString());
            }
        }
        return values;
    }


    /*****************************
     * Weather fixtures
     ****************************/
        public static final Calendar WEATHER_FIXTURE_CAL = new GregorianCalendar(2015, GregorianCalendar.JANUARY, 10, 00, 00, 00);
        public static final GeoPoint WEATHER_FIXTURE_POINT_INSIDE = new GeoPoint(1.0, 1.0);
        public static final GeoPoint WEATHER_FIXTURE_POINT_OUTSIDE = new GeoPoint(-3.0, 3.0);
        public static final WeatherForecastEnum WEATHER_CONDITION_MORNING = WeatherForecastEnum.Stormy;
        public static final int WEATHER_TEMPERATURE_MORNING = -2;
        public static final WeatherForecastEnum WEATHER_CONDITION_AFTERNOON = WeatherForecastEnum.Cloudy;
        public static final int WEATHER_TEMPERATURE_AFTERNOON = 0;
        public static final WeatherForecastEnum WEATHER_CONDITION_NIGHT = WeatherForecastEnum.Sunny;
        public static final int WEATHER_TEMPERATURE_NIGHT = 2;
        public static final GeoPoint WEATHER_FIXTURE_NW_POINT = new GeoPoint(2.0, 0.0);
        public static final GeoPoint WEATHER_FIXTURE_SE_POINT = new GeoPoint(0.0, 2.0);
        public static final Map<String, Object> WEATHER_FIXTURE;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("weather_date", new Long((WEATHER_FIXTURE_CAL).getTimeInMillis() / 1000));
            tmp.put("weather_condition_morning", WEATHER_CONDITION_MORNING.ordinal());
            tmp.put("weather_temperature_morning", WEATHER_TEMPERATURE_MORNING);
            tmp.put("weather_condition_afternoon", WEATHER_CONDITION_AFTERNOON.ordinal());
            tmp.put("weather_temperature_afternoon", WEATHER_TEMPERATURE_AFTERNOON);
            tmp.put("weather_condition_night", WEATHER_CONDITION_NIGHT.ordinal());
            tmp.put("weather_temperature_night", WEATHER_TEMPERATURE_NIGHT);
            tmp.put("weather_nw_corner_latitude", WEATHER_FIXTURE_NW_POINT.latitude());
            tmp.put("weather_nw_corner_longitude", WEATHER_FIXTURE_NW_POINT.longitude());
            tmp.put("weather_se_corner_latitude", WEATHER_FIXTURE_SE_POINT.latitude());
            tmp.put("weather_se_corner_longitude", WEATHER_FIXTURE_SE_POINT.longitude());
            WEATHER_FIXTURE = Collections.unmodifiableMap(tmp);
        };

    /*****************************
     * Contacts fixtures
     ****************************/
    public static final GeoPoint CONTACTS_FIXTURE_POINT_INSIDE_BOTH = new GeoPoint(5,5);
    public static final GeoPoint CONTACTS_FIXTURE_POINT_INSIDE_NEITHER = new GeoPoint(30,30);

        /////////////////////////
        // Contact #1
        /////////////////////////
        public static final String CONTACTS_FIXTURE_1_NAME = "Contact_1";
        public static final String CONTACTS_FIXTURE_1_VALUE = "100";
        public static final GeoPoint CONTACTS_FIXTURE_1_NW_CORNER = new GeoPoint(10, 1);
        public static final GeoPoint CONTACTS_FIXTURE_1_SE_CORNER = new GeoPoint(1, 10);
        public static final Map<String, Object> CONTACTS_FIXTURE_1;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("contact_name", CONTACTS_FIXTURE_1_NAME);
            tmp.put("contact_value", CONTACTS_FIXTURE_1_VALUE);
            tmp.put("contact_nw_corner_latitude", CONTACTS_FIXTURE_1_NW_CORNER.latitude());
            tmp.put("contact_nw_corner_longitude", CONTACTS_FIXTURE_1_NW_CORNER.longitude());
            tmp.put("contact_se_corner_latitude", CONTACTS_FIXTURE_1_SE_CORNER.latitude());
            tmp.put("contact_se_corner_longitude",  CONTACTS_FIXTURE_1_SE_CORNER.longitude());
            CONTACTS_FIXTURE_1 = Collections.unmodifiableMap(tmp);
        };

        /////////////////////////
        // Contact #2
        /////////////////////////
        public static final String CONTACTS_FIXTURE_2_NAME = "Contact_2";
        public static final String CONTACTS_FIXTURE_2_VALUE = "200";
        // !!! DEVONO ESSERE DIVERSI DA CONTACTS_1 !!!

        public static final GeoPoint CONTACTS_FIXTURE_2_NW_CORNER = new GeoPoint(10, 1);
        public static final GeoPoint CONTACTS_FIXTURE_2_SE_CORNER = new GeoPoint(1, 10);
        public static final Map<String, Object> CONTACTS_FIXTURE_2;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("contact_name", CONTACTS_FIXTURE_2_NAME);
            tmp.put("contact_value", CONTACTS_FIXTURE_2_VALUE);
            tmp.put("contact_nw_corner_latitude", CONTACTS_FIXTURE_2_NW_CORNER.latitude());
            tmp.put("contact_nw_corner_longitude", CONTACTS_FIXTURE_2_NW_CORNER.longitude());
            tmp.put("contact_se_corner_latitude", CONTACTS_FIXTURE_2_SE_CORNER.latitude());
            tmp.put("contact_se_corner_longitude",  CONTACTS_FIXTURE_2_SE_CORNER.longitude());
            CONTACTS_FIXTURE_2 = Collections.unmodifiableMap(tmp);
        };

    /*****************************
     * Experiences fixtures
     ****************************/
        /////////////////////////
        // Exp #1
        /////////////////////////
        public static final int EXPERIENCES_FIXTURE_EXPERIENCE_1_ID = 1;
        public static final String EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME = "Experience_1";

        public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_1;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("experience_id", EXPERIENCES_FIXTURE_EXPERIENCE_1_ID);
            tmp.put("experience_name", EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
            EXPERIENCES_FIXTURE_EXPERIENCE_1 = Collections.unmodifiableMap(tmp);
        };

            /////////////////////////
            // Exp #1 Track #1
            /////////////////////////
            public static final int EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_ID = 5;
            public static final String EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_NAME = "Track_1";

            public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1;
            static {
                Hashtable<String, Object> tmp = new Hashtable<String, Object>();
                tmp.put("track_id", EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_ID);
                tmp.put("track_name", EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_NAME);
                tmp.put("track_experience", EXPERIENCES_FIXTURE_EXPERIENCE_1_ID);
                EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1 = Collections.unmodifiableMap(tmp);
            };

            /////////////////////////
            // Exp #1 Track #2
            /////////////////////////
            public static final int EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_ID = 9;
            public static final String EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_NAME = "Track_2";

            public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2;
            static {
                Hashtable<String, Object> tmp = new Hashtable<String, Object>();
                tmp.put("track_id", EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_ID);
                tmp.put("track_name", EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_NAME);
                tmp.put("track_experience", EXPERIENCES_FIXTURE_EXPERIENCE_1_ID);
                EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2 = Collections.unmodifiableMap(tmp);
            };

                /////////////////////////
                // Exp #1 Userpoint #1
                /////////////////////////
                public static final double EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT = 13;
                public static final double EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON = 73;

                public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1;
                static {
                    Hashtable<String, Object> tmp = new Hashtable<String, Object>();
                    tmp.put("userpoint_x", EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT);
                    tmp.put("userpoint_y", EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON);
                    tmp.put("userpoint_experience", EXPERIENCES_FIXTURE_EXPERIENCE_1_ID);
                    EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1 = Collections.unmodifiableMap(tmp);
                };

        /////////////
        // Exp #2
        /////////////
        public static final int EXPERIENCES_FIXTURE_EXPERIENCE_2_ID = 2;
        public static final String EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME = "Experience_2";
        public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_2;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("experience_id", EXPERIENCES_FIXTURE_EXPERIENCE_2_ID);
            tmp.put("experience_name", EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME);
            EXPERIENCES_FIXTURE_EXPERIENCE_2 = Collections.unmodifiableMap(tmp);
        };


            public static final int EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_ID = 12;
            public static final String EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_NAME = "Track_3";

            public static final Map<String, Object> EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1;
            static {
                Hashtable<String, Object> tmp = new Hashtable<String, Object>();
                tmp.put("track_id", EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_ID);
                tmp.put("track_name", EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_NAME);
                tmp.put("track_experience", EXPERIENCES_FIXTURE_EXPERIENCE_2_ID);
                EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1 = Collections.unmodifiableMap(tmp);
            };

        ////////////////////////////////////////
        // TODO: this must be improved
        ///////////////////////////////////////

        public static final float raster_nw_corner_latitude = 5;
        public static final float raster_nw_corner_longitude = 0;
        public static final float raster_se_corner_latitude = 0;
        public static final float raster_se_corner_longitude = 5;
        public static final String raster_base64 = "iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAIAAACxN37FAAABJ0lEQVR42u3XwQnDMAwF0JxzKOlAXbC75tABjCssMJ4grcJ7GKPz5yPsbQMAAAAAAAAAAAAAAAAAAAAAAAAAAAAutrejD3OQCYWtVc5BJtyh0DY093lyrLdMKL+h11eHTPApBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfuTzfvZhDjKhsGjw3o5oc5zz8YpbJtQudPY4am1DU172OO/c0zKh9oaej43c1jKh9qdwVjkHmQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAf+AIrnplnW4V/mgAAAABJRU5ErkJggg==";

        public static final Map<String, Object> RASTER_FIXTURE;
        static {
            Hashtable<String, Object> tmp = new Hashtable<String, Object>();
            tmp.put("raster_nw_corner_latitude", raster_nw_corner_latitude);
            tmp.put("raster_nw_corner_longitude", raster_nw_corner_longitude);
            tmp.put("raster_se_corner_latitude", raster_se_corner_latitude);
            tmp.put("raster_se_corner_longitude", raster_se_corner_longitude);
            tmp.put("raster_base64", raster_base64);
            tmp.put("raster_experience", EXPERIENCES_FIXTURE_EXPERIENCE_1_ID);
            RASTER_FIXTURE = Collections.unmodifiableMap(tmp);
        };



}
