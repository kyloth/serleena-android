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
 * Name: Experience
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

import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITrackStorage;

import java.util.ArrayList;

/**
 * Concretizza IExperience.
 *
 * @use Viene utilizzata dal solo package Model, che ne crea le istanze in base a oggetti \fixedwidth{IExperienceStorage} ottenuti dal DAO del livello di persistenza. Questi oggetti vengono restituiti all'esterno attraverso \fixedwidth{ISerleenaDataSource}, dietro interfaccia \fixedwidth{IExperience}.
 * @field storage : IExperienceStorage Oggetto contenente i dati di persistenza dell'Esperienza
 * @field tracks : Iterable<ITrack> Insieme dei Percorsi associati all'esperienza
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class Experience implements IExperience {

    private IExperienceStorage storage;

    private Iterable<ITrack> tracks = null;

    /**
     * Crea un nuovo oggetto Experience.
     *
     * Incapsula un oggetto che realizza la persistenza dell'Esperienza, e
     * rappresenta la sorgente dati.
     *
     * @param storage Oggetto IExperienceStorage che realizza la persistenza
     *                dell'Esperienza.
     */
    public Experience(IExperienceStorage storage) {
        this.storage = storage;
    }

    /**
     * Implementa IExperience.getTracks().
     *
     * @return Insieme enumerabile di Percorsi.
     */
    @Override
    public Iterable<ITrack> getTracks() {
        if (tracks == null) {
            Iterable<ITrackStorage> ts = storage.getTracks();
            ArrayList<ITrack> result = new ArrayList<ITrack>();
            for (ITrackStorage s : ts)
                result.add(new Track(s));
            tracks = result;
        }
        return tracks;
    }

    /**
     * Implementa IExperience.getUserPoints().
     *
     * @return Insieme enumerabile di punti utente.
     */
    @Override
    public Iterable<UserPoint> getUserPoints() {
        return storage.getUserPoints();
    }

    /**
     * Implementa IExperience.addUserPoints().
     *
     * @param point Punto utente da aggiungere all'Esperienza.
     */
    @Override
    public void addUserPoints(UserPoint point) {
        storage.addUserPoint(point);
    }

    /**
     * Implementa IExperience.getName().
     *
     * @return Stringa indicante il nome dell'Esperienza.
     */
    @Override
    public String getName() {
        return storage.getName();
    }

}
