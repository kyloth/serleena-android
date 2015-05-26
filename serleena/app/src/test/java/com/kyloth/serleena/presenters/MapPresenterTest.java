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
 * Name: MapPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 * Date: 2015-05-11
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

import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;

/**
 * Contiene i test di unità per la classe MapPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class MapPresenterTest {
    int UPDATE_INTERVAL_SECONDS = 30;
    IMapView view;
    ISerleenaActivity activity;
    ISensorManager sensMan;
    ILocationManager locMan;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        view = mock(IMapView.class);
        activity = mock(ISerleenaActivity.class);
        sensMan = mock(ISensorManager.class);
        locMan = mock(ILocationManager.class);
        when(activity.getSensorManager()).thenReturn(sensMan);
        when(sensMan.getLocationSource()).thenReturn(locMan);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null view"
     * se invocato con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        MapPresenter mp = new MapPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null activity"
     * se invocato con activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        MapPresenter mp = new MapPresenter(view, null);
    }

    /**
     * Verifica che il metodo newUserPoint lanci un'eccezione
     * di tipo NoActiveExperienceException se al momento
     * dell'invocazione non è attiva un'Esperienza.
     */

    @Test
    public void newUserPointShouldThrowExceptionWhenNoActiveExperience()
    throws NoActiveExperienceException {
        MapPresenter mp = new MapPresenter(view, activity);
        exception.expect(NoActiveExperienceException.class);
        mp.newUserPoint();
    }

    /**
     * Verifica che il metodo resume fornisca correttamente i parametri
     * al metodo attachObserver chiamato sul LocationManager.
     */

    @Test
    public void resumeShouldForwardCorrectParams() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.resume();
        verify(locMan).attachObserver(mp, UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che il metodo pause fornisca correttamente il parametro
     * al metodo detachObserver chiamato sul LocationManager.
     */

    @Test
    public void pauseShouldForwardCorrectParam() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.pause();
        verify(locMan).detachObserver(mp);
    }

    /**
     * Verifica che il metodo onLocationUpdate lanci un'eccezione
     * di tipo IllegalArgumentException con messaggio "Illegal null
     * GeoPoint" quando chiamato con GeoPoint nullo.
     */

    @Test
    public void onLocationUpdateShouldThrowExceptionWhenNullGeoPoint() {
        MapPresenter mp = new MapPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null GeoPoint");
        mp.onLocationUpdate(null);
    }
}
