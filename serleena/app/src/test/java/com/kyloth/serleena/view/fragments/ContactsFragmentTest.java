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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test di unità per la classe ContactsFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ContactsFragmentTest {

    private TextView fakeName;
    private TextView fakeValue;
    private ImageView fakeImage;
    private ContactsFragment fragment;
    private IContactsPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        presenter = mock(IContactsPresenter.class);
        fragment = new ContactsFragment();
        fragment.attachPresenter(presenter);

        fakeName = new TextView(RuntimeEnvironment.application);
        fakeValue = new TextView(RuntimeEnvironment.application);
        fakeImage = new ImageView(RuntimeEnvironment.application);

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        ViewGroup v = mock(ViewGroup.class);
        when(v.findViewById(R.id.contact_image)).thenReturn(fakeImage);
        when(v.findViewById(R.id.contact_name_text)).thenReturn(fakeName);
        when(v.findViewById(R.id.contact_value_text)).thenReturn(fakeValue);

        when(inflater.inflate(
                        eq(R.layout.fragment_contacts),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.contact_name_text)).thenReturn(fakeName);
        when(v.findViewById(R.id.contact_value_text)).thenReturn(fakeValue);
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
    }

    /**
     * Verifica che sia possibile collegare un IContactsPresenter ad un
     * ContactsFragment.
     */
    @Test
    public void testAttachContactsPresenter() {
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
        fakeImage.callOnClick();
        verify(presenter, times(1)).nextContact();
        fakeName.callOnClick();
        verify(presenter, times(2)).nextContact();
        fakeValue.callOnClick();
        verify(presenter, times(3)).nextContact();
    }

    /**
     * Verifica che displayContact() imposti correttamente i widget della vista.
     */
    @Test
    public void displayContactShouldSetTextViewsAccordingly() {
        fragment.displayContact("contact_name", "contact_value");
        assertEquals(fakeName.getText().toString(), "contact_name");
        assertEquals(fakeValue.getText().toString(), "contact_value");
    }
}
