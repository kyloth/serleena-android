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
 * Name: SQLiteDAOTelemetry.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;

/**
 * Rappresenta l'implementazione della persistenza di un Tracciamento su
 * database SQLite.
 *
 * @use Istanze di SQLiteDAOTelemetry vengono create e utilizzate dal DAO SerleenaSQLiteDataSource, che le restituisce all'esterno dietro interfaccia ITelemetryStorage. Mantiene un riferimento al database di appartenenza attraverso ISerleenaSQLiteDataSource.
 * @field id : int ID della riga di database associata all'oggetto 
 * @field events : Iterable<TelemetryEvent> Eventi del Tracciamento rappresentato dall'istanza
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.persistence.ITelemetryStorage
 */
class SQLiteDAOTelemetry implements ITelemetryStorage {

    private final int id;
    private final Iterable<TelemetryEvent> events;

    /**
     * Crea un nuovo oggetto SQLiteDAOTelemetry.
     *
     * @param id ID della riga della tabella del database a cui corrisponde
     *           l'oggetto.
     * @param events Eventi che costituiscono il Tracciamento.
     */
    public SQLiteDAOTelemetry(int id, Iterable<TelemetryEvent> events) {
        this.events = events;
        this.id = id;
    }

    /**
     * Restituisce gli eventi che costituiscono il Tracciamento.
     *
     * @return Insieme enumerabile di eventi di tracciamento.
     */
    @Override
    public Iterable<TelemetryEvent> getEvents() {
        return events;
    }

    /**
     * Restituisce l'ID dell'oggetto nella tabella di appartenenza.
     *
     * @return ID dell'oggetto esperienza.
     */
    public int id() {
        return id;
    }

    /**
     * Ridefinisce Object.equals()
     *
     * @return True se e solo se entrambi gli oggetti sono non null,
     * di tipo SQLiteDAOTelemetry, e se il metodo equals restituisce true per
     * gli id e l'elenco di eventi TelemetryEvent degli oggetti. False
     * altrimenti.
     */
    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof SQLiteDAOTelemetry) {
            SQLiteDAOTelemetry otherTelemetry = (SQLiteDAOTelemetry) other;
            return id == otherTelemetry.id &&
                    events.equals(otherTelemetry.events);
        }
        return false;
    }

    /**
     * Ridefinisce Object.hashCode()
     */
    @Override
    public int hashCode() {
        return id;
    }

}
