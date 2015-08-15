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
 * Name: QuitFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.activity.SerleenaActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

/**
 * Contiene test di unità per la classe QuitFragment.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class QuitFragmentTest {

    private QuitFragment fragment;
    private Button yesButton;
    private Button noButton;

    @Before
    public void initialize() {
        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        yesButton = new Button(RuntimeEnvironment.application);
        noButton = new Button(RuntimeEnvironment.application);

        when(inflater.inflate(
                        eq(R.layout.fragment_quit),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.yes_button)).thenReturn(yesButton);
        when(v.findViewById(R.id.no_button)).thenReturn(noButton);

        fragment = new QuitFragment();
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
    }

    /**
     * Verifica che il Listener registrato all'evento onYesClick() venga
     * correttamente segnalato alla pressione del bottone.
     */
    @Test
    public void yesButtonClickShouldNotifyListener() {
        TestClick test = new TestClick();
        fragment.setOnYesClickListener(test);
        yesButton.callOnClick();
        assertTrue(test.called);
    }

    /**
     * Verifica che il Listener registrato all'evento onNoClick() venga
     * correttamente segnalato alla pressione del bottone.
     */
    @Test
    public void noButtonClickShouldNotifyListener() {
        TestClick test = new TestClick();
        fragment.setOnNoClickListener(test);
        noButton.callOnClick();
        assertTrue(test.called);
    }

    private static class TestClick implements View.OnClickListener {
        public boolean called = false;
        @Override
        public void onClick(View v) {
            called = true;
        }
    }

}
