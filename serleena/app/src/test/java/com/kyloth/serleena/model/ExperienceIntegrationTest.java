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
 * Name: ExperienceIntegrationTest.java
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
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;

/**
 * Contiene test di integrazione per la classe Experience.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class ExperienceIntegrationTest {

    private SQLiteDatabase db;
    private SerleenaDatabase serleenaDB;
    private SerleenaDataSource dataSource;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */
    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, 1);
        db = serleenaDB.getWritableDatabase();
        dataSource = new SerleenaDataSource(new SerleenaSQLiteDataSource(serleenaDB));
        TestDB.experienceQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
    }

    /**
     * Verifica che il metodo getName restituisca il nome effettivo
     * delle Esperienze salvate nel db.
     */
    @Test
    public void testGetName() {
        IExperience experience = dataSource.getExperiences().iterator().next();
        assertEquals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME, experience.getName());
    }

    /**
     * Verifica che il metodo toString() restituisca il nome dell'Esperienza.
     */
    @Test
    public void toStringShouldReturnNameOfExperience() {
        IExperience experience = dataSource.getExperiences().iterator().next();
        assertEquals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME, experience.toString());
    }

    /**
     * Verifica che il metodo getUserPoints restituisca i corretti
     * Punti Utente per le diverse Esperienze.
     */
    @Test
    public void testGetUserPoints() {
        TestDB.userPointQuery(db, 0, 5, 5, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.userPointQuery(db, 1, 10, 10, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);

        IExperience experience = dataSource.getExperiences().iterator().next();
        Iterator<UserPoint> iterator = experience.getUserPoints().iterator();
        UserPoint up1 = iterator.next();
        UserPoint up2 = iterator.next();

        assertTrue(!iterator.hasNext());

        boolean b1, b2;
        b1 = up1.latitude() == 5 && up1.longitude() == 5 &&
                up2.latitude() == 10 && up2.longitude() == 10;
        b2 = up1.latitude() == 10 && up1.longitude() == 10 &&
                up2.latitude() == 5 && up2.longitude() == 5;
        assertTrue(b1 || b2);
    }

    /**
     * Verifica che il metodo addUserPoints aggiunga un Punto
     * Utente per l'Esperienza sul quale è chiamato.
     */
    @Test
    public void testAddUserPoints() {
        IExperience experience = dataSource.getExperiences().iterator().next();
        experience.addUserPoints(new UserPoint(5, 10));

        Iterator<UserPoint> iterator = experience.getUserPoints().iterator();
        UserPoint up = iterator.next();

        assertTrue(!iterator.hasNext());
        assertTrue(5.0 == up.latitude());
        assertTrue(10.0 == up.longitude());
    }

    /**
     * Verifica che il metodo getTracks restituisca correttamente
     * i Percorsi per una determinata Esperienza.
     */
    @Test
    public void testGetTracks() {
        TestDB.experienceQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_UUID, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME);
        TestDB.trackQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_UUID, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_NAME, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.trackQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_UUID, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1_NAME, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_UUID);

        IExperience experience = null;
        for (IExperience ex : dataSource.getExperiences())
            if (ex.getName().equals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME))
                experience = ex;

        Iterator<ITrack> iterator = experience.getTracks().iterator();
        ITrack track = iterator.next();
        assertTrue(!iterator.hasNext());
        assertEquals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_NAME, track.name());
    }

}
