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
 * Name: WeatherPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 * 2.0.0    Gabriele Pozzan  Aggiunta integrazione con gli altri package,
 *                                       incrementata copertura
 */

package com.kyloth.serleena.presenters;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SerleenaSensorManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contiene test per la classe WeatherPresenter.
 * In particolare vengono integrate le componenti dei package
 * sensors, common, model e persistence; vengono usati degli stub
 * per il package presentation.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class WeatherPresenterTest {

    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;

    int LOCATION_UPDATE_INTERVAL_SECONDS = 60;
    IWeatherView view;
    ISerleenaActivity activity;
    ISerleenaActivity activity_mock;
    SerleenaSensorManager sm;

    /**
     * Mock di sensors per verificare le chiamate all'interno dei metodi.
     */
    ISensorManager sm_mock;
    ILocationManager locMan;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        view = mock(IWeatherView.class);
        activity = mock(ISerleenaActivity.class);
        activity_mock = mock(ISerleenaActivity.class);
        sm = SerleenaSensorManager.getInstance(RuntimeEnvironment.application);
        sm_mock = mock(ISensorManager.class);
        locMan = mock(ILocationManager.class);
        when(activity_mock.getSensorManager()).thenReturn(sm_mock);
        when(sm_mock.getLocationSource()).thenReturn(locMan);
        when(activity.getSensorManager()).thenReturn(sm);

        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);
        serleenaSQLDS = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, serleenaDB);
        dataSource = new SerleenaDataSource(serleenaSQLDS);

        when(activity.getDataSource()).thenReturn(dataSource);

        Date date = Calendar.getInstance().getTime();

        Calendar today = new GregorianCalendar();
        today.setTime(date);
        today.set(Calendar.HOUR_OF_DAY, 00);
        today.set(Calendar.MINUTE, 00);
        today.set(Calendar.SECOND, 00);
        today.set(Calendar.MILLISECOND, 00);
        ContentValues values = new ContentValues();
        values.put("weather_date", (today).getTimeInMillis() / 1000);
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

        Calendar tomorrow = new GregorianCalendar();
        tomorrow.setTime(today.getTime());
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        values = new ContentValues();
        values.put("weather_date", (tomorrow).getTimeInMillis() / 1000);
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

    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null view" quando
     * invocato con IWeatherView nullo.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        WeatherPresenter wp = new WeatherPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null activity"
     * quando invocato con ISerleenaActivity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        WeatherPresenter wp = new WeatherPresenter(view, null);
    }

    /**
     * Verifica che il metodo resume non sollevi eccezioni e in
     * generale non generi errori. Verifica inoltre che il metodo
     * chiami correttamente il metodo attachObserver su locMan.
     */

    @Test
    public void testResume() {
        WeatherPresenter presenter = new WeatherPresenter(view, activity);
        presenter.resume();
        WeatherPresenter presenter_mock = new WeatherPresenter(view, activity_mock);
        presenter_mock.resume();
        verify(locMan).attachObserver(presenter_mock, LOCATION_UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che il metodo pause non sollevi eccezioni e in
     * generale non generi errori. Verifica inoltre che il metodo
     * chiami correttamente il metodo detachObserver su locMan.
     */

    @Test
    public void testPause() {
        WeatherPresenter presenter = new WeatherPresenter(view, activity);
        presenter.resume();
        presenter.pause();
        WeatherPresenter presenter_mock = new WeatherPresenter(view, activity_mock);
        presenter_mock.pause();
        verify(locMan).detachObserver(presenter_mock);
    }

    /**
     * Verifica che il metodo onLocationUpdate sollevi un'eccezione
     * di tipo IllegalArgumentException con messaggio "Illegal null
     * location" se invocata con GeoPoint nullo.
     */

    @Test
    public void onLocationUpdateShouldThrowExceptionWhenNullLocation() {
        WeatherPresenter presenter = new WeatherPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        presenter.onLocationUpdate(null);
    }

    /**
     * Verifica il metodo present attraverso le chiamate dei metodi
     * onLocationUpdate e advanceDate.
     */

    @Test
    public void testPresent() {
        WeatherPresenter presenter = new WeatherPresenter(view, activity);
        presenter.onLocationUpdate(new GeoPoint(1, 1));

        verify(view, timeout(200).times(2)).setDate(any(Date.class));
        verify(view, timeout(200).times(1)).setWeatherInfo(any(IWeatherForecast.class));
        /* Nel db non sono presenti informazioni meteo
           per una localita con coordinate 15, 15
        */
        presenter.onLocationUpdate(new GeoPoint(15, 15));
        /* Nella prossima chiamata viene mantenuta la
           località impostata per ultima, dunque non ci
           saranno informazioni meteo disponibili.
        */
        presenter.advanceDate();
        verify(view, timeout(200).times(2)).clearWeatherInfo();
    }

}
