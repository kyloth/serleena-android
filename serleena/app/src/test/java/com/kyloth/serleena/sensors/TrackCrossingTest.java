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
 * Name: TrackCrossingTest.java
 * Package: com.kyloth.serleena.sensors;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.model.ITrack;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * Contiene test di unità per la classe TrackCrossing.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TrackCrossingTest {

    TrackCrossing tc;
    ILocationReachedManager locReaMan;
    ITrack track;
    ITrack oneCheckpointTrack;

    @Before
    public void initialize() {
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
        track = mock(ITrack.class);
        when(track.getCheckpoints()).thenReturn(checkpoints);

        checkpoints = new DirectAccessList<Checkpoint>() {
                    @Override public int size() { return 1; }
                    @Override public Checkpoint get(int index) {
                        return new Checkpoint(0,0);
                    }
                    @Override public Iterator<Checkpoint> iterator() {
                        return null;
                    }
                };
        oneCheckpointTrack = mock(ITrack.class);
        when(oneCheckpointTrack.getCheckpoints()).thenReturn(checkpoints);

        locReaMan = mock(ILocationReachedManager.class);
        tc = new TrackCrossing(locReaMan);
    }

    /**
     * Verifica che gli observer registrati all'oggetto TrackCrossing vengano
     * correttamente notificati.
     */
    @Test
    public void testObserversNotification() throws NoTrackCrossingException {
        ITrackCrossingObserver o1 = mock(ITrackCrossingObserver.class);
        ITrackCrossingObserver o2 = mock(ITrackCrossingObserver.class);
        ITrackCrossingObserver o3 = mock(ITrackCrossingObserver.class);

        tc.startTrack(track);

        tc.attachObserver(o1);
        tc.attachObserver(o2);
        tc.attachObserver(o3);
        tc.notifyObservers();
        verify(o1).onCheckpointCrossed(any(Integer.class));
        verify(o2).onCheckpointCrossed(any(Integer.class));
        verify(o3).onCheckpointCrossed(any(Integer.class));
    }

    /**
     * Verifica il corretto ordine di attraversamento dei checkpoint.
     */
    @Test
    public void testThatCheckpointsAreCrossedCorrectly()
            throws NoActiveTrackException, NoTrackCrossingException,
            NoSuchCheckpointException {
        tc.startTrack(track);

        ITrackCrossingObserver observer = mock(ITrackCrossingObserver.class);
        tc.attachObserver(observer);

        assertTrue(tc.getNextCheckpoint() == 0);
        tc.onLocationReached();
        verify(observer).onCheckpointCrossed(0);
        assertTrue(tc.getLastCrossed().checkPointIndex() == 0);

        assertTrue(tc.getNextCheckpoint() == 1);
        tc.onLocationReached();
        verify(observer).onCheckpointCrossed(1);
        assertTrue(tc.getLastCrossed().checkPointIndex() == 1);

        assertTrue(tc.getNextCheckpoint() == 2);
        tc.onLocationReached();
        verify(observer).onCheckpointCrossed(2);
        assertTrue(tc.getLastCrossed().checkPointIndex() == 2);

        assertTrue(tc.getNextCheckpoint() == 3);
        tc.onLocationReached();
        verify(observer).onCheckpointCrossed(3);
        assertTrue(tc.getLastCrossed().checkPointIndex() == 3);
    }

    /**
     * Verifica che il metodo getLastCrossed() chiamato quando non è stato
     * ancora attraversato alcun checkpoint per il percorso attivo sollevi
     * un'eccezione.
     */
    @Test(expected = NoSuchCheckpointException.class)
    public void testTrackLowerLimits()
            throws NoActiveTrackException, NoSuchCheckpointException {
        tc.startTrack(track);
        tc.getLastCrossed();
    }

    /**
     * Verifica che il termine del percorso venga segnalato alla chiamata del
     * metodo getNextCheckpoint() con un'eccezione.
     */
    @Test(expected = NoTrackCrossingException.class)
    public void testTrackUpperLimits()
            throws NoActiveTrackException, NoTrackCrossingException {
        tc.startTrack(oneCheckpointTrack);
        tc.onLocationReached();
        tc.getNextCheckpoint();
    }

    /**
     * Verifica che il passaggio di un percorso null a startTrack() sollevi
     * un'eccezione.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatNullTrackThrow() {
        tc.startTrack(null);
    }

    /**
     * Verifica che il tentativo di costruire un oggetto passando un
     * parametro null al costruttore sollevi un'eccezione.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatNullManagerThrows() {
        new TrackCrossing(null);
    }

    /**
     * Verifica che l'ultimo tempo parziale sia 0 quando il percorso è appena
     * iniziato.
     */
    @Test
    public void testThatLastPartialIsZeroAtBeginningOfTrack()
            throws NoActiveTrackException, NoSuchCheckpointException,
            NoTrackCrossingException {
        tc.startTrack(track);
        tc.advanceCheckpoint();
        assertTrue(tc.getLastCrossed().partialTime() == 0);
    }

    /**
     * Verifica che il tentativo di ottenere il tempo parziale quando non è
     * stato avviato alcun percorso sollevi un'eccezione.
     */
    @Test(expected = NoActiveTrackException.class)
    public void testThatLastPartialForAbortedTrackThrows1()
            throws NoActiveTrackException, NoSuchCheckpointException {
        tc.getLastCrossed();
    }

    /**
     * Verifica che la chiamata multipla di attachObserver() e
     * detachObserver() con medesimo parametro non sollevi eccezioni.
     */
    @Test
    public void testThatAttachingOrDetachingMultipleTimesDoesntThrows() {
        ITrackCrossingObserver observer = mock(ITrackCrossingObserver.class);
        tc.attachObserver(observer);
        tc.attachObserver(observer);
        tc.detachObserver(observer);
        tc.detachObserver(observer);
    }

    /**
     * Verifica che il tentativo di avanzare di checkpoint quando non vi è un
     * percorso attivo sollevi un'eccezione.
     */
    @Test(expected = NoActiveTrackException.class)
    public void testThatAdvancingCheckpointWithNoActiveTrackThrows()
            throws NoActiveTrackException, NoTrackCrossingException {
        tc.advanceCheckpoint();
    }

    /**
     * Verifica che il tentativo di ottenere il prossimo checkpoint quando
     * non vi è un percorso attivo sollevi un'eccezione.
     */
    @Test(expected = NoActiveTrackException.class)
    public void testThatNextCheckpointWithNoActiveTrackThrows()
            throws NoActiveTrackException, NoTrackCrossingException {
        tc.getNextCheckpoint();
    }

    @Test(expected = NoActiveTrackException.class)
    public void testThatGetTrackWithNoActiveTrackThrows()
            throws NoActiveTrackException {
        TrackCrossing tc = new TrackCrossing(locReaMan);
        tc.getTrack();
    }

    @Test
    public void testGetTrack() throws NoActiveTrackException {
        TrackCrossing tc = new TrackCrossing(locReaMan);
        tc.startTrack(oneCheckpointTrack);
        assertTrue(tc.getTrack() == oneCheckpointTrack);
    }

    /**
     * Verifica che getNextCheckpoint() sollevi un'eccezione in seguito alla
     * chiamata di abort().
     */
    @Test(expected = NoActiveTrackException.class)
    public void getNextCheckpointShouldThrowIfAbortedTrack()
            throws NoActiveTrackException, NoTrackCrossingException {
        tc.startTrack(track);
        tc.advanceCheckpoint();
        tc.advanceCheckpoint();
        tc.abort();
        tc.getNextCheckpoint();
    }

    /**
     * Verifica che getTrack() sollevi un'eccezione in seguito alla chiamata di
     * abort().
     */
    @Test(expected = NoActiveTrackException.class)
    public void getTrackShouldThrowIfAbortedTrack()
            throws NoActiveTrackException {
        tc.startTrack(track);
        tc.abort();
        tc.getTrack();
    }

    /**
     * Verifica che getLastCrossed() sollevi un'eccezione in seguito a una
     * chiamata a abort().
     */
    @Test(expected = NoActiveTrackException.class)
    public void getLastCrossedShouldThrowIfAbortedTrack()
            throws NoTrackCrossingException, NoSuchCheckpointException,
            NoActiveTrackException {
        tc.startTrack(track);
        tc.advanceCheckpoint();
        tc.advanceCheckpoint();
        tc.abort();
        tc.getLastCrossed();
    }

    /**
     * Verifica che una chiamata ad abort() causi la deregistrazione
     * dell'oggetto TrackCrossing dal ILocationReachedManager.
     */
    @Test
    public void abortingShouldDetachFromSensor() {
        tc.abort();
        verify(locReaMan).detachObserver(tc);
    }

    @Test
    public void shouldRegisterToSensorWhenNewTrackStarted() {
        tc.startTrack(mock(ITrack.class));
        verify(locReaMan).attachObserver(tc, any(GeoPoint.class));
    }

}
