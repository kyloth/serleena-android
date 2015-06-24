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
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

import org.robolectric.RobolectricTestRunner;

import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;


/**
 * Contiene test per la classe MapPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricTestRunner.class)
public class MapPresenterTest {

    MapPresenter mp;
    IMapView view;
    ISerleenaActivity activity;
    ISensorManager sm;
    ILocationManager locMan;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        locMan = mock(ILocationManager.class);
        sm = mock(ISensorManager.class);
        when(sm.getLocationSource()).thenReturn(locMan);

        view = mock(IMapView.class);
        activity = mock(ISerleenaActivity.class);
        when(activity.getSensorManager()).thenReturn(sm);
        when(activity.getDataSource()).thenReturn(mock(ISerleenaDataSource.class));

        mp = new MapPresenter(view, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione se invocato con
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArgument1() {
        new MapPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione se invocato con
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArgument2() {
        new MapPresenter(view, null);
    }

    /**
     * Verifica che il metodo newUserPoint lanci un'eccezione se non è attiva
     * un'Esperienza.
     */
    @Test(expected = NoActiveExperienceException.class)
    public void newUserPointShouldThrowExceptionWhenNoActiveExperience()
            throws NoActiveExperienceException, LocationNotAvailableException {
        new MapPresenter(view, activity).newUserPoint();
    }

    /**
     * Verifica che resume() causi la registrazione del presenter al sensore
     * di posizione.
     */
    @Test
    public void resumeShouldRegisterSensor() {
        mp.resume();
        verify(locMan).attachObserver(eq(mp), any(Integer.class));
    }

    /**
     * Verifica che pause() causi la deregistrazione del presenter dal sensore
     * di posizione.
     */
    @Test
    public void pauseShouldUnregisterSensor() {
        mp.pause();
        verify(locMan).detachObserver(mp);
    }

    /**
     * Verifica che setActiveExperience() sollevi un'eccezione al passaggio
     * di un'Esperienza null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setActiveExperienceShouldThrowWhenNullExperience() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.setActiveExperience(null);
    }

    /**
     * Verifica che setActiveExperience() pulisca la vista.
     */
    @Test
    public void settingActiveExperienceShouldClearView() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.setActiveExperience(mock(IExperience.class));
        verify(view).clear();
    }

    /**
     * Verifica che onLocationUpdate() sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void onLocationUpdateShouldThrowWhenNullLocation() {
        mp.onLocationUpdate(null);
    }

    @Test
    public void onLocationUpdateShouldUpdateView() {
        GeoPoint gp = mock(GeoPoint.class);
        mp.onLocationUpdate(gp);
        verify(view).setUserLocation(gp);
    }

    @Test(expected = NoActiveExperienceException.class)
    public void newUserPointShouldThrowWhenNoActiveExperience()
            throws NoActiveExperienceException, LocationNotAvailableException {
        mp.newUserPoint();
    }

}
