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
 * Name: TelemetryEvent.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import java.util.Date;

/**
 * Rappresenta un generico evento di tracciamento, individuato dall'istante di campionamento.
 *
 * @use Viene istanziato da sensors.TelemetryManager e restituito al codice client da oggetti di interfaccia ITelemetry, che restituiscono gli eventi di Tracciamento associati ad essi. Viene inoltre utilizzato dai DAO per la persistenza dei Tracciamenti.
 * @field timestamp : int Secondi dall'avvio del Tracciamento all'istante di campionamento dell'evento
 * @author  Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0
 */
public abstract class TelemetryEvent {

    private int timestamp;

    /**
     * Crea un nuovo oggetto TelemetryEvent.
     *
     * Poichè la classe è astratta, questo costruttore può essere invocato
     * solamente da sue sottoclassi.
     *
     * @param timestamp Istante di campionamento dell'evento,
     *                  espresso in secondi trascorsi dall'avvio del
     *                  Tracciamento.
     */
    public TelemetryEvent(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Restituisce l'istante di tempo in cui l'evento è stato campionato,
     * espresso in secondi trascorsi dall'avvio del Tracciamento.
     *
     * @return Secondi trascorsi dall'avvio del Tracciamento.
     */
    public int timestamp() {
        return this.timestamp;
    }

    /**
     * Restituisce il tipo di evento di Tracciamento.
     *
     * @return Valore enumerazione indicante in tipo di evento.
     */
    public abstract TelemetryEventType getType();

}
