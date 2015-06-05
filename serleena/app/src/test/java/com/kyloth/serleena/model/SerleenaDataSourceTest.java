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
 * Name: SerleenaDataSourceTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import java.util.Iterator;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import static org.mockito.Mockito.*;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.content.Context;
import android.content.ContentValues;
import android.test.AndroidTestCase;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Quadrant;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;

/**
 * Contiene test per la classe WakeupSchedule.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaDataSourceTest {

    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);
        String insertEmergencyContacts_1 = "INSERT INTO contacts " +
                                           "(contact_name, contact_value, " +
                                           "contact_ne_corner_latitude, contact_ne_corner_longitude, " +
                                           "contact_sw_corner_latitude, contact_sw_corner_longitude) " +
                                           "VALUES ('Contact_1', '100', 1, 1, 10, 10);";
        String insertEmergencyContacts_2 = "INSERT INTO contacts " +
                                           "(contact_name, contact_value, " +
                                           "contact_ne_corner_latitude, contact_ne_corner_longitude, " +
                                           "contact_sw_corner_latitude, contact_sw_corner_longitude) " +
                                           "VALUES ('Contact_2', '200', 1, 1, 10, 10);";
        Long start_weather = (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000;
        Long end_weather = (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000;
        String insertForecasts_1 = "INSERT INTO weather_forecasts " +
                                   "(weather_start, weather_end, weather_condition, " +
                                   "weather_temperature, weather_ne_corner_latitude, " +
                                   "weather_ne_corner_longitude, weather_sw_corner_latitude, " +
                                   "weather_sw_corner_longitude) " +
                                   "VALUES (" + start_weather + ", " + end_weather +
                                   ", 2, 100, 10, 10, 1, 1);";
        db.execSQL(insertEmergencyContacts_1);
        db.execSQL(insertEmergencyContacts_2);
        ContentValues values_1 = new ContentValues();
        values_1.put("weather_start", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
        values_1.put("weather_end", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000);
        values_1.put("weather_condition", WeatherForecastEnum.Sunny.ordinal());
        values_1.put("weather_temperature", 1);
        values_1.put("weather_ne_corner_latitude", 0.0);
        values_1.put("weather_ne_corner_longitude", 0.0);
        values_1.put("weather_sw_corner_latitude", 2.0);
        values_1.put("weather_sw_corner_longitude", 2.0);
        db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values_1);
        ContentValues values_2;
        values_2 = new ContentValues();
        values_2.put("experience_name", "foo");
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values_2);
        serleenaSQLDS = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, serleenaDB);
        dataSource = new SerleenaDataSource(serleenaSQLDS);
    }

    /**
     * Chiude il database per permettere il funzionamento dei test successivi.
     */

    @After
    public void cleanUp() {
        serleenaDB.close();
    }

    /**
     * Verifica che il metodo getContacts resituisca la lista dei contatti di
     * emergenza nelle vicinanze di un punto geografico fornito.
     * Verifica inoltre che restituisca una lista vuota nel caso non ci fossero
     * contatti nelle vicinanze del punto fornito.
     */

    @Test
    public void testGetContacts() {
        Iterable<EmergencyContact> contacts = dataSource.getContacts(new GeoPoint(5, 5));
        Iterator<EmergencyContact> i_contacts = contacts.iterator();
        assertTrue(i_contacts.next().name().equals("Contact_1"));
        assertTrue(i_contacts.next().name().equals("Contact_2"));
        assertFalse(i_contacts.hasNext());
        Iterable<EmergencyContact> void_contacts = dataSource.getContacts(new GeoPoint(20, 20));
        Iterator<EmergencyContact> i_void_contacts = void_contacts.iterator();
        assertFalse(i_void_contacts.hasNext());
    }

    /**
     * Verifica che il metodo getQuadrant restituisca correttamente i quadranti
     * relativi ai GeoPoint forniti come input (senza sollevare eccezioni).
     */
    @Test
    public void testGetQuadrant() {
        GeoPoint normal_point = new GeoPoint(10, 10);
        GeoPoint min_point = new GeoPoint(-90.0, -180.0);
        GeoPoint max_point = new GeoPoint(90.0 - 10 / 2.0,
                                          180.0 - 10 / 2.0);
        Quadrant quadrant = (Quadrant) dataSource.getQuadrant(normal_point);
        Quadrant max_limit_quadrant = (Quadrant) dataSource.getQuadrant(min_point);
        Quadrant min_limit_quadrant = (Quadrant) dataSource.getQuadrant(max_point);
    }

    /**
     * Verifica che il metodo getWeatherInfo restituisca correttamente
     * le informazioni meteo relative alla località e alla data fornite.
     */
    @Test
    public void testGetWeatherInfo() throws NoSuchWeatherForecastException {
        Date time = (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime();
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(new GeoPoint(1.0, 1.0),
                                   (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime());
        assertTrue(forecast != null);
        assertTrue(forecast.getAfternoonForecast() == WeatherForecastEnum.Sunny);
    }

    /**
     * Verifica che il metodo getExperiences restituisca correttamente
     * la lista delle Esperienze salvate nel db.
     */
    @Test
    public void testGetExperiences() {
        Iterable<IExperience> experiences = dataSource.getExperiences();
        Iterator<IExperience> i_experiences = experiences.iterator();
        Experience experience = (Experience) i_experiences.next();
        assertTrue(experience.getName().equals("foo"));
    }




}
