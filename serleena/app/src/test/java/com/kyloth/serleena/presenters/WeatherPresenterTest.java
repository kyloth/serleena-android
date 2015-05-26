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
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.ILocationManager;

/**
 * Contiene i test di unità per la classe WeatherPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class WeatherPresenterTest {
    int LOCATION_UPDATE_INTERVAL_SECONDS = 60;
    IWeatherView view;
    ISerleenaActivity activity;
    ISensorManager manager;
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
        manager = mock(ISensorManager.class);
        locMan = mock(ILocationManager.class);
        when(activity.getSensorManager()).thenReturn(manager);
        when(manager.getLocationSource()).thenReturn(locMan);
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
     * Verifica che il metodo present sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null location"
     * quando invocato con GeoPoint nullo.
     */

    @Test
    public void presentShouldThrowExceptionWhenNullLocation() {
        WeatherPresenter wp = new WeatherPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        wp.present(0, null);
    }

    /**
     * Verifica che il metodo onLocationUpdate sollevi un'eccezione di
     * tipo IllegalArgumentException con messaggio "Illegal null location"
     * quando invocato con GeoPoint nullo.
     */

    @Test
    public void onLocationUpdateShouldThrowExceptionWhenNullLocation() {
        WeatherPresenter wp = new WeatherPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        wp.onLocationUpdate(null);
    }

    /**
     * Verifica che il metodo resume chiami a sua volta il metodo attachObserver
     * sull' ILocationManager passando come parametri se stesso e l'intervallo
     * di aggiornamento corretto.
     */

    @Test
    public void resumeShouldForwardCorrectParams() {
        WeatherPresenter wp = new WeatherPresenter(view, activity);
        wp.resume();
        verify(locMan).attachObserver(wp, LOCATION_UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che il metodo pause chiami a sua volta il metodo detachObserver
     * sull'ILocationManager passando come parametro se stesso.
     */

    @Test
    public void pauseShouldForwardCorrectParam() {
        WeatherPresenter wp = new WeatherPresenter(view, activity);
        wp.pause();
        verify(locMan).detachObserver(wp);
    }

}
