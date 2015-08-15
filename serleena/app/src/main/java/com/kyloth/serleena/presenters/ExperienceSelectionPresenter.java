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
 * Name: ExperienceSelectionPresenter
 * Package: com.hitchikers.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer        Changes
 * 1.0        Filippo Sestini   Creazione del file
 */

package com.kyloth.serleena.presenters;

import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IExperienceActivationSource;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;

import java.util.ArrayList;
import java.util.List;

/**
 * Concretizza IExperienceSelectionPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia. Gli oggetti di tipo TrackSelectionPresenter e MapPresenter si registrano ai suoi eventi per ottenere informazioni sull'attivazione delle Esperienze.
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @field view : IObjectListView Vista associata al Presenter
 * @field selectedExperience : IExperience Esperienza selezionata e correntemente attiva
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ExperienceSelectionPresenter
        implements IExperienceSelectionPresenter, IExperienceActivationSource {

    private IExperience selectedExperience;
    private IExperienceSelectionView view;
    private ISerleenaActivity activity;

    /**
     * Crea un oggetto ExperienceSelectionPresenter.
     *
     * Accede al data source attraverso l'activity, dal quale ottiene le
     * esperienze caricate nel dispositivo, che vengono usate per popolare la
     * vista.
     * Si collega alla vista tramite il metodo attachPresenter() esposto
     * dall'interfaccia della vista.
     *
     * @param view Vista da associare al presenter. Se null,
     *             viene sollevata un'eccezione IllegalArgumentException.
     * @param activity Activity che rappresenta l'applicazione in corso e al
     *                 quale il presenter appartiene. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public ExperienceSelectionPresenter(IExperienceSelectionView view,
                                        ISerleenaActivity activity) {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.view = view;
        this.activity = activity;
        view.attachPresenter(this);
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Refresha la vista con l'elenco di Esperienze memorizzate nel datasource.
     */
    @Override
    public void resume() {
        view.setExperiences(activity.getDataSource().getExperiences());
    }

    /**
     * Implementa IPresenter.pause().
     *
     * Non effettua operazioni.
     */
    @Override
    public void pause() {

    }

    /**
     * Implementa IExperienceSelectionPresenter.activateExperience().
     *
     * @param experience Esperienza selezionata dall'utente e che deve essere
     *                   attivata. Se null, viene sollevata un'eccezione
     *                   IllegalArgumentException
     */
    @Override
    public void activateExperience(IExperience experience)
            throws IllegalArgumentException {
        if (experience == null)
            throw new IllegalArgumentException("Illegal null experience");
        selectedExperience = experience;
    }

    /**
     * Implementa IExperienceActivationSource.activeExperience().
     *
     * @return Esperienza correntemente attiva.
     */
    @Override
    public IExperience activeExperience() throws NoActiveExperienceException {
        if (selectedExperience != null)
            return selectedExperience;
        else
            throw new NoActiveExperienceException();
    }
}
