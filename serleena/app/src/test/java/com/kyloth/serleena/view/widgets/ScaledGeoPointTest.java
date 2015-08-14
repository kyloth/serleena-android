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
 * Name: ScaledGeoPointTest.java
 * Package: com.kyloth.serleena.view.widgets;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.view.widgets;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IRegion;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test di unità per la classe ScaledGeoPoint
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ScaledGeoPointTest {

    /**
     * Verifica che l'oggetto restituisca dei valori sugli assi x e y scalati
     * correttamente.
     */
    @Test
    public void measuresShouldBeScaledCorrectly() {
        GeoPoint nw = mock(GeoPoint.class);
        when(nw.latitude()).thenReturn(5.0);
        when(nw.longitude()).thenReturn(0.0);
        GeoPoint se = mock(GeoPoint.class);
        when(se.latitude()).thenReturn(0.0);
        when(se.longitude()).thenReturn(5.0);

        IRegion region = mock(IRegion.class);
        when(region.getNorthWestPoint()).thenReturn(nw);
        when(region.getSouthEastPoint()).thenReturn(se);

        ScaledGeoPoint scaledGeoPoint =
                new ScaledGeoPoint(100, 50, region, new GeoPoint(2.5, 2.5));
        assertTrue(50 == scaledGeoPoint.x());
        assertTrue(25 == scaledGeoPoint.y());
    }

}
