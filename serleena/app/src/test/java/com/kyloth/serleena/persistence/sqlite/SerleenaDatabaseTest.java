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
 * Name: SerleenaDatabaseTest.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0      Tobia Tesan        2015-05-05  Creazione file
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

/**
 * Contiene i test di unità per la classe SerleenaDatabaseTest
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaDatabaseTest {
	SerleenaDatabase sh;
	SQLiteDatabase db;
	ArrayList<String> basicStrings;
	ArrayList<String> nastyStrings;
	ArrayList<String> invalidStrings;

	@Test
	public void testGetReadableDatabase() throws Exception {
		db = sh.getReadableDatabase();
	}

	/*
	 * TABLE_EXPERIENCES
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un'Esperienza.
	 */
	@Test
	public void testAddExperience() {
		ContentValues values;

		ArrayList<String> names = new ArrayList<String>();
		names.addAll(basicStrings);
		names.addAll(nastyStrings);
		names.addAll(invalidStrings);

		for (String name : names) {
			values = new ContentValues();
			values.put("experience_name", name);
			db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
		}

	}

	/**
	 * Verifica che sia non sia possibile aggiungere un'Esperienza senza nome.
	 */
	@Test(expected = SQLException.class)
	public void testExperienceNoNameFails() {
		ContentValues values = (new ContentValues());
		values.put("experience_name", (String)null);
		db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
	}

	/*
	 * TABLE_TRACKS
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Percorso
	 */
	@Test
	public void testAddTrack() {
		ContentValues values;

		long id = makeExperience();

		ArrayList<String> names = new ArrayList<String>();

		names.addAll(basicStrings);
		names.addAll(nastyStrings);
		names.addAll(invalidStrings);

		for (String name : names) {
			values = new ContentValues();
			values.put("track_experience", id);
			values.put("track_name", name);
			db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
		}

	}

	/**
	 * Verifica che non sia possibile aggiungere un Percorso senza nome
	 */
	@Test(expected = SQLException.class)
	public void testTrackNoNameFails() {
		ContentValues values = (new ContentValues());
		values.put("track_name", (String) null);
		db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un percorso che fa riferimento a un'Esperiezna
	 * inesistente.
	 */
	@Test(expected = SQLException.class)
	public void testTrackWrongID() {
		long id = makeExperience();
		// Questo dovrebbe rompere l'integrita' referenziale?
		id += 123;
		ContentValues values;
		values = new ContentValues();
		values.put("track_experience", id);
		values.put("track_name", "bar");
		db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un percorso che non fa riferimento ad alcuna
	 * Esperienza
	 */
	@Test(expected = SQLException.class)
	public void testTrackNoExpFails() {
		ContentValues values = (new ContentValues());
		values.put("track_name", "foo");
		db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
	}

	/**
	 * Verifica che l'eliminazione di un'Esperienza elimini i suoi Percorsi.
	 */
	public void testTrackCascade() {
		ContentValues values;
		long id = makeExperience();
		values = new ContentValues();
		values.put("track_experience", id);
		values.put("track_name", "bar");
		db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
		db.delete(SerleenaDatabase.TABLE_EXPERIENCES, "id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_TRACKS,
								null,
								"track_experience = " + id,
								null,
								null,
								null,
								null);
		assertTrue(query.getCount() == 0);
	}

	/*
	 * TABLE_TELEMETRY
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Tracciamento.
	 */
	@Test
	public void testAddTelem() {
		ContentValues values;

		long id = makeTelemetry();
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", "asdfb");
		values.put("eventl_latitude", "1");
		values.put("eventl_longitude", "2");
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);

		values = new ContentValues();
		values.put("eventhc_telem", id);
		values.put("eventhc_timestamp", "asdfb");
		values.put("eventhc_value", "1");
		values.put("eventhc_type", "asdfasdf");
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Tracciamento senza Percorso.
	 */
	@Test(expected = SQLException.class)
	public void testTelemNoTrackFails() {
		ContentValues values = new ContentValues();
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
	}

	/*
	 * TABLE_TELEM_EVENTS_LOCATION
	 */

	/**
	 * Verifica che non sia possibile aggiungere un Tracciamento che fa riferimento a un
	 * Percorso inesistente.
	 */
	@Test(expected = SQLException.class)
	public void testLocationWrongTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventl_telem", 1234567890);
		values.put("eventl_timestamp", 1);
		values.put("eventl_latitude", 1);
		values.put("eventl_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
	}

	/**
	 * Verifica che l'eliminazione di un Tracciamento causi l'eliminazione dei suoi eventi location
	 */
	@Test(expected = SQLException.class)
	public void testLocationCascade() {
		ContentValues values = new ContentValues();
		long id = makeTrack();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", 1);
		values.put("eventl_latitude", 1);
		values.put("eventl_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
		db.delete(SerleenaDatabase.TABLE_TRACKS, "id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, "eventl_telem = " + id, null, null, null, null);
		assertTrue(query.getCount() == 0);
	}

	/**
	 * Verifica che non sia possibile aggiungere un evento Location che non fa riferimanto ad alcun
	 * Tracciamento.
	 */
	@Test(expected = SQLException.class)
	public void testLocationNullTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventl_timestamp", 1);
		values.put("eventl_latitude", 1);
		values.put("eventl_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
	}

	/**
	 * Verifica che non sia possibile inserire eventi Location con timestamp nullo.
	 */
	@Test(expected = SQLException.class)
	public void testLocationNullTimestampFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_latitude", 1);
		values.put("eventl_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
	}

	/**
	 * Verifica che non sia possibile inserire eventi Location con latitudine nulla.
	 */
	@Test(expected = SQLException.class)
	public void testLocationNullLatitudeFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", -1);
		values.put("eventl_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
	}

	/**
	 * Verifica che non sia possibile inserire eventi Location con longitudine nulla.
	 */
	@Test(expected = SQLException.class)
	public void testLocationNullLongitudeFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", -1);
		values.put("eventl_latitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);
	}

	/*
	 * TABLE_TELEM_EVENTS_HEART_CHECKP
	 */

	/**
	 * Verifica che non sia possibile aggiungere un evento Heart che non fa riferimanto ad alcun
	 * Tracciamento.
	 */
	@Test(expected = SQLException.class)
	public void testHeartWrongTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventl_telem", 1234567890);
		values.put("eventl_timestamp", 1);
		values.put("eventl_value", 1);
		values.put("eventl_type", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/**
	 * Verifica che l'eliminazione di un Tracciamento causi l'eliminazione dei suoi eventi Heart.
	 */
	@Test(expected = SQLException.class)
	public void testHeartCascade() {
		ContentValues values = new ContentValues();
		long id = makeTrack();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", 1);
		values.put("eventl_value", 1);
		values.put("eventl_type", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
		db.delete(SerleenaDatabase.TABLE_TRACKS, "id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, "eventl_telem = " + id, null, null, null, null);
		assertTrue(query.getCount() == 0);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi Heart che non fanno riferimento ad alcun
	 * Tracciamento.
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventl_timestamp", 1);
		values.put("eventl_value", 1);
		values.put("eventl_type", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi Heart senza timestamp
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullTimestampFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_value", 1);
		values.put("eventl_type", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi Heart senza discriminante
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullTypeFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", 1);
		values.put("eventl_value", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi Heart senza value
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullValueFails() {
		long id = makeTelemetry();
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", 1);
		values.put("eventl_type", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_HEART_CHECKP, null, values);
	}

	/*
	 * Util
	 */

	/**
	 * Metodo di utilita' per creare un'esperienza.
	 */
	private long makeExperience() {
		ContentValues values;
		values = new ContentValues();
		values.put("experience_name", "foo");
		return db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
	}

	/**
	 * Metodo di utilita' per creare un percorso in un DB vuoto.
	 * Crea automaticamente l'esperienza richiesta.
	 */
	private long makeTrack() {
		ContentValues values;
		values = new ContentValues();
		values.put("track_name", "foo");
		values.put("track_experience", makeExperience());
		return db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
	}

	/**
	 * Metodo di utilita' per creare un tracciamento in un DB vuoto.
	 * Crea automaticamente il percorso e l'esperienza richiesta.
	 */
	private long makeTelemetry() {
		ContentValues values;
		values = new ContentValues();
		values.put("telem_track", makeTrack());
		return db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
	}

	@Before
	public void setup() throws URISyntaxException {
		sh = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
		db = sh.getWritableDatabase();
		basicStrings = new ArrayList<String>();
		nastyStrings = new ArrayList<String>();
		invalidStrings = new ArrayList<String>();
		basicStrings.add("asdfghjkl");
		basicStrings.add("ASDFGHJKL");
		basicStrings.add("123456789");
		String long256 = "256CHARLONG 3456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012123456789012345678901234567890121234567890123456789012345678901212345678901234567890123456789012";
		assert(long256.length() == 256);
		nastyStrings.add(long256);
		String long512 = long256 + long256;
		assert(long512.length() == 512);
		nastyStrings.add(long512);
		invalidStrings.add("");
		invalidStrings.add("foo\"bar");
		invalidStrings.add("foo`bar");
		invalidStrings.add("\\\\\\");
	}

	@After
	public void tearDown() throws Exception {
		sh.close();
	}
}

