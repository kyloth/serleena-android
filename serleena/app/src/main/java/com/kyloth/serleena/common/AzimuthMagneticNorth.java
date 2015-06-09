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
 * Name: AzimuthMagneticNorth.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import android.hardware.GeomagneticField;

/**
 * Rappresenta un valore di rotazione sull'asse azimuth, centrato sul nord
 * magnetico.
 */
public class AzimuthMagneticNorth {
    private final float azimuthMagneticNorth;

    /**
     * Crea un nuovo oggetto AzimuthMagneticNorth. Un valore pari a 0 indica un
     * orientamento esatto verso il nord magnetico.
     *
     * @param azimuthMagneticNorth Rotazione, in gradi, attorno all'asse
     *                             azimuth. Un valore < 0 o > 360 solleva
     *                             un'eccezione IllegalArgumentException.
     */
    public AzimuthMagneticNorth(float azimuthMagneticNorth) {
        if (azimuthMagneticNorth < 0 || azimuthMagneticNorth > 360)
            throw new IllegalArgumentException("azimuth out of range");

        this.azimuthMagneticNorth = azimuthMagneticNorth;
    }

    /**
     * Restituisce il valore di orientamento rappresentato dall'istanza
     * rispetto al nord reale, in base alla declinazione del campo magnetico
     * della posizione geografica specificata.
     *
     * @param location Posizione geografica in base al quale calcolare la
     *                 declinazione del campo magnetico.
     * @return Rotazione in gradi, sull'asse azimuth rispetto al nord reale. Un
     * valore 0 indica un orientamento esatto verso il nord reale.
     */
    public float toTrueNorth(GeoPoint location) {
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");
        GeomagneticField geoField =
                new GeomagneticField(
                        Double.valueOf(location.latitude()).floatValue(),
                        Double.valueOf(location.longitude()).floatValue(),
                        0, System.currentTimeMillis());

        float declination = geoField.getDeclination();
        return azimuthMagneticNorth - declination;
    }
}
