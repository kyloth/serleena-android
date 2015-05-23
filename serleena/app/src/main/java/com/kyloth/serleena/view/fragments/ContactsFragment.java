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
 * Name: ContactsFragment
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
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presentation.IContactsView;


/**
 * Classe che implementa la schermata “Autorità locali”, in cui vengono mostrate eventuali
 * contatti di autorità locali
 *
 * @field presenter : IContactsPresenter presenter collegato a un ContactsFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class ContactsFragment extends Fragment implements IContactsView {

    /**
     * Presenter collegato a un ContactsFragment
     */
    private IContactsPresenter presenter;

    /**
     * Questo metodo viene invocato ogni volta che ContactsFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        presenter.resume();
    }

    /**
     * Questo metodo viene invocato ogni volta che un ContactsFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        presenter.pause();
    }

    /**
     * Metodo che collega un ContactsFragment al proprio Presenter.
     */
    @Override
    public void attachPresenter(IContactsPresenter presenter) throws IllegalArgumentException {
        this.presenter = presenter;
    }

    /**
     * Metodo che richiede a un ContactsFragment di visualizzare un contatto
     */
    @Override
    public void displayContact(String name, String contact) {
        TextView textName = (TextView) getActivity().findViewById(R.id.contact_name);
        TextView textValue = (TextView) getActivity().findViewById(R.id.contact_value);
        textName.setText(name);
        textValue.setText(contact);
    }

    /**
     * Metodo che rimuove il contatto visualizzato
     */
    @Override
    public void clearView() {
        TextView textName = (TextView) getActivity().findViewById(R.id.contact_name);
        TextView textValue = (TextView) getActivity().findViewById(R.id.contact_value);
        textValue.setText("NESSUN CONTATTO");
        textName.setText("DA VISUALIZZARE");
    }

    /**
     * Metodo che richiede la visualizzazione del contatto successivo alla pressione del pulsante
     * centrale.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {
        presenter.nextContact();
    }
}
