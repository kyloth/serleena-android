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


package com.kyloth.serleena.view.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class MapWidgetTest {

    private MapWidget mapWidget;

    @Before
    public void initialize() {
        mapWidget = new MapWidget(mock(Context.class));
    }

    /**
     * Verifica che il widget restituisca il quadrante come impostato
     * precedentemente.
     */
    @Test
    public void widgetShouldReturnCorrectQuadrant() {
        IQuadrant quadrant = mock(IQuadrant.class);
        mapWidget.setQuadrant(quadrant);
        assertEquals(quadrant, mapWidget.getQuadrant());
    }

    /**
     * Verifica che il widget restituisca la posizione utente come impostato
     * precedentemente.
     */
    @Test
    public void widgetShouldReturnCorrectUserPosition() {
        GeoPoint userPosition = mock(GeoPoint.class);
        mapWidget.setUserPosition(userPosition);
        assertEquals(userPosition, mapWidget.getUserPosition());
    }

    /**
     * Verifica che il widget restituisca i punti utente come impostato
     * precedentemente.
     */
    @Test
    public void widgetShouldReturnCorrectUserPoints() {
        Iterable<UserPoint> iterable = new ArrayList<>();
        mapWidget.setUserPoints(iterable);
        assertEquals(iterable, mapWidget.getUserPoints());
    }

    /**
     * Verifica che una chiamata a clear() pulisca gli elementi del widget,
     * quali quadrante, posizione utente e punti utente.
     */
    @Test
    public void clearShouldClearAllWidgetElements() {
        IQuadrant quadrant = mock(IQuadrant.class);
        Bitmap bitmap = mock(Bitmap.class);
        when(quadrant.getRaster()).thenReturn(bitmap);

        mapWidget.setQuadrant(quadrant);
        mapWidget.setUserPosition(mock(GeoPoint.class));
        mapWidget.setUserPoints(new ArrayList<UserPoint>());

        assertNotNull(mapWidget.getQuadrant());
        assertNotNull(mapWidget.getUserPoints());
        assertNotNull(mapWidget.getUserPosition());

        mapWidget.clear();

        assertNull(mapWidget.getQuadrant());
        assertNull(mapWidget.getUserPoints());
        assertNull(mapWidget.getUserPosition());
    }

    /**
     * Verifica che il widget non disegni i suoi elementi se nessun quadrante
     * è stato impostato.
     */
    @Test
    public void widgetShouldntDrawIfNoQuadrant() {
        Canvas canvas = mock(Canvas.class);

        mapWidget.onDraw(canvas);
        verifyZeroInteractions(canvas);

        mapWidget.setUserPosition(mock(GeoPoint.class));
        verifyZeroInteractions(canvas);

        mapWidget.setUserPoints(new ArrayList<UserPoint>());
        verifyZeroInteractions(canvas);
    }

}
