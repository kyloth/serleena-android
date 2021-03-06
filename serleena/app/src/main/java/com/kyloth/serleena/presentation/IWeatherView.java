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
 * Name: IWeatherView
 * Package: com.hitchikers.serleena.presentation
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.presentation;
import com.kyloth.serleena.model.IWeatherForecast;
import java.util.Date;

/**
 * Interfaccia della vista per la schermata Meteo.
 *
 * @use Viene utilizzato dal Presenter WeatherPresenter per mantenere un riferimento alla vista associata, e comunicare con essa.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since 1.0
 */
public interface IWeatherView {
    /**
     * Lega un Presenter alla vista.
     *
     * @param presenter Presenter associato alla vista.
     */
    void attachPresenter(IWeatherPresenter presenter);

    /**
     * Imposta le previsioni da visualizzare.
     *
     * @param forecast Previsioni da visualizzare.
     */
    void setWeatherInfo(IWeatherForecast forecast);

    /**
     * Imposta la data da visualizzare sulla vista.
     *
     * Tale valore indica la data a cui si riferiscono le informazioni
     * metereologiche visualizzare.
     *
     * @param date Data da visualizzare
     */
    void setDate(Date date);

    /**
     * Pulisce le informazioni metereologiche della vista.
     */
    void clearWeatherInfo();

}
