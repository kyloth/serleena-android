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
import java.util.Calendar;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
                TestFixtures.WEATHER_FIXTURE_POINT_INSIDE,
                TestFixtures.WEATHER_FIXTURE_CAL.getTime()
        );

        assertTrue(info.getMorningForecast() == TestFixtures.WEATHER_CONDITION_MORNING);
        assertTrue(info.getAfternoonForecast() == TestFixtures.WEATHER_CONDITION_AFTERNOON);
        assertTrue(info.getNightForecast()  == TestFixtures.WEATHER_CONDITION_NIGHT);
        assertTrue(info.getMorningTemperature() == TestFixtures.WEATHER_TEMPERATURE_MORNING);
        assertTrue(info.getAfternoonTemperature() == TestFixtures.WEATHER_TEMPERATURE_AFTERNOON);
        assertTrue(info.getNightTemperature() == TestFixtures.WEATHER_TEMPERATURE_NIGHT);
    }

    /**
     * Controlla che getWeatherInfo restituisca correttamente le informazioni
     * meteo per i margini di una regione.
     */
    @Test
    public void testGetWeatherInfoHitMargin()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                TestFixtures.WEATHER_FIXTURE_NW_POINT,
                TestFixtures.WEATHER_FIXTURE_CAL.getTime()
        );

        assertTrue(info.getMorningForecast() == TestFixtures.WEATHER_CONDITION_MORNING);
        assertTrue(info.getAfternoonForecast() == TestFixtures.WEATHER_CONDITION_AFTERNOON);
        assertTrue(info.getNightForecast()  == TestFixtures.WEATHER_CONDITION_NIGHT);
        assertTrue(info.getMorningTemperature() == TestFixtures.WEATHER_TEMPERATURE_MORNING);
        assertTrue(info.getAfternoonTemperature() == TestFixtures.WEATHER_TEMPERATURE_AFTERNOON);
        assertTrue(info.getNightTemperature() == TestFixtures.WEATHER_TEMPERATURE_NIGHT);

        info = sds.getWeatherInfo(
                new GeoPoint(
                        TestFixtures.WEATHER_FIXTURE_NW_POINT.latitude(),
                        TestFixtures.WEATHER_FIXTURE_NW_POINT.longitude()
                        ),
                TestFixtures.WEATHER_FIXTURE_CAL.getTime()
        );

        assertTrue(info.getMorningForecast() == TestFixtures.WEATHER_CONDITION_MORNING);
        assertTrue(info.getAfternoonForecast() == TestFixtures.WEATHER_CONDITION_AFTERNOON);
        assertTrue(info.getNightForecast()  == TestFixtures.WEATHER_CONDITION_NIGHT);
        assertTrue(info.getMorningTemperature() == TestFixtures.WEATHER_TEMPERATURE_MORNING);
        assertTrue(info.getAfternoonTemperature() == TestFixtures.WEATHER_TEMPERATURE_AFTERNOON);
        assertTrue(info.getNightTemperature() == TestFixtures.WEATHER_TEMPERATURE_NIGHT);
    }

    /**
     * Controlla che getWeatherInfo non restituisca informazioni meteo
     * della regione sbagliata, ma lanci un'eccezione.
     */
    @Test(expected = NoSuchWeatherForecastException.class)
    public void testGetWeatherInfoMissRegion()
            throws NoSuchWeatherForecastException {
        IWeatherStorage info = sds.getWeatherInfo(
                TestFixtures.WEATHER_FIXTURE_POINT_OUTSIDE,
                TestFixtures.WEATHER_FIXTURE_CAL.getTime()
        );
    }

    /**
     * Controlla che getWeatherInfo non restituisca informazioni meteo
     * relative al frame temporale sbagliato, ma lanci un'eccezione.
     */
    @Test(expected = NoSuchWeatherForecastException.class)
    public void testGetWeatherInfoMissTime()
            throws NoSuchWeatherForecastException {
        GregorianCalendar wrongDate = (GregorianCalendar) TestFixtures.WEATHER_FIXTURE_CAL.clone();
        wrongDate.add(Calendar.DAY_OF_YEAR, 10);
        IWeatherStorage info = sds.getWeatherInfo(
                TestFixtures.WEATHER_FIXTURE_POINT_OUTSIDE,
                wrongDate.getTime()
        );
    }

    @Before
    public void setup() throws URISyntaxException {
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, null, null, 1);
        db = sh.getWritableDatabase();
        sds = new SerleenaSQLiteDataSource(sh);
        ContentValues values = TestFixtures.pack(TestFixtures.WEATHER_FIXTURE);
        db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
    }
}