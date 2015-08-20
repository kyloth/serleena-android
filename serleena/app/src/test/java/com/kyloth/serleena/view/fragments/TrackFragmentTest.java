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
 * Name: TrackFragmentTest.java
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
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.view.widgets.CompassWidget;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.CompassFragment
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19, manifest = "src/main/AndroidManifest.xml")
public class TrackFragmentTest {

    private TrackFragment fragment;
    private ITrackPresenter presenter;
    private TextView trackNameText;
    private TextView nextCheckpointText;
    private TextView distanceText;
    private CompassWidget orientationWidget;
    private TextView deltaText;
    private TextView lastPartialText;
    private Activity activity;

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(Activity.class)
                .create().start().visible().get();
        fragment = new TrackFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment, "TEST").commit();

        trackNameText = mock(TextView.class);
        nextCheckpointText = mock(TextView.class);
        distanceText = mock(TextView.class);
        orientationWidget = mock(CompassWidget.class);
        deltaText = mock(TextView.class);
        lastPartialText = mock(TextView.class);

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        when(inflater.inflate(
                        eq(R.layout.fragment_track),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.track_name_text)).thenReturn(trackNameText);
        when(v.findViewById(R.id.checkpoint_numbers_text))
                .thenReturn(nextCheckpointText);
        when(v.findViewById(R.id.distance_text)).thenReturn(distanceText);
        when(v.findViewById(R.id.compass_widget_track))
                .thenReturn(orientationWidget);
        when(v.findViewById(R.id.delta_text)).thenReturn(deltaText);
        when(v.findViewById(R.id.last_partial_text))
                .thenReturn(lastPartialText);

        fragment.onCreateView(inflater, vg, mock(Bundle.class));
        presenter = mock(ITrackPresenter.class);
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che il tentativo di associare un presenter null alla vista
     * sollevi un'eccezione IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void fragmentShouldThrowIfAttachingANullPresenter() {
        fragment.attachPresenter(null);
    }

    /**
     * Verifica che gli eventi di resume e pause del fragment vengano
     * inoltrati al presenter collegato.
     */
    @Test
    public void testAttachCompassPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    @Test
    public void settingEndedCheckpointShouldSetViewAccordingly() {
        fragment.displayTrackEnded();
        String trackEnd = RuntimeEnvironment.application.getResources().getString(R.string.track_finish);
        verify(nextCheckpointText).setText(trackEnd);
    }

    @Test
    public void settingCheckpointNumbersShouldSetViewAccordingly() {
        fragment.setTotalCheckpoints(5);
        verifyZeroInteractions(nextCheckpointText);
        fragment.setCheckpointNo(3);
        verify(nextCheckpointText).setText("3/5");
    }

    @Test
    public void fragmentShouldThrowWhenCheckpointNumberIsNullOrNegative() {
        boolean ex1 = false;
        boolean ex2 = false;

        try {
            fragment.setCheckpointNo(0);
        } catch (IllegalArgumentException e) {
            ex1 = true;
        }
        try {
            fragment.setCheckpointNo(-1);
        } catch (IllegalArgumentException e) {
            ex2 = true;
        }

        assertTrue(ex1 && ex2);
    }

    @Test
    public void settingDeltaShouldSetViewAccordingly() {
        fragment.setDelta(300);
        verify(deltaText).setText("+05:00");
        fragment.setDelta(-300);
        verify(deltaText).setText("-05:00");
    }

    @Test
    public void settingDistanceShouldSetViewAccordingly() {
        fragment.setDistance(300);
        verify(distanceText).setText("300 m");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fragmentShouldThrowWhenSettingNegativeDistance() {
        fragment.setDistance(0);
        verify(distanceText).setText("0 m");
        fragment.setDistance(-1);
    }

    @Test
    public void settingLastPartialShouldSetViewAccordingly() {
        fragment.setLastPartial(20);
        verify(lastPartialText).setText("00:20");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fragmentShouldThrowWhenSettingNegativePartial() {
        fragment.setLastPartial(0);
        verify(lastPartialText).setText("00:00");
        fragment.setLastPartial(-1);
    }

    @Test
    public void settingHeadingShouldSetViewAccordingly() {
        fragment.setDirection(45);
        verify(orientationWidget).setOrientation(45);
    }

    @Test
    public void settingTrackNameShouldSetViewAccordingly() {
        fragment.setTrackName("Track1");
        verify(trackNameText).setText("Track1");
    }

    @Test
    public void clearingDeltaShouldClearItsElementInTheView() {
        fragment.clearStats();
        verify(lastPartialText).setText("");
        verify(deltaText).setText("");
    }

    @Test
    public void clearingViewShouldClearAllOfItsElements() {
        fragment.clearView();
        String noActiveTrack = RuntimeEnvironment.application.getResources().getString(R.string.track_noActiveTrack);
        verify(trackNameText).setText(noActiveTrack);
        verify(nextCheckpointText).setText("");
        verify(distanceText).setText("");
        verify(orientationWidget).setOrientation(0);
        verify(deltaText).setText("");
        verify(lastPartialText).setText("");
    }

}
