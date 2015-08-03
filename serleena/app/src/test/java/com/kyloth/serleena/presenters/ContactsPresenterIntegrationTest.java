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
 * Name: ContactsPresenterIntegrationTest.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.view.fragments.ContactsFragment;
import com.jayway.awaitility.Awaitility;

import java.util.concurrent.Callable;

/**
 * Test di integrazione di ContactsPresenter e le due dipendenze, incluse le
 * componenti di persistenza e di presentazione.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ContactsPresenterIntegrationTest {

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

    private TestSerleenaActivity activity;
    private ContactsFragment fragment;
    private SQLiteDatabase db;
    private ShadowLocationManager slm;
    private TextView name;
    private TextView value;

    @Before
    public void initialize() {
        slm = Shadows.shadowOf(
                (LocationManager) RuntimeEnvironment.application
                        .getSystemService(Context.LOCATION_SERVICE));
        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        db = serleenaDb.getWritableDatabase();

        SerleenaDataSource dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application, serleenaDb));

        activity = Robolectric.buildActivity(TestSerleenaActivity.class)
                .create().start().visible().get();
        activity.setDataSource(dataSource);

        ListFragment menuFragment =
                (ListFragment) activity.getFragmentManager()
                        .findFragmentById(R.id.main_container);
        menuFragment.onResume();
        ListAdapter adapter = menuFragment.getListAdapter();
        fragment = null;
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals("Autorità locali"))
                fragment = (ContactsFragment) adapter.getItem(i);
        activity.onObjectSelected(fragment);

        name = (TextView) fragment.getView().findViewById(
                R.id.contact_name_text);
        value = (TextView) fragment.getView().findViewById(
                R.id.contact_value_text);

        fragment.onResume();

        TestDB.contactQuery(db, 0, "Contact1", "Value1", 5, 0, 0, 5);
        TestDB.contactQuery(db, 1, "Contact2", "Value2", 6, 1, 1, 6);
        TestDB.contactQuery(db, 2, "Contact3", "Value3", 9, 5, 5, 9);
        TestDB.contactQuery(db, 3, "Contact4", "Value4", 10, 6, 6, 10);

    }

    /**
     * Verifica che i contatti presenti nel database vengano visualizzati
     * dalla vista in base alla posizione attuale dell'utente.
     */
    @Test
    public void contactsShouldBeDisplayedAccordingToDBAndCurrentLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(4);
        location.setLongitude(4);
        simulateLocation(location);

        String name1, name2, value1, value2;

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String name1 = name.getText().toString();
                return !name1.equals("NESSUN CONTATTO");
            }});

        name1 = name.getText().toString();
        value1 = value.getText().toString();

        name.callOnClick();

        name2 = name.getText().toString();
        value2 = value.getText().toString();

        boolean b1, b2;
        b1 = name1.equals("Contact1") && value1.equals("Value1") &&
                name2.equals("Contact2") && value2.equals("Value2");
        b2 = name2.equals("Contact1") && value2.equals("Value1") &&
                name1.equals("Contact2") && value1.equals("Value2");
        assertTrue(b1 || b2);
    }

    /**
     * Verifica che i contatti presenti nel database per una certa posizione
     * geografica vengano visualizzati secondo un buffer circolare.
     */
    @Test
    public void contactsShouldBeDisplayedLikeACircularBuffer() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(4);
        location.setLongitude(4);
        simulateLocation(location);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String name1 = name.getText().toString();
                return !name1.equals("NESSUN CONTATTO");
            }});

        String name1, name2, value1, value2;
        name1 = name.getText().toString();
        value1 = value.getText().toString();
        name.callOnClick();
        name2 = name.getText().toString();
        value2 = value.getText().toString();
        name.callOnClick();

        assertEquals(name1, name.getText().toString());
        assertEquals(value1, value.getText().toString());
    }

    /**
     * Verifica che il cambio di posizione dell'utente causi la corretta
     * visualizzazione dei contatti sulla vista per quella posizione.
     */
    @Test
    public void contactsShouldBeDisplayedAccordingToLocation() {
        TestDB.contactQuery(db, 4, "Contact1", "Value1", 30, 0, 0, 30);
        TestDB.contactQuery(db, 5, "Contact2", "Value2", 50, 40, 40, 50);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(20);
        location.setLongitude(20);
        simulateLocation(location);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return "Contact1".equals(name.getText().toString()) &&
                        "Value1".equals(value.getText().toString());
            }
        });

        location.setLatitude(45);
        location.setLongitude(45);
        simulateLocation(location);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return "Contact2".equals(name.getText().toString()) &&
                        "Value2".equals(value.getText().toString());
            }
        });

        location.setLatitude(60);
        location.setLongitude(60);
        simulateLocation(location);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return "NESSUN CONTATTO".equals(name.getText().toString()) &&
                        "DA VISUALIZZARE".equals(value.getText().toString());
            }
        });
    }

    private void simulateLocation(Location location) {
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(location);
    }

}

