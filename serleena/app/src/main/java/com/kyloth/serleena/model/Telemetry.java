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
 * Date: 2015-05-22
 *
 * History:
 * Version    Programmer        Date        Changes
 * 1.0        Filippo Sestini   2015-05-22  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.persistence.ITelemetryStorage;

import java.util.ArrayList;

/**
 * Concretizza ITelemetry.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class Telemetry implements  ITelemetry {

    private ITelemetryStorage storage;

    private Iterable<TelemetryEvent> allEvents = null;
    private Iterable<TelemetryEvent> locEvents = null;
    private int duration = -1;

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
        if (allEvents == null)
            allEvents = storage.getEvents();
        return allEvents;
    }

    /**
     * Implementa ITelemetry.getEvents().
     *
     * @param from L'inizio dell'intervallo, comprensivo degli
     *             eventi da restituire, espresso in secondi dall'avvio del
     *             Tracciamento.
     *             Se from > to, viene sollevata un'eccezione
     *             IllegalArgumentException.
     * @param to L'inizio dell'intervallo, comprensivo degli
     *           eventi da restituire, espresso in secondi dall'avvio del
     *           Tracciamento.
     *           Se from > to, viene sollevata un'eccezione
     *           IllegalArgumentException.
     * @return Insieme enumerabile di eventi di Tracciamento.
     * @throws NoSuchTelemetryEventException
     * @throws IllegalArgumentException
     */
    @Override
    public Iterable<TelemetryEvent> getEvents(int from, int to)
            throws NoSuchTelemetryEventException, IllegalArgumentException {
        Iterable<TelemetryEvent> allEvents = this.getEvents();
        ArrayList<TelemetryEvent> result = new ArrayList<TelemetryEvent>();

        for (TelemetryEvent e : allEvents)
            if (from <= e.timestamp() && e.timestamp() <= to)
                result.add(e);

        if (result.size() == 0)
            throw new NoSuchTelemetryEventException();
        return result;
    }

}
