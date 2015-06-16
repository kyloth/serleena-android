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
 * Name: ObjectListFragment.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione file e scrittura javadoc
 */

package com.kyloth.serleena.view.fragments;

import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kyloth.serleena.presentation.IObjectListObserver;
import com.kyloth.serleena.presentation.IObjectListView;

import java.util.ArrayList;

/**
 * Implementa una generica vista che mostra una lista di oggetti, la cui
 * selezione viene notificata a degli observer.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ObjectListFragment extends ListFragment implements
        IObjectListView {

    IObjectListObserver observer;
    private Object[] items;

    /**
     * Crea un oggetto ObjectListFragment.
     */
    public ObjectListFragment() {
    }

    /**
     * Implementa IObjectListView.setList().
     *
     * @param objects Array di oggetti da visualizzare sulla lista. Se null,
     *              viene sollevata un'eccezione IllegalNullException.
     */
    @Override
    public void setList(Iterable<Object> objects) {
        if (objects == null)
            throw new IllegalArgumentException("Illegal null array");

        ArrayList<Object> list = new ArrayList<Object>();
        for (Object o : objects)
            list.add(o);
        items = list.toArray();

        if (getActivity() != null)
            setListAdapter(new ArrayAdapter<Object>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    items));
    }

    /**
     * Implementa IObjectListView.attachObserver().
     *
     * @param observer Observer da associare.
     */
    @Override
    public void attachObserver(IObjectListObserver observer) {
        this.observer = observer;
    }

    /**
     * Ridefinisce ListFragment.onResume().
     *
     * Reimposta la lista da visualizzare per accertare che vengano mostrati
     * sempre i dati più aggiornati.
     */
    @Override
    public void onResume() {
        super.onResume();
        setListAdapter(new ArrayAdapter<Object>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                items));
    }

    /**
     * Ridefinisce ListFragment.onListItemClick().
     *
     * Segnala agli observer l'oggetto della lista selezionato.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (observer != null)
            observer.onObjectSelected(items[position]);
    }

}
