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
 * Name: ExperienceSelectionFragmentTest.java
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
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test di unità per la classe ExperienceSelectionFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.ExperienceSelectionFragment
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ExperienceSelectionFragmentTest {

    private static class TestActivity
            extends Activity implements ISerleenaActivity {
        public ISerleenaDataSource getDataSource() {
            return null;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
        public IPersistenceDataSink getDataSink() { return null; }
    }

    private Activity activity;
    private ExperienceSelectionFragment fragment;
    private IExperienceSelectionPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        presenter = mock(IExperienceSelectionPresenter.class);
        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().visible().get();
        Assert.assertNotNull("initialization failed", activity);
        fragment = new ExperienceSelectionFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment,"TEST").commit();
        assertTrue(fragment.getActivity() == activity);
    }

    /**
     * Verifica che sia possibile collegare un IExperienceSelectionPresenter ad
     * un ExperienceSelectionFragment.
     */
    @Test
    public void testAttachContactsPresenter() {
        fragment.attachPresenter(presenter);
        fragment.onResume();
        fragment.onPause();
        verify(presenter).resume();
        verify(presenter).pause();
    }

    /**
     * Verifica che setExperiences() causi la creazione di un ListAdapter, e
     * quindi il popolamento della ListView con le esperienze passate come
     * parametro.
     */
    @Test
    public void setExceptionShouldPopulateListViewWithExperiences() {
        List<IExperience> list = new ArrayList<>();
        IExperience e1 = mock(IExperience.class);
        when(e1.getName()).thenReturn("e1");
        IExperience e2 = mock(IExperience.class);
        when(e2.getName()).thenReturn("e2");
        list.add(e1);
        list.add(e2);
        fragment.setExperiences(list);

        ListAdapter adapter = fragment.getListAdapter();
        IExperience ee1 = (IExperience) adapter.getItem(0);
        IExperience ee2 = (IExperience) adapter.getItem(1);
        assertTrue((ee1.getName().equals("e1") && ee2.getName().equals("e2")) ||
                (ee1.getName().equals("e2") && ee2.getName().equals("e1")));
    }

    /**
     * Verifica che setExperiences() lanci un'eccezione
     * IllegalArgumentException quando viene passato un parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setExceptionShouldThrowWhenArgumentIsNull() {
        fragment.setExperiences(null);
    }

    /**
     * Verifica che attachPresenter() lanci un'eccezione quando viene passato
     * un parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachPresenterShouldThrowWhenNullPresenter() {
        fragment.attachPresenter(null);
    }

    /**
     * Verifica che la selezione di un elemento sulla lista di Esperienze da
     * parte dell'utente causi l'attivazione di quell'Esperienza.
     */
    @Test
    public void clickingListViewShouldActivateAssociatedExperience() {
        fragment.attachPresenter(presenter);

        List<IExperience> list = new ArrayList<>();
        IExperience e1 = mock(IExperience.class);
        IExperience e2 = mock(IExperience.class);
        list.add(e1);
        list.add(e2);
        fragment.setExperiences(list);

        ListView listView = fragment.getListView();
        fragment.onListItemClick(listView, mock(View.class), 1, 0);
        verify(presenter).activateExperience(e2);
    }

}
