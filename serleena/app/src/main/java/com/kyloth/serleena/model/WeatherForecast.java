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
 * Name: WeatherForecast.java
 * Package: com.hitchikers.serleena.model
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file e stesura
 *                                          della documentazione Javadoc.
 */
package com.kyloth.serleena.model;

import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.util.Date;

/**
 * Concretizza IWeatherForecast.
 *
 * @use Viene utilizzata dal solo package Model, che ne crea le istanze in base a oggetti IWeatherStorage ottenuti dal DAO del livello di persistenza. Questi oggetti vengono restituiti all'esterno attraverso ISerleenaDataSource, dietro interfaccia IWeatherInfo.
 * @field storage : IWeatherStorage Oggetto rappresentante i dati meteo memorizzati nel livello di persistenza, utilizzato come sorgente dati effettiva
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class WeatherForecast implements IWeatherForecast {

    private IWeatherStorage storage;

    /**
     * Crea un nuovo oggetto WeatherForecast.
     *
     * Incapsula un oggetto che realizza la persistenza delle previsioni meteo,
     * e che rappresenta la sorgente dati.
     *
     * @param storage Oggetto IWeatherStorage che realizza la persistenza
     *                delle previsioni meteo.
     */
    public WeatherForecast(IWeatherStorage storage) {
       this.storage = storage;
    }

    /**
     * Implementa IWeatherForecast.getMorningForecast().
     *
     * @return Previsioni meteo per la mattina.
     */
    @Override
    public WeatherForecastEnum getMorningForecast() {
        return storage.getMorningForecast();
    }

    /**
     * Implementa IWeatherForecast.getAfternoonForecast().
     *
     * @return Previsioni meteo per il pomeriggio.
     */
    @Override
    public WeatherForecastEnum getAfternoonForecast() {
        return storage.getAfternoonForecast();
    }

    /**
     * Implementa IWeatherForecast.getNightForecast().
     *
     * @return Previsioni meteo per la sera.
     */
    @Override
    public WeatherForecastEnum getNightForecast() {
        return storage.getNightForecast();
    }

    /**
     * Implementa IWeatherForecast.getMorningTemperature().
     *
     * @return Temperatura prevista per la mattina, in gradi centigradi.
     */
    @Override
    public int getMorningTemperature() {
        return storage.getMorningTemperature();
    }

    /**
     * Implementa IWeatherForecast.getAfternoonTemperature().
     *
     * @return Temperatura prevista per il pomeriggio, in gradi centigradi.
     */
    @Override
    public int getAfternoonTemperature() {
        return storage.getAfternoonTemperature();
    }

    /**
     * Implementa IWeatherForecast.getNightTemperature().
     *
     * @return Temperatura prevista per la sera, in gradi centigradi.
     */
    @Override
    public int getNightTemperature() {
        return storage.getNightTemperature();
    }

    /**
     * Implementa IWeatherForecast.date().
     *
     * @return Data a cui si riferiscono le previsioni.
     */
    @Override
    public Date date() {
        return storage.date();
    }

}
