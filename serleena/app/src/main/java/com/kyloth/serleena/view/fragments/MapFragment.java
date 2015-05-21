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
 * Name: MapFragment
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kyloth.serleena.presenters.OnFragmentInteractionListener;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.R;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.view.widgets.MapWidget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * Classe che implementa la visuale “Mappa” della schermata “Esperienza”
 *
 * Implementa l'interfaccia IMapView, gestendo tutte le esigenze grafiche
 * riguardo alla visuale Mappa nella schermata Esperienza.
 *
 * @field mapRaster : Bitmap immagine di background raffigurante una mappa, sopra la quale verranno disegnati punti utente e posizione
 * @field mapWidget : MapWidget widget utilizzato per disegnare la mappa
 * @field presenter : IMapPresenter presenter da notificare all'aggiunta del Fragment all'Activity
 * @field mActivity : OnFragmentInteractionListener activity a cui è legato il MapFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 * @see com.kyloth.serleena.presentation.IMapView
 */
public class MapFragment extends Fragment implements com.kyloth.serleena.presentation.IMapView  {

    private Bitmap mapRaster;
    private MapWidget mapWidget;

    private IMapPresenter presenter;
    private OnFragmentInteractionListener mActivity;

    /**
     * Questo metodo viene invocato ogni volta che un MapFragment viene collegato ad un'Activity.
     * Al termine di questo metodo, viene invocato il metodo resume() dell'IMapPresenter in ascolto.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (OnFragmentInteractionListener) activity;
            mapWidget = (MapWidget) activity.findViewById(R.id.map_image);
            presenter.resume();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Questo metodo viene invocato ogni volta che un MapFragment viene rimosso da un'Activity tramite
     * una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    private void drawCanvas() {
        mapWidget.draw(new Canvas());
    }

    /**
     * Viene impostata la posizione dell'utente e visualizzata la posizione dell'utente.
     *
     * @param point posizione dell'utente
     */
    @Override
    public void setUserLocation(GeoPoint point) {
        mapWidget.setUserPosition(point);
        drawCanvas();
    }

    /**
     * Metodo con cui viene visualizzato un quadrante.
     *
     * @param q Quadrante da visualizzare
     */
    @Override
    public void displayQuadrant(IQuadrant q) {
        mapWidget.setRaster(q.getRaster());
        drawCanvas();
    }

    /**
     * Metodo con cui vengono visualizzati i Punti Utente sulla mappa
     *
     * @param points punti utente da visualizzare
     */
    @Override
    public void displayUP(Iterable<UserPoint> points) {
        mapWidget.setUserPoints(points);
        drawCanvas();
    }

    /**
     * Metodo per eseguire un'operazione di subscribe relativa ad un IMapPresenter.
     *
     * @param presenter oggetto collegato che verrà notificato da questo Fragment
     */
    @Override
    public void attachPresenter(IMapPresenter presenter) {
        this.presenter = presenter;
    }

}
