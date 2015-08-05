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
 * Name: WeatherForecastTest.java
 * Package: com.kyloth.serleena.model;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertTrue;

/**
 * Contiene test per la classe WeatherForecast.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class WeatherForecastTest {
    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);

        ContentValues values = new ContentValues();
        values.put("weather_date", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
        values.put("weather_condition_morning", WeatherForecastEnum.Stormy.ordinal());
        values.put("weather_temperature_morning", -2);
        values.put("weather_condition_afternoon", WeatherForecastEnum.Cloudy.ordinal());
        values.put("weather_temperature_afternoon", 0);
        values.put("weather_condition_night", WeatherForecastEnum.Sunny.ordinal());
        values.put("weather_temperature_night", 2);
        values.put("weather_nw_corner_latitude", 2.0);
        values.put("weather_nw_corner_longitude", 0.0);
        values.put("weather_se_corner_latitude", 0.0);
        values.put("weather_se_corner_longitude", 2.0);

        db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);

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
     * Verifica che i metodi getter della classe restituiscano le previsioni
     * correttamente per le diverse fasce orarie.
     */
    @Test
    public void testGetters() throws NoSuchWeatherForecastException {
        GeoPoint location = new GeoPoint(1.0, 1.0);
        Date time = new Date(new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00).getTimeInMillis());
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(location, time);
        assertTrue(forecast.getMorningForecast() == WeatherForecastEnum.Stormy);
        assertTrue(forecast.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
        assertTrue(forecast.getNightForecast() == WeatherForecastEnum.Sunny);
        assertTrue(forecast.getMorningTemperature() == -2);
        assertTrue(forecast.getAfternoonTemperature() == 0);
        assertTrue(forecast.getNightTemperature() == 2);
        assertTrue(forecast.date().equals(time));
    }

    /**
     * Verifica che venga sollevata un'eccezione di tipo
     * NoSuchWeatherForecastException qualora si richiedessero
     * informazioni meteo per una località non coperta.
     */

    @Test
    public void testExceptionWrongLocation() throws NoSuchWeatherForecastException {
        GeoPoint fail_location = new GeoPoint(3.0, 3.0);
        Date time = new Date(new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00).getTimeInMillis());
        exception.expect(NoSuchWeatherForecastException.class);
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(fail_location, time);

    }

    /**
     * Verifica che venga sollevata un'eccezione di tipo
     * NoSuchWeatherForecastException qualora si richiedessero
     * informazioni meteo per una data non coperta.
     */

    @Test
    public void testExceptionWrongDate() throws NoSuchWeatherForecastException {
        GeoPoint location = new GeoPoint(1.0, 1.0);
        Date fail_time = new Date(new GregorianCalendar(2016, GregorianCalendar.JANUARY, 02, 00, 00, 00).getTimeInMillis());
        exception.expect(NoSuchWeatherForecastException.class);
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(location, fail_time);
    }

    /**
     * Verifica che getWeatherInfo sollevi IllegalArgumentException se ottiene come parametro una data con ora
     * diversa da 00:00:00
     *
     * @throws NoSuchWeatherForecastException
     */
    @Test
    public void testNonZeroHoursNotAllowed() throws NoSuchWeatherForecastException {
        GeoPoint location = new GeoPoint(1.0, 1.0);
        Date fail_time = new Date(new GregorianCalendar(2016, GregorianCalendar.JANUARY, 01, 01, 00, 00).getTimeInMillis());
        exception.expect(IllegalArgumentException.class);
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(location, fail_time);
    }


    /**
     * Verifica che getWeatherInfo sollevi IllegalArgumentException se ottiene come parametro una data con ora
     * diversa da 00:00:00
     *
     * @throws NoSuchWeatherForecastException
     */
    @Test
    public void testNonZeroMinutesNotAllowed() throws NoSuchWeatherForecastException {
        GeoPoint location = new GeoPoint(1.0, 1.0);
        Date fail_time = new Date(new GregorianCalendar(2016, GregorianCalendar.JANUARY, 01, 00, 01, 00).getTimeInMillis());
        exception.expect(IllegalArgumentException.class);
        WeatherForecast forecast = (WeatherForecast) dataSource.getWeatherInfo(location, fail_time);
    }
}
