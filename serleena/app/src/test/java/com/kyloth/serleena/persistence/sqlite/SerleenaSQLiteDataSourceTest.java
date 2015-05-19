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
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0      Tobia Tesan        2015-05-10  Creazione file
 */
package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Quadrant;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;
import java.util.GregorianCalendar;

import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeExperience;
import static com.kyloth.serleena.persistence.sqlite.SerleenaDatabaseTestUtils.makeTrack;
import static junit.framework.Assert.assertTrue;

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

	/**
	 * Verifica che il path dei raster sia != null, != ""
	 */
	@Test
	public void testGetRasterPathWellFormed() {
		assertTrue(sds.getRasterPath(0, 0) != null);
		assertTrue(sds.getRasterPath(0, 0).length() != 0);
	}

	/**
	 * Verifica che il path dei raster cambi al cambiare di I,J
	 */
	@Test
	public void testGetRasterPathAreDifferent() {
		assertTrue(sds.getRasterPath(0, 0) != sds.getRasterPath(1, 1));
	}

	/**
	 * Controlla che l'output di getIJ sia all'interno del range corretto.
	 */
	@Test
	public void testGetIJRange() {
		for (int x = -180; x < 180; x++) {
			for (int y = -90; y < 90; y++) {
				int[] ij = sds.getIJ(new GeoPoint(y, x));
				assertTrue(0 <= ij[0]);
				assertTrue(0 <= ij[1]);
				assertTrue(ij[0] <= SerleenaSQLiteDataSource.TOT_LAT_QUADRANTS);
				assertTrue(ij[1] <= SerleenaSQLiteDataSource.TOT_LONG_QUADRANTS);
			}
		}
	}

	/**
	 * Controlla che l'output di getIJ sia corretto alle estremita' del range
	 */
	@Test
	public void testGetIJTopBottom() {
		int[] ij = sds.getIJ(new GeoPoint(-90, -180));
		assertTrue(0 == ij[0]);
		assertTrue(0 == ij[1]);
		GeoPoint p = new GeoPoint(90.0 - SerleenaSQLiteDataSource.QUADRANT_LATSIZE / 2.0,
		                          180.0 - SerleenaSQLiteDataSource.QUADRANT_LONGSIZE / 2.0);
		ij = sds.getIJ(p);
		assertTrue(ij[0] == SerleenaSQLiteDataSource.TOT_LAT_QUADRANTS - 1);
		assertTrue(ij[1] == SerleenaSQLiteDataSource.TOT_LONG_QUADRANTS - 1);
	}

	/**
	 * Controlla che per un dato punto il quadrante sia quello atteso.
	 */
	@Test
	public void testGetQuadrantMidPoint() {
		GeoPoint p = new GeoPoint(-90.0 + SerleenaSQLiteDataSource.QUADRANT_LATSIZE / 2.0,
		                          -180.0 + SerleenaSQLiteDataSource.QUADRANT_LONGSIZE / 2.0);
		Quadrant q = (Quadrant) sds.getQuadrant(p);
		GeoPoint first = q.getNorthEastPoint();
		GeoPoint second = q.getSouthWestPoint();
		assertTrue(first.latitude() == 0);
		assertTrue(first.longitude() == 0);
		assertTrue(second.latitude() == SerleenaSQLiteDataSource.QUADRANT_LATSIZE);
		assertTrue(second.longitude() == SerleenaSQLiteDataSource.QUADRANT_LONGSIZE);
	}

	/**
	 * Controlla che per un dato punto al margine di un quadrante il quadrante sia
	 * uno tra i due possibili corretti.
	 */
	@Test
	public void testGetQuadrantEdgePoint() {
		GeoPoint p = new GeoPoint(-90.0 + SerleenaSQLiteDataSource.QUADRANT_LATSIZE,
		                          -180.0 + SerleenaSQLiteDataSource.QUADRANT_LONGSIZE);
		Quadrant q = (Quadrant) sds.getQuadrant(p);
		GeoPoint first = q.getNorthEastPoint();
		GeoPoint second = q.getSouthWestPoint();
		assertTrue(first.latitude() == 0
		           && second.latitude() == SerleenaSQLiteDataSource.QUADRANT_LATSIZE ||
		           first.latitude() == SerleenaSQLiteDataSource.QUADRANT_LATSIZE
		           && second.latitude() == 2 * SerleenaSQLiteDataSource.QUADRANT_LATSIZE);
		assertTrue(first.longitude() == 0
		           && second.longitude() == SerleenaSQLiteDataSource.QUADRANT_LONGSIZE ||
		           first.longitude() == SerleenaSQLiteDataSource.QUADRANT_LONGSIZE
		           && second.longitude() == 2 * SerleenaSQLiteDataSource.QUADRANT_LONGSIZE);
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
		values.put("eventl_timestamp", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 01, 00, 00)).getTimeInMillis() / 1000);
		values.put("eventl_latitude", 1.0);
		values.put("eventl_longitude", 1.0);
		values.put("eventl_telem", id);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);

		values = new ContentValues();
		values.put("eventl_timestamp", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 01, 00, 01)).getTimeInMillis() / 1000);
		values.put("eventl_latitude", 1.1);
		values.put("eventl_longitude", 1.1);
		values.put("eventl_telem", id);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);

		values = new ContentValues();
		values.put("eventl_timestamp", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 01, 00, 02)).getTimeInMillis() / 1000);
		values.put("eventl_latitude", 1.2);
		values.put("eventl_longitude", 1.2);
		values.put("eventl_telem", id);
		db.insertOrThrow(SerleenaDatabase.TABLE_TELEM_EVENTS_LOCATION, null, values);

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
	 * Controlla che getWeatherInfo restituisca correttamente le informazioni meteo
	 * dove presenti.
	 */
	@Test
	public void testGetWeatherInfoHit() {
		ContentValues values = new ContentValues();
		values.put("weather_start", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
		values.put("weather_end", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000);
		values.put("weather_condition", WeatherForecastEnum.Sunny.ordinal());
		values.put("weather_temperature", 1);
		values.put("weather_ne_corner_latitude", 0.0);
		values.put("weather_ne_corner_longitude", 0.0);
		values.put("weather_sw_corner_latitude", 2.0);
		values.put("weather_sw_corner_longitude", 2.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
		IWeatherStorage info = sds.getWeatherInfo(new GeoPoint(1.0, 1.0), (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime());
		assertTrue(info != null);
		assertTrue(info.getAfternoonForecast() == WeatherForecastEnum.Sunny);
	}


	/**
	 * Controlla che getWeatherInfo restituisca correttamente le informazioni meteo
	 * per i margini di una regione.
	 */
	@Test
	public void testGetWeatherInfoHitMargin() {
		ContentValues values = new ContentValues();
		values.put("weather_start", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
		values.put("weather_end", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000);
		values.put("weather_condition", WeatherForecastEnum.Sunny.ordinal());
		values.put("weather_temperature", 1);
		values.put("weather_ne_corner_latitude", 0.0);
		values.put("weather_ne_corner_longitude", 0.0);
		values.put("weather_sw_corner_latitude", 2.0);
		values.put("weather_sw_corner_longitude", 2.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
		IWeatherStorage info = sds.getWeatherInfo(new GeoPoint(0.0, 0.0), (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime());
		assertTrue(info != null);
		assertTrue(info.getAfternoonForecast() == WeatherForecastEnum.Sunny);
	}

	/**
	 * Controlla che getWeatherInfo non restituisca informazioni meteo
	 * della regione sbagliata.
	 */
	@Test
	public void testGetWeatherInfoMissRegion() {
		ContentValues values = new ContentValues();
		values.put("weather_start", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
		values.put("weather_end", (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000);
		values.put("weather_condition", WeatherForecastEnum.Sunny.ordinal());
		values.put("weather_temperature", 1);
		values.put("weather_ne_corner_latitude", 4.0);
		values.put("weather_ne_corner_longitude", 6.0);
		values.put("weather_sw_corner_latitude", 4.0);
		values.put("weather_sw_corner_longitude", 6.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
		IWeatherStorage info = sds.getWeatherInfo(new GeoPoint(1.0, 1.0), (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime());
		assertTrue(info == null);
	}

	/**
	 * Controlla che getWeatherInfo non restituisca informazioni meteo
	 * relatiove al frame temporale sbagliato.
	 */
	@Test
	public void testGetWeatherInfoMissTime() {
		ContentValues values = new ContentValues();
		values.put("weather_start", (new GregorianCalendar(2010, GregorianCalendar.JANUARY, 01, 00, 00, 00)).getTimeInMillis() / 1000);
		values.put("weather_end", (new GregorianCalendar(2010, GregorianCalendar.JANUARY, 01, 23, 59, 59)).getTimeInMillis() / 1000);
		values.put("weather_condition", WeatherForecastEnum.Sunny.ordinal());
		values.put("weather_temperature", 1);
		values.put("weather_ne_corner_latitude", 0.0);
		values.put("weather_ne_corner_longitude", 0.0);
		values.put("weather_sw_corner_latitude", 2.0);
		values.put("weather_sw_corner_longitude", 2.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_WEATHER_FORECASTS, null, values);
		IWeatherStorage info = sds.getWeatherInfo(new GeoPoint(1.0, 1.0), (new GregorianCalendar(2015, GregorianCalendar.JANUARY, 01)).getTime());
		assertTrue(info == null);
	}

	/**
	 * Controlla che getContacts restituisca correttamente
	 * le informazioni di contatto per la regione richiesta.
	 */
	@Test
	public void testGetContactsHit() {
		ContentValues values = new ContentValues();
		values.put("contact_name", "foo");
		values.put("contact_value", "asdfghj");
		values.put("contact_ne_corner_latitude", 0.0);
		values.put("contact_ne_corner_longitude", 0.0);
		values.put("contact_sw_corner_latitude", 2.0);
		values.put("contact_sw_corner_longitude", 2.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
		Iterable<EmergencyContact> contacts = sds.getContacts(new GeoPoint(1.0, 1.0));
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
		ContentValues values = new ContentValues();
		values.put("contact_name", "foo");
		values.put("contact_value", "asdfghj");
		values.put("contact_ne_corner_latitude", 0.0);
		values.put("contact_ne_corner_longitude", 0.0);
		values.put("contact_sw_corner_latitude", 2.0);
		values.put("contact_sw_corner_longitude", 2.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
		Iterable<EmergencyContact> contacts = sds.getContacts(new GeoPoint(0.0, 0.0));
		int i = 0;
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
		ContentValues values = new ContentValues();
		values.put("contact_name", "foo");
		values.put("contact_value", "asdfghj");
		values.put("contact_ne_corner_latitude", 10.0);
		values.put("contact_ne_corner_longitude", 10.0);
		values.put("contact_sw_corner_latitude", 20.0);
		values.put("contact_sw_corner_longitude", 20.0);
		db.insertOrThrow(SerleenaDatabase.TABLE_CONTACTS, null, values);
		Iterable<EmergencyContact> contacts = sds.getContacts(new GeoPoint(1.0, 1.0));
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
		sds = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, sh);
	}
}

