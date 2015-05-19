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
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.kyloth.serleena.presenters.OnFragmentInteractionListener;
import com.kyloth.serleena.dummy.DummyTrack;

/**
 * Classe che implementa la visuale Selezione Percorso della schermata “Esperienza”.
 *
 * In questa visuale è possibile selezionare un percorso da attivare tra quelli disponibili.
 *
 * @field mListView : AbsListView lista di elementi
 * @field mAdapter : ListAdapter adattatore che collega la lista all'ExperienceSelectionFragment
 * @field mActivity : OnFragmentInteractionListener activity a cui è legato l'ExperienceSelectionFragment
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TrackSelectionFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mActivity;

    /**
     * La ListView del ExperienceSelectionFragment.
     */
    private AbsListView mListView;

    /**
     * L'Adapter che verrà utilizzato per popolare la ListView con Views.
     */
    private ListAdapter mAdapter;

    /**
     * Questo metodo viene invocato ogni volta che un TrackSelectionFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (OnFragmentInteractionListener) activity;
            mAdapter = new ArrayAdapter<DummyTrack.DummyItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, DummyTrack.ITEMS);

            // Set the adapter
            mListView = (AbsListView) activity.findViewById(android.R.id.list);
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);
            setEmptyText("Nessun percorso disponibile");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Questo metodo viene invocato ogni volta che un TrackSelectionFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }


    /**
     * Questo metodo gestisce i click su una voce della lista.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mActivity) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mActivity.onFragmentInteraction(DummyTrack.ITEMS.get(position).id);
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

}
