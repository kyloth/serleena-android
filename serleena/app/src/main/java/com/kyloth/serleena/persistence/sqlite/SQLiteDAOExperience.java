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
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-06  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.*;

import java.util.ArrayList;

/**
 * Implementazione di IExperienceStorage per la persistenza su database
 * SQLite.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-06
 */
class SQLiteDAOExperience implements IExperienceStorage
{

    private String name;
    private int id;
    private ISerleenaSQLiteDataSource dataSource;

    /**
     * Crea una nuova istanza di SQLiteDAOExperience.
     *
     * @param   id              Id dell'oggetto nella tabella di appartenenza.
     * @param   dataSource      La sorgente di dati SQL a cui questo oggetto è
     *                          associato.
     */
    public SQLiteDAOExperience(String name, int id,
            ISerleenaSQLiteDataSource dataSource) {
        this.id = id;
        this.dataSource = dataSource;
        this.name = name;
    }

    /**
     * Implementazione di IExperienceStorage.getTracks().
     *
     * @return Insieme enumerabile di Percorsi.
     */
    public Iterable<ITrackStorage> getTracks() {
        ArrayList<ITrackStorage> list = new ArrayList<ITrackStorage>();
        for (SQLiteDAOTrack t : dataSource.getTracks(this))
            list.add(t);
        return list;
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
     * Restituisce l'ID dell'oggetto nella tabella di appartenenza.
     *
     * @return ID dell'oggetto esperienza.
     */
    public int id() {
        return this.id;
    }

}
