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
 * Name: TelemetryPresenterTest.java
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
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import com.kyloth.serleena.presentation.ITelemetryView;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.ITelemetryManager;
import com.kyloth.serleena.sensors.TrackAlreadyStartedException;

/**
 * Contiene i test di unità per la classe TelemetryPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TelemetryPresenterTest {

    private ISerleenaActivity getActivity() {
        ITelemetryManager telMan = mock(ITelemetryManager.class);
        ISensorManager sm = mock(ISensorManager.class);
        ISerleenaActivity activity = mock(ISerleenaActivity.class);
        when(sm.getTelemetryManager()).thenReturn(telMan);
        when(activity.getSensorManager()).thenReturn(sm);

        return activity;
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * quando vengono passati parametri null al costruttore.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments1() {
        new TelemetryPresenter(null, getActivity());
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * quando vengono passati parametri null al costruttore.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments2() {
        new TelemetryPresenter(mock(ITelemetryView.class), null);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * quando vengono passati parametri null al costruttore.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments3() {
        new TelemetryPresenter(null, null);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter sulla view
     * fornendo il nuovo TelemetryPresenter creato come parametro.
     */
    @Test
    public void constructorShouldCallAttachPresenterWithCorrectParam() {
        ITelemetryView view = mock(ITelemetryView.class);
        TelemetryPresenter tp =
                new TelemetryPresenter(view, getActivity());
        verify(view).attachPresenter(tp);
    }

    /**
     * Verifica che il metodo enableTelemetry chiami a sua volta il metodo
     * enableTelemetry su activity.
     */
    @Test
    public void enableTelemetryShouldForwardCallToActivity()
            throws TrackAlreadyStartedException {
        ITelemetryManager telMan = mock(ITelemetryManager.class);
        ISensorManager sm = mock(ISensorManager.class);
        ISerleenaActivity activity = mock(ISerleenaActivity.class);
        when(sm.getTelemetryManager()).thenReturn(telMan);
        when(activity.getSensorManager()).thenReturn(sm);

        TelemetryPresenter tp =
                new TelemetryPresenter(mock(ITelemetryView.class), activity);
        tp.enableTelemetry();
        verify(telMan).enable();
    }

    /**
     * Verifica che il metodo disableTelemetry chiami a sua volta il metodo
     * disableTelemetry su activity.
     */
    @Test
    public void disableTelemetryShouldForwardCallToActivity() {
        ITelemetryManager telMan = mock(ITelemetryManager.class);
        ISensorManager sm = mock(ISensorManager.class);
        ISerleenaActivity activity = mock(ISerleenaActivity.class);
        when(sm.getTelemetryManager()).thenReturn(telMan);
        when(activity.getSensorManager()).thenReturn(sm);

        TelemetryPresenter tp =
                new TelemetryPresenter(mock(ITelemetryView.class), activity);
        tp.disableTelemetry();
        verify(telMan).disable();
    }

    /**
     * Verifica che un tentativo di abilitazione del Tracciamento con
     * Percorso già avviato causi una segnalazione alla vista.
     */
    @Test
    public void enablingWithTrackAlreadyStartedShouldThrow() throws
            TrackAlreadyStartedException {
        ITelemetryManager telMan = mock(ITelemetryManager.class);
        ISensorManager sm = mock(ISensorManager.class);
        ISerleenaActivity activity = mock(ISerleenaActivity.class);
        when(sm.getTelemetryManager()).thenReturn(telMan);
        when(activity.getSensorManager()).thenReturn(sm);

        Mockito.doThrow(TrackAlreadyStartedException.class)
                .when(telMan).enable();

        ITelemetryView view = mock(ITelemetryView.class);
        TelemetryPresenter tp =
                new TelemetryPresenter(view, activity);
        tp.enableTelemetry();
        verify(view).displayTrackStartedError();
    }

}
