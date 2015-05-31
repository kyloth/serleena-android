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

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;
import com.kyloth.serleena.presentation.ISerleenaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Concretizza IExperienceSelectionPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field experiences : List<IExperience> Esperienze tra cui è possibile selezionare
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @field view : IExperienceSelectionView Vista associata al Presenter
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ExperienceSelectionPresenter
        implements IExperienceSelectionPresenter {

    private List<IExperience> experiences;
    private ISerleenaActivity activity;
    private IExperienceSelectionView view;

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

        this.activity = activity;
        this.view = view;
        this.experiences = new ArrayList<IExperience>();
        List<String> names = new ArrayList<String>();

        for (IExperience exp : activity.getDataSource().getExperiences()) {
            experiences.add(exp);
            names.add(exp.getName());
        }

        view.setList(names);
        view.attachPresenter(this);
    }

    /**
     * Implementa IExperienceSelectionPresenter.activateExperience().
     *
     * L'esperienza selezionata viene segnalata all'activity
     * dell'applicazione, che si occupa di segnalare i restanti presenter e
     * rendere l'attivazione effettiva.
     *
     * @param index Indice della lista di esperienze che rappresenta la
     *              selezione dell'utente. Se minore di zero,
     *              viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    @Override
    public void activateExperience(int index)
            throws IllegalArgumentException {
        if (index < 0 || index >= experiences.size())
            throw new IllegalArgumentException("Index out of range");

        activity.setActiveExperience(experiences.get(index));
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Non effettua operazioni, in quanto i contenuti presentati non variano
     * finchè l'applicazione è avviata, e non vengono utilizzate risorse da
     * rilasciare.
     */
    @Override
    public void resume() {

    }

    /**
     * Implementa IPresenter.pause().
     *
     * Non effettua operazioni, in quanto i contenuti presentati non variano
     * finchè l'applicazione è avviata, e non vengono utilizzate risorse da
     * rilasciare.
     */
    @Override
    public void pause() {

    }

}
