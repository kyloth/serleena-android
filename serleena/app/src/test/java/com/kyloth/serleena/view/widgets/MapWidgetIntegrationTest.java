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
 * Name: MapWidgetIntegrationTest.java
 * Package: com.kyloth.serleena.view.widgets;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.view.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Test di unità per la classe MapWidget
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class MapWidgetIntegrationTest {

    private MapWidget mapWidget;
    @Before
    public void initialize() {
        mapWidget = new MapWidget(RuntimeEnvironment.application);
    }

    private static class CCC extends Canvas {
        float lastX, lastY, lastRadius;
        int drawCounts = 0;

        @Override
        public void drawCircle(float cx, float cy, float radius, Paint paint) {
            lastX = cx;
            lastY = cy;
            lastRadius = radius;
            drawCounts++;
        }
    }

    @Test
    public void test() {
        mapWidget.setQuadrant(new IQuadrant() {
            @Override
            public Bitmap getRaster() { return null; }
            @Override
            public GeoPoint getNorthWestPoint() { return mock(GeoPoint.class); }
            @Override
            public GeoPoint getSouthEastPoint() { return mock(GeoPoint.class); }
            @Override
            public boolean contains(GeoPoint p) { return false; }
        });
        mapWidget.setUserPosition(new GeoPoint(5, 5));
        CCC canvas = new CCC();
        mapWidget.onDraw(canvas);
        assertTrue(canvas.drawCounts == 2);
    }

    @Test
    public void testt() {
        mapWidget.setQuadrant(new IQuadrant() {
            @Override
            public Bitmap getRaster() { return null; }
            @Override
            public GeoPoint getNorthWestPoint() { return mock(GeoPoint.class); }
            @Override
            public GeoPoint getSouthEastPoint() { return mock(GeoPoint.class); }
            @Override
            public boolean contains(GeoPoint p) { return false; }
        });
        List<UserPoint> list = new ArrayList<>();
        list.add(mock(UserPoint.class));
        list.add(mock(UserPoint.class));
        mapWidget.setUserPoints(list);
        CCC canvas = new CCC();
        mapWidget.onDraw(canvas);
        assertTrue(canvas.drawCounts == 2);
    }

}
