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
 * Name: ISerleenaApplication.java
 * Package: com.kyloth.serleena.presentation
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.activity;

import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.sensors.ISensorManager;

/**
 * Interfaccia dell'applicazione serleena.
 *
 * Espone metodi per l'accesso ai servizi dell'applicazione, quali sorgenti
 * di dati, persistenza, gestione dei sensori e della sincronizzazione.
 *
 * @use SerleenaActivity recupera un riferimento all'applicazione dietro questa interfaccia, attraverso il quale riesce a fornire i servizi dell'applicazione ai suoi Presenter.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface ISerleenaApplication {

    /**
     * Restituisce il data source dell'applicazione.
     *
     * @return Data source dell'applicazione.
     */
    ISerleenaDataSource getDataSource();

    /**
     * Restituisce il gestore dei sensori dell'applicazione,
     * dietro interfaccia ISensorManager.
     *
     * @return Gestore dei sensori dell'applicazione.
     */
    ISensorManager getSensorManager();

    /**
     * Restituisce il datasink dell'applicazione, dietro interfaccia
     * IPersistenceDataSink.
     *
     * @return Datasink dell'applicazione.
     */
    IPersistenceDataSink getDataSink();

}
