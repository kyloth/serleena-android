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
 * Name: TrackPresenterIntegrationTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import android.app.Application;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.AzimuthMagneticNorth;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.sensors.BackgroundLocationManager;
import com.kyloth.serleena.view.fragments.TrackFragment;
import com.kyloth.serleena.view.widgets.CompassWidget;
import com.jayway.awaitility.Awaitility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import java.util.concurrent.Callable;

import static junit.framework.Assert.assertEquals;

/**
 * Contiene i test di integrazione per la classe TrackPresenter e sue
 * dipendenze.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class TrackPresenterIntegrationTest {

    private TextView trackNameText;
    private TextView nextCheckpointText;
    private TextView distanceText;
    private CompassWidget orientationWidget;
    private TextView deltaText;
    private TrackFragment fragment;
    private TestSerleenaActivity activity;
    private SQLiteDatabase db;
    private ShadowLocationManager slm;
    private SerleenaDataSource dataSource;

    @Before
    public void initialize() {
        Application app = RuntimeEnvironment.application;
        LocationManager lm = (LocationManager) app.getSystemService(
                Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);

        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        db = serleenaDb.getWritableDatabase();
        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application, serleenaDb));
        TestDB.experienceQuery(db, 0, "experience1");
        TestDB.trackQuery(db, 0, "Track 1", 0);
        // (1, 1), (3, 3), (0, 5), (3, 7)
        TestDB.checkpointQuery(db, 0, 1, 1, 1, 0);
        TestDB.checkpointQuery(db, 1, 2, 3, 3, 0);
        TestDB.checkpointQuery(db, 2, 3, 0, 5, 0);
        TestDB.checkpointQuery(db, 3, 4, 3, 7, 0);
        // 100, 200, 300, 400
        TestDB.telemetryQuery(db, 0, 0);
        TestDB.checkPointEventQuery(db, 0, 100, 1, 0);
        TestDB.checkPointEventQuery(db, 1, 200, 2, 0);
        TestDB.checkPointEventQuery(db, 2, 300, 3, 0);
        TestDB.checkPointEventQuery(db, 3, 400, 4, 0);
        // (3, 3), (1, 1), (-1, -1), (-3, -3)
        TestDB.trackQuery(db, 1, "Track 2", 0);
        TestDB.checkpointQuery(db, 4, 1, 3, 3, 1);
        TestDB.checkpointQuery(db, 5, 2, 1, 1, 1);
        TestDB.checkpointQuery(db, 6, 3, -1, -1, 1);
        TestDB.checkpointQuery(db, 7, 4, -3, -3, 1);

        activity = Robolectric.buildActivity(TestSerleenaActivity.class)
                .create().start().visible().get();
        activity.setDataSource(dataSource);

        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                "experience1");
    }

    private void gotoFragment() {
        fragment = (TrackFragment) switchToFragmentInExperienceFragment(
                "Percorso");

        View v = fragment.getView();
        orientationWidget =
                (CompassWidget) v.findViewById(R.id.compass_widget_track);
        nextCheckpointText =
                (TextView) v.findViewById(R.id.checkpoint_numbers_text);
        trackNameText = (TextView) v.findViewById(R.id.track_name_text);
        distanceText = (TextView) v.findViewById(R.id.distance_text);
        deltaText = (TextView) v.findViewById(R.id.delta_text);
    }

    /**
     * Verifica che, senza aver attivato alcun Percorso, la vista mostri un
     * messaggio appropriato.
     */
    @Test
    public void viewShouldShowNoActiveTrackAtTheBeginning() {
        gotoFragment();
        assertEquals("NESSUN PERCORSO ATTIVO", trackNameText.getText());
    }

    /**
     * Verifica che la vista mostri correttamente il nome del Percorso attivo
     * al momento.
     */
    @Test
    public void fragmentShouldShowNameOfActiveTrack() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();
        assertEquals("Track 1", trackNameText.getText());

        switchToFragmentInExperienceFragment("Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 2");
        gotoFragment();
        assertEquals("Track 2", trackNameText.getText());
    }

    /**
     * Verifica che la vista venga impostata con la differenza di tempo
     * rispetto alla migliore prestazione per il Percorso in esecuzione.
     */
    @Test
    public void presenterShouldSetViewWithDeltaTime() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();

        final int partials[] = new int[] { 100, 200, 300, 400 };
        for (int i = 0; i < 4; i++) {
            final int index = i;
            orientationWidget.callOnClick();
            Awaitility.await().until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    int delta = Integer.valueOf(deltaText.getText().subSequence
                            (1, deltaText.length() - 3).toString());
                    return delta == activity.getSensorManager()
                            .getTrackCrossingManager().getLastCrossed()
                            .partialTime() - partials[index];
                }
            });
        }
    }

    /**
     * Verifica che sia possibile avanzare manualmente tra i checkpoint del
     * Percorso in esecuzione attraverso dei click.
     */
    @Test
    public void shouldBePossibleToAdvanceManuallyThroughATrack() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();

        assertEquals("1/4", nextCheckpointText.getText());

        orientationWidget.callOnClick();
        assertEquals("2/4", nextCheckpointText.getText());
        orientationWidget.callOnClick();
        assertEquals("3/4", nextCheckpointText.getText());
        orientationWidget.callOnClick();
        assertEquals("4/4", nextCheckpointText.getText());
        orientationWidget.callOnClick();
        assertEquals("FINE", nextCheckpointText.getText());
    }

    /**
     * Verifica che il nome del Percorso attraversato rimanga sulla vista
     * anche al termine di questo.
     */
    @Test
    public void viewShouldShowTitleOfActiveTrackWhenEnded() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();

        assertEquals("Track 1", trackNameText.getText());
        orientationWidget.callOnClick();
        orientationWidget.callOnClick();
        orientationWidget.callOnClick();
        orientationWidget.callOnClick();
        assertEquals("Track 1", trackNameText.getText());
    }

    /**
     * Verifica che lo spostamento dell'utente, e il rilevamento di questo da
     * parte dei sensori del dispositivo, causino l'avanzamento tra i
     * checkpoint del Percorso.
     */
    @Test
    public void shouldBePossibleToAdvanceThroughATrack() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();

        assertEquals("1/4", nextCheckpointText.getText());

        BackgroundLocationManager blm =
                (BackgroundLocationManager) Shadows.shadowOf(
                        RuntimeEnvironment.application)
                                .getRegisteredReceivers().get(0)
                                        .broadcastReceiver;

        simulateLocation(blm, 1, 1);

        assertEquals("2/4", nextCheckpointText.getText());
        simulateLocation(blm, 3, 3);
        assertEquals("3/4", nextCheckpointText.getText());
        simulateLocation(blm, 0, 5);
        assertEquals("4/4", nextCheckpointText.getText());
        simulateLocation(blm, 3, 7);
        assertEquals("FINE", nextCheckpointText.getText());
    }

    /**
     * Verifica che la vista venga impostata con la corretta distanza tra la
     * posizione attuale dell'utente e il prossimo checkpoint del Percorso.
     */
    @Test
    public void viewShoulsShowDistanceToNextCheckpoint() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        gotoFragment();

        BackgroundLocationManager blm =
                (BackgroundLocationManager) Shadows.shadowOf(
                        RuntimeEnvironment.application)
                        .getRegisteredReceivers().get(0)
                        .broadcastReceiver;

        float dist;
        simulateLocation(0, 0);
        dist = Math.round(new GeoPoint(0, 0).distanceTo(new GeoPoint(1, 1)));
        assertEquals((int)dist + " m", distanceText.getText());
        simulateLocation(blm, 1, 1);

        simulateLocation(2, 4);
        dist = Math.round(new GeoPoint(2, 4).distanceTo(new GeoPoint(3, 3)));
        assertEquals((int) dist + " m", distanceText.getText());
        simulateLocation(blm, 3, 3);
        simulateLocation(blm, 0, 5);
        simulateLocation(blm, 3, 7);

        assertEquals("", distanceText.getText());
    }

    /**
     * Verifica che la vista venga impostata con la corretta direzione
     * verso il quale rivolgersi per raggiungere il prossimo checkpoint del
     * Percorso.
     */
    @Test
    public void viewShouldShowCorrectHeadingTowardsNextCheckpoint() {
        ListFragment trackSelectionFragment =
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Percorso");
        activateTrackByName(trackSelectionFragment, "Track 1");
        BackgroundLocationManager blm =
                (BackgroundLocationManager) Shadows.shadowOf(
                        RuntimeEnvironment.application)
                        .getRegisteredReceivers().get(0)
                        .broadcastReceiver;

        gotoFragment();
        TrackPresenter presenter = new TrackPresenter(fragment, activity);
        fragment.onResume();
        AzimuthMagneticNorth amn = new AzimuthMagneticNorth(
                new float[] { 0, 0, 0 }, new float[] { 0, 0, 0 });
        presenter.onHeadingUpdate(amn);

        simulateLocation(0, 0);
        assertEquals(50.6, orientationWidget.getOrientation(), 0.1);
        simulateLocation(blm, 1, 1);

        simulateLocation(2, 4);
        assertEquals(-41.6, orientationWidget.getOrientation(), 0.1);
        simulateLocation(blm, 3, 3);

        simulateLocation(0, 4);
        assertEquals(94.1, orientationWidget.getOrientation(), 0.1);
    }

    private void simulateLocation(BackgroundLocationManager blm,
                                 double latitude, double longitude) {
        Bundle b = new Bundle();
        b.putDouble("latitude", latitude);
        b.putDouble("longitude", longitude);
        blm.onReceiveResult(0, b);
    }
    private void simulateLocation(double latitude, double longitude) {
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(l);
    }

    private void activateExperienceByName(
            ListFragment experienceSelectionFragment,
            String experienceName) {
        ListAdapter adapter = experienceSelectionFragment.getListAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            IExperience exp = (IExperience) adapter.getItem(i);
            if (exp.getName().equals(experienceName)) {
                experienceSelectionFragment.onListItemClick(null, null, i, 0);
                return;
            }
        }
        throw new RuntimeException();
    }

    private void activateTrackByName(
            ListFragment trackSelectionFragment,
            String trackName) {
        ListAdapter adapter = trackSelectionFragment.getListAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            ITrack track = (ITrack) adapter.getItem(i);
            if (track.name().equals(trackName)) {
                trackSelectionFragment.onListItemClick(null, null, i, 0);
                return;
            }
        }
        throw new RuntimeException();
    }

    private Fragment switchToFragmentInExperienceFragment(String string) {
        activity.getFragmentManager().findFragmentById(R.id.main_container)
                .onPause();
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

    private static class TestSerleenaActivity extends SerleenaActivity {
        private SerleenaDataSource dataSource;

        public void setDataSource(SerleenaDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public SerleenaDataSource getDataSource() {
            return dataSource;
        }
    }
}
