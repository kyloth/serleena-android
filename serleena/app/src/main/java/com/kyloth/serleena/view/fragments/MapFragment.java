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
import android.graphics.Canvas;
import android.view.KeyEvent;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.R;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.view.widgets.MapWidget;

/**
 * Classe che implementa la visuale “Mappa” della schermata “Esperienza”
 *
 * Implementa l'interfaccia IMapView, gestendo tutte le esigenze grafiche
 * riguardo alla visuale Mappa nella schermata Esperienza.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia IMapPresenter.
 * @field mapWidget : MapWidget widget utilizzato per disegnare la mappa
 * @field presenter : IMapPresenter presenter da notificare all'aggiunta del Fragment all'Activity
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 * @see com.kyloth.serleena.presentation.IMapView
 */
public class MapFragment extends Fragment implements com.kyloth.serleena.presentation.IMapView  {

    private MapWidget mapWidget;

    private IMapPresenter presenter;

    /**
     * Questo metodo viene invocato ogni volta che un MapFragment viene collegato ad un'Activity.
     * Al termine di questo metodo, viene invocato il metodo resume() dell'IMapPresenter in ascolto.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mapWidget = (MapWidget) activity.findViewById(R.id.map_image);
    }

    /**
     * Questo metodo viene invocato ogni volta che un MapFragment viene rimosso da un'Activity tramite
     * una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Viene impostata la posizione dell'utente ed in seguito visualizzata.
     *
     * @param point posizione dell'utente
     */
    @Override
    public void setUserLocation(GeoPoint point) {
        mapWidget.setUserPosition(point);
        mapWidget.draw(new Canvas());
    }

    /**
     * Metodo con cui viene visualizzato un quadrante.
     *
     * @param q Quadrante da visualizzare
     */
    @Override
    public void displayQuadrant(IQuadrant q) {
        mapWidget.setQuadrant(q);
        mapWidget.draw(new Canvas());
    }

    /**
     * Metodo con cui vengono visualizzati i Punti Utente sulla mappa
     *
     * @param points punti utente da visualizzare
     */
    @Override
    public void displayUP(Iterable<UserPoint> points) {
        mapWidget.setUserPoints(points);
        mapWidget.draw(new Canvas());
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

    /**
     * Metodo che richiede la creazione di un Punto Utente.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {
        try {
            presenter.newUserPoint();
        }
        catch (NoActiveExperienceException e) {}
    }

    /**
     * Metodo invocato quando il Fragment viene visualizzato.
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Metodo invocato quando il Fragment smette di essere visualizzato.
     *
     */
    @Override
    public void onStop() {
        super.onStop();
        presenter.pause();
    }
}
