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
 * Name: SerleenaDataSourceIntegrationTest.java
 * Package: com.kyloth.serleena.model;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.common.Quadrant;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.IRasterSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contiene test di integrazione per le classi di persistenza.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaDataSourceIntegrationTest {

    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;
    private IRasterSource rasterSource;

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);
        ContentValues contacts_1 = TestFixtures.pack(TestFixtures.CONTACTS_FIXTURE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, contacts_1);
        ContentValues contacts_2 = TestFixtures.pack(TestFixtures.CONTACTS_FIXTURE_2);
        db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, contacts_2);
        ContentValues weather_1 = TestFixtures.pack(TestFixtures.WEATHER_FIXTURE);
        db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, weather_1);
        ContentValues values_2;
        values_2 = new ContentValues();
        values_2.put("experience_name", "foo");
        ContentValues values = new ContentValues();
        values.put("raster_nw_corner_latitude", 5);
        values.put("raster_nw_corner_longitude", 0);
        values.put("raster_se_corner_latitude", 0);
        values.put("raster_se_corner_longitude", 5);
        values.put("raster_uuid", "asdlol");
        db.insertOrThrow(SerleenaDatabase.TABLE_RASTERS, null, values);
        // TODO: Sostituire con fixture
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values_2);
        rasterSource = mock(IRasterSource.class);
        serleenaSQLDS = new SerleenaSQLiteDataSource(
                RuntimeEnvironment.application,
                serleenaDB,
                rasterSource);
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
     * Verifica che il metodo getContacts resituisca la lista dei contatti di
     * emergenza nelle vicinanze di un punto geografico fornito.
     * Verifica inoltre che restituisca una lista vuota nel caso non ci fossero
     * contatti nelle vicinanze del punto fornito.
     */

    @Test
    public void testGetContacts() {
        Iterable<EmergencyContact> contacts = dataSource.getContacts(
                TestFixtures.CONTACTS_FIXTURE_POINT_INSIDE_BOTH
        );

        Iterator<EmergencyContact> i_contacts = contacts.iterator();
        String name1 = i_contacts.next().name();
        String name2 = i_contacts.next().name();
        assertTrue(
                (
                    name1.equals(TestFixtures.CONTACTS_FIXTURE_1_NAME)
                    && name2.equals(TestFixtures.CONTACTS_FIXTURE_2_NAME)
                )
                        ||
                (
                    name2.equals(TestFixtures.CONTACTS_FIXTURE_1_NAME)
                    && name1.equals(TestFixtures.CONTACTS_FIXTURE_2_NAME)
                )
        );
        assertFalse(i_contacts.hasNext());

        Iterable<EmergencyContact> void_contacts = dataSource.getContacts(TestFixtures.CONTACTS_FIXTURE_POINT_INSIDE_NEITHER);
        Iterator<EmergencyContact> i_void_contacts = void_contacts.iterator();
        assertFalse(i_void_contacts.hasNext());
    }

    /**
     * Verifica che il metodo getQuadrant restituisca correttamente i quadranti
     * relativi ai GeoPoint forniti come input (senza sollevare eccezioni).
     */
    @Test
    public void testGetQuadrant() throws NoSuchQuadrantException {
        Bitmap raster = mock(Bitmap.class);
        when(rasterSource.getRaster("asdlol")).thenReturn(raster);

        IQuadrant quadrant = dataSource.getQuadrant(new GeoPoint(5, 5));
        assertEquals(raster, quadrant.getRaster());
    }

    /**
     * Verifica che la richiesta di un quadrante non presente in database
     * sollevi un'eccezione NoSuchQuadrantException.
     */
    @Test(expected = NoSuchQuadrantException.class)
    public void testGetQuadrantThrows() throws NoSuchQuadrantException {
        dataSource.getQuadrant(new GeoPoint(10, 10));
    }

    /**
     * Verifica che il metodo getWeatherInfo restituisca correttamente
     * le informazioni meteo relative alla località e alla data fornite.
     */
    @Test
    public void testGetWeatherInfo() throws NoSuchWeatherForecastException {
        WeatherForecast forecast = (WeatherForecast)
                dataSource.getWeatherInfo(
                        TestFixtures.WEATHER_FIXTURE_POINT_INSIDE,
                        TestFixtures.WEATHER_FIXTURE_CAL.getTime()
                );
        assertTrue(forecast != null);
        assertTrue(forecast.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
    }

    /**
     * Verifica che il metodo getExperiences restituisca correttamente
     * la lista delle Esperienze salvate nel db.
     */
    @Test
    public void testGetExperiences() {
        Iterable<IExperience> experiences = dataSource.getExperiences();
        Iterator<IExperience> i_experiences = experiences.iterator();
        Experience experience = (Experience) i_experiences.next();
        assertTrue(experience.getName().equals("foo"));
    }

    /**
     * Verifica che il metodo equals() di Track restituisca risultati corretti.
     */
    @Test
    public void testTrackEquals() {
        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        SQLiteDatabase db = serleenaDb.getWritableDatabase();
        TestDB.experienceQuery(db, 0, "experience");
        TestDB.trackQuery(db, 0, "track", 0);
        TestDB.checkpointQuery(db, 0, 1, 5, 5, 0);
        TestDB.checkpointQuery(db, 1, 2, 6, 6, 0);
        TestDB.telemetryQuery(db, 0, 0);
        TestDB.telemetryQuery(db, 1, 0);
        TestDB.checkPointEventQuery(db, 0, 100, 1, 0);
        TestDB.checkPointEventQuery(db, 1, 200, 2, 0);
        TestDB.checkPointEventQuery(db, 2, 500, 1, 1);
        TestDB.checkPointEventQuery(db, 3, 600, 2, 1);
        // TODO: Cos'e'? Come fa a funzionare?

        SerleenaDataSource dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application,
                        serleenaDb,
                        mock(IRasterSource.class)));
        ITrack track1 = dataSource.getExperiences().iterator().next()
                .getTracks().iterator().next();
        ITrack track2 = dataSource.getExperiences().iterator().next()
                .getTracks().iterator().next();
        assertTrue(track1.equals(track2));
    }

}
