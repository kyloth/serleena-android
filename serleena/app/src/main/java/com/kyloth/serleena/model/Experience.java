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
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Concretizza IExperience.
 *
 * @use Viene utilizzata dal solo package Model, che ne crea le istanze in base a oggetti IExperienceStorage ottenuti dal DAO del livello di persistenza. Questi oggetti vengono restituiti all'esterno attraverso ISerleenaDataSource, dietro interfaccia IExperience.
 * @field storage : IExperienceStorage Oggetto contenente i dati di persistenza dell'Esperienza
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
final class Experience implements IExperience {

    private IExperienceStorage storage;

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
        Iterable<ITrackStorage> ts = storage.getTracks();
        ArrayList<ITrack> result = new ArrayList<ITrack>();
        for (ITrackStorage s : ts)
            result.add(new Track(s));
        return result;
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

    @Override
    public UUID getUUID() {
        return storage.getUUID();
    }

    /**
     * Implementa IExperience.getQuadrant().
     *
     * @param location Posizione geografica contenuta dal quadrante.
     * @return Oggetto IQuadrant
     * @throws NoSuchQuadrantException
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint location)
            throws NoSuchQuadrantException {
        return storage.getQuadrant(location);
    }

    /**
     * Ridefinisce Object.toString().
     *
     * Restituisce il nome dell'Esperienza.
     *
     * @return Nome dell'Esperienza.
     */
    @Override
    public String toString() {
        return getName();
    }

}
