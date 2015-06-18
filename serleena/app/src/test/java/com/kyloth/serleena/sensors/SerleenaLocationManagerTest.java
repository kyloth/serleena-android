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
import android.location.LocationManager;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import static android.location.LocationManager.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Testa le funzionalita' di base di SerleenaLocationManager
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaLocationManagerTest {
    ILocationObserver obs;
    ILocationObserver otherObs;
    ILocationObserver obs3;
    LocationManager locationManager;
    ShadowLocationManager shadowLocationManager;
    SerleenaLocationManager instance;
    GeoPoint point;
    GeoPoint otherPoint;

    Location getLocation(String provider, double latitude, double longitude) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }

    /**
     * Verifica che quando SerleenaLocationManager riceve onLocationChanged
     * aggiorni correttamente gli osservatori.
     */
    @Test
    public void testObserverGetsUpdated() {
        Location location = getLocation(GPS_PROVIDER, point.latitude(), point.longitude());
        instance.attachObserver(obs, 1000);
        instance.attachObserver(otherObs, 1000);
        instance.onLocationChanged(location);
        verify(obs).onLocationUpdate(point);
        verify(otherObs).onLocationUpdate(point);
    }

    /**
     * Verifica che quando attach viene chiamato con un observer == null
     * sollevi una IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNoNullObservers() {
        instance.attachObserver(null, 1000);
    }

    /**
     * Verifica che quando attach viene chiamato con intervallo == 0
     * sollevi una IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNoZeroInterval() {
        instance.attachObserver(obs, 0);
    }

    /**
     * Verifica che quando attach viene chiamato con intervallo < 0
     * sollevi una IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNoLTZeroInterval() {
        instance.attachObserver(obs, -1234);
    }

    /**
     * Verifica che quando detach viene chiamato con argomento null
     * sollevi una IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDetachNoNullObserver() {
        instance.detachObserver(null);
    }

    /**
     * Verifica che quando dopo il detach un osservatore non venga
     * ulteriormente aggiornato sulla posizione.
     */
    @Test
    public void testDetachedObserverGetsNoUpdates() {
        Location location = getLocation(GPS_PROVIDER, point.latitude(), point.longitude());
        Location otherLocation = getLocation(GPS_PROVIDER, otherPoint.latitude(), otherPoint.longitude());
        instance.attachObserver(obs, 1000);
        instance.attachObserver(otherObs, 1000);
        instance.onLocationChanged(location);
        verify(obs).onLocationUpdate(point);
        verify(otherObs).onLocationUpdate(point);
        instance.detachObserver(obs);
        instance.onLocationChanged(otherLocation);
        verify(obs).onLocationUpdate(point);
        verify(otherObs).onLocationUpdate(otherPoint);
    }

    /**
     * Verifica che una chiamata a singleUpdate() inoltri la richiesta la
     * LocationManager di Android.
     */
    @Test
    public void singleUpdateShouldForwardRequestToAndroidManager() {
        instance.getSingleUpdate(obs, 100);
        String provider = android.location.LocationManager.GPS_PROVIDER;
        verify(locationManager).requestSingleUpdate(eq(provider),
                any(LocationListener.class), Matchers.<Looper>isNull(Looper
                        .class));
    }

    /**
     * Verifica che getSingleUpdate() notifichi correttamente l'observer
     * registrato all'arrivo di dati aggiornati sulla posizione.
     */
    @Test
    public void singleUpdateShouldNotifyObserverCorrectly() {
        instance.getSingleUpdate(obs, 100);
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
     * Verifica che i dati sulla posizione non scaduti vengano utilizzati per
     * aggiornamenti singoli con getSingleUpdate().
     */
    @Test
    public void locationNotExpiredShouldBeUsedInSingleUpdates() {
        Location l = mock(Location.class);
        when(l.getLatitude()).thenReturn(30d);
        when(l.getLongitude()).thenReturn(20d);
        instance.onLocationChanged(l);
        instance.getSingleUpdate(obs, 100);
        ArgumentCaptor<GeoPoint> pointCaptor = ArgumentCaptor.forClass
                (GeoPoint.class);
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
        instance.getSingleUpdate(null, 100);
    }

    /**
     * Verifica che getSingleUpdate() sollevi un'eccezione al passaggio di
     * parametri fuori range.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getSingleUpdateShouldThrowWhenZeroTimeout() {
        instance.getSingleUpdate(obs, 0);
    }

    /**
     * Verifica che getSingleUpdate() sollevi un'eccezione al passaggio di
     * parametri fuori range.
     */
    @Test(expected = IllegalArgumentException.class)
    public void getSingleUpdateShouldThrowWhenTimeoutBelowZero() {
        instance.getSingleUpdate(obs, -10);
    }

    /**
     * Verifica che l'intervallo di aggiornamento sulla posizione richiesto
     * al LocationManager di Android venga aggiornato correttamente in base
     * agli intervalli dei diversi observer registrati.
     */
    @Test
    public void updateIntervalShouldBeAdjustedAccordingToObservers() {
        instance.attachObserver(obs, 50);
        verify(locationManager).requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 50000,
                SerleenaLocationManager.MINIMUM_UPDATE_DISTANCE, instance);
        instance.attachObserver(otherObs, 30);
        verify(locationManager).requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000,
                SerleenaLocationManager.MINIMUM_UPDATE_DISTANCE, instance);
        instance.attachObserver(obs3, 40);
        verify(locationManager, times(2)).requestLocationUpdates(any(String
                .class), any(Integer.class), any(Integer.class), any
                (SerleenaLocationManager.class));
        instance.detachObserver(otherObs);
        verify(locationManager).requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 40000,
                SerleenaLocationManager.MINIMUM_UPDATE_DISTANCE, instance);
    }

    /**
     * Verifica che notifyObserver() sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void notifyObserverShouldThrowWhenNullArgument() {
        instance.notifyObserver(null);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullArgument() {
        new SerleenaLocationManager(null);
    }

    @Before
    public void setUp() {
        obs = mock(ILocationObserver.class);
        otherObs = mock(ILocationObserver.class);
        obs3 = mock(ILocationObserver.class);
        locationManager = mock(LocationManager.class);
        shadowLocationManager = shadowOf(locationManager);
        instance = new SerleenaLocationManager(locationManager);
        point = new GeoPoint(12.0, 34.0);
        otherPoint = new GeoPoint(56.0, 78.0);
    }

}
