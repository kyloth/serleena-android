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
 * Name: IExperienceStorage.java
 * Package: com.kyloth.serleena.persistence
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence;

import com.kyloth.serleena.common.UserPoint;

/**
 * Interfaccia implementata da un oggetto che realizza la persistenza di
 * un' Esperienza.
 *
 * @use Viene utilizzata da concretizzazioni dell'interfaccia \fixedwidth{IPersistenceDataSource} per rappresentare dati memorizzati nel sistema di persistenza. Nello specifico, viene utilizzato da \fixedwidth{SerleenaSQLiteDataSource} nella restituzione di oggetti \fixedwidth{SQLiteDAOExperience}.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public interface IExperienceStorage {

    /**
     * Restituisce i Percorsi associati all'Esperienza,
     *
     * @return Insieme enumerabile di Percorsi.
     */
    public Iterable<ITrackStorage> getTracks();

    /**
     * Restituisce i Punti Utente associati all'Esperienza.
     *
     * @return Insieme enumerabile di Punti Utente.
     */
    public Iterable<UserPoint> getUserPoints();

    /**
     * Aggiunge un punto utente all'esperienza.
     * @param p     Punto utente.
     */
    public void addUserPoint(UserPoint p);

    /**
     * Restituisce il nome dell'esperienza.
     *
     * @return Nome dell'esperienza.
     */
    public String getName();

}
