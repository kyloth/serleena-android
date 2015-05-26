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
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ITelemetryView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ISensorManager;

/**
 * Contiene i test di unità per la classe TelemetryPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TelemetryPresenterTest {
    ISerleenaActivity activity;
    ITelemetryView view;
    ISensorManager sm;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        activity = mock(ISerleenaActivity.class);
        view = mock(ITelemetryView.class);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con messaggio "Illegal null view" al tentativo di costruire un oggetto
     * con view nulla.
     */

    @Test
    public void constructorShouldThrowWxceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        TelemetryPresenter null_view_tp = new TelemetryPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con messaggio "Illegal null activity" al tentativo di costruire un oggetto
     * con activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        TelemetryPresenter null_view_tp = new TelemetryPresenter(view, null);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter sulla view
     * fornendo il nuovo TelemetryPresenter creato come parametro.
     */

    @Test
    public void constructorShouldCallAttachPresenterWithCorrectParam() {
        TelemetryPresenter tp = new TelemetryPresenter(view, activity);
        verify(view).attachPresenter(tp);
    }

    /**
     * Verifica che il metodo enableTelemetry chiami a sua volta il metodo
     * enableTelemetry su activity.
     */

    @Test
    public void enableTelemetryShouldForwardCallToActivity() {
        TelemetryPresenter tp = new TelemetryPresenter(view, activity);
        tp.enableTelemetry();
        verify(activity).enableTelemetry();
    }

    /**
     * Verifica che il metodo disableTelemetry chiami a sua volta il metodo
     * disableTelemetry su activity.
     */

    @Test
    public void disableTelemetryShouldForwardCallToActivity() {
        TelemetryPresenter tp = new TelemetryPresenter(view, activity);
        tp.disableTelemetry();
        verify(activity).disableTelemetry();
    }

}
