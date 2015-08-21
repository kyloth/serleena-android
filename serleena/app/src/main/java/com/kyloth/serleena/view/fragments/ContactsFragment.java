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

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presentation.IContactsView;


/**
 * Classe che implementa la schermata “Autorità locali”, in cui vengono mostrate eventuali
 * contatti di autorità locali
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia IContactsPresenter.
 * @field presenter : IContactsPresenter presenter collegato a un ContactsFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class ContactsFragment extends Fragment
        implements IContactsView, View.OnClickListener {

    private IContactsPresenter presenter;
    private TextView textName;
    private TextView textValue;
    private ImageView contactImage;
    /**
     * Crea un nuovo oggetto ContactsFragment.
     */
    public ContactsFragment() {
        /* Null object pattern. */
        presenter = new IContactsPresenter() {
            @Override
            public void nextContact() { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce Fragment.onCreateView().
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(
                R.layout.fragment_contacts,
                container,
                false);

        textName = (TextView) v.findViewById(R.id.contact_name_text);
        textValue = (TextView) v.findViewById(R.id.contact_value_text);
        contactImage = (ImageView) v.findViewById(R.id.contact_image);
        textName.setOnClickListener(this);
        textValue.setOnClickListener(this);
        contactImage.setOnClickListener(this);

        return v;
    }

    /**
     * Imposta un presenter per la vista.
     */
    @Override
    public void attachPresenter(IContactsPresenter presenter)
            throws IllegalArgumentException {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
    }

    /**
     * Visualizza il contatto specificato sulla vista.
     */
    @Override
    public void displayContact(String name, String contact) {
        if (name == null || contact == null)
            throw new IllegalArgumentException("Illegal null contact");

        textName.setText(name);
        textValue.setText(contact);
    }

    /**
     * Pulisce la vista.
     */
    @Override
    public void clearView() {
        textName.setText((String) getResources().getText(R.string.contacts_noContact));
        textValue.setText(" -- ");
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

    /**
     * Ridefinisce View.OnClickListener.onClick().
     *
     * @param v Vista che ha scatenato l'evento.
     */
    @Override
    public void onClick(View v) {
        presenter.nextContact();
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Autorità locali";
    }

}
