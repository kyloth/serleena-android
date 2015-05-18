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
import android.graphics.drawable.BitmapDrawable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kyloth.serleena.OnFragmentInteractionListener;
import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Classe che implementa la visuale “Mappa” della schermata “Esperienza”
 *
 * Implementa l'interfaccia IMapView, gestendo tutte le esigenze grafiche
 * riguardo alla visuale Mappa nella schermata Esperienza.
 *
 * @field userPosition : GeoPoint salva il punto geografico corrispondente alla posizione dell'utente
 * @field upList : Iterable<UserPoint> lista dei Punti Utente relativi al quadrante visualizzato
 * @field layout : FrameLayout layout che viene visualizzato sul display
 * @field mapRaster : BitmapDrawable immagine di background raffigurante una mappa, sopra la quale verranno disegnati punti utente e posizione
 * @field presenter : IMapPresenter presenter da notificare all'aggiunta del Fragment all'Activity
 * @field mActivity : OnFragmentInteractionListener activity a cui è legato il MapFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 * @see com.kyloth.serleena.presentation.IMapView
 */
public class MapFragment extends Fragment implements com.kyloth.serleena.presentation.IMapView  {
    private GeoPoint userPosition;
    private Iterable<UserPoint> upList = new ArrayList<>();

    private FrameLayout layout;
    private BitmapDrawable mapRaster;

    private IMapPresenter presenter;

    private OnFragmentInteractionListener mActivity;

    /**
     * Questo metodo viene invocato ogni volta che MapFragment viene collegato ad un'Activity.
     * Al termine di questo metodo, viene invocato il metodo resume() dell'IMapPresenter in ascolto.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            mapRaster = new BitmapDrawable(temp);
            ImageView map = (ImageView) getActivity().findViewById(R.id.map_image);
            map.setBackground(mapRaster);
            mActivity = (OnFragmentInteractionListener) activity;
            presenter.resume();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Questo metodo viene invocato ogni volta che MapFragment viene rimosso da un'Activity tramite
     * una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    /**
     * Viene impostata la posizione dell'utente e visualizzata la posizione dell'utente.
     *
     * @param point posizione dell'utente
     */
    @Override
    public void setUserLocation(GeoPoint point) {
        userPosition = point;
        draw();
    }

    /**
     * Metodo con cui viene visualizzato un quadrante.
     *
     * @param q Quadrante da visualizzare
     */
    @Override
    public void displayQuadrant(IQuadrant q) {
        Bitmap temp = q.getRaster();
        if(temp != null)
            mapRaster = new BitmapDrawable(temp);
        ImageView map = (ImageView) getActivity().findViewById(R.id.map_image);
        map.setBackground(mapRaster);
    }

    /**
     * Metodo con cui vengono visualizzati i Punti Utente sulla mappa
     *
     * @param points punti utente da visualizzare
     */
    @Override
    public void displayUP(Iterable<UserPoint> points) {
        upList = points;
        draw();
    }

    /**
     * Metodo con cui viene disegnata una mappa con posizione e punti utente.
     */
    private void draw() {
        layout = new FrameLayout(getActivity());
        layout.setBackground(mapRaster);
        drawPosition();
        drawUp();
        getActivity().setContentView(layout);
    }

    /**
     * Metodo per disegnare posizione sulla mappa.
     */
    private void drawPosition() {
        if(userPosition == null) return;
        ImageView img = new ImageView(getActivity());
        img.setImageResource(R.drawable.mirino);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        params.topMargin = (int) userPosition.latitude();
        params.leftMargin = (int) userPosition.longitude();
        layout.addView(img, params);
    }

    /**
     * Metodo per disegnare i punti utente sulla mappa.
     */
    private void drawUp() {
        Iterator<UserPoint> it = upList.iterator();
        while (it.hasNext()) {
            UserPoint up = it.next();
            ImageView img = new ImageView(getActivity());
            img.setImageResource(R.drawable.user_point);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
            params.topMargin = (int) up.latitude();
            params.leftMargin = (int) up.longitude();
            layout.addView(img, params);
        }
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
