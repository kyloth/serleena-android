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
 * Name: CompassPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 * Date: 2015-05-11
 *
 * History:
 * Version  Programmer       Date        Changes
 * 1.0.0    Gabriele Pozzan  2015-05-11  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.model.*;
import com.kyloth.serleena.sensors.*;

/**
 * Contiene i test di unità per la classe CompassPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class CompassPresenterTest {
    ISerleenaActivity activity;
    ICompassView view;
    ISensorManager sm;
    IHeadingManager hm;
    public static final int UPDATE_INTERVAL = 5;
    /**
     * Inizializza i campi dati necessari ai test.
     */
    @Before
    public void initialize() {
        activity = mock(ISerleenaActivity.class);
        view = mock(ICompassView.class);
        sm = mock(ISensorManager.class);
        hm = mock(IHeadingManager.class);
        when(activity.getSensorManager()).thenReturn(sm);
        when(sm.getHeadingSource()).thenReturn(hm);

    }
    /**
     * Testa la correttezza del costruttore della classe.
     */
    @Test
    public void testConstructor() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        verify(view).attachPresenter(cp);
    }
    /**
     * Testa la correttezza del metodo "resume" della classe.
     */
    @Test
    public void testResume() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        cp.resume();
        verify(activity).getSensorManager();
        verify(sm).getHeadingSource();
        verify(hm).attachObserver(cp, UPDATE_INTERVAL);
    }
    /**
     * Testa la correttezza del metodo "pause" della classe.
     */
    public void testPause() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        cp.pause();
        verify(activity).getSensorManager();
        verify(sm).getHeadingSource();
        verify(hm).detachObserver(cp);
    }
    /**
     * Testa la correttezza del metodo "onHeadingUpdate" della classe.
     */
    public void testOnHeadingUpdate() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        cp.onHeadingUpdate(13.532);
        verify(view).setHeading(13.532);
    }
}
