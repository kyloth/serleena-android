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
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

import android.location.Location;
import android.location.LocationManager;

import org.junit.Test;
import java.lang.Object;

import static org.junit.Assert.*;

/**
 * Contiene i test di unità per la classe GeoPoint.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class GeoPointTest {

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     **/
    @Test
    public void testGeoPointGettersWork() {
        double latitude = 5;
        double longitude = 10;
        GeoPoint up = new GeoPoint(latitude, longitude);
        org.junit.Assert.assertTrue(up.latitude() == latitude);
        org.junit.Assert.assertTrue(up.longitude() == longitude);
    }

    /**
     * Verifica che i campi di GeoPoint non vengano scambiati
     */
    @Test
    public void testGeoPointDoesntSwap() {
        double latitude = 5;
        double longitude = 10;
        GeoPoint up = new GeoPoint(latitude, longitude);
        org.junit.Assert.assertTrue(up.latitude() != 10);
        org.junit.Assert.assertTrue(up.longitude() != 5);
    }

    /**
     * Verifica che non sia possibile sfondare il limite inferiore del range
     * ammissibile per la latitudine di GeoPoint
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGeoPointEnforcesXRangeTop() {
        double latitude = -91;
        double longitude = -180;
        GeoPoint up = new GeoPoint(latitude, longitude);
    }

    /**
     * Verifica che non sia possibile sfondare il limite superiore del range
     * ammissibile per la latitudine di GeoPoint
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGeoPointEnforcesXRangeBottom() {
        double latitude = 91;
        double longitude = 180;
        GeoPoint up = new GeoPoint(latitude, longitude);
    }

    /**
     * Verifica che non sia possibile sfondare il limite superiore del range
     * ammissibile per la longitudine di GeoPoint
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGeoPointEnforcesYRangeTop() {
        double latitude = -90;
        double longitude = 181;
        GeoPoint up = new GeoPoint(latitude, longitude);
    }

    /**
     * Verifica che non sia possibile sfondare il limite inferiore del range
     * ammissibile per la longitudine di GeoPoint
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGeoPointEnforcesYRangeBottom() {
        double latitude = 90;
        double longitude = -181;
        GeoPoint up = new GeoPoint(latitude, longitude);
    }

    /**
     * Verifica la correttezza del metodo equals().
     */
    @Test
    public void testEquals() {
        double latitude1 = 5;
        double longitude1 = 10;
        GeoPoint gp1 = new GeoPoint(latitude1, longitude1);

        double latitude2 = 5;
        double longitude2 = 10;
        GeoPoint gp2 = new GeoPoint(latitude2, longitude2);

        double latitude3 = 90;
        double longitude3 = 130;
        GeoPoint gp3 = new GeoPoint(latitude3, longitude3);

        org.junit.Assert.assertTrue(gp1.equals(gp2));
        org.junit.Assert.assertTrue(!(gp1.equals(gp3)));
        org.junit.Assert.assertTrue(!(gp1.equals(new Object())));
        org.junit.Assert.assertTrue(!(gp1.equals(null)));
        org.junit.Assert.assertTrue(
                !(new GeoPoint(5, 10).equals(new GeoPoint(5, 20))));
    }

    /**
     * Verifica la correttezza dell'overriding di hashCode().
     */
    @Test
    public void testHashCode() {
        GeoPoint gp1 = new GeoPoint(20, 30);
        org.junit.Assert.assertTrue(gp1.hashCode() == gp1.hashCode());

        GeoPoint gp2 = new GeoPoint(20, 30);
        org.junit.Assert.assertTrue(gp1.hashCode() == gp2.hashCode());
    }

    /**
     * Verifica la correttezza del metodo distanceTo().
     */
    @Test
    public void testDistanceTo() {
        double lat1 = 20, lon1 = 30, lat2 = -45, lon2 = 32;

        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        float realDistance = results[0];

        GeoPoint gp1 = new GeoPoint(lat1, lon1);
        GeoPoint gp2 = new GeoPoint(lat2, lon2);

        assertEquals(realDistance, gp1.distanceTo(gp2), 0);
    }

}
