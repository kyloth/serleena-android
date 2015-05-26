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
 * Name: SQLiteDAOTrack.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-06  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.ITrackStorage;

import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

/**
 * Implementazione di persistence.ITrackStorage per la persistenza su
 * database SQLite integrato in Android.
 *
 * @use Istanze di SQLiteDAOTrack vengono create e utilizzate dal DAO SerleenaSQLiteDataSource, che le restituisce all'esterno dietro interfaccia ITrackStorage. Mantiene un riferimento al database di appartenenza attraverso ISerleenaSQLiteDataSource.
 * @field id : int ID della riga di database associata all'oggetto
 * @field dataSource : ISerleenaSQLiteDataSource Database a cui fa riferimento l'oggetto
 * @field checkpoints : ImmutableList<Checkpoint> Lista dei checkpoints associati al Percorso rappresentato dall'oggetto
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-06
 * @see com.kyloth.serleena.persistence.ITrackStorage
 */
class SQLiteDAOTrack implements ITrackStorage {

    private int id;
    private ISerleenaSQLiteDataSource dataSource;
    private ImmutableList<Checkpoint> checkpoints;

    public SQLiteDAOTrack(ImmutableList<Checkpoint> checkpoints, int id,
                          ISerleenaSQLiteDataSource dataSource) {
        this.id = id;
        this.dataSource = dataSource;
        this.checkpoints = checkpoints;
    }

    /**
     * Implementazione di ITrackStorage.createTelemetry().
     *
     * @param events Eventi da cui creare il Tracciamento.
     */
    @Override
    public void createTelemetry(Iterable<TelemetryEvent> events) {
        dataSource.createTelemetry(events, this);
    }

    /**
     * Implementazione di ITrackStorage.getTelemetries().
     *
     * @return Tracciamenti associati al Percorso.
     */
    @Override
    public Iterable<ITelemetryStorage> getTelemetries() {
        ArrayList<ITelemetryStorage> list = new ArrayList<ITelemetryStorage>();
        for (SQLiteDAOTelemetry t : dataSource.getTelemetries(this))
            list.add(t);
        return list;
    }

    @Override
    public ImmutableList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Restituisce l'ID dell'oggetto nella tabella di appartenenza.
     *
     * @return ID dell'oggetto esperienza.
     */
    public int id() {
        return id;
    }

}
