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
 * Name: MapFragmentTest.java
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
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.view.KeyEvent;
import android.view.View;
import android.graphics.Canvas;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.Quadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presenters.SerleenaActivity;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.widgets.MapWidget;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.ITrack;
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

import java.lang.Exception;
import java.lang.Override;
import java.lang.Throwable;
import java.util.ArrayList;
import java.util.Map;

import dalvik.annotation.TestTarget;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Contiene i test di unità per la classe MapFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.MapFragment
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class MapFragmentTest {

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
    private MapFragment fragment;
    private IMapPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).
                create().get();
        Assert.assertNotNull("initialization failed", activity);
        fragment = new MapFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment, "TEST").commit();
        Assert.assertEquals("fragment not attached", fragment.getActivity(), activity);

    }

    /**
     * Verifica che sia possibile collegare un IMapPresenter ad un
     * MapFragment.
     *
     */
    @Test
    public void testAttachMapPresenter() {
        presenter = mock(IMapPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.onResume();
        fragment.onPause();
        verify(presenter).resume();
        verify(presenter).pause();
    }

    /**
     * Verifica che sia possibile impostare il widget.
     *
     */
    @Test
    public void testCanSetGetWidget() {
        MapWidget widget = mock(MapWidget.class);
        fragment.setMapWidget(widget);
        Assert.assertEquals("set/get problems", widget, fragment.getMapWidget());
    }

    /**
     * Verifica che sia possibile impostare la posizione dell'utente.
     *
     */
    @Test
    public void testCanSetUserPosition() {
        MapWidget widget = mock(MapWidget.class);
        GeoPoint point = mock(GeoPoint.class);
        fragment.setMapWidget(widget);
        fragment.setUserLocation(point);
        verify(widget).setUserPosition(point);
        verify(widget).draw((Canvas) any());
    }

    /**
     * Verifica che sia possibile mostrare un quadrante.
     *
     */
    @Test
    public void testCanDisplayQuadrant() {
        MapWidget widget = mock(MapWidget.class);
        IQuadrant q = mock(IQuadrant.class);
        fragment.setMapWidget(widget);
        fragment.displayQuadrant(q);
        verify(widget).setQuadrant(q);
        verify(widget).draw((Canvas) any());
    }

    /**
     * Verifica che sia possibile mostrare un quadrante.
     *
     */
    @Test
    public void testCanDisplayUPs() {
        MapWidget widget = mock(MapWidget.class);
        ArrayList<UserPoint> a = new ArrayList<>();
        a.add(mock(UserPoint.class));
        a.add(mock(UserPoint.class));
        fragment.setMapWidget(widget);
        fragment.displayUP(a);
        verify(widget).setUserPoints(a);
        verify(widget).draw((Canvas) any());
    }

    /**
     * Verifica che alla pressione del pulsante centrale venga richiesta
     * la creazione di un Punto Utente.
     *
     */
    @Test
    public void testShouldRequestNewUserPointOnKeyDown() {
        presenter = mock(IMapPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.keyDown(0, new KeyEvent(1, KeyEvent.KEYCODE_ENTER));
        try {verify(presenter).newUserPoint();}
        catch (NoActiveExperienceException e) {}
    }

    /**
     * Verifica che un'eccezione venga presa dal Fragment, se lanciata
     * dal IMapPresenter.
     *
     */
    @Test
    public void testShouldCauseExceptionOnKeyDown() {
        presenter = mock(IMapPresenter.class);
        try { doThrow(new NoActiveExperienceException()).
                when(presenter).newUserPoint(); }
        catch (NoActiveExperienceException e) {}
        fragment.attachPresenter(presenter);
        fragment.keyDown(0, new KeyEvent(1, KeyEvent.KEYCODE_ENTER));
    }
}
