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
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file, scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.common.TelemetryEvent;

/**
 * Contiene i test di unità per la classe Track.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */

public class TrackTest {
    ITrackStorage trackStorage;
    Track track;
    private ITrackStorage trackStorage1;
    private ITrackStorage trackStorage2;
    private int testHashCode;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        trackStorage = mock(ITrackStorage.class);
        ITelemetryStorage telStor1 = mock(ITelemetryStorage.class);
        ITelemetryStorage telStor2 = mock(ITelemetryStorage.class);
        List<ITelemetryStorage> list = new ArrayList<>();
        list.add(telStor1);
        list.add(telStor2);

        List<Checkpoint> checkpointList = new ArrayList<>();
        checkpointList.add(new Checkpoint(5, 5));
        checkpointList.add(new Checkpoint(6, 6));
        when(trackStorage.getCheckpoints()).thenReturn(
                new ListAdapter<Checkpoint>(checkpointList));

        TelemetryEvent e1 = new CheckpointReachedTelemetryEvent(100, 1);
        List<TelemetryEvent> ll = new ArrayList<>(); ll.add(e1);
        when(telStor1.getEvents()).thenReturn(ll);

        TelemetryEvent e2 = new CheckpointReachedTelemetryEvent(200, 1);
        List<TelemetryEvent> lll = new ArrayList<>(); lll.add(e2);
        when(telStor2.getEvents()).thenReturn(lll);

        when(trackStorage.getTelemetries()).thenReturn(list);
        track = new Track(trackStorage);

        testHashCode = 100;
        trackStorage1 = new ITrackStorage() {
            public void createTelemetry(Iterable<TelemetryEvent> events) { }
            public Iterable<ITelemetryStorage> getTelemetries() { return null; }
            public Iterable<ITelemetryStorage> getTelemetries(boolean ig) {
                return null;
            }
            public DirectAccessList<Checkpoint> getCheckpoints() {return null; }
            public String name() { return null; }
            public UUID getUUID() { return null; }
            public int hashCode() { return testHashCode; }
        };
        trackStorage2 = trackStorage1;
    }

    /**
     * Verifica che il metodo getTelemetries restituisca correttamente
     * la lista dei tracciamenti relativi al percorso.
     */
    @Test
    public void getTelemetriesShouldReturnCorrectValues()
            throws NoSuchTelemetryEventException {
        final Iterable<ITelemetry> telemetries = track.getTelemetries();

        Iterator<ITelemetry> iterator = telemetries.iterator();
        ITelemetry t1 = iterator.next();
        ITelemetry t2 = iterator.next();

        TelemetryEvent e1 = t1.getEvents().iterator().next();
        TelemetryEvent e2 = t2.getEvents().iterator().next();

        assertTrue((e1.timestamp() == 100 && e2.timestamp() == 200) ||
                (e1.timestamp() == 200 && e2.timestamp() == 100));
    }

    /**
     * Verifica che il metodo getBestTelemetry restituisca il tracciamento
     * con durata migliore per il percorso corrente.
     */
    @Test
    public void getBestTelemetryShouldReturnCorrectValue()
            throws NoSuchTelemetryException {
        ITelemetry best = track.getBestTelemetry();
        assertTrue(best.getEvents().iterator().next().timestamp() == 100);
    }

    /**
     * Verifica che getBestTelemetry() sollevi un'eccezione quando non il
     * Percorso non ha associato alcun Tracciamento.
     */
    @Test(expected = NoSuchTelemetryException.class)
    public void getBestTelemetryShouldThrowExceptionWhenNoTelemetry()
            throws NoSuchTelemetryException {
        ITrackStorage stor = mock(ITrackStorage.class);
        when(stor.getTelemetries()).thenReturn(
                new ArrayList<ITelemetryStorage>());
        new Track(stor).getBestTelemetry();
    }

    /**
     * Verifica che il costruttore di Track sollevi un'eccezione quando
     * vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullArguments() {
        new Track(null);
    }

    /**
     * Verifica che createTelemetry() inoltri la chiamata all'oggetto
     * ITrackStorage sottostante.
     */
    @Test
    public void createTelemetryShouldForwardCallToStorage() {
        Iterable<TelemetryEvent> events = new ArrayList<>();
        track.createTelemetry(events);
        verify(trackStorage).createTelemetry(events);
    }

    /**
     * Verifica che createTelemetry() sollevi un'eccezione quando gli vengono
     * passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void createTelemetryShouldThrowWhenNullArgument() {
        track.createTelemetry(null);
    }

    /**
     * Verifica che i checkpoint del Percorso vengano prelevati correttamente
     * dall'oggetto ITrackStorage sottostante.
     */
    @Test
    public void getCheckpointsShouldGetDataFromStorage() {
        DirectAccessList<Checkpoint> checkpoints = track.getCheckpoints();
        assertEquals(trackStorage.getCheckpoints().get(0),
                checkpoints.get(0));
        assertEquals(trackStorage.getCheckpoints().get(1),
                checkpoints.get(1));
    }

    /**
     * Verifica che il nome restituito dal metodo name() corrisponda a quello
     * restituito dall'oggetto di persistenza sottostante.
     */
    @Test
    public void trackNameShouldBeTheSameProvidedByStorageObject() {
        when(trackStorage.name()).thenReturn("name");
        assertEquals("name", track.name());
    }

    /**
     * Verifica che il metodo toString() restituisca il nome del Percorso.
     */
    @Test
    public void toStringShouldReturnNameOfTheTrack() {
        when(trackStorage.name()).thenReturn("track");
        assertEquals("track", track.toString());
    }

    /**
     * Verifica che equals restituisca true se e solo se i rispettivi oggetti
     * di persistenza sono equivalenti.
     */
    @Test
    public void tracksShouldBeEqualIfStoragesAreEqual() {
        assertEquals(new Track(trackStorage1), new Track(trackStorage2));
        assertNotEquals(
                new Track(trackStorage), new Track(mock(ITrackStorage.class)));
        assertNotEquals(null, new Track(trackStorage));
    }

    /**
     * Verifica che hashCode() restituisca lo stesso codice per oggetti il
     * cui metodo equals restituisce true.
     */
    @Test
    public void hashCodeShouldBeTheSameForEqualTracks() {
        assertEquals(
                new Track(trackStorage1).hashCode(),
                new Track(trackStorage2).hashCode());
    }

}
