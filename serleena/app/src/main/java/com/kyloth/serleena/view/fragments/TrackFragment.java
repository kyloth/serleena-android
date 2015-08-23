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

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;


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
     * Implementa ITrackView.attachPresenter().
     */
    @Override
    public void attachPresenter(ITrackPresenter presenter) {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
    }

    /**
     * Implementa ITrackView.clearView().
     */
    @Override
    public void clearView() {
        trackNameText.setText((String) getResources().getText(R.string.track_noActiveTrack));
        distanceText.setText("");
        orientationWidget.setOrientation(0);
        clearStats();
        clearCheckpoints();
    }

    /**
     * Implementa ITrackView.setDirection().
     */
    @Override
    public void setDirection(float heading) {
        orientationWidget.setOrientation(heading);
    }

    /**
     * Implementa ITrackView.setDistance().
     */
    @Override
    public void setDistance(int distance) {
        if (distance < 0)
            throw new IllegalArgumentException("Illegal negative distance");

        distanceText.setText(distance + " m");
    }

    /**
     * Implementa ITrackView.setLastPartial().
     */
    @Override
    public void setLastPartial(int seconds) {
        if (seconds < 0)
            throw new IllegalArgumentException("Illegal negative partial");

        int millis = seconds * 1000;
        String s = String.format(Locale.US, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );

        lastPartialText.setText(s);
    }

    /**
     * Implementa ITrackView.setDelta().
     */
    @Override
    public void setDelta(int seconds) {
        String sign = "+";
        if (seconds < 0) {
            sign = "-";
        }
        seconds = abs(seconds);
        int millis = seconds * 1000;
        String s = sign + String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );

        deltaText.setText(s);
    }

    /**
     * Implementa ITrackView.setCheckpointNo().
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
     * Implementa ITrackView.setTotalCheckpoints().
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
     * Implementa ITrackView.displayTrackEnded().
     */
    @Override
    public void displayTrackEnded() {
        nextCheckpointText.setText((String) getResources().getText(R.string.track_finish));
    }

    /**
     * Implementa ITrackView.setTrackName()
     */
    @Override
    public void setTrackName(String name) {
        if (name == null)
            throw new IllegalArgumentException();
        trackNameText.setText(name);
    }

    /**
     * Implementa ITrackView.clearCheckpoints().
     */
    @Override
    public void clearCheckpoints() {
        totalCheckpoints = 0;
        nextCheckpoint = 0;
        nextCheckpointText.setText("");
    }

    /**
     * Implementa ITrackView.clearStats().
     */
    @Override
    public void clearStats() {
        lastPartialText.setText("");
        deltaText.setText("");
    }

    /**
     * Ridefinisce Fragment.onResume().
     *
     * Chiama il rispettivo metodo resume() per Presenter.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinisce Fragment.onPause().
     *
     * Chiama il rispettivo metodo pause() per Presenter.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce Object.toString().
     *
     * Restituisce il nome del Fragment.
     */
    @Override
    public String toString() {
        return "Percorso";
    }

    /**
     * Ridefinisce OnClickListener.onClick().
     *
     * Comunica al Presenter la volontà di avanzare di checkpoint.
     */
    @Override
    public void onClick(View v) {
        try {
            presenter.advanceCheckpoint();
        } catch (NoTrackCrossingException|NoActiveTrackException e) { }
    }
}
