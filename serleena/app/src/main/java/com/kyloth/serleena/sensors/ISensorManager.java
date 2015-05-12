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
 * Name: IHeadingManager.java
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

/**
 * Interfaccia ad un oggetto in grado di fornire Manager appropriati
 * per ciascun tipo di sensore.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public interface ISensorManager {

    /**
     * Restituisce un Manager per ottenere dati sulla posizione
     * dell'Escursionista.
     *
     * @return Oggetto di tipo ILocationManager.
     */
    public ILocationManager getLocationSource();

    /**
     * Restituisce un Manager per ottenere dati sulla posizione
     * dell'Escursionista anche quando il dispositivo è il sleep mode.
     *
     * @return Oggetto ILocationManager.
     */
    public ILocationManager getWakefulLocationSource();

    /**
     * Restituisce un Manager per ottenere dati sull'orientamento
     * dell'Escursionista rispetto ai punti cardinali.
     *
     * @return Oggetto di tipo IHeadingManager.
     */
    public IHeadingManager getHeadingSource();

    /**
     * Restituisce un Manager per ottenere dati sul battito cardiaco
     * dell'Escursionista.
     *
     * @return Oggetto di tipo IHeartRateManager.
     */
    public IHeartRateManager getHeartRateSource();

    /**
     * Restituisce un Manager per permettere la notifica a determinati
     * oggetti al raggiungimento di particolari punti geografici.
     *
     * @return Oggetto di tipo ILocationReachedManager.
     */
    public ILocationReachedManager getLocationReachedSource();

    /**
     * Restituisce un Manager per ottenere notifiche di wakeup del
     * processore utilizzando RTC.
     *
     * @return Oggetto di tipo IWakeupManager.
     */
    public IWakeupManager getWakeupSource();

    /**
     * Restituisce un Manager per offire servizi di registrazione del
     * Tracciamento dell'Escursionista.
     *
     * @return Oggetto di tipo ITelemetryManager.
     */
    public ITelemetryManager getTelemetryManager();
}
