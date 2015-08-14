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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.Quadrant;
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

import java.io.File;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;

import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeExperience;
import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeTrack;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Suite di test per SerleenaSQLiteDataSource
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaSQLiteDataSourceTest {

    SerleenaSQLiteDataSource sds;
    SQLiteDatabase db;
    private IRasterSource rasterSource;

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
        Bitmap raster = putQuadrant(2, 0, 0, 2, "asdlol");

        assertTrue(false);
        IQuadrant quadrant = sds.getQuadrant(new GeoPoint(1, 1));
        assertEquals(2.0, quadrant.getNorthWestPoint().latitude());
        assertEquals(0.0, quadrant.getNorthWestPoint().longitude());
        assertEquals(0.0, quadrant.getSouthEastPoint().latitude());
        assertEquals(2.0, quadrant.getSouthEastPoint().longitude());
        assertEquals(raster, quadrant.getRaster());
    }

    /**
     * Verifica che la richiesta di un quadrante non presente nel database
     * sollevi un'eccezione NoSuchQuadrantException.
     */
    @Test(expected = NoSuchQuadrantException.class)
    public void queryingNonexistentQuadrantShouldThrow()
            throws NoSuchQuadrantException {
        sds.getQuadrant(new GeoPoint(1, 1));
    }

    /**
     * Controlla che per un dato punto al margine di un quadrante il quadrante sia
     * uno tra i due possibili corretti.
     */
    @Test
    public void testGetQuadrantEdgePoint()
            throws NoSuchQuadrantException {
        assertTrue(false);
        Bitmap b1 = putQuadrant(5, 0, 0, 5, "asd");
        Bitmap b2 = putQuadrant(10, 5, 5, 10, "lol");
        Bitmap b3 = putQuadrant(10, 0, 5, 5, "qwe");
        Bitmap b4 = putQuadrant(5, 5, 0, 10, "rty");
        IQuadrant quadrant = sds.getQuadrant(new GeoPoint(5, 5));
        assertTrue(
                quadrant.getRaster() == b1 ||
                quadrant.getRaster() == b2 ||
                quadrant.getRaster() == b3 ||
                quadrant.getRaster() == b4);
    }

    private Bitmap putQuadrant(double nwLat, double nwLon, double seLat,
                             double seLon, String uuid) {
        ContentValues values = new ContentValues();
        values.put("raster_nw_corner_latitude", nwLat);
        values.put("raster_nw_corner_longitude", nwLon);
        values.put("raster_se_corner_latitude", seLat);
        values.put("raster_se_corner_longitude", seLon);
        values.put("raster_uuid", uuid);
        db.insertOrThrow(SerleenaDatabase.TABLE_RASTERS, null, values);
        Bitmap raster = mock(Bitmap.class);
        when(rasterSource.getRaster(uuid)).thenReturn(raster);
        return raster;
    }

    /**
     * Controlla che sia possibile ottenere correttamente i Percorsi per
     * un'Esperienza
     */
    @Test
    public void testGetTracks() {
        long id = makeExperience(db);
        ContentValues values = new ContentValues();
        values.put("track_name", "bar");
        values.put("track_experience", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        Iterable<SQLiteDAOTrack> trax = sds.getTracks((SQLiteDAOExperience) exp);
    }

    /**
     * Controlla che un'Esperienza cancellata non abbia Percorsi
     */
    @Test
    public void testNoTracks() {
        long id = makeExperience(db);
        ContentValues values = new ContentValues();
        values.put("track_name", "bar");
        values.put("track_experience", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        String whereClause = "experience_id = " + ((SQLiteDAOExperience)exp).id();
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
        long id = makeExperience(db);
        ContentValues values = new ContentValues();
        values.put("track_name", "bar");
        values.put("track_experience", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        Iterable<IExperienceStorage> exps = sds.getExperiences();
        IExperienceStorage exp = exps.iterator().next();
        String whereClause = "experience_id = " + ((SQLiteDAOExperience)exp).id();
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
        long id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("checkpoint_num", 1);
        values.put("checkpoint_latitude", 0.000);
        values.put("checkpoint_longitude", 0.000);
        values.put("checkpoint_track", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
        values.put("checkpoint_num", 2);
        values.put("checkpoint_latitude", 1.000);
        values.put("checkpoint_longitude", 1.000);
        values.put("checkpoint_track", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
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
        long id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("telem_track", id);
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
        long id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("telem_track", id);
        id = db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 100);
        values.put("eventc_value", 0);
        values.put("eventc_telem", id);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 200);
        values.put("eventc_value", 1);
        values.put("eventc_telem", id);
        db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);

        values = new ContentValues();
        values.put("eventc_timestamp", 300);
        values.put("eventc_value", 2);
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
        long id = makeTrack(db);
        ContentValues values = new ContentValues();
        values.put("telem_track", id);
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

    @Before
    public void setup() throws URISyntaxException {
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, null, null, 1);
        db = sh.getWritableDatabase();
        rasterSource = mock(IRasterSource.class);
        sds = new SerleenaSQLiteDataSource(sh);
    }
}

