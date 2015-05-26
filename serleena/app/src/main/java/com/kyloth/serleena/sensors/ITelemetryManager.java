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
 * Name: ITelemetryManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Gabriele Pozzan   2015-05-07   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;

/**
 * Interfaccia di un oggetto che offrirà al codice client servizi
 * di registrazione del Tracciamento con la possibilità di avviarlo,
 * interromperlo e segnalare manualmente eventi da registrare nel
 * Tracciamento in corso.
 *
 * @use Viene utilizzata da SensorManager per restituire ai client il gestore dei Tracciamenti, e dal client per accedere ai servizi offerti dal sensore. È utilizzato in particolare da TrackPresenter per governare il Tracciamento.
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public interface ITelemetryManager {

    /**
     * Restituisce gli eventi di Tracciamento registrati dall'ultimo avvio del
     * Tracciamento all'invocazione del metodo.
     *
     * @return  Insieme enumerabile di eventi di Tracciamento.
     */
    public Iterable<TelemetryEvent> getEvents();

    /**
     * Avvia il Tracciamento.
     *
     * Se vi è già un Tracciamento avviato, questo viene perso.
     */
    public void start();

    /**
     * Ferma il Tracciamento.
     *
     * Se nessun Tracciamento era stato avviato in precedenza, il metodo non ha
     * alcun effetto.
     */
    public void stop();

    /**
     * Permette di segnalare manualmente eventi da registrare
     * nel Tracciamento in corso.
     *
     * @param event Evento di Tracciamento da registrare.
     */
    public void signalEvent(TelemetryEvent event);

}
