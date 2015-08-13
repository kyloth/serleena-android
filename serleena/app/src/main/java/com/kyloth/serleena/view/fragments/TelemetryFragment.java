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
 * Name: TelemetryFragment
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
import android.widget.ImageView;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITelemetryPresenter;
import com.kyloth.serleena.presentation.ITelemetryView;
import com.kyloth.serleena.sensors.TrackAlreadyStartedException;


/**
 * Classe che implementa la visuale “Tracciamento” della schermata “Esperienza”.
 *
 * In questa visuale è possibile attivare o disattivare il tracciamento.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ITelemetryPresenter.
 * @field presenter : ITelemetryPresenter presenter collegato a un TelemetryFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TelemetryFragment extends Fragment
        implements ITelemetryView, View.OnClickListener {

    /**
     * Presenter collegato a un TelemetryFragment
     */
    private ITelemetryPresenter presenter;
    private TextView status;

    public TelemetryFragment() {
        /* Null object pattern */
        presenter = new ITelemetryPresenter() {
            @Override
            public void enableTelemetry() { }
            @Override
            public void disableTelemetry() { }
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
        View v = inflater.inflate(R.layout.fragment_telemetry, container,
                false);
        ImageView iv = (ImageView) v.findViewById(R.id.telemetry_image);
        iv.setOnClickListener(this);

        status = (TextView) v.findViewById(R.id.telemetry_status);
        return v;
    }

    /**
     * Implementa ITelemetryView.attachPresenter().
     *
     * @param presenter Presenter da associare alla vista.
     */
    @Override
    public void attachPresenter(ITelemetryPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Ridefinisce Fragment.onResume().
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinisce Fragment.onPause().
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Implementa ITelemetryView.setTelemetryEnabled().
     */
    @Override
    public void setTelemetryEnabled(boolean enabled) {
        if (status != null) {
            if (enabled)
                status.setText("ON");
            else
                status.setText("OFF");
        }
    }

    /**
     * Implementa OnClickListener.onClick().
     */
    @Override
    public void onClick(View v) {
        if(status.getText().equals("ON")) {
            presenter.disableTelemetry();
            status.setText("OFF");
        }
        else {
            try {
                presenter.enableTelemetry();
                status.setText("ON");
            } catch (TrackAlreadyStartedException e) {
                status.setText("ERRORE: Percorso già avviato");
            }
        }
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Tracciamento";
    }

}
