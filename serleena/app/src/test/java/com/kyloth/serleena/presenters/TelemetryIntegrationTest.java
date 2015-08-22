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
 * Name: TelemetryIntegrationTest.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.app.ListFragment;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.sensors.ITelemetryManager;
import com.kyloth.serleena.sensors.ITrackCrossing;
import com.kyloth.serleena.sensors.NoActiveTrackException;
import com.kyloth.serleena.view.fragments.TelemetryFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test di integrazione delle classi che compongono la schermata
 * Tracciamento.
 *
 * Integra le classi dei package presenters, sensors, activity responsabili del
 * funzionamento della schermata Tracciamento.
 * Tali classi sono aggregate come dipendenze di TelemetryPresenter.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class TelemetryIntegrationTest {

    TelemetryFragment fragment;
    TelemetryPresenter presenter;
    ITelemetryManager tm;
    ImageButton button;
    TextView text;
    SerleenaActivity activity;
    final String MENU_EXPERIENCE_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_experienceFragment);
    final String MENU_TELEMETRY_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_telemetryFragment);
    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(SerleenaActivity.class)
                .create().start().resume().visible().get();
        tm = activity.getSensorManager().getTelemetryManager();

        ListFragment menuFragment =
                (ListFragment) activity.getFragmentManager()
                        .findFragmentById(R.id.main_container);
        menuFragment.onResume();
        ListAdapter adapter = menuFragment.getListAdapter();
        ListFragment expFragment = null;
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals(MENU_EXPERIENCE_LABEL))
                expFragment = (ListFragment) adapter.getItem(i);
        activity.onObjectSelected(expFragment);
        expFragment.onResume();
        adapter = expFragment.getListAdapter();
        for (int i = 0; i < adapter.getCount(); i++)
            if (adapter.getItem(i).toString().equals(MENU_TELEMETRY_LABEL))
                fragment = (TelemetryFragment) adapter.getItem(i);
        presenter = new TelemetryPresenter(fragment, activity);

        activity.onObjectSelected(fragment);
        fragment.onResume();
        button = (ImageButton) fragment.getView()
                .findViewById(R.id.telemetry_image);
        text = (TextView) fragment.getView()
                .findViewById(R.id.telemetry_status);
    }

    /**
     * Verifica che l'abilitazione o disabilitazione del Tracciamento sulla
     * vista imposti correttamente il gestore del Tracciamento, abilitandolo
     * o disabilitandolo.
     */
    @Test
    public void clickingTelemetryButtonShouldToggleTelemetryEnabling() {
        button.callOnClick();
        assertTrue(tm.isEnabled());
        button.callOnClick();
        assertTrue(!tm.isEnabled());
    }

    /**
     * Verifica che il tentativo di abilitare il Tracciamento per un Percorso
     * già avviato e in esecuzione fallisca.
     */
    @Test
    public void viewShouldDisplayErrorIfTrackAlreadyStarted()
            throws NoTrackCrossingException, NoActiveTrackException {
        ITrackCrossing tc = activity.getSensorManager()
                .getTrackCrossingManager();
        ITrack track = mock(ITrack.class);
        List<Checkpoint> checkpoints = new ArrayList<>();
        checkpoints.add(new Checkpoint(5, 5));
        checkpoints.add(new Checkpoint(6, 6));
        when(track.getCheckpoints()).thenReturn(
                new com.kyloth.serleena.common.ListAdapter<Checkpoint>(
                        checkpoints));
        tc.startTrack(track);
        tc.advanceCheckpoint();
        button.callOnClick();
        String alreadyStartedError = RuntimeEnvironment.application.getResources().getString(R.string.telemetry_alreadyStartedError);
        assertTrue(text.getText().equals(alreadyStartedError));
    }

}
