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
 * Name: TrackPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.CheckpointCrossing;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.sensors.ITrackCrossing;
import com.kyloth.serleena.sensors.NoActiveTrackException;
import com.kyloth.serleena.sensors.NoSuchCheckpointException;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Contiene i test di unità per la classe TrackPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public class TrackPresenterTest {

    private ILocationManager locMan;
    private IHeadingManager hMan;
    private ITrackCrossing tc;
    private ISensorManager sm;
    private ISerleenaActivity activity;
    private ITrackView view;
    private TrackPresenter presenter;

    @Before
    public void initialize() throws SensorNotAvailableException,
            NoSuchTelemetryException, NoTrackCrossingException {
        locMan = mock(ILocationManager.class);
        hMan = mock(IHeadingManager.class);
        tc = mock(ITrackCrossing.class);
        sm = mock(ISensorManager.class);
        view = mock(ITrackView.class);
        activity = mock(ISerleenaActivity.class);
        when(sm.getHeadingSource()).thenReturn(hMan);
        when(sm.getLocationSource()).thenReturn(locMan);
        when(sm.getTrackCrossingManager()).thenReturn(tc);
        when(activity.getSensorManager()).thenReturn(sm);
        presenter = new TrackPresenter(view, activity);
    }

    /**
     * Verifica che il Presenter si agganci alla vista nel costruttore.
     */
    @Test
    public void presenterShouldAttachHimself() {
        verify(view).attachPresenter(presenter);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se invocato con view
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullView() {
        new TrackPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se invocato con activity
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowWhenNullActivity() {
        new TrackPresenter(view, null);
    }

    /**
     * Verifica che alla chiamata di pause() sul Presenter, venga cancellata
     * la registrazione ai sensori.
     */
    @Test
    public void pauseShouldUnregisterSensors()
            throws SensorNotAvailableException, NoTrackCrossingException {
        presenter.pause();

        verify(locMan).detachObserver(presenter);
        verify(hMan).detachObserver(presenter);
    }

    /**
     * Verifica che il presenter si registri ai gestori di sensoristica on
     * resume.
     */
    @Test
    public void resumingShouldRegisterSensors()
            throws SensorNotAvailableException, NoTrackCrossingException,
            NoSuchTelemetryException, NoSuchCheckpointException, NoActiveTrackException {
        Mockito.doThrow(NoActiveTrackException.class).when(tc).getTrack();
        presenter.resume();
        verify(locMan).attachObserver(
                presenter, TrackPresenter.UPDATE_INTERVAL_SECONDS);
        verify(hMan).attachObserver(presenter);
    }

    /**
     * Verifica che una chiamata a resume() pulisca la vista associata al
     * Presenter.
     */
    @Test
    public void resumingWithNoTrackShouldClearView()
            throws NoActiveTrackException {
        Mockito.doThrow(NoActiveTrackException.class).when(tc).getTrack();
        presenter.resume();
        verify(view).clearView();
    }

    /**
     * Verifica che advanceCheckpoint() forwardi la chiamata all'oggetto
     * ITrackCrossing.
     */
    @Test
    public void advanceCheckpointShouldForwardToTrackCrossing()
            throws NoTrackCrossingException, NoActiveTrackException {
        presenter.advanceCheckpoint();
        verify(tc).advanceCheckpoint();
    }

    /**
     * Verifica che advanceCheckpoint() sollevi un'eccezione NoTrackCrossing
     * se nessun Percorso è in esecuzione.
     */
    @Test(expected = NoTrackCrossingException.class)
    public void advanceCheckpointShouldThrowIfNoTrackCrossing()
            throws NoTrackCrossingException, NoActiveTrackException {
        Mockito.doThrow(NoTrackCrossingException.class)
                .when(tc).advanceCheckpoint();
        presenter.advanceCheckpoint();
    }

    /**
     * Verifica che la vista venga impostata con il tempo parziale al
     * raggiungimento di un checkpoint.
     */
    @Test
    public void activePresenterShouldSetViewWithPartialWhenCheckpointCrossed()
            throws NoActiveTrackException, NoSuchCheckpointException {
        ITrack track = mock(ITrack.class);
        when(track.name()).thenReturn("track");
        when(track.getCheckpoints()).thenReturn(new ListAdapter<Checkpoint>
                (new ArrayList<Checkpoint>()));
        CheckpointCrossing crossing = mock(CheckpointCrossing.class);
        when(crossing.partialTime()).thenReturn(300);
        when(tc.getLastCrossed()).thenReturn(crossing);
        when(tc.getTrack()).thenReturn(track);
        presenter.resume();
        verify(view, times(1)).setLastPartial(300);
        presenter.onCheckpointCrossed();
        verify(view, times(2)).setLastPartial(300);
    }

}
