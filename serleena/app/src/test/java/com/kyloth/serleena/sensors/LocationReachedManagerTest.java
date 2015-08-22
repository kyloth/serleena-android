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
 * Name: LocationReachedManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Gabriele Pozzan   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;

import android.content.Context;
import android.app.AlarmManager;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;

/**
 * Test di unità per la classe LocationReachedManager.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class LocationReachedManagerTest {

    AlarmManager alMan;
    ILocationReachedObserver observer;
    LocationReachedManager manager;
    IBackgroundLocationManager bkgrLocMan;

    @Before
    public void initialize() {
        alMan = mock(AlarmManager.class);
        bkgrLocMan = mock(IBackgroundLocationManager.class);
        observer = mock(ILocationReachedObserver.class);
        manager = new LocationReachedManager(bkgrLocMan);
    }

    /**
     * Verifica che il metodo attachObserver lanci un'eccezione di tipo
     * IllegalArgumentException se invocato con observer null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachObserverExceptionWhenNullObserver() {
        manager.attachObserver(null, mock(GeoPoint.class));
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se gli vengono
     * passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullLocation() {
        new LocationReachedManager(null);
    }

    /**
     * Verifica che il metodo attachObserver lanci un'eccezione di tipo
     * IllegalArgumentException se invocato con observer null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void detachObserverExceptionWhenNullObserver() {
        manager.detachObserver(null);
    }

    /**
     * Verifica che la chiamata a detachObserver() non sollevi eccezioni al
     * passaggio di un observer non precedentemente registrato.
     */
    @Test
    public void detachObserverShouldNotThrownWhenObserverNotRegistered() {
        manager.detachObserver(mock(ILocationReachedObserver.class));
    }

    /**
     * Verifica che gli observer non vengano notificati se la posizione
     * obiettivo non è stata raggiunta.
     */
    @Test
    public void observerShouldNotBeNotifiedIfLocationNotReached() {
        manager.attachObserver(observer, new GeoPoint(40, 50));
        manager.onLocationUpdate(new GeoPoint(60, 70));
        verify(observer, never()).onLocationReached();
    }

    /**
     * Verifica che gli observer vengano correttamente notificati quando
     * l'utente raggiunge una posizione sufficientemente vicina alla
     * posizione obiettivo.
     */
    @Test
    public void observerShouldBeNotifiedCorrectly() {
        GeoPoint p1 = new GeoPoint(40.7485, -73.9864);
        double offset = 0.001;
        while (p1.distanceTo(new GeoPoint(p1.latitude() + offset,
                p1.longitude())) >= LocationReachedManager.LOCATION_RADIUS)
            offset = offset / 10;
        GeoPoint p2 = new GeoPoint(p1.latitude() + offset, p1.longitude());
        manager.attachObserver(observer, p1);
        manager.onLocationUpdate(p2);
        verify(observer).onLocationReached();
    }

    @Test
    public void shouldRegisterToBackgroundLocationManagercorrectly() {
        ILocationReachedObserver o1 = mock(ILocationReachedObserver.class);
        ILocationReachedObserver o2 = mock(ILocationReachedObserver.class);
        ILocationReachedObserver o3 = mock(ILocationReachedObserver.class);

        manager.attachObserver(o1, mock(GeoPoint.class));
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        manager.attachObserver(o2, mock(GeoPoint.class));
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        manager.attachObserver(o3, mock(GeoPoint.class));
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        manager.detachObserver(o3);
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        manager.detachObserver(o2);
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        manager.detachObserver(o1);
        verify(bkgrLocMan, times(1)).attachObserver(manager);
        verify(bkgrLocMan, times(1)).detachObserver(manager);
        manager.attachObserver(o1, mock(GeoPoint.class));
        verify(bkgrLocMan, times(2)).attachObserver(manager);
    }

    /**
     * Verifica che le richieste sulla posizione soddisfatte siano rimosse
     * prima di segnalare eventuali Observer.
     */
    @Test
    public void managerShouldNotifyObserverOnlyAfterProcessingRequests() {
        GeoPoint point = mock(GeoPoint.class);
        manager.attachObserver(new ILocationReachedObserver() {
            @Override
            public void onLocationReached() {
                verify(bkgrLocMan).detachObserver(manager);
            }
        }, point);
        manager.onLocationUpdate(point);
    }

}
