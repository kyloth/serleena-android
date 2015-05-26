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
 * Name: SQLiteDAOWeather.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.util.Date;

/**
 * Rappresenta l'implementazione della persistenza di un Tracciamento su
 * database SQLite.
 *
 * @use Istanze di SQLiteDAOWeather vengono create e utilizzate dal DAO SerleenaSQLiteDataSource, che le restituisce all'esterno dietro interfaccia IWeatherStorage. Mantiene un riferimento al database di appartenenza attraverso ISerleenaSQLiteDataSource.
 * @field morningForecast : WeatherForecastEnum Condizione prevista per la mattina
 * @field afternoonForecast : WeatherForecastEnum Condizione prevista per il pomeriggio
 * @field nightForecast : WeatherForecastEnum Condizione prevista per la sera
 * @field morningTemperature : int Temperatura prevista per la mattina
 * @field afternoonTemperature : int Temperatura prevista per il pomeriggio
 * @field nightTemperature : int Temperatura prevista per la sera
 * @field date : Date Data al quale si riferiscono le previsioni
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.persistence.IWeatherStorage
 */
class SQLiteDAOWeather implements IWeatherStorage {

    WeatherForecastEnum morningForecast;
    WeatherForecastEnum afternoonForecast;
    WeatherForecastEnum nightForecast;
    int morningTemperature;
    int afternoonTemperature;
    int nightTemperature;
    Date date;

    /**
     * Crea un nuovo oggetto SQLiteDAOWeather.
     *
     * @param morningForecast Condizioni previste per la mattina.
     * @param afternoonForecast Condizioni previste per il pomeriggio.
     * @param nightForecast Condizioni previste per la sera.
     * @param morningTemperature Temperatura prevista per la mattina.
     * @param afternoonTemperature Temperatura prevista per il pomeriggio.
     * @param nightTemperature Temperatura prevista per la sera.
     * @param date Data delle previsioni.
     */
    public SQLiteDAOWeather(WeatherForecastEnum morningForecast,
                            WeatherForecastEnum afternoonForecast,
                            WeatherForecastEnum nightForecast,
                            int morningTemperature, int afternoonTemperature,
                            int nightTemperature, Date date) {
        this.morningForecast = morningForecast;
        this.afternoonForecast = afternoonForecast;
        this.nightForecast = nightForecast;
        this.morningTemperature = morningTemperature;
        this.afternoonTemperature = afternoonTemperature;
        this.nightTemperature = nightTemperature;
        this.date = date;
    }

    /**
     * Implementazione di IWeather.getMorningForecast().
     *
     * @return Condizione prevista per la mattina.
     */
    @Override
    public WeatherForecastEnum getMorningForecast() {
        return morningForecast;
    }

    /**
     * Implementazione di IWeather.getAfternoonForecast.
     *
     * @return Condizione prevista per il pomeriggio.
     */
    @Override
    public WeatherForecastEnum getAfternoonForecast() {
        return afternoonForecast;
    }

    /**
     * Implementazione di IWeather.getNightForecast.
     *
     * @return Condizione prevista per la sera.
     */
    @Override
    public WeatherForecastEnum getNightForecast() {
        return nightForecast;
    }

    /**
     * Implementazione di IWeather.getMorningTemperature.
     *
     * @return Temperatura prevista per la mattina.
     */
    @Override
    public int getMorningTemperature() {
        return morningTemperature;
    }

    /**
     * Implementazione di IWeather.getAfternoonTemperature.
     *
     * @return Temperatura prevista per il pomeriggio.
     */
    @Override
    public int getAfternoonTemperature() {
        return afternoonTemperature;
    }

    /**
     * Implementazione di IWeather.getNightTemperature.
     *
     * @return Temperatura prevista per la sera.
     */
    @Override
    public int getNightTemperature() {
        return nightTemperature;
    }

    /**
     * Implementazione di IWeather.date().
     *
     * @return Data delle previsioni.
     */
    @Override
    public Date date() {
        return date;
    }

}
