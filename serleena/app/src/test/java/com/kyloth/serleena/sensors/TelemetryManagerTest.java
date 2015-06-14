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
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.model.ITrack;

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
 * Contiene test di unità per la classe TelemetryManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TelemetryManagerTest {

    private ITrack getTrack() {
        ImmutableList<Checkpoint> checkpoints =
                new ImmutableList<Checkpoint>() {
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
        ImmutableList<Checkpoint> checkpoints =
                new ImmutableList<Checkpoint>() {
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

    /**
     * Verifica che sia possibile abilitare il Tracciamento quando non vi
     * sono Percorsi in corso, o quando il Percorso non ha ancora raggiunto
     * il primo checkpoint.
     */
    @Test
    public void enablingTelemetryWithNoTrackShouldWork() throws
            TrackAlreadyStartedException,
            NoTrackCrossingException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        assertTrue(!tm.isEnabled());

        when(tc.getNextCheckpoint()).thenReturn(0);
        tm.enable();
        assertTrue(tm.isEnabled());

        tm.disable();
        assertTrue(!tm.isEnabled());

        when(tc.getNextCheckpoint()).thenThrow(NoTrackCrossingException.class);
        tm.enable();
        assertTrue(tm.isEnabled());
    }

    /**
     * Verifica che venga sollevata un'eccezione, e quindi non sia possibile
     * avviare il Tracciamento per un Percorso già in corso.
     */
    @Test(expected = TrackAlreadyStartedException.class)
    public void enablingTelemetryWithTrackStartedShouldThrow() throws
            NoTrackCrossingException,
            TrackAlreadyStartedException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        when(tc.getNextCheckpoint()).thenReturn(2);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        tm.enable();
    }

    /**
     * Verifica che la disabilitazione del Tracciamento causi la
     * deregistrazione ai sensori necessari al campionamento.
     */
    @Test
    public void disablingTelemetryShouldUnregisterSensors() {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        tm.disable();
        verify(wm).detachObserver(tm);
    }

    /**
     * Verifica che, se abilitato, il Tracciamento venga avviato
     * all'attraversamento del primo checkpoint del Percorso.
     */
    @Test
    public void telemetryShouldStartAtFirstCheckpointOfTrack()
            throws TrackAlreadyStartedException, NoTrackCrossingException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        ITrack track = getTrack();
        when(tc.getTrack()).thenReturn(track);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        tm.enable();
        tm.onCheckpointCrossed(0);

        verify(wm).attachObserver(
                tm, TelemetryManager.SAMPLING_RATE_SECONDS, false);

        CheckpointReachedTelemetryEvent crte =
                (CheckpointReachedTelemetryEvent)
                        tm.getEvents().iterator().next();
        assertTrue(crte.checkpointNumber() == 0);
        assertTrue(crte.timestamp() >= 0);
    }

    /**
     * Verifica il corretto inserimento degli eventi di Tracciamento relativi
     * all'attraversamento dei checkpoint.
     */
    @Test
    public void checkpointEventsShouldBeAddedCorrectly()
            throws NoTrackCrossingException, TrackAlreadyStartedException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        ITrack track = getTrack();
        when(tc.getTrack()).thenReturn(track);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        tm.enable();

        tm.onCheckpointCrossed(0);
        assertPresenceOfEvent(tm, 0);
        tm.onCheckpointCrossed(1);
        assertPresenceOfEvent(tm, 1);
        tm.onCheckpointCrossed(2);
        assertPresenceOfEvent(tm, 2);
    }

    /**
     * Verifica che al termine di un Percorso venga arrestato e disabilitato
     * il Tracciamento.
     */
    @Test
    public void trackEndingShouldStopAndDisableTelemetry()
            throws NoTrackCrossingException, TrackAlreadyStartedException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        ITrack track = getOneCheckpointTrack();
        when(tc.getTrack()).thenReturn(track);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        tm.enable();

        tm.onCheckpointCrossed(0);
        verify(wm).detachObserver(tm);
        assertTrue(!tm.isEnabled());
    }

    /**
     * Verifica che al termine di un Percorso, venga aggiunto a questo il
     * Tracciamento appena registrato.
     */
    @Test
    public void trackEndingShouldCreateNewTelemetry()
            throws NoTrackCrossingException, TrackAlreadyStartedException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        ITrack track = getOneCheckpointTrack();
        when(tc.getTrack()).thenReturn(track);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);
        tm.enable();

        tm.onCheckpointCrossed(0);
        Iterable<TelemetryEvent> events = tm.getEvents();
        verify(track).createTelemetry(events);
    }

    /**
     * Verifica che ad ogni wakeup, venga programmato un aggiornamento dai
     * sensori e acquisito un lock del processore per evitare che questo
     * entri in sleep mode prima della ricezione dei dati.
     */
    @Test
    public void wakeupShouldAcquireLocksAndSheduleSensors() throws
            NoTrackCrossingException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        tm.onWakeup();
        verify(pm).lock("LocationTelemetryLock");
        verify(pm).lock("HeartRateTelemetryLock");
        verify(lm).getSingleUpdate(tm, TelemetryManager.SENSOR_TIMEOUT_SECONDS);
        verify(hm).getSingleUpdate(tm, TelemetryManager.SENSOR_TIMEOUT_SECONDS);
    }

    /**
     * Verifica che la ricezione di dati dai sensori rilasci il lock
     * acquisito al momento della richiesta di tali dati.
     */
    @Test
    public void sensorUpdateShouldReleaseLock() {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        tm.onLocationUpdate(mock(GeoPoint.class));
        verify(pm).unlock("LocationTelemetryLock");

        tm.onHeartRateUpdate(23);
        verify(pm).unlock("HeartRateTelemetryLock");
    }

    /**
     * Verifica che i dati ricevuti dai sensori vengano inseriti come eventi
     * del Tracciamento in corso.
     */
    @Test
    public void sensorUpdateShouldCreateEvent() {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        GeoPoint gp = new GeoPoint(12, 21);
        tm.onLocationUpdate(gp);

        boolean found = false;
        for (TelemetryEvent e : tm.getEvents())
            if (e instanceof LocationTelemetryEvent)
                found = found ||
                        ((LocationTelemetryEvent)e).location().equals(gp);
        assertTrue(found);

        found = false;
        tm.onHeartRateUpdate(22);
        for (TelemetryEvent e : tm.getEvents())
            if (e instanceof HeartRateTelemetryEvent)
                found = found ||
                        ((HeartRateTelemetryEvent)e).heartRate() == 22;
        assertTrue(found);
    }

    /**
     * Verifica che gli eventi restituiti non siano una singola istanza, ma
     * venga restituita una copia ad ogni richiesta.
     */
    @Test
    public void eventsShouldBeReturnedAsAClone() {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        tm.onHeartRateUpdate(22);
        Iterable<TelemetryEvent> events1 = tm.getEvents();
        tm.onHeartRateUpdate(22);
        Iterable<TelemetryEvent> events2 = tm.getEvents();
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
            NoTrackCrossingException, TrackAlreadyStartedException {
        IWakeupManager wm = mock(IWakeupManager.class);
        ILocationManager lm = mock(ILocationManager.class);
        IPowerManager pm = mock(IPowerManager.class);
        IHeartRateManager hm = mock(IHeartRateManager.class);
        ITrackCrossing tc = mock(ITrackCrossing.class);
        ITrack track = getOneCheckpointTrack();
        when(tc.getTrack()).thenReturn(track);

        TelemetryManager tm = new TelemetryManager(lm, hm, wm, pm, tc);

        tm.onCheckpointCrossed(0);
        verify(wm, never()).attachObserver(eq(tm), any(Integer.class), any
                (Boolean.class));
        verify(tc, never()).getTrack();
        assertTrue(!tm.getEvents().iterator().hasNext());
    }

}
