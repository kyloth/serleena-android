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
import android.graphics.Canvas;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CompassWidgetTest {

    /**
     * Verifica che il widget ritorni un valore di orientamento come
     * impostato precedentemente.
     */
    @Test
    public void getOrientationShouldReturnCorrectOrientation() {
        CompassWidget widget = new CompassWidget(mock(Context.class));
        widget.setOrientation(5.0f);
        assertTrue(5.0f == widget.getOrientation());
    }

    /**
     * Verifica che il valore di orientamento impostato causi una rotazione
     * dell'immagine visualizzata dal widget in base ai gradi impostati.
     */
    @Test
    public void onDrawShouldRotateCanvas() {
        CompassWidget widget = new CompassWidget(mock(Context.class));
        widget.setOrientation(5.0f);
        Canvas canvas = mock(Canvas.class);

        widget.onDraw(canvas);
        verify(canvas).rotate(
                eq(-5.0f), any(Integer.class), any(Integer.class));
    }

}
