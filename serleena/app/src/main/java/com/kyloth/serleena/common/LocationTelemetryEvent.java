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
 * Name: LocationTelemetryEvent.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import java.util.Date;

/**
 * Rappresenta un evento di tracciamento che registra la posizione geografica dell'utente,
 * individuato dall'istante di campionamento e un oggetto GeoPoint.
 *
 * @author  Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0
 * @since   2015-05-05
 */
public class LocationTelemetryEvent extends TelemetryEvent {

    private GeoPoint location;

    /**
     * Crea un nuovo evento di tracciamento della posizione utente.
     *
     * @param timestamp     Istante di tempo in cui l'evento è stato campionato.
     * @param location      Posizione geografica campionata dall'evento.
     */
    public LocationTelemetryEvent(Date timestamp, GeoPoint location) {
        this.timestamp = timestamp;
        this.location = location;
    }

    /**
     * Restituisce la posizione geografica campionata dall'evento.
     *
     * @return Posizione geografica campionata.
     */
    public GeoPoint location() {
        return location;
    }

    /**
     * Overriding del metodo equals() della superclasse Object.
     *
     * @param o Oggetto da comparare.
     * @return True se entrambi gli oggetti hanno tipo LocationTelemetryEvent e si riferiscono al
     *         medesimo evento, quindi con uguale timestamp e posizione geografica campionata.
     *         False altrimenti.
     */
    public boolean equals(Object o) {
        if (o instanceof LocationTelemetryEvent) {
            LocationTelemetryEvent other = (LocationTelemetryEvent) o;
            return this.location().equals(other.location()) &&
                this.timestamp().equals(other.timestamp());
        }
        return false;
    }

}
