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
 * Name: Telemetry
 * Package: com.hitchikers.serleena.model
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.persistence.ITelemetryStorage;

import java.util.ArrayList;

/**
 * Concretizza ITelemetry.
 *
 * @use Utilizzata dal Model e dalla parte di presentazione. L'uso principale viene fatto da TrackPresenter, che utilizza i Tracciamenti dell'esperienza attiva per confrontarli con l'attività in corso.
 * @field storage : ITelemetryStorage Oggetto Tracciamento del livello di persistenza, utilizzato come sorgente dati effettiva
 * @field allEvents : Iterable<TelemetryEvent> Insieme di tutti gli eventi del Tracciamento
 * @field locEvents : Iterable<TelemetryEvent> Insieme di tutti gli eventi del Tracciamento relativi alla posizione utente
 * @field duration : int Durata in secondi del Tracciamento
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class Telemetry implements  ITelemetry {

    private ITelemetryStorage storage;

    /**
     * Crea un nuovo oggetto Telemetry.
     *
     * Incapsula un oggetto che realizza la persistenza del Tracciamento, e
     * rappresenta la sorgente dati.
     *
     * @param storage Oggetto ITelemetryStorage che realizza la persistenza del
     *                Tracciamento.
     */
    public Telemetry(ITelemetryStorage storage) {
        this.storage = storage;
    }

    /**
     * Implementa ITelemetry.getEvents().
     *
     * @return Insieme enumerabile di eventi di Tracciamento.
     */
    @Override
    public Iterable<TelemetryEvent> getEvents() {
        return storage.getEvents();
    }

    /**
     * Implementa ITelemetry.getEvents().
     *
     * @param predicate Predicato.
     * @return Insieme enumerabile di eventi che soddisfano il predicato.
     */
    @Override
    public Iterable<TelemetryEvent> getEvents(
            Predicate<TelemetryEvent> predicate)
            throws NoSuchTelemetryEventException {
        Iterable<TelemetryEvent> events = this.getEvents();
        ArrayList<TelemetryEvent> result = new ArrayList<TelemetryEvent>();

        for (TelemetryEvent e : events)
            if (predicate.apply(e))
                result.add(e);

        if (result.size() == 0)
            throw new NoSuchTelemetryEventException();

        return result;
    }

    /**
     * Implementa ITelemetry.getDuration().
     *
     * @return Durata del Tracciamento, in secondi.
     */
    @Override
    public int getDuration() {
        int duration = 0;
        Iterable<TelemetryEvent> allEvents = this.getEvents();
        for (TelemetryEvent e : allEvents)
            if (e.timestamp() > duration)
                duration = e.timestamp();
        return duration;
    }

}
