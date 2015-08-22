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

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;

/**
 * Contiene i test di unità per la classe Telemetry.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TelemetryTest {

    private Telemetry telemetry;
    private ITelemetryStorage telemetryStorage;
    private Telemetry faultyTelemetry;
    private long testTimestamp;

    private List<TelemetryEvent> getListFromIterable(Iterable<TelemetryEvent>
                                                             iterable) {
        List<TelemetryEvent> list = new ArrayList<>();
        for (TelemetryEvent e : iterable)
            list.add(e);
        return list;
    }

    @Before
    public void initialize() {
        CheckpointReachedTelemetryEvent event1 =
                mock(CheckpointReachedTelemetryEvent.class);
        CheckpointReachedTelemetryEvent event2 =
                mock(CheckpointReachedTelemetryEvent.class);
        CheckpointReachedTelemetryEvent event3 =
                mock(CheckpointReachedTelemetryEvent.class);

        when(event1.checkpointNumber()).thenReturn(1);
        when(event2.checkpointNumber()).thenReturn(2);
        when(event3.checkpointNumber()).thenReturn(3);

        testTimestamp = 100;

        when(event1.timestamp()).thenReturn(testTimestamp);
        when(event2.timestamp()).thenReturn(200L);
        when(event3.timestamp()).thenReturn(300L);

        ArrayList<TelemetryEvent> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        events.add(event3);

        ArrayList<TelemetryEvent> faultyEvents = new ArrayList<>();
        faultyEvents.add(event2);
        faultyEvents.add(event3);

        telemetryStorage = mock(ITelemetryStorage.class);
        when(telemetryStorage.getEvents()).thenReturn(events);

        ITelemetryStorage faultyTelemetryStorage = mock(ITelemetryStorage.class);
        when(faultyTelemetryStorage.getEvents()).thenReturn(faultyEvents);

        telemetry = new Telemetry(telemetryStorage);
        faultyTelemetry = new Telemetry(faultyTelemetryStorage);
    }

    /**
     * Verifica che il metodo getEvents chiamato senza parametri restituisca
     * correttamente la lista di eventi ottenuta da storage.
     */
    @Test
    public void getEventsShouldCorrectlyHandleStorageEvents() {
        TelemetryEvent te1 = mock(TelemetryEvent.class);
        TelemetryEvent te2 = mock(TelemetryEvent.class);
        ITelemetryStorage ts = mock(ITelemetryStorage.class);
        List<TelemetryEvent> list = new ArrayList<>();
        list.add(te1);
        list.add(te2);
        when(ts.getEvents()).thenReturn(list);

        Telemetry telemetry = new Telemetry(ts);
        assertTrue(telemetry.getEvents() == ts.getEvents());
    }

    /**
     * Verifica che getEvents() restituisca il risultato corretto.
     */
    @Test
    public void getEventsWithPredicateShouldWork()
            throws NoSuchTelemetryEventException {
        final TelemetryEvent te1 = mock(TelemetryEvent.class);
        final TelemetryEvent te2 = mock(TelemetryEvent.class);
        final TelemetryEvent te3 = mock(TelemetryEvent.class);
        ITelemetryStorage ts = mock(ITelemetryStorage.class);
        List<TelemetryEvent> list = new ArrayList<>();
        list.add(te1);
        list.add(te2);
        list.add(te3);
        when(ts.getEvents()).thenReturn(list);

        Predicate<TelemetryEvent> predicate = new Predicate<TelemetryEvent>() {
            @Override
            public boolean apply(TelemetryEvent telemetryEvent) {
                return (telemetryEvent == te2) || (telemetryEvent == te3);
            }
        };

        Telemetry telemetry = new Telemetry(ts);
        List<TelemetryEvent> events = getListFromIterable(
                telemetry.getEvents(predicate));
        assertTrue(events.contains(te2));
        assertTrue(events.contains(te3));
        assertTrue(events.size() == 2);
    }

    /**
     * Verifica che getEvents() sollevi NoSuchTelemetryException quando non
     * vi è alcun evento che soddisfa il predicato specificato.
     */
    @Test(expected = NoSuchTelemetryEventException.class)
    public void getEventsWithPredicateWhenNotFoundShouldThrow()
            throws NoSuchTelemetryEventException {
        final TelemetryEvent te1 = mock(TelemetryEvent.class);
        final TelemetryEvent te2 = mock(TelemetryEvent.class);
        final TelemetryEvent te3 = mock(TelemetryEvent.class);
        ITelemetryStorage ts = mock(ITelemetryStorage.class);
        List<TelemetryEvent> list = new ArrayList<>();
        list.add(te1);
        list.add(te2);
        list.add(te3);
        when(ts.getEvents()).thenReturn(list);

        Predicate<TelemetryEvent> predicate = new Predicate<TelemetryEvent>() {
            @Override
            public boolean apply(TelemetryEvent telemetryEvent) {
                return false;
            }
        };

        new Telemetry(ts).getEvents(predicate);
    }

    /**
     * Verifica che il metodo getDuration restituisca il delta dell'ultimo
     * evento relativi al tracciamento.
     */
    @Test
    public void getDurationShouldReturnLatestTimestamp() {
        CheckpointReachedTelemetryEvent te1 =
                new CheckpointReachedTelemetryEvent(20L, 1);
        CheckpointReachedTelemetryEvent te2 =
                new CheckpointReachedTelemetryEvent(25L, 2);
        CheckpointReachedTelemetryEvent te3 =
                new CheckpointReachedTelemetryEvent(30L, 3);
        ITelemetryStorage ts = mock(ITelemetryStorage.class);
        List<TelemetryEvent> list = new ArrayList<>();
        list.add(te1);
        list.add(te2);
        list.add(te3);
        when(ts.getEvents()).thenReturn(list);
        Telemetry telemetry = new Telemetry(ts);
        assertEquals(telemetry.startTimestamp(), 20L);
        assertEquals(new Telemetry(ts).getDuration(), 10L);
    }

    /**
     * Verifica che il timestamp di inizio del Tracciamento corrisponda al
     * timestamp dell'evento campionato all'attraversamento del primo
     * checkpoint del Percorso.
     */
    @Test
    public void firstTimestampShouldBeTimestampOfFirstCheckpoint()
            throws NoSuchTelemetryEventException {
        CheckpointReachedTelemetryEvent firstCheckpointEvent =
                (CheckpointReachedTelemetryEvent) telemetry.getEvents(
                        new Predicate<TelemetryEvent>() {
                    @Override
                    public boolean apply(TelemetryEvent telemetryEvent) {
                        if (telemetryEvent instanceof
                                CheckpointReachedTelemetryEvent) {
                            CheckpointReachedTelemetryEvent event =
                                    (CheckpointReachedTelemetryEvent)
                                            telemetryEvent;
                            return event.checkpointNumber() == 1;
                        }
                        return false;
                    }
                }).iterator().next();
        assertEquals(testTimestamp, firstCheckpointEvent.timestamp());
    }

    /**
     * Verifica che il tentativo di ottenere il timestamp di inizio di un
     * Tracciamento privo di evento relativo al primo checkpoint del Percorso
     * sollevi un'eccezione a runtime irrecuperabile.
     */
    @Test(expected = RuntimeException.class)
    public void telemetryWithoutCheckpointEventsShouldThrow() {
        faultyTelemetry.startTimestamp();
    }

}

