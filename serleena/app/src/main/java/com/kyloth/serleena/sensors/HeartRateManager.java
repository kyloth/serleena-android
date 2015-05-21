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
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class HeartRateManager implements IHeartRateManager {

    private static HeartRateManager instance;

    private Context context;

    /**
     * Crea un oggetto HeartRateManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    private HeartRateManager(Context context) {
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

    /**
     * Restituisce la singola istanza della classe.
     *
     * Implementa il pattern Singleton.
     *
     * @param context Contesto dell'applicazione. Se null, viene sollevata
     *                un'eccezione IllegalArgumentException.
     * @return Istanza della classe.
     */
    public static HeartRateManager getInstance(Context context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");

        if (instance == null)
            instance = new HeartRateManager(context);
        return instance;
    }
}
