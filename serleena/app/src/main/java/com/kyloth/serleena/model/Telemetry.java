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

    /**
     * Implementa ITelemetry.getEvents().
     *
     * @param type Tipo di eventi da restituire.
     * @return Insieme enumerabile di eventi di Tracciamento.
     * @throws NoSuchTelemetryEventException
     */
    @Override
    public Iterable<TelemetryEvent> getEvents(TelemetryEventType type)
            throws NoSuchTelemetryEventException {

        if (type == TelemetryEventType.Location && locEvents != null)
            return locEvents;

        Iterable<TelemetryEvent> allEvents = this.getEvents();
        ArrayList<TelemetryEvent> result = new ArrayList<TelemetryEvent>();

        for (TelemetryEvent e : allEvents)
            if (e.getType() == type)
                result.add(e);

        if (result.size() == 0)
            throw new NoSuchTelemetryEventException();

        if (type == TelemetryEventType.Location)
            locEvents = result;

        return result;
    }

    /**
     * Implementa ITelemetry.getEventAtLocation().
     *
     * @param loc Posizione campionata dall'evento che si vuole ottenere. Se
     *            null, viene sollevata un'eccezione IllegalArgumentException.
     * @param tolerance Tolleranza, in metri, indicante quanto la posizione
     *                  registrata dall'evento restituito può discostarsi dal
     *                  valore richiesto. Se < 0, viene sollevata
     *                  un'eccezione IllegalOperationException.
     * @return Evento di Tracciamento di tipo LocationTelemetryEvent.
     * @throws NoSuchTelemetryEventException
     * @throws IllegalArgumentException
     */
    @Override
    public LocationTelemetryEvent getEventAtLocation(GeoPoint loc,
                                                     int tolerance)
            throws NoSuchTelemetryEventException, IllegalArgumentException {
        if (loc == null)
            throw new IllegalArgumentException("Illegal null location");

        Iterable<TelemetryEvent> events =
                this.getEvents(TelemetryEventType.Location);
        int distance = Integer.MAX_VALUE;
        LocationTelemetryEvent event = null;

        for (TelemetryEvent e : events) {
            LocationTelemetryEvent lte = (LocationTelemetryEvent)e;
            int thisDistance = Math.round(lte.location().distanceTo(loc));
            if (thisDistance <= tolerance && thisDistance < distance) {
                event = lte;
                distance = thisDistance;
            }
        }

        if (event == null)
            throw new NoSuchTelemetryEventException();
        return event;
    }

    /**
     * Implementa ITelemetry.getDuration().
     *
     * @return Durata del Tracciamento, in secondi.
     */
    @Override
    public int getDuration() {
        if (duration == -1) {
            duration = 0;
            Iterable<TelemetryEvent> allEvents = this.getEvents();
            for (TelemetryEvent e : allEvents)
                if (e.timestamp() > duration)
                    duration = e.timestamp();
        }
        return duration;
    }

}
