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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.sensors.ITrackCrossing;
import com.kyloth.serleena.sensors.NoSuchCheckpointException;
import com.kyloth.serleena.sensors.NoTrackCrossingException;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Contiene i test di unità per la classe TrackPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class TrackPresenterTest {

    private ITrack getTrack() throws NoSuchTelemetryException {
        DirectAccessList<Checkpoint> checkpoints =
                new DirectAccessList<Checkpoint>() {
            @Override
            public int size() {
                return Integer.MAX_VALUE;
            }
            @Override
            public Checkpoint get(int index) {
                return new Checkpoint(0, 0);
            }
            @Override
            public Iterator<Checkpoint> iterator() {
                return null;
            }
        };
        ITrack t = mock(ITrack.class);
        when(t.getCheckpoints()).thenReturn(checkpoints);
        when(t.getBestTelemetry()).thenThrow(NoSuchTelemetryException.class);
        return t;
    }

    private ITrack getOneCheckpointTrack() throws NoSuchTelemetryException {
        return getOneCheckpointTrack(new Checkpoint(0, 0));
    }

    private ITrack getOneCheckpointTrack(final Checkpoint cp)
            throws NoSuchTelemetryException {
        DirectAccessList<Checkpoint> checkpoints =
                new DirectAccessList<Checkpoint>() {
                    @Override public int size() { return 1; }
                    @Override public Checkpoint get(int index) {
                        return cp;
                    }
                    @Override public Iterator<Checkpoint> iterator() {
                        return null;
                    }
                };
        ITrack track = mock(ITrack.class);
        when(track.getCheckpoints()).thenReturn(checkpoints);
        when(track.getBestTelemetry()).thenThrow(NoSuchTelemetryException.class);
        return track;
    }

    private void setTrackCrossing(ITrack track, int lastCrossed)
            throws NoSuchCheckpointException, NoTrackCrossingException {
        when(tc.getTrack()).thenReturn(track);
        if (lastCrossed >= 0)
            when(tc.getLastCrossed()).thenReturn(lastCrossed);
        else
            when(tc.getLastCrossed()).thenThrow(NoSuchCheckpointException
                    .class);
        if (lastCrossed == track.getCheckpoints().size() - 1) {
            when(tc.getNextCheckpoint()).thenThrow(NoTrackCrossingException
                    .class);
            when(tc.isTrackCrossing()).thenReturn(false);
        }
        else {
            when(tc.getNextCheckpoint()).thenReturn(lastCrossed + 1);
            when(tc.isTrackCrossing()).thenReturn(true);
        }
    }

    ILocationManager locMan;
    IHeadingManager hMan;
    ITrackCrossing tc;
    ISensorManager sm;
    ISerleenaActivity activity;
    ITrackView view;
    ITrack singleCheckpointTrack;
    ITrack track;

    @Before
    public void initialize() throws SensorNotAvailableException,
            NoSuchTelemetryException, NoTrackCrossingException {
        singleCheckpointTrack = getOneCheckpointTrack();
        track = getTrack();
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
    }

    /**
     * Verifica che il Presenter si agganci alla vista nel costruttore.
     */
    @Test
    public void presenterShouldAttachHimself() {
        TrackPresenter tp = new TrackPresenter(view, activity);
        verify(view).attachPresenter(tp);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se invocato con view
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowWhenNullView() {
        new TrackPresenter(null, mock(ISerleenaActivity.class));
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se invocato con activity
     * null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowWhenNullActivity() {
        new TrackPresenter(mock(ITrackView.class), null);
    }

    /**
     * Verifica che una chiamata a resume() quando non vi sono Percorsi
     * attivi non comporti la registrazione agli eventi dei sensori.
     */
    @Test
    public void resumeWhenNoTrackActiveShouldNotRegisterSensors()
            throws SensorNotAvailableException, NoTrackCrossingException,
            NoSuchCheckpointException {
        TrackPresenter tp = new TrackPresenter(view, activity);
        when(tc.getTrack()).thenThrow(NoTrackCrossingException.class);
        when(tc.isTrackCrossing()).thenReturn(false);

        tp.resume();
        verify(view).clearView();
        verify(locMan, never()).attachObserver(tp,
                TrackPresenter.UPDATE_INTERVAL_SECONDS);
        verify(hMan, never()).attachObserver(tp);
    }

    /**
     * Verifica che alla chiamata di pause() sul Presenter, venga cancellata
     * la registrazione ai sensori.
     */
    @Test
    public void pauseShouldUnregisterSensors()
            throws SensorNotAvailableException, NoTrackCrossingException {
        TrackPresenter tp = new TrackPresenter(view, activity);
        tp.pause();

        verify(locMan).detachObserver(tp);
        verify(hMan).detachObserver(tp);
    }

    @Test
    public void resumingWithActiveTrackShouldRegisterSensors()
            throws SensorNotAvailableException, NoTrackCrossingException,
            NoSuchTelemetryException, NoSuchCheckpointException {
        TrackPresenter tp = new TrackPresenter(view, activity);
        setTrackCrossing(track, 0);
        tp.resume();
        verify(locMan).attachObserver(tp,
                TrackPresenter.UPDATE_INTERVAL_SECONDS);
        verify(hMan).attachObserver(tp);
    }

    /**
     * Verifica che, successivamente alla chiamata di resume con un Percorso
     * appena terminato, venga impostata di conseguenza la vista.
     */
    @Test
    public void resumingWithTrackEndedShouldDisplayOnView()
            throws NoTrackCrossingException, NoSuchCheckpointException {
        setTrackCrossing(singleCheckpointTrack, 0);
        TrackPresenter tp = new TrackPresenter(view, activity);
        tp.resume();
        verify(view).displayTrackEnded();
    }

    /**
     * Verifica che una chiamata a resume() con nessun Percorso attivo o
     * terminato pulisca la vista associata al Presenter.
     */
    @Test
    public void resumingWithNoTrackShouldClearView() throws NoTrackCrossingException {
        when(tc.getTrack()).thenThrow(NoTrackCrossingException.class);
        TrackPresenter tp = new TrackPresenter(view, activity);
        tp.resume();
        verify(view).clearView();
    }

    /**
     * Verifica che il metodo updateDistance() imposti il valore corretto sulla
     * vista, in base alla distanza tra l'utente e il prossimo checkpoint da
     * raggiungere.
     */
    @Test
    public void updateDistanceShouldSetCorrectValue() throws
            NoSuchTelemetryException,
            NoTrackCrossingException, NoSuchCheckpointException {
        ITrack track = getOneCheckpointTrack(
                new Checkpoint(50.06638888888889, -5.714722222222222));
        TrackPresenter tp = new TrackPresenter(view, activity);
        setTrackCrossing(track, -1);
        tp.updateDistance(
                new Checkpoint(58.64388888888889, -3.0700000000000003));

        ArgumentCaptor<Integer> argument =
                ArgumentCaptor.forClass(Integer.class);
        verify(view).setDistance(argument.capture());
        Integer res = argument.getValue();
        assertTrue(968000 <= res && res <= 971000);
    }

    /**
     * Verifica che il metodo updateDistance() sollevi un'eccezione al
     * passaggio di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateDistanceShouldThrowWhenNullArguments()
            throws NoTrackCrossingException {
        TrackPresenter tp = new TrackPresenter(view, activity);
        tp.updateDistance(null);
    }

    /**
     * Verifica che il metodo updateDistance() sollevi un'eccezione
     * NoTrackCrossingException quando non vi sono Percorso in corso.
     */
    @Test(expected = NoTrackCrossingException.class)
    public void updateDistanceShouldThrowWhenNoTrackCrossing()
            throws NoSuchTelemetryException, NoTrackCrossingException,
            NoSuchCheckpointException {
        ITrack track = getOneCheckpointTrack();
        TrackPresenter tp = new TrackPresenter(view, activity);
        setTrackCrossing(track, 0);
        tp.updateDistance(new Checkpoint(0, 0));
    }

    /**
     * Verifica che advanceCheckpoint() forwardi la chiamata all'oggetto
     * ITrackCrossing.
     */
    @Test
    public void advanceCheckpointShouldForwardToTrackCrossing()
            throws NoTrackCrossingException, NoSuchTelemetryException,
            NoSuchCheckpointException {
        ITrack track = getOneCheckpointTrack();
        TrackPresenter tp = new TrackPresenter(view, activity);
        setTrackCrossing(track, -1);
        tp.advanceCheckpoint();
        verify(tc).advanceCheckpoint();
    }

    @Test
    public void updateDeltaShouldWorkCorrectly()
            throws NoSuchTelemetryException, NoTrackCrossingException,
            NoSuchCheckpointException {
        ITelemetry best = new ITelemetry() {
            @Override
            public Iterable<TelemetryEvent> getEvents() { return null; }
            @Override
            public Iterable<TelemetryEvent> getEvents(
                    Predicate<TelemetryEvent> predicate)
                    throws NoSuchTelemetryEventException {
                List<TelemetryEvent> list = new ArrayList<>();
                list.add(new CheckpointReachedTelemetryEvent(200, 0));
                return list;
            }
            @Override
            public int getDuration() { return 0; }
        };

        ITrack track = mock(ITrack.class);
        when(track.getBestTelemetry()).thenReturn(best);
        when(tc.getTrack()).thenReturn(track);
        when(tc.getLastCrossed()).thenReturn(0);
        when(tc.lastPartialTime()).thenReturn(150);

        TrackPresenter tp = new TrackPresenter(view, activity);
        tp.updateDelta();
        verify(view).setDelta(-50);
    }

}
