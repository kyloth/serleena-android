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
 * Name: TelemetryManagerTest.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.model.ITrack;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test di unità per la classe TelemetryManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TelemetryManagerTest {

    private ITrack getTrack() {
        DirectAccessList<Checkpoint> checkpoints =
                new DirectAccessList<Checkpoint>() {
                    @Override public int size() { return Integer.MAX_VALUE; }
                    @Override public Checkpoint get(int index) {
                        return new Checkpoint(0, 0);
                    }
                    @Override public Iterator<Checkpoint> iterator() {
                        return null;
                    }
                };
        ITrack track = mock(ITrack.class);
        when(track.getCheckpoints()).thenReturn(checkpoints);
        return track;
    }

    private ITrack getOneCheckpointTrack() {
        DirectAccessList<Checkpoint> checkpoints =
                new DirectAccessList<Checkpoint>() {
                    @Override public int size() { return 1; }
                    @Override public Checkpoint get(int index) {
                        return new Checkpoint(0,0);
                    }
                    @Override public Iterator<Checkpoint> iterator() {
                        return null;
                    }
                };
        ITrack track = mock(ITrack.class);
        when(track.getCheckpoints()).thenReturn(checkpoints);
        return track;
    }

    private void assertPresenceOfEvent(TelemetryManager tm, int checkpointNum) {
        boolean found = false;
        for (TelemetryEvent e : tm.getEvents())
            if (e instanceof CheckpointReachedTelemetryEvent) {
                CheckpointReachedTelemetryEvent crte =
                        (CheckpointReachedTelemetryEvent) e;
                found = found || (crte.checkpointNumber() == checkpointNum);
            }
        assertTrue(found);
    }

    IBackgroundLocationManager bkgrLocMan;
    ITrackCrossing tc;
    TelemetryManager manager;

    @Before
    public void initialize() {
        bkgrLocMan = mock(IBackgroundLocationManager.class);
        tc = mock(ITrackCrossing.class);
        manager = new TelemetryManager(tc);
    }

    /**
     * Verifica che sia possibile abilitare il Tracciamento quando non vi
     * sono Percorsi in corso, o quando il Percorso non ha ancora raggiunto
     * il primo checkpoint.
     */
    @Test
    public void enablingTelemetryWithNoTrackShouldWork()
            throws TrackAlreadyStartedException, NoTrackCrossingException,
            NoActiveTrackException {
        assertTrue(!manager.isEnabled());

        when(tc.getNextCheckpoint()).thenReturn(0);
        manager.enable();
        assertTrue(manager.isEnabled());

        manager.disable();
        assertTrue(!manager.isEnabled());

        when(tc.getNextCheckpoint()).thenThrow(NoTrackCrossingException.class);
        manager.enable();
        assertTrue(manager.isEnabled());
    }

    /**
     * Verifica che venga sollevata un'eccezione, e quindi non sia possibile
     * avviare il Tracciamento per un Percorso già in corso.
     */
    @Test(expected = TrackAlreadyStartedException.class)
    public void enablingTelemetryWithTrackStartedShouldThrow()
            throws NoTrackCrossingException, TrackAlreadyStartedException,
            NoActiveTrackException {
        when(tc.getNextCheckpoint()).thenReturn(2);
        manager.enable();
    }

    /**
     * Verifica che, se abilitato, il Tracciamento venga avviato
     * all'attraversamento del primo checkpoint del Percorso.
     */
    @Test
    public void telemetryShouldStartAtFirstCheckpointOfTrack()
            throws TrackAlreadyStartedException, NoTrackCrossingException,
            NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = getTrack();
        when(tc.getTrack()).thenReturn(track);
        when(tc.getLastCrossed()).thenReturn(mock(CheckpointCrossing.class));

        manager.enable();
        manager.onCheckpointCrossed();

        CheckpointReachedTelemetryEvent crte =
                (CheckpointReachedTelemetryEvent)
                        manager.getEvents().iterator().next();
        assertTrue(crte.checkpointNumber() == 1);
        assertTrue(crte.timestamp() >= 0);
    }

    /**
     * Verifica il corretto inserimento degli eventi di Tracciamento relativi
     * all'attraversamento dei checkpoint.
     */
    @Test
    public void checkpointEventsShouldBeAddedCorrectly()
            throws NoTrackCrossingException, TrackAlreadyStartedException,
            NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = getTrack();
        when(tc.getTrack()).thenReturn(track);

        manager.enable();

        CheckpointCrossing crossing = mock(CheckpointCrossing.class);
        when(tc.getLastCrossed()).thenReturn(crossing);

        when(crossing.checkPointIndex()).thenReturn(0);
        manager.onCheckpointCrossed();
        assertPresenceOfEvent(manager, 1);

        when(crossing.checkPointIndex()).thenReturn(1);
        manager.onCheckpointCrossed();
        assertPresenceOfEvent(manager, 2);

        when(crossing.checkPointIndex()).thenReturn(2);
        manager.onCheckpointCrossed();
        assertPresenceOfEvent(manager, 3);
    }

    /**
     * Verifica che al termine di un Percorso venga arrestato e disabilitato
     * il Tracciamento.
     */
    @Test
    public void trackEndingShouldStopAndDisableTelemetry()
            throws NoTrackCrossingException, TrackAlreadyStartedException,
            NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = getOneCheckpointTrack();
        when(tc.getTrack()).thenReturn(track);
        when(tc.getLastCrossed()).thenReturn(mock(CheckpointCrossing.class));

        manager.enable();

        manager.onCheckpointCrossed();
        assertTrue(!manager.isEnabled());
    }

    /**
     * Verifica che al termine di un Percorso, venga aggiunto a questo il
     * Tracciamento appena registrato.
     */
    @Test
    public void trackEndingShouldCreateNewTelemetry()
            throws NoTrackCrossingException, TrackAlreadyStartedException,
            NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = getOneCheckpointTrack();
        CheckpointCrossing crossing = mock(CheckpointCrossing.class);
        when(tc.getTrack()).thenReturn(track);
        when(tc.getLastCrossed()).thenReturn(crossing);

        manager.enable();

        manager.onCheckpointCrossed();
        Iterable<TelemetryEvent> events = manager.getEvents();
        verify(track).createTelemetry(events);
    }

    /**
     * Verifica che gli eventi restituiti non siano una singola istanza, ma
     * venga restituita una copia ad ogni richiesta.
     */
    @Test
    public void eventsShouldBeReturnedAsAClone()
            throws TrackAlreadyStartedException, NoTrackCrossingException,
            NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = getTrack();
        when(tc.getTrack()).thenReturn(track);
        CheckpointCrossing crossing = mock(CheckpointCrossing.class);
        when(tc.getLastCrossed()).thenReturn(crossing);
        manager.enable();

        manager.onCheckpointCrossed();
        Iterable<TelemetryEvent> events1 = manager.getEvents();
        when(crossing.checkPointIndex()).thenReturn(1);
        manager.onCheckpointCrossed();
        Iterable<TelemetryEvent> events2 = manager.getEvents();
        assertTrue(events1 != events2);

        int count1 = 0, count2 = 0;
        for (TelemetryEvent e : events1)
            count1++;
        for (TelemetryEvent e : events2)
            count2++;
        assertTrue(count1 < count2);
    }

    /**
     * Verifica che il Tracciamento non venga avviato se non abilitato.
     */
    @Test
    public void telemetryShouldNotStartIfNotEnabled() throws
            NoTrackCrossingException, TrackAlreadyStartedException,
            NoActiveTrackException {
        ITrack track = getOneCheckpointTrack();
        when(tc.getTrack()).thenReturn(track);

        manager.onCheckpointCrossed();
        verify(tc, never()).getTrack();
        assertTrue(!manager.getEvents().iterator().hasNext());
    }

}
