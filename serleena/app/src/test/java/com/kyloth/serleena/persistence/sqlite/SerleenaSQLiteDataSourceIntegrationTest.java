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
 * Name: SerleenaSQLiteDataSourceTest.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version  Programmer   Changes
 * 1.0      Tobia Tesan  Creazione file
 */
package com.kyloth.serleena.persistence.sqlite;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.Region;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;
import java.util.UUID;

import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeExperience;
import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeTrack;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Test di integrazione tra la classe SerleenaSQLiteDataSource e il database
 * SQLite dell'applicazione, rappresentato dalla classe SerleenaDatabase.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaSQLiteDataSourceIntegrationTest {

    private SerleenaSQLiteDataSource sds;
    private SQLiteDatabase db;
    private Bitmap testBitmap;
    private String testBase64;

    @Before
    public void setup() throws URISyntaxException {
        Application app = RuntimeEnvironment.application;
        SerleenaDatabase sh = new SerleenaDatabase(app, null, null, 1);
        db = sh.getWritableDatabase();
        sds = new SerleenaSQLiteDataSource(sh);

        testBase64 = "asdfghjklqwertyuiopzxcvbnm";
        byte[] data = Base64.decode(testBase64, Base64.DEFAULT);
        testBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione
     * IllegalArgumentException se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfArgumentIsNull() {
        new SerleenaSQLiteDataSource(null);
    }

    /**
     * Controlla che per un dato punto il quadrante sia quello atteso.
     */
    @Test
    public void testGetQuadrant() throws NoSuchQuadrantException {
        ContentValues exp = new ContentValues();
        exp.put("experience_uuid", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID.toString());
        exp.put("experience_name", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, exp);
        SQLiteDAOExperience expp = new SQLiteDAOExperience(
                "experience",
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID,
                sds
        );

        TestDB.quadrantQuery(db, 2, 0, 0, 2, testBase64, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);

        IQuadrant quadrant = sds.getQuadrant(new GeoPoint(1, 1), expp);
        assertEquals(2.0, quadrant.getNorthWestPoint().latitude());
        assertEquals(0.0, quadrant.getNorthWestPoint().longitude());
        assertEquals(0.0, quadrant.getSouthEastPoint().latitude());
        assertEquals(2.0, quadrant.getSouthEastPoint().longitude());
        assertTrue(TestDB.bitmapEquals(testBitmap, quadrant.getRaster()));
    }

    /**
     * Verifica che la richiesta di un quadrante non presente nel database
     * sollevi un'eccezione NoSuchQuadrantException.
     */
    @Test(expected = NoSuchQuadrantException.class)
    public void queryingNonexistentQuadrantShouldThrow()
            throws NoSuchQuadrantException {
        ContentValues exp = new ContentValues();
        exp.put("experience_uuid", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID.toString());
        exp.put("experience_name", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
        long expId =
                db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, exp);
        SQLiteDAOExperience expp = new SQLiteDAOExperience(
                "experience", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID, sds);
        sds.getQuadrant(new GeoPoint(1, 1), expp);
    }

    /**
     * Controlla che per un dato punto al margine di un quadrante il quadrante
     * sia uno tra i due possibili corretti.
     */
    @Test
    public void testGetQuadrantEdgePoint()
            throws NoSuchQuadrantException {
        ContentValues exp = new ContentValues();
        exp.put("experience_name", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
        exp.put("experience_uuid", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID.toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, exp);
        SQLiteDAOExperience expp = new SQLiteDAOExperience(
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME,
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID,
                sds);
        TestDB.quadrantQuery(db, 5, 0, 0, 5, "asd", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 10, 5, 5, 10, "lol", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 10, 0, 5, 5, "qwe", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 5, 5, 0, 10, "rty", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        IQuadrant quadrant = sds.getQuadrant(new GeoPoint(5, 5), expp);
        assertTrue(
                TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(5, 0), new GeoPoint(0, 5))) ||
                TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(10, 5), new GeoPoint(5, 10))) ||
                TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(10, 0), new GeoPoint(5, 5))) ||
                TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(5, 5), new GeoPoint(0, 10))));
    }

    /**
     * Controlla che sia possibile ottenere correttamente i Percorsi per
     * un'Esperienza
     */
    @Test
    public void testGetTracks() {
        ContentValues values = new ContentValues();
        values.put("track_uuid", UUID.randomUUID().toString());
        values.put("track_name", "bar");
        values.put("track_experience", makeExperience(db).toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks((SQLiteDAOExperience) exp);
        assertTrue(trax.iterator().hasNext());
    }

    /**
     * Controlla che un'Esperienza cancellata non abbia Percorsi
     */
    @Test
    public void testNoTracks() {
        ContentValues values = new ContentValues();
        values.put("track_uuid", UUID.randomUUID().toString());
        values.put("track_name", "bar");
        values.put("track_experience", makeExperience(db).toString());
        long id = db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        String whereClause = "experience_uuid = \"" + ((SQLiteDAOExperience)exp).getUUID() + "\"";
        db.delete(SerleenaDatabase.TABLE_EXPERIENCES, whereClause, null);
        Iterable<SQLiteDAOTrack> trax = sds.getTracks((SQLiteDAOExperience) exp);
        int i = 0;
        for (SQLiteDAOTrack track : trax) {
            i++;
        }
        assertTrue(i == 0);
    }

    /**
     * Controlla che un'Esperienza cancellata non abbia Percorsi
     */
    @Test
    public void testExperienceSlippage() {
        ContentValues values = new ContentValues();
        values.put("track_uuid", UUID.randomUUID().toString());
        values.put("track_name", "bar");
        values.put("track_experience", makeExperience(db).toString());
        long id = db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        String whereClause = "experience_uuid = \"" + ((SQLiteDAOExperience)exp).getUUID().toString() + "\"";
        Iterable<SQLiteDAOTrack> trax = sds.getTracks((SQLiteDAOExperience) exp);
        db.delete(SerleenaDatabase.TABLE_EXPERIENCES, whereClause, null);
        int i = 0;
        for (SQLiteDAOTrack track : trax) {
            i++;
        }
        assertTrue(i == 1);
    }

    /**
     * Controlla che getCheckpoints restituisca correttamente i checkpoint.
     */
    @Test
    public void testGetCheckpoints() {
        UUID id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("checkpoint_num", 1);
        values.put("checkpoint_latitude", 0.000);
        values.put("checkpoint_longitude", 0.000);
        values.put("checkpoint_track", id.toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
        values.put("checkpoint_num", 2);
        values.put("checkpoint_latitude", 1.000);
        values.put("checkpoint_longitude", 1.000);
        values.put("checkpoint_track", id.toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        SQLiteDAOExperience exp = (SQLiteDAOExperience) exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks(exp);
        SQLiteDAOTrack track = trax.iterator().next();
        assertTrue(track.getCheckpoints().size() == 2);
    }

    /**
     * Controll che getTelemetries restituisca correttamente i Tracciamenti.
     */
    @Test
    public void testGetTelemetries() {
        ContentValues values = new ContentValues();
        values.put("telem_track", makeTrack(db).toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        SQLiteDAOExperience exp = (SQLiteDAOExperience) exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks(exp);
        SQLiteDAOTrack track = trax.iterator().next();
        Iterable<ITelemetryStorage> telemetries = track.getTelemetries();
        int i = 0;
        for (ITelemetryStorage telem : telemetries) {
            i++;
        }
        assertTrue(i == 2);
    }

    /**
     * Controlla che getEvents restituisca correttamente gli Eventi del
     * Tracciamento
     */
    @Test
    public void testGetTelemetryEvents() {
        ContentValues values = new ContentValues();
        values.put("telem_track", makeTrack(db).toString());
        long id = db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 100);
        values.put("eventc_value", 1);
        values.put("eventc_telem", id);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 200);
        values.put("eventc_value", 2);
        values.put("eventc_telem", id);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 300);
        values.put("eventc_value", 3);
        values.put("eventc_telem", id);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);

        Iterable<IExperienceStorage> exps = sds.getExperiences();
        SQLiteDAOExperience exp = (SQLiteDAOExperience) exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks(exp);
        SQLiteDAOTrack track = trax.iterator().next();
        Iterable<ITelemetryStorage> telemetries = track.getTelemetries();
        ITelemetryStorage telem = telemetries.iterator().next();
        int i = 0;
        for (TelemetryEvent event : telem.getEvents()) {
            i++;
        }
        assertTrue(i == 3);
    }

    /**
     * Controlla che addUserPoint aggiunga i punti utente.
     */
    @Test
    public void testAddUserPoint() {
        UUID id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("telem_track", id.toString());
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        SQLiteDAOExperience exp = (SQLiteDAOExperience) exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks(exp);
        SQLiteDAOTrack track = trax.iterator().next();
        Iterable<ITelemetryStorage> telemetries = track.getTelemetries();
        int i = 0;
        for (ITelemetryStorage telem : telemetries) {
            i++;
        }
        assert(i == 2);
    }

    /**
     * Controlla che getExperience restituisca correttamente le esperienze.
     */
    @Test
    public void testGetExperiences() {
        ContentValues values;

        makeExperience(db);

        Iterable<IExperienceStorage> exps = sds.getExperiences();
        int i = 0;
        for (IExperienceStorage exp : exps) {
            i++;
        }
        assertTrue(i == 1);
    }

    /**
     * Controlla che getContacts restituisca correttamente
     * le informazioni di contatto per la regione richiesta.
     */
    @Test
    public void testGetContactsHit() {
        ContentValues values = TestFixtures.pack(TestFixtures.CONTACTS_FIXTURE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
        Iterable<EmergencyContact> contacts = sds.getContacts(
                TestFixtures.CONTACTS_FIXTURE_POINT_INSIDE_BOTH
        );
        int i = 0;
        for (EmergencyContact contact : contacts) {
            i++;
        }
        assertTrue(i == 1);
    }

    /**
     * Controlla che getContacts restituisca correttamente
     * le informazioni di contatto per i margini di una regione.
     */
    @Test
    public void testGetContactsHitMargin() {
        ContentValues values = TestFixtures.pack(TestFixtures.CONTACTS_FIXTURE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);

        Iterable<EmergencyContact> contacts;
        int i;

        contacts = sds.getContacts(TestFixtures.CONTACTS_FIXTURE_1_NW_CORNER);
        i = 0;
        for (EmergencyContact contact : contacts) {
            i++;
        }
        assertTrue(i == 1);

        contacts = sds.getContacts(TestFixtures.CONTACTS_FIXTURE_1_SE_CORNER);
        i = 0;
        for (EmergencyContact contact : contacts) {
            i++;
        }
        assertTrue(i == 1);

        contacts = sds.getContacts(
                new GeoPoint(
                        TestFixtures.CONTACTS_FIXTURE_1_NW_CORNER.latitude(),
                        TestFixtures.CONTACTS_FIXTURE_1_SE_CORNER.longitude()
                )
        );
        i = 0;
        for (EmergencyContact contact : contacts) {
            i++;
        }
        assertTrue(i == 1);

    }

    /**
     * Controlla che getContacts non restituisca infomazioni di contatto
     * per la regione sbagliata.
     */
    @Test
    public void testGetContactsMiss() {
        ContentValues values = TestFixtures.pack(TestFixtures.CONTACTS_FIXTURE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
        Iterable<EmergencyContact> contacts = sds.getContacts(
                TestFixtures.CONTACTS_FIXTURE_POINT_INSIDE_NEITHER
        );
        int i = 0;
        for (EmergencyContact contact : contacts) {
            i++;
        }
        assertTrue(i == 0);
    }

}

