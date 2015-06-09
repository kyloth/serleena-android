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


package com.kyloth.serleena.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Contiene i test di unità per la classe AzimuthMagneticNorth.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class AzimuthMagneticNorthTest {

    /**
     * Verifica che i risultati restituiti dalla correzione da nord
     * magnetico a nord reale siano esatti, confrontando i dati restituiti
     * con valori noti a priori.
     */
    @Test
    public void testCorrectResult() {
        AzimuthMagneticNorth az = new AzimuthMagneticNorth(0);
        GeoPoint loc = new GeoPoint(34, -118);
        float result = az.toTrueNorth(loc);
        assertEquals(-12, result, 0.5);
    }

    /**
     * Verifica che il passaggio di un parametro null al metodo toTrueNorth()
     * sollevi un'eccezione.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatNullArgumentThrows() {
        AzimuthMagneticNorth az = new AzimuthMagneticNorth(0);
        az.toTrueNorth(null);
    }

    /**
     * Verifica che un valore di azimuth fuori range (negativo) sollevi
     * un'eccezione alla costruzione dell'istanza.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatAzimuthOutOfRangeThrows1() {
        new AzimuthMagneticNorth(-10);
    }

    /**
     * Verifica che un valore di azimuth fuori range (> 360) sollevi
     * un'eccezione alla costruzione dell'istanza.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatAzimuthOutOfRangeThrows2() {
        new AzimuthMagneticNorth(400);
    }

}
