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
 * Name: SerleenaDataSource.java
 * Package: com.hitchikers.serleena.model
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IPersistenceDataSource;

import java.util.ArrayList;
import java.util.Date;

/**
 * Concretizza SerleenaDataSource.
 *
 * @use Viene utilizzato direttamente solo dall'activity dell'applicazione, SerleenaActivity. Questa si occupa di crearne un'istanza, passando al costruttore un DAO SerleenaSQLiteDataSource, che implementa IPersistenceStorage, e passarla ai suoi presenter dietro interfaccia IPersistenceDataSource
 * @field dataSource : IPersistenceDataSource DAO dello strato di persistenza da utilizzare come sorgente dati effettiva
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SerleenaDataSource implements ISerleenaDataSource {

    private IPersistenceDataSource dataSource;

    /**
     * Crea un nuovo oggetto SerleenaDataSource basato su una fonte di dati
     * persistenti specificata.
     *
     * @param dataSource Datasource che realizza la persistenza dei dati
     *                   utilizzati da SerleenaDataSource.
     */
    public SerleenaDataSource(IPersistenceDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Implementa ISerleenaDataSource.getExperiences().
     *
     * @return Insieme enumerabile di Esperienze.
     */
    @Override
    public Iterable<IExperience> getExperiences() {
        Iterable<IExperienceStorage> storage = dataSource.getExperiences();
        ArrayList<IExperience> result = new ArrayList<IExperience>();

        for (IExperienceStorage s : storage)
            result.add(new Experience(s));

        return result;
    }

    /**
     * Implementa ISerleenaDataSource.getWeatherInfo().
     *
     * @param p Il punto geografico per cui interessano le previsioni meteo.
     * @param date La data per cui interessano le previsioni.
     * @return Previsioni meteo.
     * @throws NoSuchWeatherForecastException
     */
    @Override
    public IWeatherForecast getWeatherInfo(GeoPoint p, Date date)
            throws NoSuchWeatherForecastException {
        return new WeatherForecast(dataSource.getWeatherInfo(p, date));
    }

    /**
     * Implementa ISerleenaDataSource.getQuadrant().
     *
     * @param point Posizione geografica il cui quadrante si vuole ottenere.
     * @return Quadrante contenente il punto geografico specificato.
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint point) {
        return dataSource.getQuadrant(point);
    }

    /**
     * Implementa ISerleenaDataSource.getContacts().
     *
     * @param loc Posizione geografica delle cui vicinanze si vogliono
     *            ottenere i contatti di emergenza.
     * @return Insieme enumerabile di contatti di emergenza.
     */
    @Override
    public Iterable<EmergencyContact> getContacts(GeoPoint loc) {
        return dataSource.getContacts(loc);
    }

}
