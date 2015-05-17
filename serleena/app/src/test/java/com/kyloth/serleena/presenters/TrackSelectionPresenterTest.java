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
 * Name: TrackSelectionPresenterTest.java
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
import java.util.ArrayList;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackSelectionView;

/**
 * Contiene i test di unità per la classe TrackSelectionPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TrackSelectionPresenterTest {
    private ISerleenaActivity activity;
    private ITrackSelectionView view;
    private IExperience experience;
    private ITrack track_1;
    private ITrack track_2;
    private ITrack track_3;
    private ArrayList<ITrack> track_list;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        activity = mock(ISerleenaActivity.class);
        view = mock(ITrackSelectionView.class);
        experience = mock(IExperience.class);
        track_1 = mock(ITrack.class);
        track_2 = mock(ITrack.class);
        track_3 = mock(ITrack.class);
        track_list = new ArrayList<ITrack>();
        track_list.add(track_1);
        track_list.add(track_2);
        track_list.add(track_3);
        when(experience.getTracks()).thenReturn(track_list);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con messaggio "Illegal null view" al tentativo di costruire un oggetto
     * con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        TrackSelectionPresenter null_view_tsp = new TrackSelectionPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con messaggio "Illegal null activity" al tentativo di costruire un oggetto
     * con activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        TrackSelectionPresenter null_view_tsp = new TrackSelectionPresenter(view, null);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter sulla view
     * fornendo il nuovo TrackSelectionPresenter creato come parametro.
     */

    @Test
    public void constructorShouldCallAttachPresenterWithCorrectParam() {
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        verify(view).attachPresenter(tsp);
    }

    /**
     * Verifica che il metodo setActiveExperience lanci un'eccezione IllegalArumentException
     * con messaggio "Illegal null experience" quando chiamato con parametro nullo.
     */

    @Test
    public void setActiveExperienceShouldThrowExceptionWhenNullExperience() {
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null experience");
        tsp.setActiveExperience(null);
    }

    /**
     * Verifica che il metodo activateTrack lanci un'eccezione IllegalArgumentException
     * con messaggio "Index out of range" quando chiamato con parametro inferiore a zero
     * o superiore alla dimensione della lista dei Percorsi.
     */

    @Test
    public void activateTrackShouldThrowExceptionWhenIndexOutOfRange() {
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Index out of range");
        tsp.activateTrack(-1);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Index out of range");
        tsp.activateTrack(3);
    }

}
