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


package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.FragmentManager;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presenters.ContactsPresenter;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class ContactsFragmentIntegrationTest {

    private SerleenaActivity activity;
    private ContactsFragment fragment;
    private IContactsPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        activity = (SerleenaActivity) Robolectric.buildActivity(SerleenaActivity.class).
                   create().start().visible().get();
        assertNotNull("initialization failed", activity);
        fragment = new ContactsFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment, "TEST").commit();
        assertEquals("fragment not attached", fragment.getActivity(), activity);
        presenter = new ContactsPresenter(fragment, activity);
    }

    @Test
    public void attachPresenterShouldThrowExceptionWhenNullPresenter() {
        exception.expect(IllegalArgumentException.class);
        fragment.attachPresenter(null);
    }

    @Test
    public void testDisplayContact() {
        fragment.displayContact("Foo", "Bar");
    }

    @Test
    public void testClearView() {
        fragment.clearView();
    }

    @Test
    public void testOnResume() {
        fragment.onResume();
    }

    @Test
    public void testOnPause() {
        fragment.onPause();
    }

    @Test
    public void testOnClick() {
        fragment.onClick(null);
    }

    @Test
    public void testToString() {
        String toString = fragment.toString();
        assertEquals(toString, "Autorità locali");
    }

}

