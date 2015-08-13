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
 * Name: CheckpointCrossing.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;

/**
 * Rappresenta dettagli su una prestazione relativa all'attraversameno di un
 * checkpoint durarnte un'esperienza.
 *
 * @use Oggetti di tipo CheckpointCrossing vengono restituiti da TrackCrossing per fornire informazioni sull'ultimo checkpoint attraversato dall'utente, e vengono consumati da TrackPresenter e TelemetryManager rispettivamente per visualizzare tali informazioni e produrne un Tracciamento.
 * @field index : int Indice in base zero del checkpoint a cui si riferisce l'istanza.
 * @field partial : int Tempo parziale, in secondi.
 * @field track : ITrack Percorso a cui si riferisce l'istanza.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class CheckpointCrossing {

    private int index;
    private int partial;
    private ITrack track;

    /**
     * Crea un nuovo oggetto CheckpointCrossing.
     *
     * @param checkpointIndex Indice del checkpoint attraversato.
     * @param partialTime Tempo parziale.
     * @param track Percorso a cui si riferisce il checkpoint.
     */
    public CheckpointCrossing(int checkpointIndex,
                              int partialTime,
                              ITrack track) {
        if (checkpointIndex < 0)
            throw new IllegalArgumentException("Illegal checkpoint number");
        if (partialTime < 0)
            throw new IllegalArgumentException("Illegal negative time");
        if (track == null)
            throw new IllegalArgumentException("Illegal null track");

        this.index = checkpointIndex;
        this.partial = partialTime;
        this.track = track;
    }

    /**
     * Restituisce l'indice del checkpoint a cui la prestazione rappresentata
     * dall'istanza di riferisce.
     *
     * @return Indice in base 0 indicante il checkpoint.
     */
    public int checkPointIndex() {
        return index;
    }

    /**
     * Restituisce il tempo parziale per il checkpoint.
     *
     * @return Tempo parziale, espresso in secondi dall'inizio del Percorso
     * all'attraversamento del checkpoint.
     */
    public int partialTime() {
        return partial;
    }

    /**
     * Restituisce la differenza tra la prestazione migliore per il Percorso
     * e quella rappresentata dall'oggetto, per lo specifico checkpoint.
     *
     * Una differenza negativa indica un miglioramento della prestazione
     * rispetto a quanto fatto precedentemente.
     *
     * Solleva un'eccezione NoSuchTelemetryException se il Percorso
     * considerarto non contiene altri Tracciamenti.
     * Solleva un'eccezione NoSuchTelemetryEventException se il Tracciamento
     * migliore per il Percorso non contiene un'evento per il checkpoint
     * considerato.
     *
     * @return Differenza in secondi.
     *
     * @throws NoSuchTelemetryException
     * @throws NoSuchTelemetryEventException
     */
    public int delta()
            throws NoSuchTelemetryException, NoSuchTelemetryEventException {
        ITelemetry best = track.getBestTelemetry();
        Predicate<TelemetryEvent> p = new Predicate<TelemetryEvent>() {
            @Override
            public boolean apply(TelemetryEvent telemetryEvent) {
                return telemetryEvent.getType() ==
                        TelemetryEventType.CheckpointReached &&
                        ((CheckpointReachedTelemetryEvent) telemetryEvent)
                                .checkpointNumber() == index + 1;
            }
        };
        Iterable<TelemetryEvent> events = best.getEvents(p);
        TelemetryEvent event = events.iterator().next();
        return partial - event.timestamp();
    }

}
