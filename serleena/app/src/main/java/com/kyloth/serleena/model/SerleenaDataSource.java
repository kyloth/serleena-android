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
 * Date: 2015-05-22
 *
 * History:
 * Version    Programmer        Date        Changes
 * 1.0        Filippo Sestini   2015-05-22  Creazione del file e stesura
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

}
