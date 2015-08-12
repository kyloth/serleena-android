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
 * Name: SQLiteDAOExperienceTest.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0      Gabriele Pozzan  Creazione file, codice e javadoc
 */

package com.kyloth.serleena.persistence.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.ITrackStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Contiene test per la classe SQLiteDAOExperience.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SQLiteDAOExperienceTest {
    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;

    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);

        db.insertOrThrow(
                SerleenaDatabase.TABLE_EXPERIENCES,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1)
        );

        db.insertOrThrow(
                SerleenaDatabase.TABLE_EXPERIENCES,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2)
        );

        db.insertOrThrow(
                SerleenaDatabase.TABLE_TRACKS,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1)
        );

        db.insertOrThrow(
                SerleenaDatabase.TABLE_TRACKS,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2)
        );

        db.insertOrThrow(
                SerleenaDatabase.TABLE_TRACKS,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_TRACK_1)
        );

        db.insertOrThrow(
                SerleenaDatabase.TABLE_USER_POINTS,
                null,
                TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1)
        );

        serleenaSQLDS = new SerleenaSQLiteDataSource(
                RuntimeEnvironment.application,
                serleenaDB,
                mock(IRasterSource.class));
    }

    /**
     * Chiude il database per permettere il funzionamento dei test successivi.
     */

    @After
    public void cleanUp() {
        serleenaDB.close();
    }

    /**
     * Verifica la correttezza del costruttore e dei metodi getter
     * della classe.
     */

    @Test
    public void testConstructorAndGetters() {
        SQLiteDAOExperience daoExp = new SQLiteDAOExperience(
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME,
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_ID,
                serleenaSQLDS);
        assertEquals(
                daoExp.getName(),
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME
        );
        assertEquals(
                daoExp.id(),
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_ID
        );
    }

    /**
     * Verifica che il metodo getTracks restituisca correttamente
     * l'insieme dei percorsi collegati all'esperienza il cui id
     * è fornito alla costruzione di SQLiteDAOExperience.
     */

    @Test
    public void testGetTracks() {
        SQLiteDAOExperience daoExp = new SQLiteDAOExperience(
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME,
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_ID,
                serleenaSQLDS);
        Iterable<ITrackStorage> trackStorage = daoExp.getTracks();
        Iterator<ITrackStorage> i_trackStorage = trackStorage.iterator();
        SQLiteDAOTrack track_1 = (SQLiteDAOTrack) i_trackStorage.next();
        SQLiteDAOTrack track_2 = (SQLiteDAOTrack) i_trackStorage.next();
        assertFalse(i_trackStorage.hasNext());
        assertTrue(
                (track_1.id() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_ID &&
                 track_2.id() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_ID) ||
                (track_1.id() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_2_ID &&
                 track_2.id() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_ID)
                );
    }

    /**
     * Verifica che il metodo getUserPoints restituisca correttamente
     * i punti utente associati all'esperienza il cui id è fornito
     * alla costruzione di SQLiteDAOExperience.
     */

    @Test
    public void testGetUserPoints() {
        SQLiteDAOExperience daoExp = new SQLiteDAOExperience(
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME,
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_ID,
                serleenaSQLDS);
        Iterable<UserPoint> ups = daoExp.getUserPoints();
        Iterator<UserPoint> i_ups = ups.iterator();
        UserPoint up = i_ups.next();
        assertTrue(up.latitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT);
        assertTrue(up.longitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON);
    }

    /**
     * Verifica che il metodo addUserPoint inserisca correttamente
     * un nuovo punto utente associato all'esperienza il cui id
     * è fornito alla costruzione di SQLiteDAOExperience.
     */

    @Test
    public void testAddUserPoint() {
        SQLiteDAOExperience daoExp = new SQLiteDAOExperience(
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME,
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_ID,
                serleenaSQLDS);
        UserPoint up_1 = new UserPoint(44, 59);
        daoExp.addUserPoint(up_1);
        Iterable<UserPoint> ups = daoExp.getUserPoints();
        Iterator<UserPoint> i_ups = ups.iterator();
        UserPoint ups_1 = i_ups.next();
        UserPoint ups_2 = i_ups.next();
        assertTrue(
                (
                    (ups_1.latitude() == 44
                            &&
                            ups_1.longitude() == 59) &&
                    (ups_2.latitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT
                            &&
                            ups_2.longitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON)
                )
                        ||
                (
                    (ups_2.latitude() == 44
                            &&
                            ups_2.longitude() == 59) &&
                    (ups_1.latitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT
                            &&
                            ups_1.longitude() == TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON)
                )
        );
    }
}
