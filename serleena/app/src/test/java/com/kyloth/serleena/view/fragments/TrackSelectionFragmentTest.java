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
 * Name: TrackSelectionFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 * 1.1.0    Filippo Sestini  Aumento copertura.
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.FragmentManager;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.presentation.ITrackSelectionPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19, manifest = "src/main/AndroidManifest.xml")
public class TrackSelectionFragmentTest {

    private static class TestActivity
            extends Activity implements ISerleenaActivity {
        public ISerleenaDataSource getDataSource() {
            return null;
        }
        public ISensorManager getSensorManager() {
            return null;
        }
        public IPersistenceDataSink getDataSink() { return null; }
    }

    private Activity activity;
    private TrackSelectionFragment fragment;
    private ITrackSelectionPresenter presenter;

    /**
     * Inizializza il test.
     */
    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().visible().get();
        fragment = new TrackSelectionFragment();
        FragmentManager fm = activity.getFragmentManager();
        fm.beginTransaction().add(fragment, "TEST").commit();
        presenter = mock(ITrackSelectionPresenter.class);
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che resume() e pause() vengano chiamati correttamente dalla
     * vista.
     */
    @Test
    public void presenterShouldBeAttachable() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che setTracks() sollevi un'eccezione quando il parametro è null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setTracksShouldThrowWhenNullArgument() {
        fragment.setTracks(null);
    }

    /**
     * Verifica che attachPresenter() sollevi un'eccezione quando il
     * parametro è null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachPresenterShouldThrowWhenNullArgument() {
        fragment.attachPresenter(null);
    }

    /**
     * Verifica che alla selezione di un elemento di lista da parte
     * dell'utente, venga segnalato correttamente al presenter il Percorso
     * selezionato.
     */
    @Test
    public void presenterShouldBeSignaledWithRightTrack() {
        ArrayList<ITrack> list = new ArrayList<>();
        list.add(mock(ITrack.class));
        ITrack track = mock(ITrack.class);
        list.add(track);
        list.add(mock(ITrack.class));

        fragment.setTracks(list);
        fragment.onListItemClick(fragment.getListView(), null, 1, 0);
        verify(presenter).activateTrack(track);
    }

    /**
     * Verifica che toString() restituisca il valore corretto.
     */
    @Test
    public void toStringShouldReturnCorrectValue() {
        assertEquals(fragment.toString(), "Imposta Percorso");
    }

}
