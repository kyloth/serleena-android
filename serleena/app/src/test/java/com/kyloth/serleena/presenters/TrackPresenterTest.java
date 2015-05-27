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
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationReachedManager;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.ITelemetryManager;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Contiene i test di unità per la classe TrackPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TrackPresenterTest {

    ISerleenaActivity activity;
    ITrackView view;
    ITelemetryManager telMan;
    ILocationManager locMan;
    ILocationReachedManager lrMan;
    IHeadingManager hMan;
    ISensorManager sMan;
    ITrack activeTrack;
    Checkpoint cp1;
    Checkpoint cp2;
    Checkpoint cp3;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() throws SensorNotAvailableException {
        activity = mock(ISerleenaActivity.class);
        view = mock(ITrackView.class);
        sMan = mock(ISensorManager.class);
        telMan = mock(ITelemetryManager.class);
        locMan = mock(ILocationManager.class);
        lrMan = mock(ILocationReachedManager.class);
        hMan = mock(IHeadingManager.class);
        activeTrack = mock(ITrack.class);
        cp1 = mock(Checkpoint.class);
        cp2 = mock(Checkpoint.class);
        cp3 = mock(Checkpoint.class);
        when(activity.getSensorManager()).thenReturn(sMan);
        when(sMan.getTelemetryManager()).thenReturn(telMan);
        when(sMan.getLocationSource()).thenReturn(locMan);
        when(sMan.getLocationReachedSource()).thenReturn(lrMan);
        when(activeTrack.getCheckpoints()).thenReturn(new ListAdapter(Arrays.asList(new Checkpoint[] {cp1, cp2, cp3})));
        when(sMan.getHeadingSource()).thenThrow(SensorNotAvailableException.class);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio Illegal null view
     * se invocato con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        TrackPresenter presenter = new TrackPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione di tipo
     * IllegalArgumentException con messaggio Illegal null activiy
     * se invocato con activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        TrackPresenter presenter = new TrackPresenter(view, null);
    }

    /**
     * Verifica il metodo nextCheckpoint tramite le chiamate di
     * advanceCheckpoint e onLocationReached.
     */

    @Test
    public void testAdvanceCheckpointAndOnLocationReached() {
        TrackPresenter presenter = new TrackPresenter(view, activity);
        presenter.setActiveTrack(activeTrack);
        presenter.onTelemetryEnabled();
        presenter.advanceCheckpoint();
        verify(view).setCheckpointNo(eq(1));
        presenter.onTelemetryDisabled();
        verify(telMan).stop();
        presenter.advanceCheckpoint();
        verify(view).setCheckpointNo(eq(2));
        presenter.onTelemetryEnabled();
        presenter.onLocationReached();
        verify(view).clearView();
        verify(telMan, times(2)).stop();
    }

    /**
     * Verifica che i metodi pause e resume passino correttamente
     * i parametri ai diversi manager.
     */

    @Test
    public void testPauseAndResume() throws SensorNotAvailableException {
        TrackPresenter presenter = new TrackPresenter(view, activity);
        exception.expect(SensorNotAvailableException.class);
        presenter.resume();
        verify(locMan).attachObserver(presenter, 60);
        when(sMan.getHeadingSource()).thenReturn(hMan);
        presenter.resume();
        verify(hMan).attachObserver(presenter, 60);
        presenter.pause();
        verify(hMan).detachObserver(presenter);
    }

    /**
     * Verifica che il metodo updateView passi correttamente i
     * parametri ai metodi della view e che la chiamata
     * di computeDelta sollevi un'eccezione corretta qualora
     * non fosse fornito un parametro di tipo ITelemetry.
     */

    @Test
    public void testUpdateView() throws NoSuchTelemetryException,
        NoSuchTelemetryEventException {
        TrackPresenter presenter = new TrackPresenter(view, activity);
        GeoPoint loc = mock(GeoPoint.class);
        ITelemetry bestTelemetry = mock(ITelemetry.class);
        LocationTelemetryEvent event = mock(LocationTelemetryEvent.class);
        presenter.setActiveTrack(activeTrack);
        when(loc.distanceTo(cp1)).thenReturn((float) 15);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null telemetry");
        presenter.updateView(loc);
        verify(view).setDistance(15);
        when(activeTrack.getBestTelemetry()).thenReturn(bestTelemetry);
        when(bestTelemetry.getEventAtLocation(loc, 30)).thenReturn(event);
        when(event.timestamp()).thenReturn(0);
        presenter.updateView(loc);

    }


}
