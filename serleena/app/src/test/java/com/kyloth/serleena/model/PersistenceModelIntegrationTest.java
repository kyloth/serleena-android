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


package com.kyloth.serleena.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Test di integrazione tra le classi del package persistence e quelle del
 * package model.
 *
 * Verifica che gli oggetti del business model vengano correttamente creati in
 * base al contenuto della parte di persistenza, basata su SQLite.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class PersistenceModelIntegrationTest {

    SQLiteDatabase db;
    SerleenaDataSource dataSource;

    private <T> boolean containsEquals(Iterable<T> elements, T element) {
        for (T t : elements)
            if (t.equals(element))
                return true;
        return false;
    }

    @Before
    public void initialize() {
        SerleenaDatabase serleenaDb = new SerleenaDatabase(
                RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDb.getWritableDatabase();
        serleenaDb.onConfigure(db);
        serleenaDb.onUpgrade(db, 1, 2);

        SerleenaSQLiteDataSource sqlDataSource =
                new SerleenaSQLiteDataSource(serleenaDb);
        dataSource = new SerleenaDataSource(sqlDataSource);
    }

    /**
     * Verifica che gli oggetti di classe Experience ritornino un nome
     * corrispondente a quanto presente nel database.
     */
    @Test
    public void experienceShouldReturnItsName() {
        ContentValues values;
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        Iterator<IExperience> iterator = dataSource.getExperiences().iterator();
        assertEquals(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME, iterator.next().getName());
    }

    /**
     * Verifica che gli oggetti di classe Experience ritornino un elenco di
     * punti utente assiciati ad essi corrispondente a quanto presente nel
     * database.
     */
    @Test
    public void experienceShouldReturnItsUserPoints() {
        ContentValues values;
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_2);
        db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);

        assertTrue(containsEquals(
                dataSource.getExperiences().iterator().next().getUserPoints(),
                new UserPoint(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LAT,
                        TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1_LON)));
        assertTrue(containsEquals(
                dataSource.getExperiences().iterator().next().getUserPoints(),
                new UserPoint(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_2_LAT,
                        TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_2_LON)));
    }

    /**
     * Verifica che nuovi punti utente vengano correttamente aggiunti ad oggetti
     * di tipo Experience, e che questi siano reperibili in successive
     * richieste sullo stesso oggetto.
     */
    @Test
    public void userPointShouldBeAddedCorrectly() {
        ContentValues values;
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        IExperience experience = dataSource.getExperiences().iterator().next();
        assertTrue(!experience.getUserPoints().iterator().hasNext());

        experience.addUserPoints(new UserPoint(3, 4));
        assertTrue(containsEquals(experience.getUserPoints(),
                new UserPoint(3, 4)));
    }

    /**
     * Verifica che gli eventi di Tracciamento associati a un particolare
     * Percorso corrispondano a quanto presente nel database.
     */
    @Test
    public void trackShouldReturnItsEvents() {
        ContentValues values;
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        TestDB.telemetryQuery(db, 1, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_UUID);
        TestDB.checkPointEventQuery(db, 0, 300, 1, 1);
        TestDB.checkPointEventQuery(db, 1, 300, 2, 1);

        IExperience experience = dataSource.getExperiences().iterator().next();
        ITrack track = experience.getTracks().iterator().next();
        ITelemetry telemetry = track.getTelemetries().iterator().next();

        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 1)));
        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 2)));
    }

    /**
     * Verifica che nuovi Tracciamenti vengano correttamente aggiunti ad oggetti
     * di tipo Track, e che questi siano reperibili in successive
     * richieste sullo stesso oggetto.
     */
    @Test
    public void createTelemetryShouldWorkCorrectly() {
        ContentValues values;
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);

        IExperience experience = dataSource.getExperiences().iterator().next();
        ITrack track = experience.getTracks().iterator().next();

        List<TelemetryEvent> list = new ArrayList<>();
        list.add(new CheckpointReachedTelemetryEvent(300, 1));
        list.add(new CheckpointReachedTelemetryEvent(300, 2));

        track.createTelemetry(list);

        ITelemetry telemetry = track.getTelemetries().iterator().next();

        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 1)));
        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 2)));
    }

}
