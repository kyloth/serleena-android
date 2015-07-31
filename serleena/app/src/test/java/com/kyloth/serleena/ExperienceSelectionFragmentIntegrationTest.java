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
 * Name: ExperienceSelectionFragmentIntegrationTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.IExperienceActivationObserver;
import com.kyloth.serleena.presentation.IExperienceActivationSource;
import com.kyloth.serleena.presentation.ITrackSelectionView;
import com.kyloth.serleena.presenters.ExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presenters.TrackSelectionPresenter;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.view.fragments.ExperienceSelectionFragment;
import com.kyloth.serleena.view.fragments.TrackSelectionFragment;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Contiene i test di integrazione per la classe ExperienceSelectionFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.ExperienceSelectionFragment
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ExperienceSelectionFragmentIntegrationTest {

    /**
     * Activity che permette di fornire un datasource impostato ad hoc per i test.
     */
    private static class CustomDataSourceActivity
            extends SerleenaActivity {
        private SerleenaDataSource ds;

        public void setDataSource (SerleenaDataSource dataSource) {
            ds = dataSource;
        }

        @Override
        public ISerleenaDataSource getDataSource() {
            return ds;
        }
    }

    /**
     * Presenter che fornisce un metodo per verificare facilmente se a questo viene notificata
     * correttamente l'attivazione di un'esperienza.
     */
    private class TestingTrackSelection extends TrackSelectionPresenter {
        private String name;

        public TestingTrackSelection(ITrackSelectionView view, ISerleenaActivity activity,
                                     IExperienceActivationSource source) {
            super(view, activity, source);
        }
        @Override
        public void onExperienceActivated(IExperience experience) {
            super.onExperienceActivated(experience);
            name = experience.getName();
        }
        public String getName() { return name; }
    }

    private ExperienceSelectionFragment fragment;
    private TrackSelectionFragment trackFragment;
    private ExperienceSelectionPresenter presenter;
    private static ArrayList<IExperience> list = new ArrayList<>();
    private TestingTrackSelection obs;
    private CustomDataSourceActivity activity;

    private Application app;
    private SerleenaDataSource dataSource;
    private SerleenaDatabase db;
    private SQLiteDatabase sqLiteDatabase;
    private ShadowLocationManager slm;

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        app = RuntimeEnvironment.application;
        fragment = new ExperienceSelectionFragment();

        activity = Robolectric.buildActivity(CustomDataSourceActivity.class).
                create().start().visible().get();

        db = TestDB.getEmptyDatabase();
        sqLiteDatabase = db.getWritableDatabase();
        updateExperiencesList();

        fragment.attachPresenter(presenter);

        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(0, adapter.getCount());
    }

    /**
     * Metodo che aggiorna la lista di esperienze che il Presenter fornisce al Fragment
     * aggiornando il datasource fornito all'Activity.
     */
    private void updateExperiencesList() {
        switchToExpSel();
        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application, db));
        activity.setDataSource(dataSource);
        presenter = new ExperienceSelectionPresenter(fragment,activity);
        switchToTrackSel();
        obs = new TestingTrackSelection(trackFragment,activity,presenter);
        presenter.attachObserver(obs);
    }

    /**
     * Metodo che fa diventare ExperienceSelectionFragment il Fragment visualizzato dall'Activity.
     */
    private void switchToExpSel() {
        activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
        ListFragment menu = (ListFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        menu.onListItemClick(null, null, 0, 0);
        ListFragment expMenu = (ListFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        expMenu.onListItemClick(null, null, 2, 0);
        fragment = (ExperienceSelectionFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        fragment.onResume();
    }

    /**
     * Metodo che fa diventare TrackSelectionFragment il Fragment visualizzato dall'Activity.
     */
    private void switchToTrackSel() {
        activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
        ListFragment menu = (ListFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        menu.onListItemClick(null, null, 0, 0);
        ListFragment expMenu = (ListFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        expMenu.onListItemClick(null, null, 3, 0);
        trackFragment = (TrackSelectionFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        trackFragment.onResume();
    }

    /**
     * Test che verifica se la lista e' stata popolata correttamente con una singola
     * esperienza.
     */
    @Test
    public void initializationShouldHaveOneExperienceInListAdapter() {
        initializationWithOneExperience();
        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(1, adapter.getCount());
        IExperience exp = (IExperience) adapter.getItem(0);
        Assert.assertEquals("expo", exp.getName());
    }

    /**
     * Metodo che carica una sola esperienza nel database e nell'Activity.
     */
    private void initializationWithOneExperience() {
        db = TestDB.getEmptyDatabase();
        sqLiteDatabase = db.getWritableDatabase();
        TestDB.experienceQuery(sqLiteDatabase, 1, "expo");
        updateExperiencesList();
    }

    /**
     * Test che verifica la corretta attivazione di un'esperienza.
     */
    @Test
    public void testActivateExperience() {
        initializationWithOneExperience();
        fragment.onListItemClick(null, null, 0, 0);
        Assert.assertEquals("expo",obs.getName());
    }

    /**
     * Test che verifica il corretto popolamento della lista nel Fragment quando questa
     * ha due esperienze al suo interno.
     */
    @Test
    public void shouldPutTwoExperiencesInExperiencesList() {
        SerleenaDatabase otherDb = TestDB.getEmptyDatabase();
        SQLiteDatabase sqlDb = otherDb.getWritableDatabase();
        TestDB.experienceQuery(sqlDb, 1, "expo");
        TestDB.experienceQuery(sqlDb, 2, "Visita alle mani di Gianni Morandi");
        updateExperiencesList();

        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(2, adapter.getCount());
        IExperience exp = (IExperience) adapter.getItem(0);
        Assert.assertEquals("expo", exp.getName());
        exp = (IExperience) adapter.getItem(1);
        Assert.assertEquals("Visita alle mani di Gianni Morandi", exp.getName());
    }

}
