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
 * Name: LocationReachedRequest.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.GeoPoint;

/**
 * Rappresenta una richiesta, fatta ad un oggetto LocationReachedManager, di
 * essere notificati al raggiungimento di una particolare posizione geografica.
 *
 * La classe rappresenta questa richiesta incapsulando l'oggetto
 * ILocationObserver che ha richiesto l'aggiornamento, e la posizione geografica
 * al raggiungimento della quale questo vuole essere notificato.
 *
 * @use Viene utilizzato da LocationReachedManager per memorizzare e organizzare gli Observer registrati ad esso.
 * @field observer ILocationReachedObserver Observer a cui la richiesta si riferisce.
 * @field location GeoPoint Posizione geografica a cui la richiesta si riferisce.
 * @author Filippo Sestini <sestini.filippo@gmail,com>
 * @version 1.0.0
 */
public class LocationReachedRequest {

    private ILocationReachedObserver observer;
    private GeoPoint location;

    /**
     * Crea un nuovo oggetto LocationReachedRequest.
     *
     * @param observer Observer a cui si riferisce la richiesta.
     * @param location Posizione geografica a cui si riferisce la richiesta.
     */
    public LocationReachedRequest(
            ILocationReachedObserver observer, GeoPoint location) {
        this.observer = observer;
        this.location = location;
    }

    /**
     * Restituisce l'Observer a cui si riferisce la richiesta.
     *
     * @return Observer a cui si riferisce la richiesta.
     */
    public ILocationReachedObserver observer() {
        return observer;
    }

    /**
     * Restituisce la posizione geografica a cui si riferisce la richiesta.
     *
     * @return Posizione geografica a cui si riferisce la richiesta.
     */
    public GeoPoint location() {
        return location;
    }

    /**
     * Ridefinisce Object.equals().
     *
     * @param o Oggetto da confrontare
     * @return True se entrambi gli oggetti sono non null, di tipo
     * LocationReachedRequest, e gli oggetti ILocationObserver e
     * GeoPoint incapsulati sono uguali secondo i loro rispettivi metodi
     * equals(). False altrimenti.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof LocationReachedRequest) {
            LocationReachedRequest other = (LocationReachedRequest) o;
            return observer.equals(other.observer) &&
                    location.equals(other.location);
        }
        return false;
    }

    /**
     * Ridefinisce Object.hashCode()
     */
    public int hashCode() {
        return (int)(location.latitude() + location.longitude());
    }

}
