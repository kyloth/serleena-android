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
 * Name: AzimuthMagneticNorthTest.java
 * Package: com.kyloth.serleena.common;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file, scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.common;

import android.hardware.SensorManager;

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
     * Verifica che il passaggio di un parametro null al metodo toTrueNorth()
     * sollevi un'eccezione.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorWithNullArgumentsShouldThrow() {
        float[] value = new float[] { 0, 0, 0 };
        AzimuthMagneticNorth az = new AzimuthMagneticNorth(value, value);
        az.toTrueNorth(null);
    }

    /**
     * Verifica che vengano restituiti valori corretti di orientamento in base a
     * valori di campo magnetico e accelerazione noti.
     */
    @Test
    public void objectShouldReturnCorrectValues() {
        final float[] accelerometerValues = new float[] { 11, 22, 33 };
        final float[] magneticFieldValues = new float[] { 32, 21, 10 };
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        float expected = -(float) Math.toDegrees(values[0]);

        AzimuthMagneticNorth az = new AzimuthMagneticNorth(
                accelerometerValues, magneticFieldValues);
        assertEquals(expected, az.orientation(), 0);

        GeoPoint loc = new GeoPoint(34, -118);
        float result = az.toTrueNorth(loc);
        assertEquals(expected + 12, result, 0.5);
    }

    /**
     * Verifica che il metodo equals() rispetti la specifica.
     */
    @Test
    public void twoEqualInstancesShouldBeEqual() {
        final float[] accelerometerValues = new float[] { 11, 22, 33 };
        final float[] magneticFieldValues = new float[] { 32, 21, 10 };

        AzimuthMagneticNorth az1 = new AzimuthMagneticNorth
                (accelerometerValues, magneticFieldValues);
        AzimuthMagneticNorth az2 = new AzimuthMagneticNorth
                (accelerometerValues, magneticFieldValues);
        assertTrue(az1.equals(az2));

        assertTrue(!az1.equals(null));
        assertTrue(!az1.equals(new Object()));
        assertTrue(!az1.equals(new AzimuthMagneticNorth(
                new float[] { 33, 22, 11 },
                new float[] {43, 5, 12 }
        )));
    }

    /**
     * Verifica che istanze considerate equivalenti dal metodo equals() abbiano
     * un hash code uguale, secondo il contratto imposto da Object.
     */
    @Test
    public void equalsShouldHaveSameHashCode() {
        final float[] accelerometerValues = new float[] { 11, 22, 33 };
        final float[] magneticFieldValues = new float[] { 32, 21, 10 };

        AzimuthMagneticNorth az1 = new AzimuthMagneticNorth
                (accelerometerValues, magneticFieldValues);
        AzimuthMagneticNorth az2 = new AzimuthMagneticNorth
                (accelerometerValues, magneticFieldValues);

        assertTrue(az1.hashCode() == az2.hashCode());
    }

}
