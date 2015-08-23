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
 * Name: WeatherDataEntity.java
 * Package: com.hitchikers.serleena.synchronization
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.synchronization.kylothcloud;

import com.kyloth.serleena.common.Region;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

/**
 * Struct rappresentante una previsione metereologica
 * @field boundingRect : Region Oggetto rappresentante l'area geografica relativa alla previsione meteo
 * @field date : long Timestamp associato alla previsione meteo
 * @field morning : ForecastTuple Previsione associata la mattino
 * @field afternoon : ForecastTuple Previsione associata al pomeriggio
 * @field night : ForecastTuple Previsione associata alla notte
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
public class WeatherDataEntity implements IKylothDataEntity {

    /**
     * Classe che rappresenta una condizione meteo nei suoi aspetti di
     * temperatura e tempo atmosferico.
     *
     * @field forecast : WeatherForecastEnumn Rappresenta la condizione meteo prevista
     * @field temperature : float Rappresenta la temperatura prevista
     */

    public static class ForecastTuple {
        public WeatherForecastEnum forecast;
        public float temperature;
    }
    public ForecastTuple morning;
    public ForecastTuple afternoon;
    public ForecastTuple night;
    public Region boundingRect;
    public long date;
    public WeatherDataEntity () {
        morning = new ForecastTuple();
        afternoon = new ForecastTuple();
        night = new ForecastTuple();
    }
}
