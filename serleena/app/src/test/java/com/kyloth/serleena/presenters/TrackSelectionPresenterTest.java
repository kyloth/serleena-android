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
import java.util.List;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.IExperienceActivationSource;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackSelectionView;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.ITrackCrossing;

/**
 * Contiene i test di unità per la classe TrackSelectionPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class TrackSelectionPresenterTest {
    private ISerleenaActivity activity;
    private ITrackSelectionView view;
    private IExperienceActivationSource source;
    private IExperience experience;
    private ISensorManager sm;
    private ITrackCrossing tc;
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
        source = mock(IExperienceActivationSource.class);
        sm = mock(ISensorManager.class);
        tc = mock(ITrackCrossing.class);
        when(sm.getTrackCrossingManager()).thenReturn(tc);
        when(activity.getSensorManager()).thenReturn(sm);

        ITrack track1 = mock(ITrack.class);
        ITrack track2 = mock(ITrack.class);
        ITrack track3 = mock(ITrack.class);
        List<ITrack> trackList = new ArrayList<ITrack>();
        trackList.add(track1);
        trackList.add(track2);
        trackList.add(track3);
        when(experience.getTracks()).thenReturn(trackList);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con in caso di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments1() {
        new TrackSelectionPresenter(null, activity, source);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con in caso di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments2() {
        new TrackSelectionPresenter(view, null, source);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException
     * con in caso di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldThrowExceptionWhenNullArguments3() {
        new TrackSelectionPresenter(view, activity, null);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter sulla view
     * fornendo il nuovo TrackSelectionPresenter creato come parametro.
     */
    @Test
    public void constructorShouldCallAttachPresenterWithCorrectParam() {
        TrackSelectionPresenter p = new TrackSelectionPresenter(view,
                activity, source);
        verify(view).attachPresenter(p);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter sulla view
     * fornendo il nuovo TrackSelectionPresenter creato come parametro.
     */
    @Test
    public void constructorShouldCallAttachObserverToExperienceSource() {
        TrackSelectionPresenter p = new TrackSelectionPresenter(view,
                activity, source);
        verify(source).attachObserver(p);
    }

    /**
     * Verifica che il metodo onExperienceActivated() lanci un'eccezione
     * IllegalArgumentException quando chiamato con parametro nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void onExperienceActivatedShouldThrowExceptionWhenNullExperience() {
        new TrackSelectionPresenter(view, activity, source)
                .onExperienceActivated(null);
    }

    /**
     * Verifica che il metodo activateTrack() lanci un'eccezione
     * IllegalArgumentException quando chiamato con parametro nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void activateTrackShouldThrowExceptionWhenNullTrack() {
        new TrackSelectionPresenter(view, activity, source)
                .activateTrack(null);
    }

    /**
     * Verifica che alla selezione di un Percorso, questo venga attivato
     * attraverso l'oggetto ITrackCrossing fornito dall'activity.
     */
    @Test
    public void trackShouldBeStartedWhenSelected() {
        TrackSelectionPresenter p =
                new TrackSelectionPresenter(view, activity, source);
        ITrack track = mock(ITrack.class);
        p.activateTrack(track);
        verify(tc).startTrack(track);
    }

    @Test
    public void onExperienceActivatedShouldPopulateView() {
        TrackSelectionPresenter p = new TrackSelectionPresenter(view,
                activity, source);
        p.onExperienceActivated(experience);
        verify(view).setTracks(experience.getTracks());
    }

}
