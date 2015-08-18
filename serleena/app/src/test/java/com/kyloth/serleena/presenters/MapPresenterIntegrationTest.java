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
 * Name: MapPresenterIntegrationTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file, scrittura
 *                           codice e documentazione Javadoc
 */
package com.kyloth.serleena.presenters;

import android.app.Application;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.Region;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;
import com.kyloth.serleena.presenters.MapPresenter;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.widgets.MapWidget;
import com.jayway.awaitility.Awaitility;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.eclipse.jetty.server.Authentication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contiene i test di integrazione per MapPresenter e le sue dipendenze,
 * quali MapFragment, il codice di persistenza e il codice di gestione dei
 * sensori di posizione.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class MapPresenterIntegrationTest {

    private SQLiteDatabase db;

    private static class CustomDatasourceActivity extends SerleenaActivity {
        private SerleenaDataSource dataSource;

        public void setDataSource(SerleenaDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public SerleenaDataSource getDataSource() {
            return dataSource;
        }
    }

    private MapFragment fragment;
    private CustomDatasourceActivity activity;
    private MapWidget mapWidget;
    private Application app;
    private LocationManager lm;
    private ShadowLocationManager slm;
    private SerleenaDataSource dataSource;

    @Before
    public void initialize() {
        app = RuntimeEnvironment.application;
        lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);

        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        db = serleenaDb.getWritableDatabase();
        TestDB.experienceQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID, "experience");

        dataSource = new SerleenaDataSource(new SerleenaSQLiteDataSource(serleenaDb));

        activity = Robolectric.buildActivity(CustomDatasourceActivity.class)
                .create().start().visible().get();
        activity.setDataSource(dataSource);
    }

    /**
     * Verifica che il click della mappa sulla visuale provochi il corretto
     * inserimento di un punto utente per l'Esperienza in corso.
     */
    @Test
    public void userGestureShouldAddNewUserPointToExperience()
            throws NoActiveExperienceException, LocationNotAvailableException {
        TestDB.quadrantQuery(db, 40, 0, 0, 40, "asd", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                "experience");
        gotoFragment();

        ShadowLocationManager slm = Shadows.shadowOf(lm);
        Location expectedLocation = createLocation(12.0, 20.0);

        slm.simulateLocation(expectedLocation);
        mapWidget.callOnClick();

        Awaitility.await().until(
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        IExperience experience =
                                dataSource.getExperiences().iterator().next();
                        return experience.getUserPoints().iterator().hasNext();
                    }
                });
        IExperience experience = dataSource.getExperiences().iterator().next();
        assertEquals(
                experience.getUserPoints().iterator().next(),
                new UserPoint(12, 20));
    }

    /**
     * Verifica che la mappa venga impostata con la posizione utente
     * aggiornata fornita dal modulo GPS.
     */
    @Test
    public void presenterShouldSetMapWithLatestLocationUpdate() {
        TestDB.quadrantQuery(db, 30, 0, 0, 30, "asdlol", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);

        gotoFragment();

        Location expectedLocation = createLocation(12.0, 20.0);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        GeoPoint latest = mapWidget.getUserPosition();
        assertEquals(latest.latitude(), 12.0);
        assertEquals(latest.longitude(), 20.0);

        expectedLocation = createLocation(15.0, 10.0);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        latest = mapWidget.getUserPosition();
        assertEquals(latest.latitude(), 15.0);
        assertEquals(latest.longitude(), 10.0);
    }

    /**
     * Verifica che la vista mostri i quadranti associati alla sola
     * esperienza attiva.
     */
    @Test
    public void viewShouldShowQuadrantAccordingToActiveExperience() {
        TestDB.experienceQuery(db, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_UUID, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME);
        TestDB.quadrantQuery(db, 3, 1, 1, 3, "asdlol", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 5, 0, 0, 5, "lolasd", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_UUID);

        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                "experience");
        gotoFragment();

        Location expectedLocation = createLocation(2.5, 2.5);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                IQuadrant quadrant = mapWidget.getQuadrant();
                return quadrant != null && TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(3, 1), new GeoPoint(1, 3)));
            }
        });

        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_2_NAME);
        gotoFragment();

        expectedLocation = createLocation(2.5, 2.5);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                IQuadrant quadrant = mapWidget.getQuadrant();
                return quadrant != null && TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(5, 0), new GeoPoint(0, 5)));
            }
        });
    }

    /**
     * Verifica che la vista mostri correttamente il quadrante relativo alla
     * posizione corrente e all'Esperienza attiva al momento.
     */
    @Test
    public void viewShouldShowQuadrantForCurrentLocation() {
        TestDB.quadrantQuery(db, 5, 0, 0, 5, "asdlol", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 10, 5, 5, 10, "lolasd", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);

        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                "experience");
        gotoFragment();

        Location expectedLocation = createLocation(2.5, 2.5);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                IQuadrant quadrant = mapWidget.getQuadrant();
                return quadrant != null && TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(5, 0), new GeoPoint(0, 5)));
            }
        });

        expectedLocation = createLocation(7.5, 7.5);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                IQuadrant quadrant = mapWidget.getQuadrant();
                return quadrant != null && TestDB.quadrantHasRegion(quadrant,
                        new Region(new GeoPoint(10, 5), new GeoPoint (5, 10)));
            }
        });
    }

    /**
     * Verifica che la vista mostri i punti utente associati all'Esperienza
     * attiva che ricadono entro il perimetro del quadrante.
     */
    @Test
    public void viewShouldShowUserPointForCurrentQuadrant() {
        TestDB.userPointQuery(db, 0, 1, 1, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.userPointQuery(db, 1, 2, 2, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.userPointQuery(db, 2, 6, 6, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.userPointQuery(db, 3, 7, 7, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);
        TestDB.quadrantQuery(db, 5, 0, 0, 5, "asdlol", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_UUID);

        activateExperienceByName(
                (ListFragment) switchToFragmentInExperienceFragment(
                        "Imposta Esperienza"),
                "experience");
        gotoFragment();

        Location expectedLocation = createLocation(2.5, 2.5);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mapWidget.getQuadrant() != null;
            }
        });

        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mapWidget.getUserPoints() != null;
            }
        });

        Iterator<UserPoint> iterator = mapWidget.getUserPoints().iterator();
        UserPoint p1 = iterator.next();
        UserPoint p2 = iterator.next();
        assertTrue(!iterator.hasNext());

        boolean b1 = p1.latitude() == 1 && p1.longitude() == 1 &&
                p2.latitude() == 2 && p2.longitude() == 2;
        boolean b2 = p1.latitude() == 2 && p1.longitude() == 2 &&
                p2.latitude() == 1 && p2.longitude() == 1;
        assertTrue(b1 || b2);
    }

    private Location createLocation(double latitude, double longitude) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
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

    private void gotoFragment() {
        fragment = (MapFragment) switchToFragmentInExperienceFragment("Mappa");
        mapWidget =
                (MapWidget) fragment.getView().findViewById(R.id.map_widget);
    }

}
