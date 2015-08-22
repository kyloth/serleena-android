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
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice
 *                              e di Javadoc
 */

package com.kyloth.serleena.view.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.view.widgets.MapWidget;

/**
 * Classe che implementa la schermata "Mappa".
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia IMapPresenter.
 * @field presenter : IMapPresenter Presenter collegato alla vista
 * @field map : MapWidget Componente che realizza la mappa
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
public class MapFragment extends Fragment implements IMapView,
        View.OnClickListener {

    private MapWidget map;
    private IMapPresenter presenter;

    /**
     * Crea un nuovo oggetto MapFragment.
     */
    public MapFragment() {
        /* Null object pattern */
        presenter = new IMapPresenter() {
            @Override
            public void newUserPoint() throws NoActiveExperienceException { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce Fragment.onCreateView().
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        map = (MapWidget) v.findViewById(R.id.map_widget);
        map.setOnClickListener(this);
        return v;
    }

    /**
     * Implementa OnClickListener.onClick().
     *
     * Segnala al Presenter la volontà di aggiungere un Punto Utente per la
     * posizione geografica attuale.
     * Se non vi è alcuna Esperienza attiva, o non è possibile geolocalizzare
     * correttamente il dispositivo, viene mostrato un messaggio di errore.
     */
    @Override
    public void onClick(View v) {
        try {
            presenter.newUserPoint();
        } catch (NoActiveExperienceException e) {
            AlertDialog.Builder alertBuilder =
                    new AlertDialog.Builder(getActivity());
            alertBuilder.setMessage(
                    (String) getResources().getText(
                            R.string.map_noActiveExperience));
            alertBuilder.setNeutralButton(
                    (String) getResources().getText(
                            R.string.map_accept),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertBuilder.create().show();
        } catch (LocationNotAvailableException ee) {
            AlertDialog.Builder alertBuilder =
                    new AlertDialog.Builder(getActivity());
            alertBuilder.setMessage(
                    (String) getResources().getText(
                            R.string.map_cantFix));
            alertBuilder.setNeutralButton(
                    (String) getResources().getText(
                            R.string.map_accept),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertBuilder.create().show();
        }
    }

    /**
     * Implementa IMapView.setUserLocation().
     *
     * Imposta la mappa con la posizione utente specificata.
     *
     * @param point Posizione utente da visualizzare
     */
    @Override
    public void setUserLocation(GeoPoint point) {
        map.setUserPosition(point);
    }

    /**
     * Implementa IMapView.displayQuadrant().
     *
     * Imposta la mappa con il quadrante specificato.
     *
     * @param q Quadrante da visualizzare
     */
    @Override
    public void displayQuadrant(IQuadrant q) {
        if (q == null)
            throw new IllegalArgumentException("Illegal null quadrant");
        map.setQuadrant(q);
    }

    /**
     * Implementa IMapView.displayUP().
     *
     * Imposta la mappa con i punti utente specificati.
     *
     * @param points Punti Utente da visualizzare
     */
    @Override
    public void displayUP(Iterable<UserPoint> points) {
        if (points == null)
            throw new IllegalArgumentException("Illegal null points");
        map.setUserPoints(points);
    }

    /**
     * Implementa IMapView.attachPresenter().
     */
    @Override
    public void attachPresenter(IMapPresenter presenter) {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
    }

    /**
     * Implementa IMapView.clear().
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * Ridefinisce Fragment.onResume().
     *
     * Notifica il Presenter che la vista si trova in primo piano.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinisce Fragment.onPause().
     *
     * Notifica il Presenter che la vista si trova in background.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Mappa";
    }

}
