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

import android.app.Fragment;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.IRasterSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.IExperienceActivationSource;
import com.kyloth.serleena.presentation.ITrackSelectionView;
import com.kyloth.serleena.presenters.ExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presenters.TrackSelectionPresenter;
import com.jayway.awaitility.Awaitility;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.lang.Override;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

    private ExperienceSelectionFragment fragment;
    private CustomDataSourceActivity activity;

    private SerleenaDataSource dataSource;
    private SQLiteDatabase db;
    private ListAdapter experienceListAdapter;

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        SerleenaDatabase serleenaDB = TestDB.getEmptyDatabase();
        db = serleenaDB.getWritableDatabase();
        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application,
                        serleenaDB,
                        mock(IRasterSource.class)));
    }

    public void setup() {
        activity = Robolectric.buildActivity(CustomDataSourceActivity.class).
                create().start().visible().get();
        activity.setDataSource(dataSource);

        fragment =
                (ExperienceSelectionFragment)
                        switchToFragmentInExperienceFragment(
                                "Imposta Esperienza");
        experienceListAdapter = fragment.getListAdapter();
    }

    private Fragment switchToFragmentInExperienceFragment(String string) {
        activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
        ListFragment menuFragment =
                (ListFragment) activity.getFragmentManager()
                        .findFragmentById(R.id.main_container);
        menuFragment.onResume();
        ListAdapter adapter = menuFragment.getListAdapter();
        ListFragment expFragment = null;
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals("Esperienza"))
                expFragment = (ListFragment) adapter.getItem(i);
        activity.onObjectSelected(expFragment);
        expFragment.onResume();
        adapter = expFragment.getListAdapter();
        Fragment frag = null;
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals(string))
                frag = (Fragment) adapter.getItem(i);
        activity.onObjectSelected(frag);
        frag.onResume();
        return frag;
    }

    /**
     * Verifica che la vista visualizzi un elenco vuoto quando il DB non
     * contiene esperienze.
     */
    @Test
    public void zeroExperiencesInDbShouldLeaveListBlank() {
        setup();
        assertEquals(0, experienceListAdapter.getCount());
    }

    /**
     * Verifica che la lista venga popolata correttamente con una singola
     * esperienza quando il DB ne contiene solamente una.
     */
    @Test
    public void oneExperienceInDbShouldBeShownByView() {
        TestDB.experienceQuery(db, 0, "experience");
        setup();

        assertEquals(1, experienceListAdapter.getCount());
        IExperience exp = (IExperience) experienceListAdapter.getItem(0);
        assertEquals("experience", exp.getName());
    }

    /**
     * Verifica che la lista sia stata popolata correttamente con una singola
     * esperienza.
     */
    @Test
    public void twoExperiencesInDbShouldBeShownByView() {
        TestDB.experienceQuery(db, 0, "experience1");
        TestDB.experienceQuery(db, 1, "experience2");
        setup();

        assertEquals(2, experienceListAdapter.getCount());
        IExperience exp1 = (IExperience) experienceListAdapter.getItem(0);
        IExperience exp2 = (IExperience) experienceListAdapter.getItem(1);
        assertTrue(
                (exp1.getName().equals("experience1") &&
                        exp2.getName().equals("experience2")) ||
                (exp1.getName().equals("experience2") &&
                        exp2.getName().equals("experience1")));
    }

    /**
     * Verifica che l'esperienza attivata dall'interazione dell'utente con la
     * lista venga comunicata correttamente agli osservatori dell'evento, in
     * particolare alla vista "Imposta Percorso".
     */
    @Test
    public void testActivateExperience() {
        TestDB.experienceQuery(db, 0, "experience");
        TestDB.trackQuery(db, 0, "track", 0);
        TestDB.checkpointQuery(db, 0, 1, 5, 6, 0);
        setup();

        fragment.onListItemClick(null, null, 0, 0);
        switchToFragmentInExperienceFragment("Imposta Percorso");

        final ListFragment trackFragment = (ListFragment) activity
                .getFragmentManager().findFragmentById(R.id.main_container);
        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return trackFragment.getListAdapter()
                        .getCount() > 0;
            }
        });
        ITrack track = (ITrack) trackFragment.getListAdapter().getItem(0);
        assertEquals(1, track.getCheckpoints().size());
        Checkpoint c = track.getCheckpoints().get(0);
        assertEquals(5.0, c.latitude());
        assertEquals(6.0, c.longitude());
    }

}
