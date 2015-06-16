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
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.AzimuthMagneticNorth;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SerleenaSensorManager;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Contiene test per la classe CompassPresenter.
 * In particolare vengono utilizzati degli stub per il package presentation e
 * viene integrato il package sensors.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class CompassPresenterTest {
    int UPDATE_INTERVAL = 5;
    ISerleenaActivity activity; //activity che ritorna veri oggetti sensors
    ISerleenaActivity activity_mock; //activity che ritorna mock di sensors
    ICompassView view;
    SerleenaSensorManager sm;
    ISensorManager sm_mock;
    IHeadingManager hm_mock;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() throws SensorNotAvailableException {
        activity = mock(ISerleenaActivity.class);
        activity_mock = mock(ISerleenaActivity.class);
        view = mock(ICompassView.class);
        sm = SerleenaSensorManager.getInstance(RuntimeEnvironment.application);
        sm_mock = mock(ISensorManager.class);
        hm_mock = mock(IHeadingManager.class);
        when(activity.getSensorManager()).thenReturn(sm);
        when(activity_mock.getSensorManager()).thenReturn(sm_mock);
        when(sm_mock.getHeadingSource()).thenReturn(hm_mock);
    }

    /**
     * Verifica che il costruttore chiami a sua volta il metodo
     * attachPresenter sulla view passando il nuovo oggetto creato
     * come parametro.
     */

    @Test
    public void testConstructor() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        verify(view).attachPresenter(cp);
    }

    /**
     * Verifica che il metodo resume non sollevi eccezioni e in
     * generale non causi errori con oggetti integrati da sensors.
     */

    @Test
    public void testResume() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        cp.resume();

    }

    /**
     * Verifica che il metodo resume chiami correttamente i metodi
     * su stub di oggetti del package sensors.
     */

    @Test
    public void testResumeWithMocks() {
        CompassPresenter cp = new CompassPresenter(view, activity_mock);
        cp.resume();
        verify(hm_mock).attachObserver(cp);
    }

    /**
     * Verifica che il metodo pause non sollevi eccezioni o
     * causi errori utilizzando oggetti integrati da sensors.
     */

    @Test
    public void testPause() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        cp.pause();
    }

    /**
     * Verifica che il metodo pause chiami correttamente i metodi
     * previsti su dei mock di oggetti del package sensors.
     */

    @Test
    public void testPauseWithMock() {
        CompassPresenter cp = new CompassPresenter(view, activity_mock);
        cp.pause();
        verify(hm_mock).detachObserver(cp);
    }

    /**
     * Verifica che il metodo onHeadingUpdate imposti correttamente
     * il valore della direzione sulla view.
     */

    @Test
    public void testOnHeadingUpdate() {
        CompassPresenter cp = new CompassPresenter(view, activity);
        AzimuthMagneticNorth az = mock(AzimuthMagneticNorth.class);
        when(az.orientation()).thenReturn(12.4f);
        cp.onHeadingUpdate(az);
        verify(view).setHeading(12.4f);
    }

}
