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
 * Name: BackgroundLocationManagerTest.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.kyloth.serleena.common.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Contiene i test di unità per la classe BackgroundLocationManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class BackgroundLocationManagerTest {

    Context context;
    Context contextMock;
    AlarmManager alarmManager;
    BackgroundLocationManager manager;
    BackgroundLocationManager managerWithContextMock;
    ILocationObserver o1;
    ILocationObserver o2;
    ILocationObserver o3;

    @Before
    public void initialize() {
        o1 = mock(ILocationObserver.class);
        o2 = mock(ILocationObserver.class);
        o3 = mock(ILocationObserver.class);
        contextMock = mock(Context.class);
        context = RuntimeEnvironment.application;
        alarmManager = mock(AlarmManager.class);
        manager = new BackgroundLocationManager(context, alarmManager);
        managerWithContextMock = new BackgroundLocationManager(contextMock,
                alarmManager);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullArgument1() {
        new BackgroundLocationManager(null, alarmManager);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullArgument2() {
        new BackgroundLocationManager(context, null);
    }

    /**
     * Verifica che la registrazione del primo observer comporti la
     * registrazione di un allarme.
     */
    @Test
    public void managerShouldRegisterToAlarmManagerOnFirstObserver() {
        manager.attachObserver(mock(ILocationObserver.class));
        verify(alarmManager, times(1)).setInexactRepeating(
                any(Integer.class),
                any(Long.class),
                eq(60000L),
                any(PendingIntent.class));
        manager.attachObserver(mock(ILocationObserver.class));
        verify(alarmManager, times(1)).setInexactRepeating(
                any(Integer.class),
                any(Long.class),
                eq(60000L),
                any(PendingIntent.class));
        manager.attachObserver(mock(ILocationObserver.class));
        verify(alarmManager, times(1)).setInexactRepeating(
                any(Integer.class),
                any(Long.class),
                eq(60000L),
                any(PendingIntent.class));
    }

    /**
     * Verifica che l'istanza registri se stessa come BroadcastReceiver
     * solamente se vi sono observer registrati.
     */
    @Test
    public void receiverShouldBeRegisteredOnlyWithObservers() {
        ILocationObserver o1 = mock(ILocationObserver.class);
        ILocationObserver o2 = mock(ILocationObserver.class);
        ILocationObserver o3 = mock(ILocationObserver.class);

        managerWithContextMock.attachObserver(o1);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        managerWithContextMock.attachObserver(o2);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        managerWithContextMock.attachObserver(o3);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        managerWithContextMock.detachObserver(o1);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        managerWithContextMock.detachObserver(o2);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        managerWithContextMock.detachObserver(o3);
        verify(contextMock, times(1)).registerReceiver(
                eq(managerWithContextMock), any(IntentFilter.class));
        verify(contextMock, times(1)).unregisterReceiver(managerWithContextMock);
        verify(contextMock).stopService(any(Intent.class));
    }

    /**
     * Verifica che attachObserver() sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachObserverShouldThrowWhenNullArgument() {
        manager.attachObserver(null);
    }

    /**
     * Verifica che detachObserver() sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void detachObserverShouldThrowWhenNullArgument() {
        manager.detachObserver(null);
    }

    /**
     * Verifica che il metodo notifyObservers() notifichi correttamente tutti
     * gli observer registrati all'oggetto.
     */
    @Test
    public void notifyObserversShouldNotifyAllObservers() {
        ILocationObserver o1 = mock(ILocationObserver.class);
        ILocationObserver o2 = mock(ILocationObserver.class);
        ILocationObserver o3 = mock(ILocationObserver.class);

        Bundle b = new Bundle();
        b.putDouble("latitude", 40d);
        b.putDouble("longitude", 50d);
        manager.onReceiveResult(0, b);

        manager.attachObserver(o1);
        manager.attachObserver(o2);
        manager.attachObserver(o3);

        manager.notifyObservers();
        verify(o1).onLocationUpdate(any(GeoPoint.class));
        verify(o2).onLocationUpdate(any(GeoPoint.class));
        verify(o3).onLocationUpdate(any(GeoPoint.class));
    }

    /**
     * Verifica che il metodo onReceiveResult() notifichi gli observer con i
     * dati sulla posizione comunicati al metodo stesso.
     */
    @Test
    public void onReceiveResultShouldNotifyObserversWithReceivedGeoPoint() {
        ILocationObserver o = mock(ILocationObserver.class);
        manager.attachObserver(o);

        Bundle b = new Bundle();
        b.putDouble("latitude", 40d);
        b.putDouble("longitude", 50d);
        manager.onReceiveResult(0, b);

        verify(o).onLocationUpdate(eq(new GeoPoint(40, 50)));
    }

    /**
     * Verifica che gli observer non vengano notificati se non è stato ancora
     * ricevuto alcun dato sulla posizione.
     */
    @Test
    public void notifyObserversShouldNotNotifyIfNoLocation() {
        ILocationObserver o1 = mock(ILocationObserver.class);
        ILocationObserver o2 = mock(ILocationObserver.class);
        ILocationObserver o3 = mock(ILocationObserver.class);

        manager.attachObserver(o1);
        manager.attachObserver(o2);
        manager.attachObserver(o3);

        manager.notifyObservers();
        verify(o1, never()).onLocationUpdate(any(GeoPoint.class));
        verify(o2, never()).onLocationUpdate(any(GeoPoint.class));
        verify(o3, never()).onLocationUpdate(any(GeoPoint.class));
    }

    /**
     * Verifica che la chiamata a onReceive, causata dalla ricezione di un
     * wakeup dall'AlarmManager, causi l'avvio del servizio LocationService.
     */
    @Test
    public void onReceiveShouldStartALocationService() {
        manager.onReceive(contextMock, mock(Intent.class));
        ArgumentCaptor<Intent> intentArgumentCaptor =
                ArgumentCaptor.forClass(Intent.class);
        verify(contextMock).startService(intentArgumentCaptor.capture());
        assertEquals(
                "com.kyloth.serleena.sensors.LocationService",
                intentArgumentCaptor.getValue().getComponent().getClassName());
    }

}
