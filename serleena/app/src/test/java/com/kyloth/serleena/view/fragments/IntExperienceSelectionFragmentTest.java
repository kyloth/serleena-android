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
 * Name: IntExperienceSelectionFragmentTest.java
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
import android.app.FragmentManager;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IExperienceActivationObserver;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.view.fragments.ExperienceSelectionFragment;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import dalvik.annotation.TestTargetClass;

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
public class IntExperienceSelectionFragmentTest {

    private ExperienceSelectionFragment fragment;
    private ExperienceSelectionPresenter presenter;
    private static ArrayList<IExperience> list = new ArrayList<>();
    private IExperienceActivationObserver obs;

    private static class TestActivity
            extends Activity implements ISerleenaActivity {
        public ISerleenaDataSource getDataSource() {
            ISerleenaDataSource dataSource = mock(ISerleenaDataSource.class);
            when(dataSource.getExperiences()).thenReturn(list);
            return dataSource;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
    }
    private TestActivity activity;

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().visible().get();
        Assert.assertNotNull("Initialization failed", activity);
        fragment = new ExperienceSelectionFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment,"TEST").commit();
        assertTrue(fragment.getActivity() == activity);
        Assert.assertNotNull("Transition failed", fragment.getActivity());

        obs = mock(IExperienceActivationObserver.class);
        populateList();
        fragment.attachPresenter(presenter);
    }

    private void populateList() {
        ListAdapter adapter = fragment.getListAdapter();
        IExperience exp1 = mock(IExperience.class);
        list.add(exp1);
        IExperience exp2 = mock(IExperience.class);
        list.add(exp2);
        presenter = new ExperienceSelectionPresenter(fragment,activity);
        presenter.attachObserver(obs);
    }

    @Test
    public void testGiveSomeExperiencesToFragment() {
        list.clear();
        presenter = new ExperienceSelectionPresenter(fragment,activity);
        ListAdapter adapter = fragment.getListAdapter();
        Assert.assertEquals(0, adapter.getCount());
        populateList();
        adapter = fragment.getListAdapter();
        Assert.assertEquals(adapter.getItem(0), list.get(0));
        Assert.assertEquals(adapter.getItem(1), list.get(1));
        Assert.assertEquals(2, adapter.getCount());
    }

    @Test
    public void testActivateExperience() {
        IExperience experience = list.get(0);
        fragment.onListItemClick(null, null, 0, 0);
        verify(obs).onExperienceActivated(experience);
    }

}
