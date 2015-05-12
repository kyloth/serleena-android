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
 * Name: ILocationReachedManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Gabriele Pozzan   2015-05-06   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 * 1.0.1    Filippo Sestini   2015-05-11   Aggiunta del metodo detachObserver
 *                                         e modifica di notifyAtLocation in
 *                                         attachObserver.
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.GeoPoint;

/**
 * Interfaccia che verrà implementata da un oggetto in grado di segnalare,
 * in base alla posizione dell'Escursionista, il raggiungimento di un punto
 * geografico definito.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public interface ILocationReachedManager {
    /**
     * Metodo che permette di registrare un oggetto che verrà notificato
     * al raggiungimento del punto geografico "location".
     *
     * @param observer ILocationReachedObserver da registrare.
     * @param location Punto geografico il cui raggiungimento deve generare una notifica.
     */
    public void attachObserver(ILocationReachedObserver observer,
                               GeoPoint location);

    /**
     * Annulla la registrazione di un observer precedentemente registrato
     * all'oggetto LocationReachedManager.
     *
     * @param observer Oggetto da deregistrare ILocationReachedManager.
     */
    public void detachObserver(ILocationReachedManager observer);
}
