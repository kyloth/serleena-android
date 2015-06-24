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
 * Name: ContactsFragmentTest.java
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
import android.view.ViewGroup;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.presentation.IContactsPresenter;
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

import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe ContactsFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ContactsFragmentTest {

    private static class TestActivity
            extends Activity implements ISerleenaActivity {
        public ISerleenaDataSource getDataSource() {
            return null;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
    }

    private Activity activity;
    private ContactsFragment fragment;
    private IContactsPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza il test.
     *
     */
    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().visible().get();
        Assert.assertNotNull("initialization failed", activity);
        fragment = new ContactsFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment, "TEST").commit();
        Assert.assertEquals("fragment not attached", fragment.getActivity(), activity);
    }

    /**
     * Verifica che sia possibile collegare un IContactsPresenter ad un
     * ContactsFragment.
     */
    @Test
    public void testAttachContactsPresenter() {
        presenter = mock(IContactsPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.onResume();
        fragment.onPause();
        verify(presenter).resume();
        verify(presenter).pause();
    }

    /**
     * Verifica che il metodo displayContact() sollevi un'eccezione al
     * passaggio di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayContactsShouldThrowWhenNullArguments1() {
        fragment.displayContact(null, "");
    }

    /**
     * Verifica che il metodo displayContact() sollevi un'eccezione al
     * passaggio di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayContactsShouldThrowWhenNullArguments2() {
        fragment.displayContact("", null);
    }

    /**
     * Verifica che il metodo displayContact() sollevi un'eccezione al
     * passaggio di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayContactsShouldThrowWhenNullArguments3() {
        fragment.displayContact(null, null);
    }

    /**
     * Verifica che al click su uno qualsiasi dei widget della visuale, venga
     * richiesto il prossimo contatto.
     */
    @Test
    public void clickShouldAskForTheNextContact() {
        presenter = mock(IContactsPresenter.class);
        fragment.attachPresenter(presenter);

        ViewGroup vg = (ViewGroup) fragment.getView();
        for (int i = 0; i < vg.getChildCount(); i++) {
            vg.getChildAt(i).callOnClick();
            verify(presenter, times(i+1)).nextContact();
        }
    }

}
