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
 * Name: QuitFragment.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura del codice e di Javadoc
 */

package com.kyloth.serleena.view.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.KeyEventCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kyloth.serleena.R;

/**
 * Rappresenta una schermata attraverso il quale è possibile arrestare in modo
 * sicuro l'applicazione.
 *
 * @use Viene inserito da SerleenaActivity nel menù principale.
 * @field yesButton : Button Pulsante di conferma
 * @field noButton : Button Pulsante di rifiuto
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class QuitFragment extends Fragment implements View.OnClickListener {

    private Button yesButton;
    private Button noButton;
    private View.OnClickListener yesListener;
    private View.OnClickListener noListener;

    public QuitFragment() { }

    /**
     * Ridefinisce Fragment.onCreateView().
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quit, container, false);
        yesButton = (Button) v.findViewById(R.id.yes_button);
        noButton = (Button) v.findViewById(R.id.no_button);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        return v;
    }


    /**
     * Ridefinisce OnClickListener.onClick().
     *
     * @param v Vista che ha scatenato l'evento.
     */
    @Override
    public void onClick(View v) {
        if (v == yesButton)
            onYesClick();
        else
            onNoClick();
    }

    /**
     * Ridefinisce Object.toString().
     *
     * @return Nome del Fragment
     */
    @Override
    public String toString() {
        return "Esci";
    }

    /**
     * Viene invocato in seguito al rifiuto dell'utente di uscire
     * dall'applicazione. Invoca il Listener impostato con setOnNoClickListener.
     */
    public void onYesClick() {
        if (yesListener != null)
            yesListener.onClick(yesButton);
    }

    /**
     * Viene invocato in seguito alla conferma dell'utente di uscire
     * dall'applicazione. Invoca il Listener impostato con
     * setOnYesClickListener.
     */
    public void onNoClick() {
        if (noListener != null)
            noListener.onClick(noButton);
    }

    /**
     * Imposta il Listener che verrà chiamato alla conferma dell'utente di
     * uscire dall'applicazione.
     */
    public void setOnYesClickListener(View.OnClickListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("Illegal null listener");
        yesListener = listener;
    }

    /**
     * Imposta il Listener che verrà chiamato al rifiuto dell'utente di
     * uscire dall'applicazione.
     */
    public void setOnNoClickListener(View.OnClickListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("Illegal null listener");
        noListener = listener;
    }

}
