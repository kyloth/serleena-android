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
 * Name: TrackTest.java
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

import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.LocationTelemetryEvent;

/**
 * Contiene i test di unità per la classe Track.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TrackTest {
    ITrackStorage track_storage;
    ITrackStorage track_storage_nt;
    ITelemetryStorage tel_storage1;
    ITelemetryStorage tel_storage2;
    ITelemetryStorage tel_storage_nt;
    LocationTelemetryEvent lte1;
    LocationTelemetryEvent lte2;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        track_storage = mock(ITrackStorage.class);
        track_storage_nt = mock(ITrackStorage.class);
        tel_storage1 = mock(ITelemetryStorage.class);
        tel_storage2 = mock(ITelemetryStorage.class);
        tel_storage_nt = mock(ITelemetryStorage.class);
        lte1 = mock(LocationTelemetryEvent.class);
        when(lte1.timestamp()).thenReturn(20);
        lte2 = mock(LocationTelemetryEvent.class);
        when(lte2.timestamp()).thenReturn(10);
        when(tel_storage1.getEvents()).thenReturn(Arrays.asList(new TelemetryEvent[]
                {lte1}));
        when(tel_storage2.getEvents()).thenReturn(Arrays.asList(new TelemetryEvent[]
                {lte2}));
        when(tel_storage_nt.getEvents()).thenReturn(Arrays.asList(new TelemetryEvent[] {}));
        when(track_storage.getTelemetries()).thenReturn(Arrays.asList(new ITelemetryStorage[]
                {tel_storage1, tel_storage2}));
        when(track_storage_nt.getTelemetries()).thenReturn(Arrays.asList(new ITelemetryStorage[]
                {}));
    }

    /**
     * Verifica che il metodo getTelemetries restituisca correttamente
     * la lista dei tracciamenti relativi al percorso.
     */
    @Test
    public void getTelemetriesShouldReturnCorrectValues() {
        Track track = new Track(track_storage);
        Iterable<ITelemetry> telemetries = track.getTelemetries();
        Iterator<ITelemetry> i_telemetries = telemetries.iterator();
        Iterable<TelemetryEvent> events_1 = ((Telemetry) i_telemetries.next()).getEvents();
        Iterator<TelemetryEvent> i_events1 = events_1.iterator();
        Iterable<TelemetryEvent> events_2 = ((Telemetry) i_telemetries.next()).getEvents();
        Iterator<TelemetryEvent> i_events2 = events_2.iterator();
        assertTrue(i_events1.next().timestamp() == 20);
        assertTrue(i_events2.next().timestamp() == 10);
    }

    /**
     * Verifica che il metodo getBestTelemetry restituisca il tracciamento
     * con durata migliore per il percorso corrente.
     */
    @Test
    public void getBestTelemetryShouldReturnCorrectValue()
    throws NoSuchTelemetryException {
        Track track = new Track(track_storage);
        ITelemetry best_telemetry = track.getBestTelemetry();
        Iterable<TelemetryEvent> events = ((Telemetry) best_telemetry).getEvents();
        Iterator<TelemetryEvent> i_events = events.iterator();
        assertTrue(i_events.next().timestamp() == 10);
    }

    @Test
    public void getBestTelemetryShouldThrowExceptionWhenNoTelemetry()
    throws NoSuchTelemetryException {
        Track track = new Track(track_storage_nt);
        exception.expect(NoSuchTelemetryException.class);
        ITelemetry best_telemetry = track.getBestTelemetry();
    }


}
