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
 * Name: QuitFragmentIntegrationTest.java
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
import android.view.KeyEvent;
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
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;

/**
 * Contiene test di integrazione per la classe QuitFragment.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class QuitFragmentIntegrationTest {

    private static class TestActivity extends SerleenaActivity {
        public boolean backPressed = false;
        public boolean finishing = false;
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            backPressed = true;
        }
        @Override
        public void finish() {
            super.finish();
            finishing = true;
        }
    }

    private TestActivity activity;
    private QuitFragment fragment;
    private Button yesButton;
    private Button noButton;

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class)
                .create().start().resume().visible().get();
        fragment = switchToQuitFragment();
        yesButton = (Button) fragment.getView().findViewById(R.id.yes_button);
        noButton = (Button) fragment.getView().findViewById(R.id.no_button);
    }

    /**
     * Verifica che la pressione del pulsante "sì" segnali all'activity di
     * chiudersi.
     */
    @Test
    public void hittingYesShouldCloseTheActivity() {
        yesButton.callOnClick();
        assertTrue(activity.finishing);
    }

    /**
     * Verifica che la pressione del pulsante "no" segnali all'activity di
     * tornare indietro allo stato precedente.
     */
    @Test
    public void hittingNoShouldGoBack() {
        noButton.callOnClick();
        assertTrue(activity.backPressed);
    }

    private QuitFragment switchToQuitFragment() {
        activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
        ListFragment menuFragment =
                (ListFragment) activity.getFragmentManager()
                        .findFragmentById(R.id.main_container);
        menuFragment.onResume();
        ListAdapter adapter = menuFragment.getListAdapter();
        QuitFragment quitFragment = null;
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals("Esci"))
                quitFragment = (QuitFragment) adapter.getItem(i);
        activity.onObjectSelected(quitFragment);
        quitFragment.onResume();
        return quitFragment;
    }
}
