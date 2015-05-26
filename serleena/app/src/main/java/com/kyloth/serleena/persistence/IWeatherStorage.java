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
 * Name: IWeatherStorage.java
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

import java.util.Date;

/**
 * Interfaccia implementata da un oggetto che realizza la persistenza di
 * previsioni meteorologiche.
 *
 * @use Viene utilizzata da concretizzazioni dell'interfaccia \fixedwidth{IPersistenceDataSource} per rappresentare dati memorizzati nel sistema di persistenza. Nello specifico, viene utilizzato da \fixedwidth{SerleenaSQLiteDataSource} nella restituzione di oggetti \fixedwidth{SQLiteDAOWeather}.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public interface IWeatherStorage {

    /**
     * Restituisce la previsione metereologica prevista per la mattina.
     *
     * @return Previsione metereologica.
     */
    public WeatherForecastEnum getMorningForecast();

    /**
     * Restituisce la previsione metereologica prevista per il pomeriggio.
     *
     * @return Previsione metereologica.
     */
    public WeatherForecastEnum getAfternoonForecast();

    /**
     * Restituisce la previsione metereologica prevista per la sera.
     *
     * @return Previsione metereologica.
     */
    public WeatherForecastEnum getNightForecast();

    /**
     * Restituisce la temperatura prevista per la mattina.
     *
     * @return Valore della temperatura in gradi centigradi.
     */
    public int getMorningTemperature();

    /**
     * Restituisce la temperatura prevista per il pomeriggio.
     *
     * @return Valore della temperatura in gradi centigradi.
     */
    public int getAfternoonTemperature();

    /**
     * Restituisce la temperatura prevista per la sera.
     *
     * @return Valore della temperatura in gradi centigradi.
     */
    public int getNightTemperature();

    /**
     * Restituisce la data a cui si riferiscono le previsioni.
     *
     * @return Data delle previsioni.
     */
    public Date date();

}
