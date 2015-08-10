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
 * Name: CheckpointCrossingTest.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test di unità per la classe CheckpointCrossing.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class CheckpointCrossingTest {

    private ITelemetry best;
    private ITrack track;
    private CheckpointCrossing crossing;

    @Before
    public void initialize() throws NoSuchTelemetryException {
        best = mock(ITelemetry.class);
        track = mock(ITrack.class);
        crossing = new CheckpointCrossing(2, 150, track);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se gli vengono passati Percorsi null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfNullTrack() {
        new CheckpointCrossing(2, 150, null);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se gli viene passato un tempo parziale negativo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfNegativePartial() {
        new CheckpointCrossing(2, -1, track);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se gli viene passato un numero di checkpoint
     * inferiore a 1.
     */
    @Test
    public void ctorShouldThrowIfCheckpointNumberBelowOne() {
        boolean b1 = false;
        boolean b2 = false;

        try {
            new CheckpointCrossing(0, 100, track);
        } catch (IllegalArgumentException e) {
            b1 = true;
        }
        try {
            new CheckpointCrossing(-1, 100, track);
        } catch (IllegalArgumentException e) {
            b2 = true;
        }

        assertTrue(b1 && b2);
    }

    /**
     * Verifica che venga restituito il corretto numero di checkpoint
     * attraversato.
     */
    @Test
    public void objectShouldReturnCorrectCheckpointNumber() {
        assertEquals(2, crossing.checkPointNumber());
    }

    /**
     * Verifica che venga restituito il corretto tempo parziale.
     */
    @Test
    public void objectShouldReturnCorrectPartialTime() {
        assertEquals(150, crossing.partialTime());
    }

    /**
     * Verifica che venga sollevata un'eccezione NoSuchTelemetryException se
     * non sono disponibili Tracciamenti su cui effettuare il confronto.
     */
    @Test(expected = NoSuchTelemetryException.class)
    public void objectShouldThrowWhenAskedForDeltaIfNoBestTelemetryAvailable()
            throws NoSuchTelemetryException, NoSuchTelemetryEventException {
        when(track.getBestTelemetry())
                .thenThrow(NoSuchTelemetryException.class);
        crossing.delta();
    }

    /**
     * Verifica che venga sollevata un'eccezione NoSuchTelemetryEventException
     * se non sono disponibili eventi di Tracciamento su cui effettuare il
     * confronto.
     */
    @Test(expected = NoSuchTelemetryEventException.class)
    public void objectShouldThrowWhenAskedForDeltaIfNoEventAvailable()
            throws NoSuchTelemetryException, NoSuchTelemetryEventException {
        when(track.getBestTelemetry()).thenReturn(best);
        when(best.getEvents(Matchers.<Predicate<TelemetryEvent>>any()))
                .thenThrow(NoSuchTelemetryEventException.class);
        crossing.delta();
    }

    /**
     * Verifica che la differenza tra la prestazione rappresentata
     * dall'oggetto e quella migliore per il Percorso venga calcolata
     * correttamente.
     */
    @Test
    public void objectShouldReturnCorrectDelta()
            throws NoSuchTelemetryException, NoSuchTelemetryEventException {
        List<TelemetryEvent> list = new ArrayList<>();
        list.add(new CheckpointReachedTelemetryEvent(200, 2));
        when(best.getEvents(Matchers.<Predicate<TelemetryEvent>>any()))
                .thenReturn(list);
        when(track.getBestTelemetry()).thenReturn(best);

        assertEquals(-50, crossing.delta());
    }
}
