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
 *
 * History:
 * Version  Programmer   Changes
 * 1.0      Tobia Tesan  Creazione file
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

import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeExperience;
import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeTelemetry;
import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeTrack;
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

		long id = makeExperience(db);

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
		long id = makeExperience(db);
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
	@Test
	public void testTrackCascade() {
		ContentValues values;
		long id = makeExperience(db);
		values = new ContentValues();
		values.put("track_experience", id);
		values.put("track_name", "bar");
		db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
		db.delete(SerleenaDatabase.TABLE_EXPERIENCES, "experience_id = " + id, null);
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

		long id = makeTelemetry(db);
		values = new ContentValues();
		values.put("eventc_telem", id);
		values.put("eventc_timestamp", 300);
		values.put("eventc_value", "1");
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
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
	public void testCheckpointWrongTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventc_telem", 2342342);
		values.put("eventc_timestamp", 300);
		values.put("eventc_value", "1");
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un evento checkpoint che non
	 * fa riferimanto ad alcun Tracciamento.
	 */
	@Test(expected = SQLException.class)
	public void testHeartWrongTelemFails() {
		ContentValues values = new ContentValues();
		values.put("eventc_telem", 1234567890);
		values.put("eventc_timestamp", 1);
		values.put("eventc_value", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
	}

	/**
	 * Verifica che l'eliminazione di un Tracciamento causi l'eliminazione dei
	 * suoi eventi checkpoint.
	 */
	@Test(expected = SQLException.class)
	public void testHeartCascade() {
		ContentValues values = new ContentValues();
		long id = makeTrack(db);
		values.put("eventc_telem", id);
		values.put("eventc_timestamp", 1);
		values.put("eventc_value", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
		db.delete(SerleenaDatabase.TABLE_TRACKS, "track_id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null,
				"eventc_telem = " + id, null, null, null, null);
		assertTrue(query.getCount() == 0);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi checkpoint senza
	 * timestamp.
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullTimestampFails() {
		long id = makeTelemetry(db);
		ContentValues values;
		values = new ContentValues();
		values.put("eventc_telem", id);
		values.put("eventc_value", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere eventi checkpoint senza value.
	 */
	@Test(expected = SQLException.class)
	public void testHeartNullValueFails() {
		long id = makeTelemetry(db);
		ContentValues values;
		values = new ContentValues();
		values.put("eventl_telem", id);
		values.put("eventl_timestamp", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP, null, values);
	}

	/*
	 * TABLE_CONTACTS
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Contatto.
	 */
	@Test
	public void testAddContact() {
		ContentValues values;
		values = new ContentValues();
		ArrayList<String> names = new ArrayList<String>();

		names.addAll(basicStrings);
		names.addAll(nastyStrings);
		names.addAll(invalidStrings);

		for (String name : names) {
			for (String contact : names) {
				values.put("contact_name", name);
				values.put("contact_value", contact);
				values.put("contact_nw_corner_latitude", 1);
				values.put("contact_nw_corner_longitude", 1);
				values.put("contact_se_corner_latitude", 1);
				values.put("contact_se_corner_longitude", 1);
				db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
			}
		}
	}

	/**
	 * Verifica che non sia possibile aggiungere un Contatto senza nome.
	 */
	@Test(expected = SQLException.class)
	public void testContactNullNameFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("contact_value", "foo");
		values.put("contact_nw_corner_latitude", 1);
		values.put("contact_nw_corner_longitude", 1);
		values.put("contact_se_corner_latitude", 1);
		values.put("contact_se_corner_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Contatto senza value.
	 */
	@Test(expected = SQLException.class)
	public void testContactNullValueFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("contact_name", "foo");
		values.put("contact_nw_corner_latitude", 1);
		values.put("contact_nw_corner_longitude", 1);
		values.put("contact_se_corner_latitude", 1);
		values.put("contact_se_corner_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
	}

	/*
	 * WEATHER_FORECASTS
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Forecast.
	 */
	@Test
	public void testAddForecast() {
		ContentValues values;
		values = new ContentValues();
		ArrayList<String> strings = new ArrayList<String>();

		strings.addAll(basicStrings);
		strings.addAll(nastyStrings);
		strings.addAll(invalidStrings);

		for (String value : strings) {
			values.put("weather_condition_morning", value);
			values.put("weather_temperature_morning", 1);
			values.put("weather_condition_afternoon", value);
			values.put("weather_temperature_afternoon", 1);
			values.put("weather_condition_night", value);
			values.put("weather_temperature_night", 1);
			values.put("weather_date", 1);
			values.put("weather_nw_corner_latitude", 1);
			values.put("weather_nw_corner_longitude", 1);
			values.put("weather_se_corner_latitude", 1);
			values.put("weather_se_corner_longitude", 1);
		}
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Forecast con attributo start nullo.
	 */
	@Test(expected = SQLException.class)
	public void testForecastNullStartFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("weather_condition", "foo");
		values.put("weather_temperature", 1);
		values.put("weather_end", 1);
		values.put("weather_nw_corner_latitude", 1);
		values.put("weather_nw_corner_longitude", 1);
		values.put("weather_se_corner_latitude", 1);
		values.put("weather_se_corner_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Forecast con attributo end nullo.
	 */
	@Test(expected = SQLException.class)
	public void testForecastNullEndFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("weather_condition", "foo");
		values.put("weather_temperature", 1);
		values.put("weather_start", 1);
		values.put("weather_nw_corner_latitude", 1);
		values.put("weather_nw_corner_longitude", 1);
		values.put("weather_se_corner_latitude", 1);
		values.put("weather_se_corner_longitude", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
	}

	/*
	 * TABLE_USER_POINTS
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Punto Utente
	 */
	@Test
	public void testAddUserPoint() {
		ContentValues values;
		values = new ContentValues();
		values.put("userpoint_experience", makeExperience(db));
		values.put("userpoint_x", 1);
		values.put("userpoint_y", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Punto Utente senza Esperienza.
	 */
	@Test(expected = SQLException.class)
	public void testUserPointNoExperienceFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("userpoint_x", 1);
		values.put("userpoint_y", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Punto Utente che fa riferimetno ad una
	 * Esperienza inesistente.
	 */
	@Test(expected = SQLException.class)
	public void testUserPointWrongExperienceFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("userpoint_experience", 12345);
		values.put("userpoint_x", 1);
		values.put("userpoint_y", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Punto Utente che fa riferimento ad una
	 * Esperienza inesistente.
	 */
	@Test
	public void testUserPointCascade() {
		ContentValues values;
		long id = makeExperience(db);
		values = new ContentValues();
		values.put("userpoint_experience", id);
		values.put("userpoint_x", 1);
		values.put("userpoint_y", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
		db.delete(SerleenaDatabase.TABLE_EXPERIENCES, "experience_id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_USER_POINTS, null, "userpoint_experience = " + id, null, null, null, null);
		assertTrue(query.getCount() == 0);
	}

	/*
	 * TABLE_CHECKPOINTS
	 */

	/**
	 * Verifica che sia possibile aggiungere correttamente un Checkpoint.
	 */
	@Test
	public void testAddCheckpoint() {
		ContentValues values;
		values = new ContentValues();
		values.put("checkpoint_latitude", 1);
		values.put("checkpoint_longitude", 1);
		values.put("checkpoint_num", 1);
		values.put("checkpoint_track", makeTrack(db));
		db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Checkpoint senza Percorso.
	 */
	@Test(expected = SQLException.class)
	public void testCheckpointNoTrackFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("checkpoint_latitude", 1);
		values.put("checkpoint_longitude", 1);
		values.put("checkpoint_num", 1);
		db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
	}

	/**
	 * Verifica che non sia possibile aggiungere un Checkpoint che fa riferimento
	 * a un Percorso inesistente.
	 */
	@Test(expected = SQLException.class)
	public void testCheckpointWrongTrackFails() {
		ContentValues values;
		values = new ContentValues();
		values.put("checkpoint_latitude", 1);
		values.put("checkpoint_longitude", 1);
		values.put("checkpoint_num", 1);
		values.put("checkpoint_track", 12345);
		db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
	}

	/**
	 * Verifica che l'eliminazione di un Percorso ne elimini i Checkpoint.
	 */
	@Test
	public void testCheckpointCascade() {
		ContentValues values;
		values = new ContentValues();
		long id = makeTrack(db);
		values.put("checkpoint_latitude", 1);
		values.put("checkpoint_longitude", 1);
		values.put("checkpoint_num", 1);
		values.put("checkpoint_track", id);
		db.insertOrThrow(SerleenaDatabase.TABLE_CHECKPOINTS, null, values);
		db.delete(SerleenaDatabase.TABLE_TRACKS, "track_id = " + id, null);
		Cursor query = db.query(SerleenaDatabase.TABLE_CHECKPOINTS, null, "checkpoint_track = " + id, null, null, null, null);
		assertTrue(query.getCount() == 0);
	}

	/*
	 * Util
	 */

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

