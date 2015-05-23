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
 * Name: CardioFragment
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
import android.view.View;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ICardioPresenter;
import com.kyloth.serleena.presentation.ICardioView;


/**
 * Classe che implementa la schermata “Cardio”, in cui vengono mostrate eventuali informazioni
 * relative ad un sensore Fitness collegato allo smartwatch
 *
 * @field presenter : ICardioPresenter presenter collegato a un CardioFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class CardioFragment extends Fragment implements ICardioView {

    ICardioPresenter presenter;

    /**
     * Questo metodo viene invocato ogni volta che un CardioFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TextView hr = (TextView) getActivity().findViewById(R.id.heart_rate);
        hr.setText("NESSUN DATO DA VISUALIZZARE");
        presenter.resume();
    }

    /**
     * Questo metodo viene invocato ogni volta che un CardioFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        presenter.pause();
    }

    /**
     * Metodo che visualizza sullo schermo dello smartwatch il valore del battito cardiaco.
     */
    @Override
    public void setHeartRate(int rate) {
        Integer rateValue = new Integer(rate);
        TextView hr = (TextView) getActivity().findViewById(R.id.heart_rate);
        hr.setText(rateValue.toString());
    }

    /**
     * Questo metodo serve per collegare un CardioFragment al proprio presenter.
     */
    @Override
    public void attachPresenter(ICardioPresenter presenter) {
        this.presenter = presenter;
    }
}
