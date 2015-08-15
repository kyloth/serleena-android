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
 * Name: ExperienceSelectionPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;


/**
 * Contiene i test di unità per la classe ExperienceSelectionPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class ExperienceSelectionPresenterTest {

    private ISerleenaActivity activity;
    private IExperienceSelectionView view;
    private Iterable<IExperience> exps;

    /**
     * Inizializza i campi dati necessari a condurre i test.
     */
    @Before
    public void initialize() {
        activity = mock(ISerleenaActivity.class);
        view = mock(IExperienceSelectionView.class);
        exps = new ArrayList<IExperience>();
        ISerleenaDataSource ds = mock(ISerleenaDataSource.class);
        when(ds.getExperiences()).thenReturn(exps);
        when(activity.getDataSource()).thenReturn(ds);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * al tentativo di costruire un oggetto con view nulla.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullView() {
        new ExperienceSelectionPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * al tentativo di costruire un oggetto con activity nulla.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullActivity() {
        new ExperienceSelectionPresenter(view, null);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * al tentativo di costruire un oggetto con entrambi i parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments() {
        new ExperienceSelectionPresenter(null, null);
    }

    /**
     * Verifica che activateExperience() sollevi un'eccezione
     * IllegalArgumentException se gli viene passato un parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void activateExperienceShouldThrowWhenNullExperience() {
        new ExperienceSelectionPresenter(view, activity)
                .activateExperience(null);
    }

    /**
     * Verifica che il presenter popoli la vista durante resume().
     */
    @Test
    public void presenterShouldPopulateViewOnResume() {
        IExperienceSelectionPresenter p =
                new ExperienceSelectionPresenter(view, activity);
        p.resume();
        verify(view).setExperiences(exps);
    }

    /**
     * Verifica che activeExperience() restituisca correttamente l'Esperienza
     * attiva.
     */
    @Test
    public void activateExperienceShouldNotifyObservers()
            throws NoActiveExperienceException {
        ExperienceSelectionPresenter p =
                new ExperienceSelectionPresenter(view, activity);
        p.activateExperience(mock(IExperience.class));
        IExperience e = mock(IExperience.class);
        p.activateExperience(e);
        assertEquals(e, p.activeExperience());
    }

    /**
     * Verifica che activeExperience() sollevi un'eccezione
     * NoActiveExperienceException se non vi è un'Esperienza attiva.
     */
    @Test(expected = NoActiveExperienceException.class)
    public void activeExperienceShouldThrowIfNoActiveExperience()
            throws NoActiveExperienceException {
        ExperienceSelectionPresenter p =
                new ExperienceSelectionPresenter(view, activity);
        p.activeExperience();
    }

}
