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

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ICardioPresenter;
import com.kyloth.serleena.presentation.ICardioView;


/**
 * Implementa la schermata “Cardio”, in cui vengono mostrate informazioni
 * relative ad un eventuale sensore Fitness collegato allo smartwatch
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ICardioPresenter.
 * @field presenter : ICardioPresenter presenter collegato a un CardioFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class CardioFragment extends Fragment implements ICardioView {

    private ICardioPresenter presenter;
    private TextView hr;

    public CardioFragment() {
        /* Null object pattern */
        presenter = new ICardioPresenter() {
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        hr = (TextView) v.findViewById(R.id.heartrate_text);
        return v;
    }

    /**
     * Indica alla vista il valore di battito cardiaco da visualizzare.
     */
    @Override
    public void setHeartRate(int rate) {
        hr.setText(String.valueOf(rate) + " bpm");
    }

    /**
     * Aggancia un Presenter alla vista.
     */
    @Override
    public void attachPresenter(ICardioPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Ridefinizione di Fragment.onResume().
     *
     * Segnala al presenter che la vista è in stato attivo e visibile.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinizione di Fragment.onPause().
     *
     * Segnala al presenter che la vista è in stato non visibile.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

}
