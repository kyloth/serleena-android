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


package com.kyloth.serleena;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import android.app.Application;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.presenters.ContactsPresenter;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.view.fragments.ContactsFragment;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;

import com.jayway.awaitility.Awaitility;

import java.util.concurrent.Callable;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ContactsPresenterIntegrationTest {

    private static class CustomDataSourceActivity extends SerleenaActivity {
        private SerleenaDataSource dataSource;

        public void setDataSource(SerleenaDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public SerleenaDataSource getDataSource() {
            return dataSource;
        }
    }

    ContactsFragment fragment;
    CustomDataSourceActivity activity;
    ContactsPresenter presenter;
    Application app;
    LocationManager lm;
    ShadowLocationManager slm;
    SerleenaDataSource dataSource;
    TextView textName;
    TextView textValue;

    @Before
    public void initialize() {
        app = RuntimeEnvironment.application;
        lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);
        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
	textName = new TextView(app);
	textValue = new TextView(app);
        when(inflater.inflate(
                 eq(R.layout.fragment_contacts),
                 any(ViewGroup.class),
                 any(Boolean.class)
             )
            ).thenReturn(vg);
        when(vg.findViewById(R.id.contact_name_text)).thenReturn(textName);
        when(vg.findViewById(R.id.contact_value_text)).thenReturn(textValue);
        fragment = new ContactsFragment();
        fragment.onCreateView(inflater, vg, Bundle.EMPTY);
        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        SQLiteDatabase db = serleenaDb.getWritableDatabase();
        TestDB.contactQuery(db, 1, "Contact_1", "000", 0, 0, 2, 2);
        TestDB.contactQuery(db, 2, "Contact_2", "111", 0, 0, 2, 2);

        dataSource = new SerleenaDataSource(new SerleenaSQLiteDataSource(app, serleenaDb));
        activity = Robolectric.buildActivity(CustomDataSourceActivity.class)
                   .create().start().visible().get();
        activity.setDataSource(dataSource);
        presenter = new ContactsPresenter(fragment, activity);
    }

    @Test
    public void testEmptyContacts() {
        String name = textName.getText().toString();
        String value = textValue.getText().toString();
        assertEquals("NESSUN CONTATTO", name);
        assertEquals("DA VISUALIZZARE", value);
    }
    
    @Test
    public void testNonEmptyContacts() {
	Location currentLocation = createLocation(1, 1);
	fragment.onResume();
	slm.simulateLocation(currentLocation);

	Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !(textName.getText().toString().equals("NESSUN CONTATTO"));
            }
        });
        String name = textName.getText().toString();
        String value = textValue.getText().toString();
        assertEquals("Contact_1", name);
        assertEquals("000", value);
    }

    @Test
    public void testNextContact() {
        Location currentLocation = createLocation(1, 1);
	fragment.onResume();
	slm.simulateLocation(currentLocation);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !(textName.getText().toString().equals("NESSUN CONTATTO"));
            }
        });

        String name = textName.getText().toString();
        String value = textValue.getText().toString();
        assertEquals("Contact_1", name);
        assertEquals("000", value);
        presenter.nextContact();
        name = textName.getText().toString();
        value = textValue.getText().toString();
        assertEquals("Contact_2", name);
        assertEquals("111", value);
        presenter.nextContact();
        name = textName.getText().toString();
        value = textValue.getText().toString();
        assertEquals("Contact_1", name);
        assertEquals("000", value);
    }
    
    private Location createLocation(double latitude, double longitude) {
	Location location = new Location(LocationManager.GPS_PROVIDER);
	location.setLatitude(latitude);
	location.setLongitude(longitude);
	location.setTime(System.currentTimeMillis());
	return location;
    }

}
