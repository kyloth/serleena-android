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
 * Name: TelemetryTest.java
 * Package: com.kyloth.serleena.model;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;

/**
 * Contiene i test di unità per la classe Telemetry.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TelemetryTest {

    ITelemetryStorage storage;
    LocationTelemetryEvent te1;
    TelemetryEvent te2;
    LocationTelemetryEvent te3;
    GeoPoint gp1;
    GeoPoint gp2;
    GeoPoint test_point1;
    GeoPoint test_point2;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        storage = mock(ITelemetryStorage.class);
        gp1 = mock(GeoPoint.class);
        gp2 = mock(GeoPoint.class);
        te1 = mock(LocationTelemetryEvent.class);
        when(te1.timestamp()).thenReturn(10);
        when(te1.getType()).thenReturn(TelemetryEventType.Location);
        when(te1.location()).thenReturn(gp1);
        te2 = mock(TelemetryEvent.class);
        when(te2.timestamp()).thenReturn(20);
        when(te2.getType()).thenReturn(TelemetryEventType.HeartRate);
        te3 = mock(LocationTelemetryEvent.class);
        when(te3.timestamp()).thenReturn(30);
        when(te3.location()).thenReturn(gp2);
        when(te3.getType()).thenReturn(TelemetryEventType.Location);
        when(storage.getEvents()).thenReturn(Arrays.asList(new TelemetryEvent[]
                                             {te1, te2, te3}));
        when(gp1.distanceTo(test_point1)).thenReturn(new Float(10));
        when(gp1.distanceTo(test_point2)).thenReturn(new Float(20));
        when(gp2.distanceTo(test_point1)).thenReturn(new Float(5));
        when(gp2.distanceTo(test_point2)).thenReturn(new Float(25));
    }

    /**
     * Verifica che il metodo getEvents chiamato senza parametri restituisca
     * correttamente la lista di eventi ottenuta da storage.
     */
    @Test
    public void getEventsShouldCorrectlyHandleStorageEvents() {
        Telemetry telemetry = new Telemetry(storage);
        Iterable<TelemetryEvent> events = telemetry.getEvents();
        Iterator<TelemetryEvent> i_events = events.iterator();
        assertTrue(i_events.next() == te1);
        assertTrue(i_events.next() == te2);
        assertTrue(i_events.next() == te3);
    }

    /**
     * Verifica che il metodo getEvents restituisca solo gli eventi i
     * cui timestamp siano compresi tra i parametri forniti.
     */
    @Test
    public void getEventsShouldReturnEventsBetweenParams()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        Iterable<TelemetryEvent> events = telemetry.getEvents(15, 25);
        Iterator<TelemetryEvent> i_events = events.iterator();
        assertTrue(i_events.next() == te2);
        assertFalse(i_events.hasNext());
    }

    /**
     * Verifica che il metodo getEvents lanci un'eccezione di tipo
     * NoSuchTelemetryEventException quando non fossero presenti
     * eventi il cui timestamp sia compreso tra i parametri forniti.
     */
    @Test
    public void getEventsShouldThrowExceptionWhenNoEventsInBoundaries()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(NoSuchTelemetryEventException.class);
        Iterable<TelemetryEvent> events = telemetry.getEvents(35, 40);
    }

    /**
     * Verifica che il metodo getEvents restituisca tutti gli eventi
     * del tipo TelemetryEventType specificato come parametro.
     */
    @Test
    public void getEventsShouldReturnCorrectTypeEvents()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        Iterable<TelemetryEvent> events = telemetry.getEvents(TelemetryEventType.Location);
        Iterator<TelemetryEvent> i_events = events.iterator();
        assertTrue(i_events.next() == te1);
        assertTrue(i_events.next() == te3);
        assertFalse(i_events.hasNext());
    }

    /**
     * Verifica che il metodo getEvents lanci un'eccezione di tipo
     * NoSuchTelemetryEventException qualora non fossero presenti
     * eventi del tipo specificato come parametro.
     */
    @Test
    public void getEventsShouldThrowExceptionWhenNoEventsOfType()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(NoSuchTelemetryEventException.class);
        Iterable<TelemetryEvent> events = telemetry.getEvents(TelemetryEventType.CheckpointReached);
    }

    /**
     * Verifica che il metodo getEventAtLocation lanci un'eccezione
     * di tipo IllegalArgumentException con messaggio "Illegal null
     * location" se invocato con GeoPoint nullo.
     */
    public void getEventAtLocationShouldThrowExceptionWhenNullGeoPoint()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        LocationTelemetryEvent event = telemetry.getEventAtLocation(null, 10);
    }

    /**
     * Verifica che il metodo getEventAtLocation restituisca l'evento
     * la cui distanza sia la più breve e minore della tolleranza specificata
     * come parametro.
     */
    public void getEventAtLocationShouldReturnNearestEvent()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        LocationTelemetryEvent lte = telemetry.getEventAtLocation(test_point1, 15);
        assertTrue(lte == te3);
    }

    /**
     * Verifica che il metodo getEventAtLocation lanci un'eccezione di
     * tipo NoSuchTelemetryEventException qualora non vi fossero eventi
     * la cui distanza sia minore della tolleranza fornita come parametro.
     */
    public void getEventAtLocationShouldThrowExceptionWhenTooFarEvents()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(NoSuchTelemetryEventException.class);
        LocationTelemetryEvent lte = telemetry.getEventAtLocation(test_point2, 15);
    }

    /**
     * Verifica che il metodo getDuration restituisca il timestamp
     * maggiore tra quelli degli eventi relativi al tracciamento.
     */
    public void getDurationShouldReturnGreatestTimestamp() {
        Telemetry telemetry = new Telemetry(storage);
        int duration = telemetry.getDuration();
        assertTrue(duration == 30);
    }

    /**
     * Verifica che il metodo getEvents sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal time below zero"
     * se invocato con almeno uno dei due parametri minore di zero.
     */
    public void getEventsShouldThrowExceptionWhenParamsBelowZero()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal time below zero");
        telemetry.getEvents(-1, 2);
        exception.expect(IllegalArgumentException.class);
        telemetry.getEvents(2, -1);
    }

    /**
     * Verifica che il metodo getEvents sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal time segment"
     * se invocato con parametro to minore del parametro from.
     */
    public void getEventsShouldThrowExceptionWhenIllegalSegment()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal time segment");
        telemetry.getEvents(5, 3);
    }

    /**
     * Verifica che il metodo getEventAtLocation sollevi un'eccezione
     * di tipo IllegalArgumentException con messaggio "Illegal tolerance
     * below zero" se invocato con tolleranza minore di zero.
     */
    public void getEventsAtLocationShouldThrowExceptionWhenNegativeTolerance()
    throws NoSuchTelemetryEventException {
        Telemetry telemetry = new Telemetry(storage);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal tolerance below zero");
        telemetry.getEventAtLocation(test_point1, -2);
    }
}
