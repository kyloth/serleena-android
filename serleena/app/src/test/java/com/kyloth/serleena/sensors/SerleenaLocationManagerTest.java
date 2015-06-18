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
 */

package com.kyloth.serleena.sensors;

import android.app.Application;
import android.location.Location;
import android.location.LocationManager;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.sensors.ILocationObserver;
import com.kyloth.serleena.sensors.SerleenaLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import static android.location.LocationManager.*;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Testa le funzionalita' di base di SerleenaLocationManager
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaLocationManagerTest {
    StubbyLocationObserver obs;
    StubbyLocationObserver otherObs;
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

    class StubbyLocationObserver implements ILocationObserver {
        GeoPoint latestLocation;

        StubbyLocationObserver() {
            latestLocation = null;
        }

        @Override
        public void onLocationUpdate(GeoPoint loc) {
            latestLocation = loc;
        }

        public GeoPoint getLatestLocation() {
            return latestLocation;
        }
    };

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
        assertTrue(obs.getLatestLocation().equals(point));
        assertTrue(otherObs.getLatestLocation().equals(point));
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
        assertTrue(obs.getLatestLocation().equals(point));
        assertTrue(otherObs.getLatestLocation().equals(point));
        instance.detachObserver(obs);
        instance.onLocationChanged(otherLocation);
        assertTrue(obs.getLatestLocation().equals(point));
        assertTrue(otherObs.getLatestLocation().equals(otherPoint));
    }

    @Before
    public void setUp() {
        obs = new StubbyLocationObserver();
        otherObs = new StubbyLocationObserver();
        locationManager = (LocationManager)
                          RuntimeEnvironment.application.getSystemService(RuntimeEnvironment.application.LOCATION_SERVICE);
        shadowLocationManager = shadowOf(locationManager);
        instance = new SerleenaLocationManager(locationManager);
        point = new GeoPoint(12.0, 34.0);
        otherPoint = new GeoPoint(56.0, 78.0);
    }

}
