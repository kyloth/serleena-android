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
 * Name: SQLiteDAOTrackTest.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0      Gabriele Pozzan  Creazione file, codice e javadoc
 */

package com.kyloth.serleena.persistence.sqlite;


import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;

/**
 * Contiene test per la classe SQLiteDAOExperience.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0
 */

public class SQLiteDAOTrackTest {

    private SerleenaSQLiteDataSource serleenaSQLDS;
    private DirectAccessList<Checkpoint> emptyCheckpointList;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        serleenaSQLDS = mock(SerleenaSQLiteDataSource.class);
        emptyCheckpointList = new ListAdapter<>(new ArrayList<Checkpoint>());
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullCheckpoints() {
        new SQLiteDAOTrack(null, 0, "", serleenaSQLDS);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullName() {
        new SQLiteDAOTrack(emptyCheckpointList, 0, null, serleenaSQLDS);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione al passaggio di
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullDataSource() {
        new SQLiteDAOTrack(emptyCheckpointList, 0, "", null);
    }

    /**
     * Verifica che l'id ritornato da id() sia quello corretto.
     */
    @Test
    public void idShouldBeReturnedCorrectly() {
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(
                emptyCheckpointList, 123, "", serleenaSQLDS);
        assertEquals(123, daoTrack.id());
    }

    /**
     * Verifica che il nome ritornato da name() sia quello corretto.
     */
    @Test
    public void nameShouldBeReturnedCorrectly() {
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(
                emptyCheckpointList, 123, "name", serleenaSQLDS);
        assertEquals("name", daoTrack.name());
    }

    /**
     * Verifica che il metodo getCheckPoints restituisca la
     * lista di Checkpoint passata al costruttore.
     */
    @Test
    public void testGetCheckpoints() {
        Checkpoint cp1 = new Checkpoint(1, 1);
        Checkpoint cp2 = new Checkpoint(2, 2);
        Checkpoint cp3 = new Checkpoint(3, 3);
        List<Checkpoint> list = Arrays.asList(cp1, cp2, cp3);
        DirectAccessList<Checkpoint> cpList = new ListAdapter<Checkpoint>(list);

        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(cpList, 1, "", serleenaSQLDS);
        DirectAccessList<Checkpoint> returnList = daoTrack.getCheckpoints();
        assertTrue(cp1.equals(returnList.get(0)));
        assertTrue(cp2.equals(returnList.get(1)));
        assertTrue(cp3.equals(returnList.get(2)));
    }

    /**
     * Verifica che il metodo createTelemetry chiami correttamente
     * il metodo createTelemetry di SerleenaSQLiteDataSource fornendo
     * come parametri la lista degli eventi passata e il DAOTrack stesso.
     */
    @Test
    public void createTelemetryShouldForwardCorrectParams() {
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(
                emptyCheckpointList, 123, "", serleenaSQLDS);
        Iterable<TelemetryEvent> mock_list =
                (Iterable<TelemetryEvent>) mock(Iterable.class);
        daoTrack.createTelemetry(mock_list);
        verify(serleenaSQLDS).createTelemetry(mock_list, daoTrack);
    }

    /**
     * Verifica che il metodo getTelemetries richieda correttamente a
     * SerleenaSQLiteDataSource la lista dei tracciamenti e la restituisca.
     */
    @Test
    public void testGetTelemetries() {
        SQLiteDAOTelemetry t1 = new SQLiteDAOTelemetry(1, null);
        SQLiteDAOTelemetry t2 = new SQLiteDAOTelemetry(2, null);
        Iterable<SQLiteDAOTelemetry> telemetryList = Arrays.asList(t1, t2);

        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(
                emptyCheckpointList, 123, "", serleenaSQLDS);
        when(serleenaSQLDS.getTelemetries(daoTrack)).thenReturn(telemetryList);
        Iterable<ITelemetryStorage> result = daoTrack.getTelemetries();
        Iterator<ITelemetryStorage> i_result = result.iterator();
        assertTrue(i_result.next() == t1);
        assertTrue(i_result.next() == t2);
    }
}
