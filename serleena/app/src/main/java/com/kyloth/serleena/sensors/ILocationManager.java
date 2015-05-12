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
 * Name: ILocationManager
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer       Date        Changes
 * 1.0.0    Gabriele Pozzan  2015-05-06  Creazione file e scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.GeoPoint;

/**
 * Interfaccia che verrà implementata da un oggetto in grado di fornire
 * informazioni sulla posizione dell'Escursionista.
 *
 * Tali dati verranno forniti su singola richiesta o comunicati in
 * modalità push a un oggetto ILocationObserver registrato agli eventi
 * di ILocationManager tramite pattern "Observer".
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public interface ILocationManager {
    /**
     * Registra un ILocationObserver che, tramite il pattern "Observer"
     * sarà notificato, a un intervallo fissato, dei cambiamenti di stato.
     *
     * @param observer ILocationObserver da registrare.
     * @param interval Intervallo di tempo per la notifica all'oggetto "observer".
     */
    public void attachObserver(ILocationObserver observer, int interval);
    /**
     * Cancella la registrazione di un ILocationObserver.
     *
     * @param observer ILocationObserver la cui registrazione come "observer" di
     *                 questo oggetto sarà cancellata.
     */
    public void detachObserver(ILocationObserver observer);
    /**
     * Permette di ottenere un singolo 1aggiornamento dei dati di posizione su
     * richiesta esplicita. L'aggiornamento viene dato in maniera asincrona
     * tramite callback all'observer, dato il tempo possibilmente non
     * trascurabile per ottenere i dati.
     *
     * @param observer Oggetto ILocationObserver a cui comunicare i dati.
     *                 Se null, viene lanciata un'eccezione
     *                 IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public void getSingleUpdate(ILocationObserver observer)
        throws IllegalArgumentException;

    /**
     * Metodo "notify" basato sull'omonimo metodo della classe "Subject" del
     * Design Pattern "Observer".
     */
    public void notifyObservers();
}
