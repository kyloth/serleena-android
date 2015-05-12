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

