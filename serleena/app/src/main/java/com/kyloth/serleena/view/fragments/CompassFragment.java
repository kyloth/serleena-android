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
 * Name: CompassFragment
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

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.view.widgets.CompassWidget;

/**
 * Classe che implementa la schermata “Bussola”, in cui vengono fornite all'utente delle indicazioni
 * rispetto i poli geografici
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ICompassPresenter.
 * @field presenter : ICompassPresenter presenter collegato ad un CompassFragment
 * @field widget : CompassWidget componente grafica che visualizza una bussola sullo schermo dello smartwatch
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class CompassFragment extends Fragment implements ICompassView {

    private ICompassPresenter presenter;

    private CompassWidget widget;

    /**
     * Questo metodo viene invocato ogni volta che un CompassFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * Metodo utilizzato per impostare il widget da visualizzare
     *
     * @param wid Widget da visualizzare sul Fragment
     */
    public void setWidget(CompassWidget wid) {
        widget = wid;
    }

    /**
     * Metodo per ottenere il Widget visualizzato dal Fragment
     *
     * @return widget Il CompassWidget visualizzato sul CompassFragment
     */
    public CompassWidget getWidget() {
        return widget;
    }

    /**
     * Metodo utilizzato per impostare l'orientamento del CompassWidget visualizzato.
     */
    @Override
    public void setHeading(double heading) {
        widget.setVisibility(View.VISIBLE);
        widget.setBearing((float) heading);
    }

    /**
     * Metodo utilizzato per collegare un ICompassPresenter a un CompassFragment
     */
    @Override
    public void attachPresenter(ICompassPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Metodo utilizzato per smettere di visualizzare il CompassWidget.
     */
    @Override
    public void clearView() {
        widget.setVisibility(View.INVISIBLE);
    }

    /**
     * Metodo invocato alla pressione del pulsante centrale dello smartwatch.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {

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
    public void onPause() {
        super.onPause();
        presenter.pause();
    }
}
