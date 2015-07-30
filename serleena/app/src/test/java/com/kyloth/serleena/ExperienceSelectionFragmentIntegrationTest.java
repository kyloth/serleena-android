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
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.IExperienceActivationObserver;
import com.kyloth.serleena.presenters.ExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;

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

    private static class CustomDataSourceActivity
            extends Activity implements ISerleenaActivity {
        private SerleenaDataSource ds;

        public void setDataSource (SerleenaDataSource dataSource) {
            ds = dataSource;
        }

        @Override
        public ISerleenaDataSource getDataSource() {
            return ds;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
    }

    private ExperienceSelectionFragment fragment;
    private ExperienceSelectionPresenter presenter;
    private static ArrayList<IExperience> list = new ArrayList<>();
    private IExperienceActivationObserver obs;
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
        LocationManager lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);
        fragment = new ExperienceSelectionFragment();

        activity = Robolectric.buildActivity(CustomDataSourceActivity.class).
                create().start().visible().get();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment,"TEST").commit();

        db = TestDB.getEmptyDatabase();
        sqLiteDatabase = db.getWritableDatabase();
        updateExperiencesList();

        fragment.attachPresenter(presenter);

        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(0, adapter.getCount());
    }

    private void updateExperiencesList() {
        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application, db));
        activity.setDataSource(dataSource);
        presenter = new ExperienceSelectionPresenter(fragment,activity);
    }

    @Test
    public void populatedDBShouldCauseSomeExperiencesInListAdapter() {
        TestDB.experienceQuery(sqLiteDatabase, 1, "expo");
        updateExperiencesList();

        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(1, adapter.getCount());
        IExperience exp = (IExperience) adapter.getItem(0);
        Assert.assertEquals("expo",exp.getName());
    }

    @Test
    public void testActivateExperience() {
        //IExperience experience = list.get(0);
        //fragment.onListItemClick(null, null, 0, 0);
        //verify(obs).onExperienceActivated(experience);
    }

}
