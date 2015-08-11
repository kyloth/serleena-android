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
 * Name: TrackFragment
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */
package com.kyloth.serleena.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.transition.TransitionValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.sensors.NoActiveTrackException;
import com.kyloth.serleena.view.widgets.CompassWidget;


/**
 * Classe che implementa la visuale “Percorso” della schermata “Esperienza”.
 *
 * In questa visuale vengono visualizzate le informazioni riguardanti il percorso attivo.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ITrackPresenter.
 * @field presenter : ITrackPresenter presenter collegato a un TrackFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TrackFragment extends Fragment implements ITrackView, View.OnClickListener {

    private ITrackPresenter presenter;
    private TextView trackNameText;
    private TextView nextCheckpointText;
    private TextView distanceText;
    private CompassWidget orientationWidget;
    private TextView deltaText;
    private TextView lastPartialText;

    private int totalCheckpoints;
    private int nextCheckpoint;

    /**
     * Crea un nuovo oggetto TrackFragment.
     */
    public TrackFragment() {
        /* Null object pattern */
        presenter = new ITrackPresenter() {
            @Override
            public void advanceCheckpoint() throws NoTrackCrossingException { }
            @Override
            public void abortTrack() { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce Fragment.onCreateView()
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track, container, false);

        orientationWidget =
                (CompassWidget) v.findViewById(R.id.compass_widget_track);
        nextCheckpointText =
                (TextView) v.findViewById(R.id.checkpoint_numbers_text);
        trackNameText = (TextView) v.findViewById(R.id.track_name_text);
        distanceText = (TextView) v.findViewById(R.id.distance_text);
        deltaText = (TextView) v.findViewById(R.id.delta_text);
        lastPartialText = (TextView) v.findViewById(R.id.last_partial_text);

        orientationWidget.setOnClickListener(this);

        return v;
    }

    /**
     * Aggancia un presenter alla vista.
     *
     * @param presenter Presenter da agganciare. Se null, viene sollevata
     *                  un'eccezione IllegalArgumentException.
     */
    @Override
    public void attachPresenter(ITrackPresenter presenter) {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
    }

    /**
     * Pulisce la vista.
     */
    @Override
    public void clearView() {
        trackNameText.setText("NESSUN PERCORSO ATTIVO");
        nextCheckpointText.setText("");
        distanceText.setText("");
        orientationWidget.setOrientation(0);
        deltaText.setText("");
        lastPartialText.setText("");

        totalCheckpoints = 0;
        nextCheckpoint = 0;
    }

    /**
     * Imposta l'orientamento necessario all'utente a raggiungere il prossimo
     * checkpoint.
     *
     * @param heading Direzione del prossimo checkpoint in gradi.
     */
    @Override
    public void setDirection(float heading) {
        orientationWidget.setOrientation(heading);
    }

    /**
     * Metodo per impostare la distanza dal prossimo checkpoint.
     *
     * @param distance Distanza dal prossimo checkpoint
     */
    @Override
    public void setDistance(int distance) {
        if (distance < 0)
            throw new IllegalArgumentException("Illegal negative distance");

        distanceText.setText(distance + " m");
    }

    /**
     * Metodo per impostare il tempo di attraversamento dell'ultimo checkpoint.
     *
     * @param seconds Secondi impiegati per raggiungere l'ultimo checkpoint attraversato
     */
    @Override
    public void setLastPartial(int seconds) {
        if (seconds < 0)
            throw new IllegalArgumentException("Illegal negative partial");

        lastPartialText.setText("ULTIMO PARZIALE: " + seconds + " s");
    }

    /**
     * Metodo per impostare il vantaggio rispetto alla miglior prestazione.
     *
     * @param seconds Secondi di scarto dalla miglior prestazione
     */
    @Override
    public void setDelta(int seconds) {
        deltaText.setText("(" + seconds + " s)");
    }

    /**
     * Metodo per impostare l'ultimo checkpoint attraversato.
     *
     * @param n Numero che rappresenta quale checkpoint è stato attraversato
     */
    @Override
    public void setCheckpointNo(int n) {
        if (n <= 0)
            throw new IllegalArgumentException(
                    "Illegal null or negative checkpoint");

        nextCheckpoint = n;
        if (totalCheckpoints > 0)
            nextCheckpointText.setText(nextCheckpoint + "/" + totalCheckpoints);
    }

    /**
     * Metodo per impostare il numero totale di checkpoint.
     *
     * @param n Numero totale di checkpoint
     */
    @Override
    public void setTotalCheckpoints(int n) {
        if (n <= 0)
            throw new IllegalArgumentException(
                    "Illegal null or negative checkpoint");

        totalCheckpoints = n;
        if (nextCheckpoint > 0)
            nextCheckpointText.setText(nextCheckpoint + "/" + totalCheckpoints);
    }

    /**
     * ITrackView.telemetryEnabled().
     */
    @Override
    public void telemetryEnabled(boolean b) {

    }

    /**
     * ITrackView.displayTrackEnded().
     */
    @Override
    public void displayTrackEnded() {
        nextCheckpointText.setText("FINE");
    }

    /**
     * Implementa ITrackView.clearDelta().
     */
    @Override
    public void clearDelta() {
        deltaText.setText("");
    }

    /**
     * Implementa ITrackView.setTrackName()
     *
     * @param name Nome del Percorso.
     */
    @Override
    public void setTrackName(String name) {
        trackNameText.setText(name);
    }

    /**
     * Metodo invocato quando il Fragment viene visualizzato.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Metodo invocato quando il Fragment smette di essere visualizzato.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce Fragment.onDestroy().
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.abortTrack();
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Percorso";
    }

    @Override
    public void onClick(View v) {
        try {
            presenter.advanceCheckpoint();
        } catch (NoTrackCrossingException|NoActiveTrackException e) { }
    }
}
