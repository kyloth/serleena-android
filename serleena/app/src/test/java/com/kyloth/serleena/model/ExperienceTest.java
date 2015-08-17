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
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import static org.mockito.Mockito.*;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;

/**
 * Contiene test per la classe Experience.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class ExperienceTest {
    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);
        ContentValues values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        serleenaSQLDS = new SerleenaSQLiteDataSource(serleenaDB);
        dataSource = new SerleenaDataSource(serleenaSQLDS);
    }

    /**
     * Chiude il database per permettere il funzionamento dei test successivi.
     */
    @After
    public void cleanUp() {
        serleenaDB.close();
    }

    /**
     * Verifica che il metodo getName restituisca il nome effettivo
     * delle Esperienze salvate nel db.
     */
    @Test
    public void testGetName() {
        Iterable<IExperience> experiences = dataSource.getExperiences();
        Iterator<IExperience> i_experiences = experiences.iterator();
        String name = i_experiences.next().getName();
        assertTrue(
                name.equals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME) ||
                name.equals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME)
        );
    }

    /**
     * Verifica che il metodo getUserPoints restituisca i corretti
     * Punti Utente per le diverse Esperienze.
     */
    @Test
    public void testGetUserPoints() {
        String insertUserPoints_1 = "INSERT INTO user_points " +
                "(userpoint_id, userpoint_x, userpoint_y, userpoint_experience) " +
                "VALUES (1, 2, 2, \"" + TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID + "\")";
        String insertUserPoints_2 = "INSERT INTO user_points " +
                "(userpoint_id, userpoint_x, userpoint_y, userpoint_experience) " +
                "VALUES (2, 3, 3, \""  + TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID + "\")";
        db.execSQL(insertUserPoints_1);
        db.execSQL(insertUserPoints_2);
        Iterable<IExperience> experiences = dataSource.getExperiences();
        Iterator<IExperience> i_experiences = experiences.iterator();
        Experience exp_1 = (Experience) i_experiences.next();
        Experience exp_2 = (Experience) i_experiences.next();
        Iterable<UserPoint> up_1 = exp_1.getUserPoints();
        Iterable<UserPoint> up_2 = exp_2.getUserPoints();
        Iterator<UserPoint> i_1 = up_1.iterator();
        Iterator<UserPoint> i_2 = up_2.iterator();
        assertFalse(i_2.hasNext());
        assertTrue(i_1.next().latitude() == 2);
        assertTrue(i_1.next().latitude() == 3);
    }

    /**
     * Verifica che il metodo addUserPoints aggiunga un Punto
     * Utente per l'Esperienza sul quale è chiamato.
     */
    @Test
    public void testAddUserPoints() {
        UserPoint up = new UserPoint(5, 5);
        Iterable<IExperience> experiences = dataSource.getExperiences();
        Experience exp = (Experience) experiences.iterator().next();
        exp.addUserPoints(up);
        Iterable<UserPoint> ups = exp.getUserPoints();
        assertTrue(up.equals(ups.iterator().next()));
    }

    /**
     * Verifica che il metodo getTracks restituisca correttamente
     * i Percorsi in base all'Esperienza sul quale è chiamato.
     */
    @Test
    public void testGetTracks() {
        // TODO
    }
}
