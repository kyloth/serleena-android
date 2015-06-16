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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;

/**
 * Contiene test di unità per la classe LocationReachedManager.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class LocationReachedManagerTest {

    AlarmManager alMan;
    WakeupManager wm;
    ILocationManager locMan;
    ILocationReachedObserver observer;
    Context context = RuntimeEnvironment.application;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        alMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        wm = new WakeupManager(context, alMan);
        locMan = mock(ILocationManager.class);
        observer = mock(ILocationReachedObserver.class);
    }

    /**
     * Verifica che il metodo attachObserver lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null observer"
     * se invocato con observer nullo.
     */

    @Test
    public void attachObserverExceptionWhenNullObserver() {
        LocationReachedManager lrm = new LocationReachedManager(wm, locMan);
        GeoPoint location = new GeoPoint(12, 12);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null observer");
        lrm.attachObserver(null, location);
    }

    /**
     * Verifica che il metodo attachObserver lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null location"
     * se invocato con ILocationManager nullo.
     */

    @Test
    public void attachObserverExceptionWhenNullLocation() {
        LocationReachedManager lrm = new LocationReachedManager(wm, locMan);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        lrm.attachObserver(observer, null);
    }

    /**
     * Verifica che il metodo detachObserver lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null observer"
     * se invocato con observer nullo.
     */

    @Test
    public void detachObserverExceptionWhenNullObserver() {
        LocationReachedManager lrm = new LocationReachedManager(wm, locMan);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null observer");
        lrm.detachObserver(null);
    }

    /**
     * Verifica che il metodo attachObserver non sollevi eccezioni e in
     * generale non causi errori.
     */

    @Test
    public void testAttachObserverHit() {
        LocationReachedManager lrm = new LocationReachedManager(wm, locMan);
        GeoPoint location = new GeoPoint(12, 12);
        lrm.attachObserver(observer, location);
    }

    /**
     * Verifica che il metodo detachObserver non sollevi eccezioni e in
     * generale non causi errori.
     */

    @Test
    public void testDetachObserverHit() {
        LocationReachedManager lrm = new LocationReachedManager(wm, locMan);
        GeoPoint location = new GeoPoint(12, 12);
        lrm.attachObserver(observer, location);
        lrm.detachObserver(observer);
    }
}

