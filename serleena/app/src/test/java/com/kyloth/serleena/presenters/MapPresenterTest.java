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
 * Name: MapPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 * 2.0.0    Gabriele Pozzan  Aggiunta integrazione con gli altri package,
 *                                       incrementata copertura
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Iterator;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.sensors.SerleenaSensorManager;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;


/**
 * Contiene test per la classe MapPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class MapPresenterTest {

    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;

    int UPDATE_INTERVAL_SECONDS = 30;
    IMapView view;
    ISerleenaActivity activity;
    SerleenaSensorManager sm;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        view = mock(IMapView.class);
        activity = mock(ISerleenaActivity.class);
        sm = SerleenaSensorManager.getInstance(RuntimeEnvironment.application);
        when(activity.getSensorManager()).thenReturn(sm);

        serleenaDB = new SerleenaDatabase(
                RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);

        serleenaSQLDS = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, serleenaDB);
        dataSource = new SerleenaDataSource(serleenaSQLDS);
        when(activity.getDataSource()).thenReturn(dataSource);

        String insert_experience = "INSERT INTO experiences " +
            "(experience_id, experience_name) " +
            "VALUES (1, 'Experience')";
        String insert_ups = "INSERT INTO user_points " +
            "(userpoint_id, userpoint_x, userpoint_y, userpoint_experience) " +
            "VALUES (1, 5, 5, 1)";
        db.execSQL(insert_experience);
        db.execSQL(insert_ups);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null view"
     * se invocato con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        MapPresenter mp = new MapPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null activity"
     * se invocato con activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        MapPresenter mp = new MapPresenter(view, null);
    }

    /**
     * Verifica che il metodo newUserPoint lanci un'eccezione
     * di tipo NoActiveExperienceException se al momento
     * dell'invocazione non è attiva un'Esperienza.
     */

    @Test
    public void newUserPointShouldThrowExceptionWhenNoActiveExperience()
            throws NoActiveExperienceException {
        MapPresenter mp = new MapPresenter(view, activity);
        exception.expect(NoActiveExperienceException.class);
        mp.newUserPoint();
    }

    /**
     * Verifica che il metodo resume non sollevi eccezioni e in
     * generale non causi errori.
     */

    @Test
    public void testResume() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.resume();
    }

    /**
     * Verifica che il metodo pause non sollevi eccezioni e in generale
     * non causi errori.
     */

    @Test
    public void testPause() {
        MapPresenter mp = new MapPresenter(view, activity);
        mp.resume();
        mp.pause();
    }

    /**
     * Verifica che il metodo newUserPoint non causi errori o sollevi
     * eccezioni.
     */

    @Test
    public void testNewUserPoint() throws NoActiveExperienceException {
        MapPresenter mp = new MapPresenter(view, activity);
        Iterable<IExperience> experiences = dataSource.getExperiences();
        mp.setActiveExperience(experiences.iterator().next());
        mp.newUserPoint();
        mp.onLocationUpdate(new GeoPoint(5, 4));
        mp.newUserPoint();
    }

}
