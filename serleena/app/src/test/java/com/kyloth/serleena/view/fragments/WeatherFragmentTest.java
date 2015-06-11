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
 * Name: WeatherFragmentTest.java
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
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.ServiceTestCase;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presenters.SerleenaActivity;
import com.kyloth.serleena.view.fragments.WeatherFragment;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.sensors.ISensorManager;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Text;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Throwable;
import java.lang.reflect.Method;
import java.util.Date;

import dalvik.annotation.TestTarget;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Contiene i test di unit� per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.CompassFragment
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19, manifest = "src/main/AndroidManifest.xml")
public class WeatherFragmentTest {

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

    private Activity activity;
    private WeatherFragment fragment;
    private IWeatherPresenter presenter;

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
        fragment = new WeatherFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment,"TEST").commit();
        Assert.assertEquals("fragment not attached", fragment.getActivity(), activity);
    }

    /**
     * Verifica che sia possibile collegare un IContactsPresenter ad un
     * ContactsFragment.
     *
     */
    @Test
    public void testAttachContactsPresenter() {
        presenter = mock(IWeatherPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.onResume();
        fragment.onPause();
        verify(presenter).resume();
        verify(presenter).pause();
    }

    /**
     * Verifica che sia possibile impostare una data.
     *
     */
    @Test
    public void testSetDate() {
        fragment.setDate(new Date());
    }

    /**
     * Verifica che alla pressione del pulsante centrale venga
     * richiesto il prossimo contatto.
     *
     */
    @Test
    public void testShouldRequestNextContactOnKeyDown() {
        presenter = mock(IWeatherPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.keyDown(0, new KeyEvent(1, KeyEvent.KEYCODE_ENTER));
        verify(presenter).advanceDate();
    }
}
