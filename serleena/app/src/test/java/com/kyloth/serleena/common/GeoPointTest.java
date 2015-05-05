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
 * Name: GeoPointTest.java
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

import com.kyloth.serleena.common.UserPoint;

import org.junit.Test;

import java.lang.Object;
import java.lang.String;
import dalvik.annotation.TestTargetClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Contiene i test di unità per la classe GeoPoint.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public class GeoPointTest {

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     *
     * Vengono testati i valori restituiti dai metodi della classe.
     * In particolare, si verifica che i valori passati al costruttore nella
     * creazione dell'oggetto vengano correttamente restituiti dai metodi.
     */
    @Test
    public void testGetters() {
        double latitude = 5;
        double longitude = 10;
        GeoPoint up = new GeoPoint(latitude, longitude);

        org.junit.Assert.assertTrue(up.latitude() == latitude);
        org.junit.Assert.assertTrue(up.longitude() == longitude);
        org.junit.Assert.assertTrue(up.latitude() != 10);
        org.junit.Assert.assertTrue(up.longitude() != 5);
    }

    /**
     * Testa la correttezza del metodo equals().
     */
    @Test
    public void testEqualsMethod() {
        double latitude1 = 5;
        double longitude1 = 10;
        GeoPoint gp1 = new GeoPoint(latitude1, longitude1);

        double latitude2 = 5;
        double longitude2 = 10;
        GeoPoint gp2 = new GeoPoint(latitude2, longitude2);

        double latitude3 = 115;
        double longitude3 = 130;
        GeoPoint gp3 = new GeoPoint(latitude3, longitude3);

        org.junit.Assert.assertTrue(gp1.equals(gp2));
        org.junit.Assert.assertTrue(!(gp1.equals(gp3)));
        org.junit.Assert.assertTrue(!(gp1.equals(new Object())));
        org.junit.Assert.assertTrue(!(gp1.equals(null)));
    }

}
