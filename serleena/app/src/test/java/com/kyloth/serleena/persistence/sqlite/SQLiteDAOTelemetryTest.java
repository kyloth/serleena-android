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
 * Name: SQLiteDAOTelemetryTest.java
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

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.kyloth.serleena.common.TelemetryEvent;

import android.database.sqlite.SQLiteDatabase;

/**
 * Test di unità per la classe SQLiteDAOExperience.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0
 */
public class SQLiteDAOTelemetryTest {

    SerleenaSQLiteDataSource dataSource;

    /**
     * Inizializza i campi dati necessari alla conduzione
     * dei test.
     */

    @Before
    public void initialize() {
        dataSource = mock(SerleenaSQLiteDataSource.class);
    }

    /**
     * Verifica che il costruttore inizializzi correttamente i
     * campi dati della classe e che i metodi getter li restituiscano
     * di conseguenza.
     */

    @Test
    public void testConstructorAndGetters() {
        TelemetryEvent te1 = mock(TelemetryEvent.class);
        TelemetryEvent te2 = mock(TelemetryEvent.class);
        Iterable<TelemetryEvent> event_list = Arrays.asList(new TelemetryEvent[]
                                              {te1, te2});
        SQLiteDAOTelemetry daoTel = new SQLiteDAOTelemetry(149, event_list);
        assertTrue(daoTel.id() == 149);
        Iterable<TelemetryEvent> ret_list = daoTel.getEvents();
        Iterator<TelemetryEvent> i_ret = ret_list.iterator();
        assertTrue(i_ret.next() == te1);
        assertTrue(i_ret.next() == te2);
    }

    /**
     * Verifica che il comportamento di equals risponda alla specifica.
     */
    @Test
    public void equalsShouldBehaveAsExpected() {
        List<TelemetryEvent> list1 = new ArrayList<>();
        List<TelemetryEvent> list2 = new ArrayList<>();
        List<TelemetryEvent> list3 = new ArrayList<>();
        list3.add(mock(TelemetryEvent.class));
        SQLiteDAOTelemetry t1 = new SQLiteDAOTelemetry(0, list1);
        SQLiteDAOTelemetry t2 = new SQLiteDAOTelemetry(0, list2);
        SQLiteDAOTelemetry t3 = new SQLiteDAOTelemetry(1, list1);
        SQLiteDAOTelemetry t4 = new SQLiteDAOTelemetry(0, list3);

        assertTrue(t1.equals(t2));
        assertTrue(!t1.equals(t3));
        assertTrue(!t1.equals(t4));
        assertTrue(!t1.equals(null));
        assertTrue(!t1.equals(new Object()));
    }

}
