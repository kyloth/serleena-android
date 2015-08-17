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
 * Name: TrackSelectionPresenterIntegrationTest.java
 * Package: com.kyloth.serleena
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.app.Fragment;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.NoActiveTrackException;

import org.junit.Ignore;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.lang.Override;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Contiene i test di integrazione per la classe TrackSelectionPresenter e le
 * sue dipendenze.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class TrackSelectionPresenterIntegrationTest {

    private static class TestingActivity extends SerleenaActivity {
        private ISerleenaDataSource ds;

        public void setDataSource(ISerleenaDataSource dataSource) {
            ds = dataSource;
        }

        @Override
        public ISerleenaDataSource getDataSource() {
            return ds;
        }
    }

    private TestingActivity activity;
    private SQLiteDatabase db;

    @Before
    public void initialize() {
        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        db = serleenaDb.getWritableDatabase();
        SerleenaDataSource dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(serleenaDb));
        activity = Robolectric.buildActivity(TestingActivity.class)
                .create().start().resume().visible().get();
        activity.setDataSource(dataSource);

        UUID expUUID1 = UUID.randomUUID();
        UUID expUUID2 = UUID.randomUUID();
        TestDB.experienceQuery(db, expUUID1, "experience1");
        TestDB.experienceQuery(db, expUUID2, "experience2");
        TestDB.trackQuery(db, UUID.randomUUID(), "track1", expUUID1);
        TestDB.trackQuery(db, UUID.randomUUID(), "track2", expUUID1);
        TestDB.trackQuery(db, UUID.randomUUID(), "track3", expUUID2);
        TestDB.trackQuery(db, UUID.randomUUID(), "track4", expUUID2);
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
     * Verifica che la vista Imposta Percorso mostri i Percorsi associati alla
     * vista attivata da Imposta Esperienza.
     */
    @Test
    public void viewShouldShowTracksOfActiveExperience() {
        ListFragment expFrag =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza");
        IExperience experience = null;
        for (int i = 0; i < expFrag.getListAdapter().getCount(); i++)
            if (((IExperience)expFrag.getListAdapter().getItem(i))
                .getName().equals("experience1")) {
                expFrag.onListItemClick(null, null, i, 0);
                experience = (IExperience) expFrag.getListAdapter().getItem(i);
            }

        ListFragment trackFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        assertFragmentContainsTracks(trackFragment, experience.getTracks());
    }

    /**
     * Verifica che il Percorso selezionato dalla vista venga attivato, e
     * quindi l'attivazione venga segnalata ai componenti incaricati della
     * gestione del Percorso.
     */
    @Test
    public void selectingTrackFromViewShouldActivateIt()
            throws NoTrackCrossingException, NoActiveTrackException {
        ListFragment expFrag =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza");
        for (int i = 0; i < expFrag.getListAdapter().getCount(); i++)
            if (((IExperience)expFrag.getListAdapter().getItem(i))
                    .getName().equals("experience1")) {
                expFrag.onListItemClick(null, null, i, 0);
            }

        ListFragment trackFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        ITrack track = null;
        for (int i = 0; i < trackFragment.getListAdapter().getCount(); i++)
            if (((ITrack)trackFragment.getListAdapter().getItem(i))
                    .name().equals("track2")) {
                trackFragment.onListItemClick(null, null, i, 0);
                track = (ITrack) trackFragment.getListAdapter().getItem(i);
            }

        assertEquals(track, activity.getSensorManager()
                .getTrackCrossingManager().getTrack());
    }

    private void assertFragmentContainsTracks(
            ListFragment trackFragment, Iterable<ITrack> tracks) {
        boolean asd = true;
        for (int i = 0; i < trackFragment.getListAdapter().getCount(); i++) {
            boolean lol = false;
            for (ITrack t : tracks)
                lol = lol || t.equals(trackFragment.getListAdapter().getItem(i));
            asd = asd && lol;
        }
        assertTrue(asd);
    }

}
