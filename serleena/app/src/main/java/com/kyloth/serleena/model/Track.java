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
 * Name: Track
 * Package: com.hitchikers.serleena.model
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.ITrackStorage;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Concretizza ITrack.
 *
 * @use Utilizzata dal Model e dalla parte di presentazione. Il principale utilizzo viene fatto da TrackPresenter, che mantiene un riferimento al Percorso attivo per guidare l'utente nell'attraversamento del Percorso. Attraverso ITrack è possibile inoltre aggiungere nuovi Tracciamenti al Percorso, come insieme di oggetti TelemetryEvent.
 * @field storage : ITrackStorage Oggetto rappresentante il Percorso memorizzato nel livello di persistenza, utilizzato come sorgente dati effettiva
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class Track implements ITrack {

    private ITrackStorage storage;

    /**
     * Crea un nuovo oggetto Track.
     *
     * Incapsula un oggetto che realizza la persistenza del Percorso, e
     * rappresenta la sorgente dati.
     *
     * @param storage Oggetto ITrackStorage che realizza la persistenza del
     *                Percorso.
     */
    public Track(ITrackStorage storage) {
        if (storage == null)
            throw new IllegalArgumentException();
        this.storage = storage;
    }

    /**
     * Implementa ITrack.name()
     */
    @Override
    public String name() {
        return this.storage.name();
    }

    /**
     * Implementa ITrack.getTelemetries().
     */
    @Override
    public Iterable<ITelemetry> getTelemetries() {
        Iterable<ITelemetryStorage> tels = storage.getTelemetries();
        ArrayList<ITelemetry> result = new ArrayList<ITelemetry>();

        for (ITelemetryStorage ts : tels)
            result.add(new Telemetry(ts));

        return result;
    }

    /**
     * Implementa ITrack.createTelemetry().
     */
    @Override
    public void createTelemetry(Iterable<TelemetryEvent> events) {
        if (events == null)
            throw new IllegalArgumentException();

        storage.createTelemetry(events);
    }

    /**
     * Implementa ITrack.getCheckpoints().
     */
    @Override
    public DirectAccessList<Checkpoint> getCheckpoints() {
        return storage.getCheckpoints();
    }

    /**
     * Implementa ITrack.getBestTelemetry().
     */
    @Override
    public ITelemetry getBestTelemetry() throws NoSuchTelemetryException {
        Iterable<ITelemetry> telemetries = getTelemetries();
        int min = Integer.MAX_VALUE;
        ITelemetry best = null;

        for (ITelemetry t : telemetries)
            if (t.getDuration() < min) {
                best = t;
                min = t.getDuration();
            }

        if (best == null)
            throw new NoSuchTelemetryException();

        return best;
    }

    /**
     * Ridefinisce Object.equals().
     *
     * @param other Oggetto da confrontare con l'istanza.
     * @return True se il metodo equals() sugli oggetti di persistenza
     *         incapsulati restituisce True. False altrimenti.
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Track) {
            Track otherTrack = (Track) other;
            return storage.equals(otherTrack.storage);
        }
        return false;
    }

    /**
     * Ridefinisce Object.hashCode()
     */
    @Override
    public int hashCode() {
        return storage.hashCode();
    }

    /**
     * Ridefinisce Object.toString()
     *
     * Restituisce il nome del Percorso.
     *
     * @return Nome del Percorso.
     */
    @Override
    public String toString() {
        return name();
    }

}
