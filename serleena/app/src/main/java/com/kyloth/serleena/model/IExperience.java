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
 * Name: IExperience
 * Package: com.hitchikers.serleena.model
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;

import java.util.UUID;

/**
 * Interfaccia realizzata da oggetti che rappresentano un'Esperienza.
 *
 * @use Utilizzata dal Model e dalla parte di presentazione
 * @author  Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since   1.0
 */
public interface IExperience {

    /**
     * Restituisce i Percorsi dell'Esperienza.
     * 
     * @return  Ritorna un Iterable che contiene tutti i percorsi
     *          dell'Esperienza.
     * @version 1.0
     * @since   1.0
     */
    Iterable<ITrack> getTracks();

    /**
     * Restituisce i punti utente dell'Esperienza.
     *
     * @return  Ritorna un Iterable che contiene tutti i Punti Utente
     *          propri dell'Esperienza, compresi quelli creati sull'orologio..
     * @version 1.0
     * @since   1.0
     */
    Iterable<UserPoint> getUserPoints();

    /**
     * Aggiunge un punto utente dato all'Esperienza.
     *
     * @version 1.0
     * @since   1.0
     */
    void addUserPoints(UserPoint point);

    /**
     * Restituisce il nome dell'esperienza.
     *
     * @return Nome dell'esperienza.
     */
    String getName();

    /**
     * Restituisce il quadrante contenente la posizione geografica
     * specificata, tra quelli associati all'Esperienza rappresentata
     * dall'istanza.
     *
     * Se non vi sono quadranti contenenti la posizione specificata, viene
     * sollevata un'eccezione NosuchQuadrantException.
     *
     * @param location Posizione geografica contenuta dal quadrante richiesto.
     * @return Oggetto IQuadrant rappresentante il quadrante.
     * @throws NoSuchQuadrantException
     */
    IQuadrant getQuadrant(GeoPoint location) throws NoSuchQuadrantException;
}
