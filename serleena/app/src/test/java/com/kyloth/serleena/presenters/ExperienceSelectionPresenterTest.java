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
 * Date: 2015-05-11
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;
import com.kyloth.serleena.presentation.ISerleenaActivity;


/**
 * Contiene i test di unità per la classe ExperienceSelectionPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class ExperienceSelectionPresenterTest {

    private ISerleenaActivity activity;
    private IExperienceSelectionView view;
    private ISerleenaDataSource source;
    private IExperience exp;
    private ArrayList<IExperience> exp_list;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari a condurre i test.
     */

    @Before
    public void initialize() {
        activity = mock(ISerleenaActivity.class);
        view = mock(IExperienceSelectionView.class);
        source = mock(ISerleenaDataSource.class);
        exp = mock(IExperience.class);
        exp_list = new ArrayList<IExperience>();
        exp_list.add(exp);
        exp_list.add(exp);
        exp_list.add(exp);
        when(activity.getDataSource()).thenReturn(source);
        when(source.getExperiences()).thenReturn(exp_list);
    }

    /**
     * Testa la correttezza del costruttore della classe.
     */

    @Test
    public void testConstructor() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        ExperienceSelectionPresenter fail_esp_1 = new ExperienceSelectionPresenter(null, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        ExperienceSelectionPresenter fail_esp_2 = new ExperienceSelectionPresenter(view, null);
        ExperienceSelectionPresenter esp = new ExperienceSelectionPresenter(view, activity);
        verify(activity).getDataSource();
        verify(source).getExperiences();
        verify(view).attachPresenter(esp);
    }

    /**
     * Testa la correttezza del metodo "activateExperience".
     */

    @Test
    public void testActivateExperience() {
        ExperienceSelectionPresenter esp = new ExperienceSelectionPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Index out of range");
        esp.activateExperience(-1);
        exception.expect(IllegalArgumentException.class);
        esp.activateExperience(5);
        esp.activateExperience(0);
        verify(activity).setActiveExperience(exp);
    }
}
