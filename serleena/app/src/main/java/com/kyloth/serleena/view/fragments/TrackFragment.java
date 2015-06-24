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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.view.widgets.CompassWidget;


/**
 * Classe che implementa la visuale “Percorso” della schermata “Esperienza”.
 *
 * In questa visuale vengono visualizzate le informazioni riguardanti il percorso attivo.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ITrackPresenter.
 * @field presenter : ITrackPresenter presenter collegato a un TrackFragment
 * @field direction : Float direzione del prossimo checkpoint
 * @field totalCheckpoints : Integer numero di checkpoint totali
 * @field currentCheckpoint : Integer checkpoint a cui è arrivato l'utente
 * @field distance : Integer distanza dal prossimo checkpoint
 * @field gainVsGhost : Integer vantaggio netto sulla prestazione del ghost
 * @field elapsedTime : Integer tempo rilevato all'ultimo checkpoint
 * @field partialTW : TextView View dove è visualizzato il confronto tra checkpoint attraversati e totali
 * @field nextTW : TextView View dove sono visualizzate informazioni sul successivo checkpoint
 * @field elapsedTimeTW : TextView View dove è visualizzato il tempo parziale rilevato all'ultimo checkpoint
 * @field ghostTimeTW : TextView View dove è visualizzato il guadagno rispetto alla prestazione migliore
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TrackFragment extends Fragment implements ITrackView {

    /**
     * Presenter collegato a un TrackFragment
     */
    private ITrackPresenter presenter;

    /**
     * Crea un nuovo oggetto TrackFragment.
     */
    public TrackFragment() {
        /* Null object pattern */
        presenter = new ITrackPresenter() {
            @Override
            public void advanceCheckpoint() throws NoTrackCrossingException { }
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
        return inflater.inflate(R.layout.fragment_track, container, false);
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

    }

    /**
     * Imposta l'orientamento necessario all'utente a raggiungere il prossimo
     * checkpoint.
     *
     * @param heading Direzione del prossimo checkpoint in gradi.
     */
    @Override
    public void setDirection(float heading) {

    }

    /**
     * Metodo per impostare la distanza dal prossimo checkpoint.
     *
     * @param distance Distanza dal prossimo checkpoint
     */
    @Override
    public void setDistance(int distance) {

    }

    /**
     * Metodo per impostare il tempo di attraversamento dell'ultimo checkpoint.
     *
     * @param seconds Secondi impiegati per raggiungere l'ultimo checkpoint attraversato
     */
    @Override
    public void setLastPartial(int seconds) {

    }

    /**
     * Metodo per impostare il vantaggio rispetto alla miglior prestazione.
     *
     * @param seconds Secondi di scarto dalla miglior prestazione
     */
    @Override
    public void setDelta(int seconds) {

    }

    /**
     * Metodo per impostare l'ultimo checkpoint attraversato.
     *
     * @param n Numero che rappresenta quale checkpoint è stato attraversato
     */
    @Override
    public void setCheckpointNo(int n) {

    }

    /**
     * Metodo per impostare il numero totale di checkpoint.
     *
     * @param n Numero totale di checkpoint
     */
    @Override
    public void setTotalCheckpoints(int n) {

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
        throw new UnsupportedOperationException();
    }

    /**
     * Implementa ITrackView.clearDelta().
     */
    @Override
    public void clearDelta() {

    }

    /**
     * Metodo invocato quando il Fragment viene visualizzato.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
        CompassWidget w = (CompassWidget) getView().findViewById(R.id
                .compass_widget_track);
        w.setDirection(90);
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
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Percorso";
    }

}
