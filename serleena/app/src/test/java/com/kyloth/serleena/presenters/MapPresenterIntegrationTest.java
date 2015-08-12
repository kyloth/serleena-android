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
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.IRasterSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presenters.MapPresenter;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.widgets.MapWidget;
import com.jayway.awaitility.Awaitility;

import java.util.concurrent.Callable;

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

    private static class MyMapWidget extends MapWidget {
        private GeoPoint latestPosition;

        public MyMapWidget(Context context) {
            super(context);
        }

        @Override
        public void setUserPosition(GeoPoint userPosition) {
            super.setUserPosition(userPosition);
            latestPosition = userPosition;
        }
        public GeoPoint latestPosition() {
            return latestPosition;
        }
    }

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

    MapFragment fragment;
    CustomDatasourceActivity activity;
    MapPresenter presenter;
    MyMapWidget mapWidget;
    Application app;
    LocationManager lm;
    ShadowLocationManager slm;
    SerleenaDataSource dataSource;

    @Before
    public void initialize() {
        app = RuntimeEnvironment.application;
        lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);
        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        mapWidget = new MyMapWidget(app);

        when(inflater.inflate(
                        Matchers.eq(R.layout.fragment_map),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.map_widget)).thenReturn(mapWidget);
        fragment = new MapFragment();
        fragment.onCreateView(inflater, vg, Bundle.EMPTY);

        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        SQLiteDatabase db = serleenaDb.getWritableDatabase();
        TestDB.experienceQuery(db, 1, "exp");

        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application,
                        serleenaDb,
                        mock(IRasterSource.class)));

        activity = Robolectric.buildActivity(CustomDatasourceActivity.class)
                .create().start().visible().get();
        activity.setDataSource(dataSource);
        presenter = new MapPresenter(fragment, activity);
        presenter.setActiveExperience(
                dataSource.getExperiences().iterator().next());
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che il click della mappa sulla visuale provochi il corretto
     * inserimento di un punto utente per l'Esperienza in corso.
     */
    @Test
    public void userGestureShouldAddNewUserPointToExperience()
            throws NoActiveExperienceException, LocationNotAvailableException {
        ShadowLocationManager slm = Shadows.shadowOf(lm);
        Location expectedLocation = createLocation(12.0, 20.0);

        fragment.onResume();

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
        fragment.onResume();
        Location expectedLocation = createLocation(12.0, 20.0);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        GeoPoint latest = mapWidget.latestPosition();
        assertEquals(latest.latitude(), 12.0);
        assertEquals(latest.longitude(), 20.0);

        expectedLocation = createLocation(15.0, 10.0);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(expectedLocation);

        latest = mapWidget.latestPosition();
        assertEquals(latest.latitude(), 15.0);
        assertEquals(latest.longitude(), 10.0);
    }

    private Location createLocation(double latitude, double longitude) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }

}
