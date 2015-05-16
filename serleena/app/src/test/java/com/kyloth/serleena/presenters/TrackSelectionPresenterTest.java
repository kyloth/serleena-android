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
    private ITrack track;
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
        track = mock(ITrack.class);
        track_list = new ArrayList<ITrack>();
        track_list.add(track);
        track_list.add(track);
        track_list.add(track);
        when(experience.getTracks()).thenReturn(track_list);
    }

    /**
     * Testa la correttezza del costruttore della classe.
     */

    @Test
    public void testConstructor() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        TrackSelectionPresenter fail_tsp_1 = new TrackSelectionPresenter(null, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        TrackSelectionPresenter fail_tsp_2 = new TrackSelectionPresenter(view, null);
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        verify(view).attachPresenter(tsp);
        verify(view).clearList();
    }

    /**
     * Testa la correttezza del metodo "setActiveExperience".
     */

    @Test
    public void testSetActiveExperience() {
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null experience");
        tsp.setActiveExperience(null);
        tsp.setActiveExperience(experience);
        verify(view).clearList();
    }

    /**
     * Testa la correttezza del metodo "activateTrack".
     */

    @Test
    public void testActivateTrack() {
        TrackSelectionPresenter tsp = new TrackSelectionPresenter(view, activity);
        tsp.setActiveExperience(experience);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Index out of range");
        tsp.activateTrack(-1);
        exception.expect(IllegalArgumentException.class);
        tsp.activateTrack(4);
        tsp.activateTrack(0);
        verify(activity).setActiveTrack(track);
    }
}
