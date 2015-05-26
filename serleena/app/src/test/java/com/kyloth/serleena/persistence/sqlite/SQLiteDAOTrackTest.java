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

import java.util.Arrays;
import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.common.ImmutableList;
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
    SerleenaSQLiteDataSource serleenaSQLDS;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        serleenaSQLDS = mock(SerleenaSQLiteDataSource.class);
    }

    /**
     * Verifica che un nuovo oggetto sia costruito correttamente e che
     * il metodo id ritorni il parametro passato.
     */

    @Test
    public void testConstructorAndId() {
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(null, 123, serleenaSQLDS);
        assertTrue(daoTrack.id() == 123);
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
        ImmutableList<Checkpoint> cp_list = new ListAdapter(Arrays.asList
                (new Checkpoint[] {cp1, cp2, cp3}));

        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(cp_list, 123, serleenaSQLDS);
        ImmutableList<Checkpoint> return_list = daoTrack.getCheckpoints();
        Iterator<Checkpoint> i_cp = return_list.iterator();
        assertTrue(i_cp.next().latitude() == 1);
        assertTrue(i_cp.next().latitude() == 2);
        assertTrue(i_cp.next().latitude() == 3);
    }

    /**
     * Verifica che il metodo createTelemetry chiami correttamente
     * il metodo createTelemetry di SerleenaSQLiteDataSource fornendo
     * come parametri la lista degli eventi passata e il DAOTrack stesso.
     */

    @Test
    public void createTelemetryShouldForwardCorrectParams() {
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(null, 123, serleenaSQLDS);
        Iterable<TelemetryEvent> mock_list = (Iterable<TelemetryEvent>) mock(Iterable.class);
        daoTrack.createTelemetry(mock_list);
        verify(serleenaSQLDS).createTelemetry(mock_list, daoTrack);
    }

    /**
     * Verifica che il metodo getTelemetries richieda correttamente a
     * SerleenaSQLiteDataSource la lista dei tracciamenti e la restituisca.
     */

    @Test
    public void testGetTelemetries() {
        SQLiteDAOTelemetry t1 = mock(SQLiteDAOTelemetry.class);
        SQLiteDAOTelemetry t2 = mock(SQLiteDAOTelemetry.class);
        Iterable<SQLiteDAOTelemetry> t_list = Arrays.asList(new SQLiteDAOTelemetry[] {t1, t2});
        SQLiteDAOTrack daoTrack = new SQLiteDAOTrack(null, 123, serleenaSQLDS);
        when(serleenaSQLDS.getTelemetries(daoTrack)).thenReturn(t_list);
        Iterable<ITelemetryStorage> result = daoTrack.getTelemetries();
        Iterator<ITelemetryStorage> i_result = result.iterator();
        assertTrue(i_result.next() == t1);
        assertTrue(i_result.next() == t2);
    }
}
