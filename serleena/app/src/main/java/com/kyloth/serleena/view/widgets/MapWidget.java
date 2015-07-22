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
 * Name: MapWidget
 * Package: com.kyloth.serleena.view.widgets
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura del codice e di
 *                              Javadoc
 */

package com.kyloth.serleena.view.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;

import java.util.ArrayList;

/**
 * Classe che implementa il widget presente nella visuale Mappa della schermata Esperienza
 *
 * @field quadrant : IQuadrant Quadrante da visualizzare
 * @field userPosition : GeoPoint salva il punto geografico corrispondente alla posizione dell'utente
 * @field upList : Iterable<UserPoint> lista dei Punti Utente relativi al quadrante visualizzato
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.widget.ImageView
 */
public class MapWidget extends ImageView {

    private IQuadrant quadrant;

    private GeoPoint userPosition;
    private Iterable<UserPoint> upList = new ArrayList<>();

    /**
     * Crea un nuovo oggetto MapWidget.
     */
    public MapWidget(Context context) {
        super(context);
        init(context);
    }

    /**
     * Crea un nuovo oggetto MapWidget.
     */
    public MapWidget(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Crea un nuovo oggetto MapWidget.
     */
    public MapWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * TODO: Inizializzazione a dei valori dimostrativi. Eliminare!
     */
    public void init(Context context) {
        this.setQuadrant(new IQuadrant() {
            @Override
            public Bitmap getRaster() {
                Resources res = getResources();
                Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable
                        .roadmap_240);
                return bitmap;
            }

            @Override
            public GeoPoint getNorthWestPoint() {
                return new GeoPoint(30, 30);
            }

            @Override
            public GeoPoint getSouthEastPoint() {
                return new GeoPoint(10, 10);
            }

            @Override
            public boolean contains(GeoPoint p) throws IllegalArgumentException {
                return true;
            }
        });
        ArrayList<UserPoint> ups = new ArrayList<>();
        ups.add(new UserPoint(11, 20));
        ups.add(new UserPoint(21, 14));
        ups.add(new UserPoint(10, 10));
        ups.add(new UserPoint(30, 30));
        this.setUserPosition(new GeoPoint(20, 20));
        this.setUserPoints(ups);
    }

    /**
     * Imposta il quadrante da visualizzare.
     *
     * @param q Quadrante da visualizzare.
     */
    public void setQuadrant(IQuadrant q) {
        quadrant = q;
    }

    /**
     * Imposta la posizione dell'utente da visualizzare.
     *
     * @param userPosition Posizione dell'utente da visualizzare.
     */
    public void setUserPosition(GeoPoint userPosition) {
        this.userPosition = userPosition;
    }

    /**
     * Imposta la lista di punti utente da visualizzare.
     *
     * @param ups Punti utente da visualizzare.
     */
    public void setUserPoints(Iterable<UserPoint> ups) {
        upList = ups;
    }

    /**
     * Ridefinisce ImageView.onDraw().
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(quadrant != null) {
            if (quadrant.getRaster() != null)
                canvas.drawBitmap(quadrant.getRaster(), 0, 0, null);

            for (UserPoint up : upList)
                if (quadrant.contains(up))
                    drawUserPoint(up, canvas);

            drawUserPosition(userPosition, canvas);
        }
    }

    public void clear() {
        quadrant = null;
        userPosition = null;
        upList = null;
        invalidate();
    }

    private void drawUserPoint(UserPoint up, Canvas canvas) {
        Paint red = new Paint();
        red.setColor(Color.RED);
        Paint black = new Paint();
        black.setColor(Color.BLACK);
        canvas.drawCircle(getLeft(up), getTop(up), 6, black);
        canvas.drawCircle(getLeft(up), getTop(up), 5, red);
    }

    private void drawUserPosition(GeoPoint gp, Canvas canvas) {
        Paint white = new Paint();
        white.setColor(Color.WHITE);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        Paint black = new Paint();
        black.setColor(Color.BLACK);
        canvas.drawCircle(getLeft(gp), getTop(gp), 10, black);
        canvas.drawCircle(getLeft(gp), getTop(gp), 9, white);
        canvas.drawCircle(getLeft(gp), getTop(gp), 6, blue);
    }

    private float getTop(GeoPoint point) {
        double topLat = quadrant.getNorthWestPoint().latitude();
        double botLat = quadrant.getSouthEastPoint().latitude();
        double height = getMeasuredHeight();
        return (float)(height*(topLat - point.latitude()) / (topLat - botLat));
    }

    private float getLeft(GeoPoint point) {
        double topLon = quadrant.getNorthWestPoint().longitude();
        double botLon = quadrant.getSouthEastPoint().longitude();
        double width = getMeasuredWidth();
        return (float)(width * (topLon-point.longitude()) / (topLon - botLon));
    }

}
