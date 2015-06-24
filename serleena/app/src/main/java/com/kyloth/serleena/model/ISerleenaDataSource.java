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
 * Name: ISerleenaDataSource
 * Package: com.hitchikers.serleena.model
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;

import java.util.Date;

/**
 * E' l’interfaccia realizzata da un datasource.
 *
 * @use \`E possibile interrogarlo per ottenere la lista delle esperienze, delle autorit\`a competenti per un dato punto geografico o il quadrante di mappa per un dato punto. Restituisce al codice client i dati del Model dietro interfacce definite nel package stesso.
 * @author  Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since   1.0
 */
public interface ISerleenaDataSource {

    /**
     * Restituisce tutte le esperienze presenti nel datasource.
     *
     * @version 1.0
     */
    Iterable<IExperience> getExperiences();

    /**
     * Restituisce le previsioni meteo per un dato punto geografico e data
     * disponibili nel datasource.
     * Se il datasource non contiene alcun elemento che soddisfi i parametri
     * specificati, viene sollevata un'eccezione NoSuchWeatherForecastException.
     *
     * @param p Il punto geografico per cui interessano le previsioni meteo
     * @param date La data per cui interessano le previsioni
     * @version 1.0
     */
    IWeatherForecast getWeatherInfo(GeoPoint p, Date date)
            throws NoSuchWeatherForecastException;

    /**
     * Restituisce il quadrante di afferenza per una dato punto geografico.
     *
     * @version 1.0
     */
    IQuadrant getQuadrant(GeoPoint point);

    /**
     * Restituisce i contatti di emergenza che hanno giurisdizione su un dato
     * punto geografico.
     *
     * @version 1.0
     */
    DirectAccessList<EmergencyContact> getContacts(GeoPoint loc);

}
