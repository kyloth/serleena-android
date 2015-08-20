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
 * Name: ITrackStorage.java
 * Package: com.kyloth.serleena.persistence
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.TelemetryEvent;

import java.util.UUID;

/**
 * Interfaccia implementata da un oggetto che realizza la persistenza di un
 * Percorso.
 *
 * @use Viene utilizzata da concretizzazioni dell'interfaccia IPersistenceDataSource per rappresentare dati memorizzati nel sistema di persistenza. Nello specifico, viene utilizzato da SerleenaSQLiteDataSource nella restituzione di oggetti SQLiteDAOTrack.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface ITrackStorage {

    /**
     * Aggiunge un Tracciamento al Percorso, costruendolo da un elenco di
     * eventi di Tracciamento.
     *
     * @param events Elenco di eventi di Tracciamento.
     */
    void createTelemetry(Iterable<TelemetryEvent> events);

    /**
     * Restituisce i Tracciamenti che compongono il Percorso.
     *
     * @return Insieme enumerabile di Tracciamenti.
     */
    Iterable<ITelemetryStorage> getTelemetries();

    // HACK per SHANDROID-372
    Iterable<ITelemetryStorage> getTelemetries(boolean includeGhost);

    /**
     * Restituisce i Checkpoint che compongono il percorso.
     *
     * @return Insieme di Checkpoint.
     */
    DirectAccessList<Checkpoint> getCheckpoints();

    /**
     * Restituisce il nome del Percorso.
     *
     * @return Nome del percorso.
     */
    String name();

    UUID getUUID();
}
