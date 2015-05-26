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

import android.app.Activity;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITelemetryPresenter;
import com.kyloth.serleena.presentation.ITelemetryView;


/**
 * Classe che implementa la visuale “Tracciamento” della schermata “Esperienza”.
 *
 * In questa visuale è possibile attivare o disattivare il tracciamento.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia \fixedwidth{ITelemetryPresenter}.
 * @field presenter : ITelemetryPresenter presenter collegato a un TelemetryFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TelemetryFragment extends Fragment implements ITelemetryView {

    /**
     * Presenter collegato a un TelemetryFragment
     */
    private ITelemetryPresenter presenter;

    /**
     * Questo metodo viene invocato ogni volta che un TelemetryFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        ImageButton button = (ImageButton) activity.findViewById(R.id.telemetry_image);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView status = (TextView) activity.findViewById(R.id.telemetry_status);
                if(status.getText().equals("ON")) {
                    presenter.disableTelemetry();
                    status.setText("OFF");
                }
                else {
                    presenter.enableTelemetry();
                    status.setText("ON");
                }
            }
        });
        presenter.resume();
    }

    /**
     * Questo metodo viene invocato ogni volta che un TelemetryFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        presenter.pause();
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
     * Metodo che richiede la abilitazione o la disabilitazione del tracciamento.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {
        TextView status = (TextView) getActivity().findViewById(R.id.telemetry_status);
        if(status.getText().equals("ON")) {
            presenter.disableTelemetry();
            status.setText("OFF");
        }
        else {
            presenter.enableTelemetry();
            status.setText("ON");
        }
    }
}
