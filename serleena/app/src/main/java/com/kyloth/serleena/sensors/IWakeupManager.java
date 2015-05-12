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
 * Name: IWakeupManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Gabriele Pozzan   2015-05-06   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.UnregisteredObserverException;

/**
 * Interfaccia che verrà implementata da un oggetto in grado di
 * segnalare eventi di wakeup del processo utilizzando RTC.
 *
 * Tali eventi verranno segnalati in modalità push ad oggetti
 * di tipo IWakeupObserver registrati a un IWakeupManager tramite
 * il pattern "Observer".
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public interface IWakeupManager {

    /**
     * Registra un IWakeupObserver che, tramite il pattern "Observer"
     * sarà notificato, a un intervallo fissato, dei cambiamenti di stato.
     *
     * @param observer IWakeupObserver da registrare. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @param interval Intervallo di tempo per la notifica all'oggetto
     *                 "observer". Se minore o uguale a zero,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @param oneTimeOnly True se il wakeup avviene una sola volta,
     *                    false se il wakeup è ripetuto.
     * @throws java.lang.IllegalArgumentException
     */
    public void attachObserver(IWakeupObserver observer, int interval,
                               boolean oneTimeOnly)
        throws IllegalArgumentException;
    /**
     * Cancella la registrazione di un IWakeupObserver.
     *
     * @param observer IWakeupObserver la cui registrazione come "observer" di
     *                 questo oggetto sarà cancellata. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     *                 Se non precedentemente registrato,
     *                 viene sollevata un'eccezione
     *                 UnregisteredObserverException.
     * @throws com.kyloth.serleena.common.UnregisteredObserverException
     * @throws java.lang.IllegalArgumentException
     */
    public void detachObserver(IWakeupObserver observer)
            throws UnregisteredObserverException, IllegalArgumentException;

    /**
     * Metodo "notify" basato sull'omonimo metodo della classe "Subject" del
     * Design Pattern "Observer".
     *
     * @param observer Oggetto "observer" da notificare.
     */
    public void notifyObserver(IWakeupObserver observer);
}
