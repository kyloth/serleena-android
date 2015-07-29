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
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaSQLiteDataSourceWeatherTest {

    SerleenaSQLiteDataSource sds;
    SQLiteDatabase db;


    /**
     * Controlla che getWeatherInfo restituisca correttamente le informazioni
     * meteo dove presenti.
     */
    @Test
    public void testGetWeatherInfoHit()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                new GeoPoint(1.0, 1.0),
                (new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY,
                        01)
                ).getTime());
        assertTrue(info.getMorningForecast() == WeatherForecastEnum.Stormy);
        assertTrue(info.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
        assertTrue(info.getNightForecast() == WeatherForecastEnum.Sunny);
        assertTrue(info.getMorningTemperature() == -2);
        assertTrue(info.getAfternoonTemperature() == 0);
        assertTrue(info.getNightTemperature() == 2);
    }

    /**
     * Controlla che getWeatherInfo restituisca correttamente le informazioni
     * meteo per i margini di una regione.
     */
    @Test
    public void testGetWeatherInfoHitMargin()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                new GeoPoint(0.0, 0.0),
                (new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY,
                        01,
                        00,
                        00,
                        00)
                ).getTime());
        assertTrue(info.getMorningForecast() == WeatherForecastEnum.Stormy);
        assertTrue(info.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
        assertTrue(info.getNightForecast() == WeatherForecastEnum.Sunny);
        assertTrue(info.getMorningTemperature() == -2);
        assertTrue(info.getAfternoonTemperature() == 0);
        assertTrue(info.getNightTemperature() == 2);

        info = sds.getWeatherInfo(
                new GeoPoint(2.0, 2.0),
                (new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY,
                        01,
                        00,
                        00,
                        00)
                ).getTime());
        assertTrue(info.getMorningForecast() == WeatherForecastEnum.Stormy);
        assertTrue(info.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
        assertTrue(info.getNightForecast() == WeatherForecastEnum.Sunny);
        assertTrue(info.getMorningTemperature() == -2);
        assertTrue(info.getAfternoonTemperature() == 0);
        assertTrue(info.getNightTemperature() == 2);
    }

    /**
     * Controlla che getWeatherInfo non restituisca informazioni meteo
     * della regione sbagliata, ma lanci un'eccezione.
     */
    @Test(expected = NoSuchWeatherForecastException.class)
    public void testGetWeatherInfoMissRegion()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                new GeoPoint(3.0, 3.0),
                (new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY, 01)).getTime());
    }

    /**
     * Controlla che getWeatherInfo non restituisca informazioni meteo
     * relative al frame temporale sbagliato, ma lanci un'eccezione.
     */
    @Test(expected = NoSuchWeatherForecastException.class)
    public void testGetWeatherInfoMissTime()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                new GeoPoint(1.0, 1.0),
                (new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY, 02)).getTime());
    }

    @Before
    public void setup() throws URISyntaxException {
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, null, null, 1);
        db = sh.getWritableDatabase();
        sds = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, sh);        ContentValues values = new ContentValues();
        values.put("weather_date", (
                new GregorianCalendar(2015,
                        GregorianCalendar.JANUARY, 01, 00, 00,
                        00)).getTimeInMillis() / 1000);
        values.put("weather_condition_morning", WeatherForecastEnum.Stormy.ordinal());
        values.put("weather_temperature_morning", -2);
        values.put("weather_condition_afternoon", WeatherForecastEnum.Cloudy.ordinal());
        values.put("weather_temperature_afternoon", 0);
        values.put("weather_condition_night", WeatherForecastEnum.Sunny.ordinal());
        values.put("weather_temperature_night", 2);
        values.put("weather_nw_corner_latitude", 0.0);
        values.put("weather_nw_corner_longitude", 0.0);
        values.put("weather_se_corner_latitude", 2.0);
        values.put("weather_se_corner_longitude", 2.0);
        db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
    }
}