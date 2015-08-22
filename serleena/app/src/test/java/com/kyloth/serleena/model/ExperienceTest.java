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
 * Name: ExperienceTest.java
 * Package: com.hitchikers.serleena.model
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                             della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test di unità per la classe Experience
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ExperienceTest {

    private IExperienceStorage experienceStorage;
    private Experience experience;
    private ITrackStorage track1;
    private ITrackStorage track2;
    private ITrackStorage track3;
    private ArrayList<ITrackStorage> trackStorages;
    private ArrayList<UserPoint> userPoints;
    private String testName;
    private GeoPoint testLocation;
    private IQuadrant testQuadrant;

    @Before
    public void initialize() throws Exception {
        experienceStorage = mock(IExperienceStorage.class);
        experience = new Experience(experienceStorage);

        track1 = mock(ITrackStorage.class);
        track2 = mock(ITrackStorage.class);
        track3 = mock(ITrackStorage.class);
        trackStorages = new ArrayList<ITrackStorage>();
        trackStorages.add(track1);
        trackStorages.add(track2);
        trackStorages.add(track3);

        when(track1.name()).thenReturn("track1");
        when(track2.name()).thenReturn("track2");
        when(track3.name()).thenReturn("track3");

        when(experienceStorage.getTracks()).thenReturn(trackStorages);

        userPoints = new ArrayList<UserPoint>();
        userPoints.add(mock(UserPoint.class));
        userPoints.add(mock(UserPoint.class));

        when(experienceStorage.getUserPoints()).thenReturn(userPoints);

        testName = "Experience Name";
        when(experienceStorage.getName()).thenReturn(testName);

        testLocation = mock(GeoPoint.class);
        testQuadrant = mock(IQuadrant.class);
        when(experienceStorage.getQuadrant(testLocation))
                .thenReturn(testQuadrant);
    }

    /**
     * Verifica che vengano correttamente restituiti i Percorsi di
     * un'Esperienza in base a quanto restituito dall'oggetto di persistenza.
     */
    @Test
    public void testGetTracks() throws Exception {
        Iterable<ITrack> tracks = experience.getTracks();
        for (ITrackStorage ts : trackStorages) {
            boolean contains = false;
            for (ITrack t : tracks)
                contains = contains || t.name().equals(ts.name());
            assertTrue(contains);
        }
    }

    /**
     * Verifica che vengano correttamente restituiti i Punti Utente di
     * un'Esperienza in base a quanto restituito dall'oggetto di persistenza.
     */
    @Test
    public void testGetUserPoints() throws Exception {
        assertEquals(userPoints, experience.getUserPoints());
    }

    /**
     * Verifica che l'aggiunta di un Punto Utente inoltri la richiesta
     * all'oggetto di persistenza.
     */
    @Test
    public void testAddUserPoints() throws Exception {
        UserPoint up = mock(UserPoint.class);
        experience.addUserPoints(up);
        verify(experienceStorage).addUserPoint(up);
    }

    /**
     * Verifica che venga correttamente restituito il nome di
     * un'Esperienza in base a quanto restituito dall'oggetto di persistenza.
     */
    @Test
    public void testGetName() throws Exception {
        assertEquals(testName, experience.getName());
    }

    /**
     * Verifica che la richiesta di un quadrante associato all'Esperienza
     * venga inoltrata all'oggetto di persistenza.
     */
    @Test
    public void testGetQuadrantQueriesStorage() throws Exception {
        experience.getQuadrant(testLocation);
        verify(experienceStorage).getQuadrant(testLocation);
    }

    /**
     * Verifica che venga correttamente restituito il quadrante di
     * un'Esperienza in base a quanto restituito dall'oggetto di persistenza.
     */
    @Test
    public void testGetQuadrant() throws NoSuchQuadrantException {
        assertEquals(testQuadrant, experience.getQuadrant(testLocation));
    }

    /**
     * Verifica che il metodo toString() restituisca il nome dell'Esperienza
     * rappresentata dall'istanza.
     */
    @Test
    public void testToString() throws Exception {
        assertEquals(testName, experience.toString());
    }

}