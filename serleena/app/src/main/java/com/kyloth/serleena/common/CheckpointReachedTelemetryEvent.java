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
 * Name: CheckpointReachedTelemetryEvent.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

import java.util.Date;

/**
 * Rappresenta un evento di tracciamento che registra il raggiungimento di un
 * checkpoint durante lo svolgimento di un persorso da parte dell'utente.
 * E` individuato dall'istante di campionamento e dal numero del checkpoint
 * raggiunto.
 *
 * @author  Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0
 * @since   2015-05-05
 */
public class CheckpointReachedTelemetryEvent extends TelemetryEvent {

    private int checkpointNumber;

    /**
     * Crea un nuovo evento di tracciamento di raggiungimento di un checkpoint.
     *
     * @param timestamp         Istante di tempo di campionamento,
     *                          espresso in secondi dall'avvio del Tracciamento.
     * @param checkpointNumber  Numero del checkpoint raggiunto.
     */
    public CheckpointReachedTelemetryEvent(int timestamp,
                                           int checkpointNumber) {
        super(timestamp);
        this.checkpointNumber = checkpointNumber;
    }

    /**
     * Restituisce il numero di checkpoint associato all'evento.
     *
     * @return Numero del checkpoint raggiunto.
     */
    public int checkpointNumber() {
        return checkpointNumber;
    }


    /**
     * Overriding del metodo equals() della superclasse Object.
     *
     * @param o Oggetto da comparare.
     * @return  True se l'oggetto da comparare è false, entrambi gli oggetti
     *          hanno tipo CheckpointReachedTelemetryEvent e si riferiscono al
     *          medesimo evento, quindi con uguale timestamp e numero di
     *          checkpoint superato. False altrimenti.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof CheckpointReachedTelemetryEvent) {
            CheckpointReachedTelemetryEvent other =
                    (CheckpointReachedTelemetryEvent) o;
            return this.checkpointNumber() == other.checkpointNumber() &&
                    this.timestamp() == other.timestamp();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return checkpointNumber;
    }

    @Override
    public TelemetryEventType getType() {
        return TelemetryEventType.CheckpointReached;
    }
}
