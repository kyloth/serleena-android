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
 * Name: IPersistenceDataSource.java
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

import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import java.util.Date;

/**
 * Interfaccia rappresentante un Data Access Object. per l'accesso alla
 * sorgente dati che realizza la persistenza.
 *
 * @use Viene utilizzata nella creazione di oggetti DAO \fixedwidth{SerleenaDataSource}, che mantengono internamente riferimenti a \fixedwidth{IPersistenceDataSource}, da cui traggono i dati da restituire al client.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public interface IPersistenceDataSource {

    /**
     * Restituisce le Esperienze memorizzate nella sorgente dati.
     *
     * @return Insieme enumerabile di Esperienze.
     */
    public Iterable<IExperienceStorage> getExperiences();

    /**
     * Restituisce le previsioni metereologiche relative a una
     * specifica data e posizione geografica memorizzate nella sorgente dati.
     *
     * @param location Posizione geografica di cui si vogliono ottenere le
     *                 previsioni.
     * @param date Data di cui si vogliono ottenere le previsioni.
     * @return Insieme enumerabile di oggetti IWeatherStorage.
     */
    public IWeatherStorage getWeatherInfo(GeoPoint location, Date date);

    /**
     * Restituisce il quadrante che contiene il punto geografico specificato.
     *
     * @param location Posizione geografica che ricade nei limiti del quadrante.
     * @return Oggetto IQuadrant relativo alla posizione specificata.
     */
    public IQuadrant getQuadrant(GeoPoint location);

    /**
     * Restituisce i contatti di emergenza di autorità locali al punto
     * geografico specificato.
     *
     * @param location Punto geografico del cui intorno si vogliono ottenere
     *                 i contatti di autorità locali.
     * @return Insieme enumerabile di contatti.
     */
    public Iterable<EmergencyContact> getContacts(GeoPoint location);

}
