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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UserPoint;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe che implementa il widget presente nella visuale Mappa della schermata Esperienza
 *
 * @field userPosition : GeoPoint salva il punto geografico corrispondente alla posizione dell'utente
 * @field upList : Iterable<UserPoint> lista dei Punti Utente relativi al quadrante visualizzato
 * @field layout : FrameLayout layout che viene visualizzato sul display
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 * @see com.kyloth.serleena.presentation.IMapView
 */
public class MapWidget extends ImageView {

    private GeoPoint userPosition;
    private Iterable<UserPoint> upList = new ArrayList<>();


    public MapWidget(Context context) {
        super(context);
    }

    public void setUserPosition(GeoPoint userPosition) {
        this.userPosition = userPosition;
    }

    public void setUserPoints(Iterable<UserPoint> ups) {
        upList= ups;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(userPosition != null)
            draw(canvas, null);
        Iterator<UserPoint> it = upList.iterator();
        while (it.hasNext()) {
            draw(canvas, it.next());
        }
        super.draw(canvas);
    }

    /**
     * Metodo con cui viene disegnata una mappa con posizione e punti utente.
     */
    private void draw(Canvas canvas, GeoPoint up) {
        ImageView img = new ImageView(getContext());
        img.setImageResource(R.drawable.user_point);
        if(up == null) { // il punto da visualizzare e' la posizione dell'utente
            up = userPosition;
            img.setImageResource(R.drawable.mirino);
        }
        Float top = (float) up.latitude();
        Float left = (float) up.longitude();
        img.setMaxHeight(100);
        img.setMaxWidth(100);
        BitmapDrawable pos = (BitmapDrawable) img.getDrawable();
        canvas.drawBitmap(pos.getBitmap(), left, top, null);
    }
}
