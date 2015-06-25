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
 * Name: TelemetryFragmentTest.java
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
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.ITelemetryPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.TrackAlreadyStartedException;

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
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.CompassFragment
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class TelemetryFragmentTest {

    private static class TestActivity
            extends Activity implements ISerleenaActivity {
        public void setActiveExperience(IExperience experience) { }
        public void setActiveTrack(ITrack track) { }
        public void enableTelemetry() {}
        public void disableTelemetry() {}
        public ISerleenaDataSource getDataSource() {
            return null;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
    }

    private TestActivity activity;
    private TelemetryFragment fragment;
    private ITelemetryPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).create().get();
        Assert.assertNotNull("initialization failed", activity);
        fragment = new TelemetryFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment,"TEST").commit();
        Assert.assertEquals(fragment.getActivity(), activity);
        presenter = mock(ITelemetryPresenter.class);
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che i metodi resume() e pause() del presenter vengano
     * chiamati correttamente.
     */
    @Test
    public void testAttachCompassPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che la distruzione del Fragment interrompa il Tracciamento,
     * che altrimenti continuerebbe anche dopo il termine dell'applicazione.
     */
    @Test
    public void destroyingFragmentShouldDisableTelemetry() {
        fragment.onDestroy();
        verify(presenter).disableTelemetry();
    }

    @Test
    public void presenterShouldBeNotifiedWhenUserClicks() throws
            TrackAlreadyStartedException {
        fragment.onClick(mock(View.class));
        verify(presenter).enableTelemetry();
        fragment.onClick(mock(View.class));
        verify(presenter).disableTelemetry();
        fragment.onClick(mock(View.class));
        verify(presenter, times(2)).enableTelemetry();
        fragment.onClick(mock(View.class));
        verify(presenter, times(2)).disableTelemetry();
    }

}
