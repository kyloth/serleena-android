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
 * Name: TrackSelectionFragment
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kyloth.serleena.presentation.ITrackSelectionPresenter;
import com.kyloth.serleena.presentation.ITrackSelectionView;

import java.util.ArrayList;

/**
 * Classe che implementa la visuale Selezione Percorso della schermata “Esperienza”.
 *
 * In questa visuale è possibile selezionare un percorso da attivare tra quelli disponibili.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia \fixedwidth{ITrackSelectionPresenter}.
 * @field presenter : IExperienceSelectionPresenter Presenter collegato a un ExperienceSelectionFragment
 * @field mListView : AbsListView lista di elementi
 * @field mAdapter : ArrayAdapter adattatore che collega la lista all'ExperienceSelectionFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TrackSelectionFragment extends Fragment
        implements AbsListView.OnItemClickListener, ITrackSelectionView {

    /**
     * Presenter collegato a questo ExperienceSelectionFragment
     */
    private ITrackSelectionPresenter presenter;

    /**
     * Lista dei nomi dei percorsi
     */
    private ArrayList<String> trackNames = new ArrayList<>();

    /**
     * La ListView del ExperienceSelectionFragment.
     */
    private AbsListView mListView;

    /**
     * L'Adapter che verrà utilizzato per popolare la ListView con Views.
     */
    private ArrayAdapter<String> mAdapter;

    /**
     * Questo metodo viene invocato ogni volta che un TrackSelectionFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mAdapter = new ArrayAdapter<>(activity,android.R.layout.simple_list_item_1);
        for(String e : trackNames) mAdapter.add(e);

        // Set the adapter
        mListView = (AbsListView) activity.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        setEmptyText("Nessun percorso disponibile");
        presenter.resume();
    }

    /**
     * Questo metodo viene invocato ogni volta che un TrackSelectionFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        presenter.pause();
    }


    /**
     * Questo metodo gestisce i click su una voce della lista.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() != null) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            presenter.activateTrack(position);
        }
    }

    /**
     * Questo metodo salva un testo visualizzato sul display in caso di lista
     * vuota.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void setList(Iterable<String> names) {
        if(mAdapter == null)
            for (String name : names) trackNames.add(name);
        else
            for (String name : names) mAdapter.add(name);
    }

    @Override
    public void clearList() {
        if(mAdapter != null)
            mAdapter.clear();
    }

    @Override
    public void attachPresenter(ITrackSelectionPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Metodo invocato alla pressione del pulsante centrale dello smartwatch.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {    }
}
