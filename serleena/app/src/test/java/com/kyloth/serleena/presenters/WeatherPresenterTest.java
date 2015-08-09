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

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.verification.VerificationModeFactory;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Contiene test di unità per la classe WeatherPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class WeatherPresenterTest {

    private ISerleenaDataSource dataSource;
    private IWeatherView view;
    private ISerleenaActivity activity;
    private Date[] dates = new Date[7];
    private ILocationManager locMan;
    private WeatherPresenter presenter;

    @Before
    public void initialize() {
        view = mock(IWeatherView.class);
        activity = mock(ISerleenaActivity.class);
        locMan = mock(ILocationManager.class);
        dataSource = mock(ISerleenaDataSource.class);
        ISensorManager sensorManager = mock(ISensorManager.class);

        when(sensorManager.getLocationSource()).thenReturn(locMan);
        when(activity.getSensorManager()).thenReturn(sensorManager);
        when(activity.getDataSource()).thenReturn(dataSource);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);
        dates[0] = calendar.getTime();

        for (int i = 1; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dates[i] = calendar.getTime();
        }

        presenter = new WeatherPresenter(view, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException se invocato con IWeatherView nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullView() {
        new WeatherPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException quando invocato con activity nulla.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullActivity() {
        new WeatherPresenter(view, null);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException quando invocato con entrambi i parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowIfNullArgument() {
        new WeatherPresenter(null, null);
    }

    /**
     * Verifica che il resume del presenter causi la registrazione di questo
     * ai gestori dei sensori di cui ha bisogno, con intervallo di
     * aggiornamento secondo specifica.
     */
    @Test
    public void presenterShouldRegisterItselfToSensorsOnResume() {
        presenter.resume();
        verify(locMan).attachObserver(
                presenter, WeatherPresenter.LOCATION_UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che mettendo in pausa il presenter venga cancellata la
     * registrazione di questo ai gestori dei sensori.
     */
    @Test
    public void presenterShouldUnregisterItselfFromSensorsOnPause() {
        presenter.pause();
        verify(locMan).detachObserver(presenter);
    }

    /**
     * Verifica che il metodo onLocationUpdate sollevi un'eccezione
     * di tipo IllegalArgumentException se invocata con GeoPoint nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void onLocationUpdateShouldThrowExceptionWhenNullLocation() {
        presenter.onLocationUpdate(null);
    }

    /**
     * Verifica che la notifica di una nuova posizione utente causi la
     * presentazione sulla vista delle informazioni meteo per quella
     * posizione e per il giorno correntemente impostato, che può essere
     * quello corrente o uno dei successivi sei.
     */
    @Test
    public void onLocationUpdateShouldSetViewAccordingly()
            throws NoSuchWeatherForecastException {
        GeoPoint point1 = mock(GeoPoint.class);
        GeoPoint point2 = mock(GeoPoint.class);
        IWeatherForecast forecast1 = mock(IWeatherForecast.class);
        IWeatherForecast forecast2 = mock(IWeatherForecast.class);
        when(dataSource.getWeatherInfo(point1, dates[0])).thenReturn(forecast1);
        when(dataSource.getWeatherInfo(point2, dates[2])).thenReturn(forecast2);
        presenter.onLocationUpdate(point1);
        verify(view, timeout(10000)).setWeatherInfo(forecast1);

        presenter.advanceDate();
        presenter.advanceDate();
        presenter.onLocationUpdate(point2);
        verify(view, timeout(10000)).setWeatherInfo(forecast2);
    }

    /**
     * Verifica che la richiesta di avanzamento di data imposti la vista con
     * le informazioni corrette per il giorno selezionato.
     */
    @Test
    public void
    advanceDateShouldSetWeatherInfoAccordingToHowManyDaysAfterCurrent()
            throws NoSuchWeatherForecastException {
        GeoPoint point = mock(GeoPoint.class);
        presenter.onLocationUpdate(point);

        IWeatherForecast[] forecasts = new IWeatherForecast[7];
        for (int i = 0; i < 7; i++) {
            forecasts[i] = mock(IWeatherForecast.class);
            when(dataSource.getWeatherInfo(point, dates[i]))
                    .thenReturn(forecasts[i]);
        }

        for (int i = 1; i <= 7; i++) {
            presenter.advanceDate();
            verify(view, timeout(10000)).setWeatherInfo(forecasts[i % 7]);
        }
    }

    /**
     * Verifica che la vista venga pulita quando vengono richieste
     * informazioni per un altro giorno.
     */
    @Test
    public void viewShouldBeClearedWhenAdvancingDate() {
        presenter.advanceDate();
        verify(view, times(1)).clearWeatherInfo();
        presenter.onLocationUpdate(mock(GeoPoint.class));
        verify(view, timeout(10000).times(2)).clearWeatherInfo();
    }

    /**
     * Verifica che la data selezionata venga impostata sulla vista, anche se
     * non sono presenti informazioni metereologiche per quella data.
     */
    @Test
    public void presenterShouldSetDateEvenIfThereIsNoWeatherForecast()
            throws NoSuchWeatherForecastException {
        when(dataSource.getWeatherInfo(any(GeoPoint.class), any(Date.class)))
                .thenThrow(NoSuchWeatherForecastException.class);
        presenter.onLocationUpdate(mock(GeoPoint.class));

        for (int i = 1; i <= 7; i++) {
            presenter.advanceDate();
            verify(view, timeout(10000)).setDate(dates[i % 7]);
        }
    }

    /**
     * Verifica che il presenter imposti la data corrente di presentazione
     * sulla vista durante il resume.
     */
    @Test
    public void presenterShouldSetCurrentDateWhenResuming() {
        verify(view, times(0)).setDate(any(Date.class));
        presenter.resume();
        verify(view).setDate(dates[0]);
    }

}
