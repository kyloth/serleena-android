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
 * Name: LocationServiceTest.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowPowerManager;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test di unità per la classe LocationService
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class LocationServiceTest {

    private ShadowLocationManager slm;
    private LocationService service;

    @Before
    public void initialize() {
        slm = Shadows.shadowOf(
            (LocationManager) RuntimeEnvironment.application.getSystemService(
                Context.LOCATION_SERVICE));
        service = Robolectric.buildService(
                LocationService.class).attach().create().get();
    }

    /**
     * Verifica che il servizio si registri agli aggiornamento sulla
     * posizione utente alla sua creazione.
     */
    @Test
    public void serviceShouldRegisterItselfToLocationUpdatesUponCreation() {
        List<LocationListener> listeners =
                slm.getRequestLocationUpdateListeners();
        assertEquals(listeners.size(), 1);
        assertEquals(
                listeners.iterator().next().getClass(),
                LocationService.class);
    }

    /**
     * Verifica che il servizio crei un WakeLock alla sua creazione.
     */
    @Test
    public void serviceShouldCreateAWakelockUponCreation() {
        assertNotNull(ShadowPowerManager.getLatestWakeLock());
    }

    /**
     * Verifica che tutti i ServiceResultReceiver registrati al servizio
     * tramite Intent ricevano correttamente gli aggiornamenti sulla
     * posizione utente appena disponibili.
     */
    @Test
    public void resultReceiverShouldReceiveLocationUpdate() {
        ServiceResultReceiver receiver1 = mock(ServiceResultReceiver.class);
        ServiceResultReceiver receiver2 = mock(ServiceResultReceiver.class);
        ServiceResultReceiver receiver3 = mock(ServiceResultReceiver.class);

        Intent intent;
        intent = new Intent();
        intent.putExtra("receiverTag", receiver1);
        service.onStartCommand(intent, 0, 0);

        intent = new Intent();
        intent.putExtra("receiverTag", receiver2);
        service.onStartCommand(intent, 0, 1);

        intent = new Intent();
        intent.putExtra("receiverTag", receiver3);
        service.onStartCommand(intent, 0, 2);

        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(5);
        l.setLongitude(6);
        slm.simulateLocation(l);

        Predicate<ServiceResultReceiver> predicate =
                new Predicate<ServiceResultReceiver>() {
            @Override
            public boolean apply(ServiceResultReceiver receiver) {
                ArgumentCaptor<Bundle> captor =
                        ArgumentCaptor.forClass(Bundle.class);
                verify(receiver).send(eq(0), captor.capture());
                Bundle bundle = captor.getValue();
                return bundle.getDouble("latitude") == 5.0 &&
                        bundle.getDouble("longitude") == 6.0;
            }
        };

        assertTrue(predicate.apply(receiver1));
        assertTrue(predicate.apply(receiver2));
        assertTrue(predicate.apply(receiver3));
    }

    /**
     * Verifica che il servizio cancelli la registrazione agli aggiornamenti
     * sulla posizione utente durante la sua terminazione.
     */
    @Test
    public void serviceShouldUnregisterItselfUpotDestruction() {
        service.onDestroy();
        List<LocationListener> listeners =
                slm.getRequestLocationUpdateListeners();
        assertEquals(listeners.size(), 0);
    }

    /**
     * Verifica che il servizio cancelli la registrazione agli aggiornamenti
     * sulla posizione utente una volta ricevuto l'aggiornamento.
     */
    @Test
    public void serviceShouldUnregisterItselfWhenLocationUpdateReceived() {
        service.onLocationChanged(mock(Location.class));
        List<LocationListener> listeners =
                slm.getRequestLocationUpdateListeners();
        assertEquals(listeners.size(), 0);
    }

    /**
     * Verifica che il servizio notifichi la posizione utente ai receiver una
     * sola volta, indipendentemente da quante chiamate a onLocationChanged()
     * vengono fatte.
     */
    @Test
    public void serviceShouldNotifyOnlyOnce() {
        ServiceResultReceiver receiver = mock(ServiceResultReceiver.class);

        Intent intent = new Intent();
        intent.putExtra("receiverTag", receiver);
        service.onStartCommand(intent, 0, 0);

        service.onLocationChanged(mock(Location.class));
        verify(receiver, times(1))
                .send(any(Integer.class), any(Bundle.class));
        service.onLocationChanged(mock(Location.class));
        verify(receiver, times(1))
                .send(any(Integer.class), any(Bundle.class));
        service.onLocationChanged(mock(Location.class));
        verify(receiver, times(1))
                .send(any(Integer.class), any(Bundle.class));
    }

}
