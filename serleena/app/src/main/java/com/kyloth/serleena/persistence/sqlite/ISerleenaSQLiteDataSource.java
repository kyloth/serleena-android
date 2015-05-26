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
 * Name: ISerleenaSQLiteDataSource.java
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

import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IPersistenceDataSource;

/**
 * Descrive l’interfaccia di un Data Access Object per accesso a basso livello
 * al database SQLite dell’applicazione. Viene utilizzata dagli oggetti
 * interni al package per ottenere dati relativi alle entit`a che rappresentano.
 *
 * @use Viene utilizzata dagli oggetti del package persistence.sqlite come datasource verso il database SQLite a cui essi appartengono. Ogni oggetti rappresentante dati memorizzati su SQLite mantiene un riferimento a un oggetto ISerleenaSQLiteDataSource per ottenere i dati da restituire nei suoi metodi.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-06
 */
public interface ISerleenaSQLiteDataSource extends IPersistenceDataSource {

    /**
     * Restituisce i Percorsi associati a una specifica esperienza.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Percorsi.
     * @return Insieme enumerabile di Percorsi.
     */
    public Iterable<SQLiteDAOTrack> getTracks(SQLiteDAOExperience experience);

    /**
     * Restituisce i Tracciamenti associati a uno specifico Percorso.
     *
     * @param track Percorso di cui si vogliono ottenere i Tracciamenti.
     * @return Insieme enumerabile di Tracciamenti.
     */
    public Iterable<SQLiteDAOTelemetry> getTelemetries(SQLiteDAOTrack track);

    /**
     * Restituisce i Punti Utente associati a una specifica Esperienza.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Punti Utente.
     * @return Insieme enumerabile di Punti Utente.
     */
    public Iterable<UserPoint> getUserPoints(SQLiteDAOExperience experience);

    /**
     * Aggiunge un nuovo punto utente al database, associato all'Esperienza
     * specificata.
     *
     * @param experience Esperienza a cui aggiungere il punto utente.
     * @param point Punto utente da aggiungere.
     */
    public void addUserPoint(SQLiteDAOExperience experience, UserPoint point);

    /**
     * Aggiunge al database un nuovo Tracciamento associato al Percorso
     * specificato.
     *
     * @param events Eventi di tracciamento da cui costruire il Tracciamento.
     * @param track Percorso a cui associare il Tracciamento.
     */
    public void createTelemetry(Iterable<TelemetryEvent> events,
                                SQLiteDAOTrack track);

}
