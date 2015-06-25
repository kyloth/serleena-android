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
 * Name: TelemetryPresenter.java
 * Package: com.hitchikers.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer        Changes
 * 1.0        Filippo Sestini   Creazione del file
 */

package com.kyloth.serleena.presenters;

import com.kyloth.serleena.presentation.ITelemetryPresenter;
import com.kyloth.serleena.presentation.ITelemetryView;
import com.kyloth.serleena.sensors.ITelemetryManager;
import com.kyloth.serleena.sensors.TrackAlreadyStartedException;

/**
 * Concretizza ITelemetryPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field telMan : ITelemetryManager Gestore dei Tracciamenti dell'applicazione
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TelemetryPresenter implements ITelemetryPresenter {

    private ITelemetryManager telMan;
    private ITelemetryView view;

    /**
     * Crea un oggetto TelemetryPresenter.
     *
     * Si collega alla vista tramite il metodo attachPresenter() esposto
     * dall'interfaccia della vista.
     *
     * @param view Vista da associare al presenter. Se null,
     *             viene sollevata un'eccezione IllegalArgumentException.
     * @param activity Activity che rappresenta l'applicazione in corso e al
     *                 quale il presenter appartiene. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public TelemetryPresenter(ITelemetryView view, ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.telMan = activity.getSensorManager().getTelemetryManager();
        this.view = view;
        view.attachPresenter(this);
    }

    /**
     * Implementa ITelemetryPresenter.enableTelemetry().
     *
     * Segnala all'activity associata al presenter che l'utente ha abilitato
     * il tracciamento.
     */
    @Override
    public void enableTelemetry() throws TrackAlreadyStartedException {
        telMan.enable();
    }

    /**
     * Implementa ITelemetryPresenter.disableTelemetry().
     *
     * Segnala all'activity associata al presenter che l'utente ha disabilitato
     * il tracciamento.
     */
    @Override
    public void disableTelemetry() {
        telMan.disable();
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Non effettua operazioni, in quanto non vi sono particolari contenuti
     * da presentare, e non vengono utilizzate risorse da rilasciare.
     */
    @Override
    public void resume() {

    }

    /**
     * Implementa IPresenter.pause().
     *
     * Non effettua operazioni, in quanto non vi sono particolari contenuti
     * da presentare, e non vengono utilizzate risorse da rilasciare.
     */
    @Override
    public void pause() {

    }

}
