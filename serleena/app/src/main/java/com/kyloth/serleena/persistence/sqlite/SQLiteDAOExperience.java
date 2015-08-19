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
 * Name: SQLiteDAOExperience.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.*;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Implementazione di IExperienceStorage per la persistenza su database
 * SQLite.
 *
 * @use Istanze di SQLiteDAOExperience vengono create e utilizzate dal DAO SerleenaSQLiteDataSource, che le restituisce all'esterno dietro interfaccia IExperienceStorage. Mantiene un riferimento al database di appartenenza attraverso ISerleenaSQLiteDataSource.
 * @field name : String Nome dell'Esperienza
 * @field getUUID : int ID della riga di database associata all'istanza
 * @field dataSource : ISerleenaSQLiteDataSource Database a cui fa riferimento l'oggetto
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class SQLiteDAOExperience implements IExperienceStorage
{

    private String name;
    private UUID uuid;
    private ISerleenaSQLiteDataSource dataSource;

    /**
     * Crea una nuova istanza di SQLiteDAOExperience.
     *
     * @param   uuid              Id dell'oggetto nella tabella di appartenenza.
     * @param   dataSource      La sorgente di dati SQL a cui questo oggetto è
     *                          associato.
     */
    public SQLiteDAOExperience(String name, UUID uuid,
            ISerleenaSQLiteDataSource dataSource) {
        this.uuid = uuid;
        this.dataSource = dataSource;
        this.name = name;
    }

    /**
     * Implementazione di IExperienceStorage.getTracks().
     *
     * @return Insieme enumerabile di Percorsi.
     */
    @Override
    public Iterable<ITrackStorage> getTracks() {
        ArrayList<ITrackStorage> list = new ArrayList<ITrackStorage>();
        for (SQLiteDAOTrack t : dataSource.getTracks(this))
            list.add(t);
        return list;
    }

    @Override
    public Iterable<UserPoint> getUserPoints(boolean localOnly) {
        return dataSource.getUserPoints(this, localOnly);
    }

    @Override
    public Iterable<UserPoint> getUserPoints() {
        return dataSource.getUserPoints(this);
    }

    /**
     * Implementazione di IExperienceStorage.addUserPoint().
     *
     * @param p     Punto geografico associato al punto utente.
     */
    @Override
    public void addUserPoint(UserPoint p) {
        dataSource.addUserPoint(this, p);
    }

    /**
     * Implementa IExperienceStorage.getName().
     *
     * @return Nome dell'Esperienza.
     */
    @Override
    public String getName() {
        return name;
    }



    /**
     * Implementa IExperienceStorage.getQuadrant().
     *
     * @param location Posizione geografica contenente il quadrante.
     * @return Oggetto IQuadrant
     * @throws NoSuchQuadrantException
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint location)
            throws NoSuchQuadrantException {
        return dataSource.getQuadrant(location, this);
    }

    /**
     * Restituisce l'ID dell'oggetto nella tabella di appartenenza.
     *
     * @return ID dell'oggetto esperienza.
     */
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

}
