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
 * Name: SensorNotAvailableException.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-08
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-08   Creazione file e scrittura
 *                                         codice e documentazione Javadoc.
 */

package com.kyloth.serleena.sensors;

/**
 * Segnala la non disponibilità di un sensore.
 *
 * Viene sollevata quando si verifica una situazione eccezionale
 * nell'esecuzione del codice, in cui viene richiesto l'utilizzo di un sensore
 * che non è disponibile nel dispositivo.
 *
 * @author Filippo Sestini
 * @version 1.0.0
 */
public class SensorNotAvailableException extends Exception {

    private String sensor;

    /**
     * Crea un nuovo oggetto SensorNotAvailableException.
     *
     * @param sensor Nome del sensore.
     */
    public SensorNotAvailableException(String sensor) {
        super();
        this.sensor = sensor;
    }

    /**
     * Restituisce il nome del sensore oggetto dell'eccezione.
     *
     * @return Nome del sensore.
     */
    public String getSensorName() {
        return sensor;
    }

}
