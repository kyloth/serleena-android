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
 * Name: HeartRateManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-21
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-21   Creazione file e scrittura
 *                                         codice e documentazione Javadoc.
 */

package com.kyloth.serleena.sensors;

import android.content.Context;

/**
 * Concretizza IHeartRateManager
 *
 * @use Viene istanziato da \fixedwidth{SensorManager} e restituito al codice client dietro interfaccia.
 * @field context : Context Contesto dell'applicazione
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class HeartRateManager implements IHeartRateManager {

    private Context context;

    /**
     * Crea un oggetto HeartRateManager.
     *
     * @param context Contesto dell'applicazione.
     */
    public HeartRateManager(Context context) {
        this.context = context;
    }

    /**
     * Implementa  IHeartRateManager.attachObserver().
     *
     * @param observer IHeartRateObserver da registrare.
     * @param interval Intervallo di tempo per la notifica all'oggetto
     *                 "observer".
     */
    @Override
    public void attachObserver(IHeartRateObserver observer, int interval) {

    }

    /**
     * Implementa IHeartRateManager.detachObserver().
     *
     * @param observer Observer la cui registrazione viene cancellata.
     */
    @Override
    public void detachObserver(IHeartRateObserver observer) {

    }

    /**
     * Implementa IHeartRateManager.getSingleUpdate().
     *
     * @param observer IHeartRateObserver destinatario dell'aggiornamento.
     * @param timeout Timeout in secondi oltre il quale il sensore invoca in
     *                ogni caso l'observer.
     */
    @Override
    public void getSingleUpdate(IHeartRateObserver observer, int timeout) {

    }

    /**
     * Implementa IHeartRateManager.notifyObserver().
     *
     * @param observer Oggetto observer da seegnalare.
     */
    @Override
    public void notifyObserver(IHeartRateObserver observer) {

    }

}
