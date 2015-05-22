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
 * Name: LocationReachedManagerTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.app.AlarmManager;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UnregisteredObserverException;

/**
 * Contiene i test di unità per la classe LocationReachedManager.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class LocationReachedManagerTest {
    LocationReachedManager lrm;
    Context context;
    ILocationReachedObserver observer;
    GeoPoint location;
    AlarmManager alarmManager;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        context = mock(Context.class);
        observer = mock(ILocationReachedObserver.class);
        location = mock(GeoPoint.class);
        alarmManager = mock(AlarmManager.class);
        when(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager);
        lrm = LocationReachedManager.getInstance(context);
    }

    /**
     * Verifica che il metodo getInstance applichi correttamente il pattern
     * Singleton ritornando ogni volta la stessa istanza dell'oggetto
     * LocationReachedManager.
     */

    @Test
    public void getIstanceShouldReturnSameInstance() {
        LocationReachedManager lrm_1 = LocationReachedManager.getInstance(context);
        LocationReachedManager lrm_2 = LocationReachedManager.getInstance(context);
        assertTrue(lrm_1 == lrm_2);
    }

    /**
     * Verifica che il metodo attachObserver sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null observer" quando
     * invocato con un ILocationReachedObserver nullo.
     */

    @Test
    public void attachObserverShouldThrowExceptionWhenNullObserver() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null observer");
        lrm.attachObserver(null, location);
    }

    /**
     * Verifica che il metodo attachObserver sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null location" quando
     * invocato con un GeoPoint nullo.
     */

    @Test
    public void attachObserverShouldThrowExceptionWhenNullLocation() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        lrm.attachObserver(observer, null);
    }

    /**
     * Verifica che il metodo detachObserver sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null observer" quando
     * invocato con un ILocationReachedObserver nullo.
     */

    @Test
    public void detachObserverShouldThrowExceptionWhenNullObserver() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null observer");
        lrm.detachObserver(null);
    }

    /**
     * Verifica che il metodo detachObserver sollevi un'eccezione di tipo
     * UnregisteredObserverException qualora il ILocationReachedObserver
     * usato come parametro non fosse registrato tra gli observer dell'oggetto
     * di invocazione.
     */

    @Test
    public void detachObserverShouldThrowExceptionWhenUnregisteredObserver() {
        lrm.attachObserver(observer, location);
        ILocationReachedObserver new_observer = mock(ILocationReachedObserver.class);
        exception.expect(UnregisteredObserverException.class);
        lrm.detachObserver(new_observer);
    }
}
