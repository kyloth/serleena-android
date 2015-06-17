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
 * Name: ExperienceSelectionFragment.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione file e scrittura javadoc
 */

package com.kyloth.serleena.view.fragments;

        import android.app.ListFragment;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

        import com.kyloth.serleena.model.IExperience;
        import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
        import com.kyloth.serleena.presentation.IExperienceSelectionView;

        import java.util.ArrayList;

/**
 * Implementa IExperienceSelectionFragment offrendo una vista da cui è
 * possibile selezionare l'Esperienza da attivare da una lista.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ExperienceSelectionFragment extends ListFragment
        implements IExperienceSelectionView {

    private IExperienceSelectionPresenter presenter;
    private IExperience[] experiences;

    /**
     * Crea un oggetto ExperienceSelectionFragment.
     */
    public ExperienceSelectionFragment() {
        /* Null object pattern */
        presenter = new IExperienceSelectionPresenter() {
            @Override
            public void activateExperience(IExperience experience)
                    throws IllegalArgumentException { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce ListFragment.onCreate().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.experiences = new IExperience[] { };
    }

    /**
     * Implementa IExperienceSelectionView.setExperiences().
     *
     * @param experiences Esperienze da visualizzare sulla lista. Se null,
     *                    viene sollevata un'eccezione IllegalArgumentException.
     */
    @Override
    public void setExperiences(Iterable<IExperience> experiences) {
        if (experiences == null)
            throw new IllegalArgumentException("Illegal null experiences");

        ArrayList<IExperience> list = new ArrayList<IExperience>();
        for (IExperience t : experiences)
            list.add(t);
        this.experiences = (IExperience[]) list.toArray();

        if (getActivity() != null)
            setListAdapter(new ArrayAdapter<IExperience>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    this.experiences));
    }

    /**
     * Implementa IExperienceSelectionView.attachObserver().
     *
     * @param presenter Presenter da associare. Se null, viene sollevata
     *                  un'eccezione IllegalArgumentException.
     */
    @Override
    public void attachPresenter(IExperienceSelectionPresenter presenter) {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
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
        setListAdapter(new ArrayAdapter<IExperience>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,
                this.experiences));
        presenter.resume();
    }

    /**
     * Ridefinisce ListFragment.onPause().
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce ListFragment.onListItemClick().
     *
     * Segnala al presenter il Percorso selezionato dall'utente.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        presenter.activateExperience(this.experiences[position]);
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Imposta Esperienza";
    }

}
