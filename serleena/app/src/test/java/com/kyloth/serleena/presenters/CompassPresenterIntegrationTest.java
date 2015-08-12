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
 * Name: CompassPresenterIntegrationTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.app.Fragment;
import android.app.ListFragment;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.sensors.HeadingManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SensorNotAvailableException;
import com.kyloth.serleena.view.fragments.CompassFragment;
import com.kyloth.serleena.view.widgets.CompassWidget;

import junit.framework.Assert;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.lang.IllegalAccessException;
import java.lang.Integer;
import java.lang.NoSuchFieldException;
import java.lang.Override;
import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Contiene i test di integrazione per la classe ExperienceSelectionFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class CompassPresenterIntegrationTest {

    private CompassFragment fragment;
    private TestActivity activity;
    private CompassWidget widget;

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().resume().visible().get();

        ListFragment menu = (ListFragment) activity.getFragmentManager().
                findFragmentById(R.id.main_container);
        for (int i = 0; i < menu.getListAdapter().getCount(); i++) {
            Fragment f = (Fragment) menu.getListAdapter().getItem(i);
            if (f.toString().equals("Bussola"))
                fragment = (CompassFragment) f;
        }

        activity.onObjectSelected(fragment);
        fragment.onResume();
        widget = (CompassWidget)
                fragment.getView().findViewById(R.id.compass_widget);
    }

    /**
     * Verifica che la schermata Bussola venga impostata correttamente in
     * base ai dati ricevuti dai sensori.
     */
    @Test
    public void testOnHeadingUpdate()
            throws NoSuchFieldException, IllegalAccessException {
        final float[] accelerometerValues = new float[] { 11, 22, 33 };
        final float[] magneticFieldValues = new float[] { 32, 21, 10 };
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        float expected = - (float) Math.toDegrees(values[0]);

        SensorEvent accEvent = createSensorEvent(accelerometerValues);
        when(accEvent.sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);
        SensorEvent magEvent = createSensorEvent(magneticFieldValues);
        when(magEvent.sensor.getType()).thenReturn(Sensor.TYPE_MAGNETIC_FIELD);

        activity.headingManager().onSensorChanged(accEvent);
        activity.headingManager().onSensorChanged(magEvent);

        Assert.assertEquals(expected, widget.getOrientation());
    }

    private static SensorEvent createSensorEvent(float[] values)
            throws NoSuchFieldException, IllegalAccessException {
        SensorEvent sensorEvent = mock(SensorEvent.class);
        sensorEvent.sensor = mock(Sensor.class);

        Field valuesField = SensorEvent.class.getField("values");
        valuesField.setAccessible(true);
        valuesField.set(sensorEvent, values);

        return sensorEvent;
    }

    private static class TestActivity extends SerleenaActivity {
        private ISensorManager sm;
        private HeadingManager hm;

        public TestActivity() {
            super();

            SensorManager mockSensorManager = mock(SensorManager.class);
            when(mockSensorManager.getDefaultSensor(any(Integer.class)))
                    .thenReturn(mock(Sensor.class));
            try {
                hm = new HeadingManager(mockSensorManager);
                sm = mock(ISensorManager.class);
                when(sm.getHeadingSource())
                        .thenReturn(hm);
            } catch (SensorNotAvailableException e) {
                throw new RuntimeException();
            }
        }

        @Override
        public ISensorManager getSensorManager() {
            return sm;
        }

        public HeadingManager headingManager() {
            return hm;
        }

    }

}
