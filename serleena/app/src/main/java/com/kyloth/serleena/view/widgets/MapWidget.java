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
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */

package com.kyloth.serleena.view.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UserPoint;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe che implementa il widget presente nella visuale Mappa della schermata Esperienza
 *
 * @field mapRaster : Bitmap immagine di background raffigurante una mappa, sopra la quale verranno disegnati punti utente e posizione
 * @field userPosition : GeoPoint salva il punto geografico corrispondente alla posizione dell'utente
 * @field upList : Iterable<UserPoint> lista dei Punti Utente relativi al quadrante visualizzato
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.widget.ImageView
 */
public class MapWidget extends ImageView {

    private Bitmap mapRaster;

    private GeoPoint userPosition;
    private Iterable<UserPoint> upList = new ArrayList<>();

    /**
     * Costruttore di MapWidget a un parametro.
     *
     * @param context Activity in cui è presente il widget
     */
    public MapWidget(Context context) {
        super(context);
        mapRaster = BitmapFactory.decodeResource(context.getResources(),R.drawable.background);
    }

    /**
     * Costruttore di MapWidget a due parametri.
     *
     * @param context Activity in cui è presente il widget
     * @param attrs attributi come altezza, larghezza definiti nell'XML corrispondente a MapWidget
     */
    public MapWidget(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    /**
     * Costruttore di MapWidget a tre parametri.
     *
     * @param context Activity in cui è presente il widget
     * @param attrs attributi come altezza, larghezza definiti nell'XML corrispondente a MapWidget
     * @param defStyle stile da applicare a questa vista, può corrispondere a 0 o a un'id di una risorsa
     */
    public MapWidget(Context context,AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
    }

    /**
     * Metodo che imposta l'immagine da visualizzare relativa alla mappa.
     *
     * @param raster Bitmap contenente la mappa
     */
    public void setRaster(Bitmap raster) {
        mapRaster = raster;
    }

    /**
     * Metodo che imposta la posizione dell'utente da visualizzare.
     *
     * @param userPosition Posizione dell'utente
     */
    public void setUserPosition(GeoPoint userPosition) {
        this.userPosition = userPosition;
    }

    /**
     * Metodo che imposta la lista dei punti utente da visualizzare
     *
     * @param ups Punti utente
     */
    public void setUserPoints(Iterable<UserPoint> ups) {
        upList= ups;
    }

    /**
     * Metodo che viene invocato quando si richiede l'operazione draw() di un'ImageView.
     *
     * @param canvas Oggetto su cui disegnare
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mapRaster,0,0,null);
        if(userPosition != null)
            draw(canvas, null);
        Iterator<UserPoint> it = upList.iterator();
        while (it.hasNext()) {
            draw(canvas, it.next());
        }
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        super.onDraw(canvas);
    }

    /**
     * Metodo che disegna un punto sul canvas.
     *
     * @param canvas Oggetto su cui disegnare
     * @param point Punto geografico (posizione o punto utente) da visualizzare
     */
    private void draw(Canvas canvas, GeoPoint point) {
        ImageView img = new ImageView(getContext());
        img.setImageResource(R.drawable.user_point);
        if(point == null) { // il punto da visualizzare e' la posizione dell'utente
            point = userPosition;
            img.setImageResource(R.drawable.mirino);
        }
        Float top = (float) point.latitude();
        Float left = (float) point.longitude();
        img.setMaxHeight(100);
        img.setMaxWidth(100);
        Bitmap bmp = ((BitmapDrawable) img.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888,true);
        bmp = bmp.createScaledBitmap(bmp,100,100,false);
        canvas.drawBitmap(bmp, left, top, null);
    }
}
