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
 * Name: SerleenaLocationManagerTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Tobia Tesan      Creazione iniziale file
 * 1.1.0    Filippo Sestini  Aggiunta di test case e aumento copertura
 */

package com.kyloth.serleena.sensors;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import com.kyloth.serleena.common.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLocationManager;

import static android.location.LocationManager.*;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * Testa le funzionalita' di base di SerleenaLocationManager
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.1.0
 */
@RunWith(RobolectricTestRunner.class)
public class SerleenaLocationManagerTest {
    ILocationObserver obs;
    ILocationObserver otherObs;
    ILocationObserver obs3;
    LocationManager locationManager;
    ShadowLocationManager shadowLocationManager;
    SerleenaLocationManager manager;
    GeoPoint point;
    GeoPoint otherPoint;

    Location getLocation(String provider, double latitude, double longitude) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }

    @Before
    public void initialize() {
        obs = mock(ILocationObserver.class);
        otherObs = mock(ILocationObserver.class);
        obs3 = mock(ILocationObserver.class);
        locationManager = mock(LocationManager.class);
        manager = new SerleenaLocationManager(locationManager);
        point = new GeoPoint(12.0, 34.0);
        otherPoint = new GeoPoint(56.0, 78.0);
    }


    /**
     * Verifica che quando SerleenaLocationManager riceve onLocationChanged
     * aggiorni correttamente gli osservatori.
     */
    @Test
    public void testObserverGetsUpdated() {
        ArgumentCaptor<SerleenaLocationManager.SerleenaLocationListener> captor =
                ArgumentCaptor.forClass(SerleenaLocationManager.SerleenaLocationListener.class);
        ArgumentCaptor<GeoPoint> geoCaptor = ArgumentCaptor.forClass(GeoPoint
                .class);

        manager.attachObserver(obs, 40);
        verify(locationManager).requestLocationUpdates(
                eq(LocationManager.GPS_PROVIDER),
                eq(40L),
                any(Float.class),
                any(SerleenaLocationManager.SerleenaLocationListener.class));
    }

    /**
     * Verifica che attachObserver() sollevi un'eccezione quando gli viene
     * passato un parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachObserverShouldThrowWhenNullArgument() {
        manager.attachObserver(null, 1000);
    }

    /**
     * Verifica che attachObserver() sollevi un'eccezione quando gli viene
     * passato un parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void detachObserverShouldThrowWhenNullArgument() {
        manager.detachObserver(null);
    }

    /**
     * Verifica che attachObserver() sollevi un'eccezione quando gli viene
     * passato un intervallo < 0.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachObserverShouldThrowWhenNegativeInterval() {
        manager.attachObserver(obs, -1234);
    }

    /**
     * Verifica che attachObserver() sollevi un'eccezione quando gli viene
     * passato un intervallo nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachObserverShouldThrowWithNullInterval() {
        manager.attachObserver(obs, 0);
    }

    /**
     * Verifica che detachObserver() inoltri la cancellazione
     * dell'aggiornamento al gestore di posizione di Android.
     */
    @Test
    public void detachObserverShouldRemoveFromLocationManager() {
        manager.attachObserver(obs, 100);
        manager.detachObserver(obs);
        verify(locationManager).removeUpdates(any(LocationListener.class));
    }

    /**
     * Verifica che una chiamata a singleUpdate() inoltri la richiesta la
     * LocationManager di Android.
     */
    @Test
    public void singleUpdateShouldForwardRequestToAndroidManager() {
        manager.getSingleUpdate(obs);
        verify(locationManager).requestSingleUpdate(
                eq(LocationManager.GPS_PROVIDER),
                any(LocationListener.class),
                Matchers.<Looper>isNull(Looper.class));
    }

    /**
     * Verifica che getSingleUpdate() notifichi correttamente l'observer
     * registrato all'arrivo di dati aggiornati sulla posizione.
     */
    @Test
    public void singleUpdateShouldNotifyObserverCorrectly() {
        manager.getSingleUpdate(obs);
        String provider = android.location.LocationManager.GPS_PROVIDER;
        ArgumentCaptor<LocationListener> captor =
                ArgumentCaptor.forClass(LocationListener.class);
        verify(locationManager).requestSingleUpdate(eq(provider),
                captor.capture(), Matchers.<Looper>isNull(Looper
                        .class));

        LocationListener listener = captor.getValue();
        ArgumentCaptor<GeoPoint> pointCaptor = ArgumentCaptor.forClass
                (GeoPoint.class);
        Location l = mock(Location.class);
        when(l.getLatitude()).thenReturn(30d);
        when(l.getLongitude()).thenReturn(20d);
        listener.onLocationChanged(l);
        verify(obs).onLocationUpdate(pointCaptor.capture());
        assertTrue(pointCaptor.getValue().latitude() == 30 &&
                pointCaptor.getValue().longitude() == 20);
    }

    /**
     * Verifica che getSingleUpdate() sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getSingleUpdateShouldThrowWhenNullObserver() {
        manager.getSingleUpdate(null);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullArgument() {
        new SerleenaLocationManager(null);
    }

    /**
     * Verifica che gli oggetti SerleenaLocationListener segnalino correttamente
     * l'observer.
     */
    @Test
    public void locationListenerShouldNotifyObserver() {
        SerleenaLocationManager.SerleenaLocationListener listener =
                new SerleenaLocationManager.SerleenaLocationListener(obs);
        Location l = getLocation(LocationManager.GPS_PROVIDER, 22, 33);
        listener.onLocationChanged(l);
        verify(obs).onLocationUpdate(eq(new GeoPoint(22, 33)));
    }

}
