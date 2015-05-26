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
 * Name: ContactsPresenterTest.java
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

import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.ILocationManager;

/**
 * Contiene i test di unità per la classe ContactsPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class ContactsPresenterTest {

    private ContactsPresenter cp;
    private IContactsView view;
    private ISerleenaActivity activity;
    private ISensorManager sensor_manager;
    private ILocationManager locMan;
    private int UPDATE_INTERVAL_SECONDS = 180;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        view = mock(IContactsView.class);
        activity = mock(ISerleenaActivity.class);
        sensor_manager = mock(ISensorManager.class);
        locMan = mock(ILocationManager.class);
        when(activity.getSensorManager()).thenReturn(sensor_manager);
        when(sensor_manager.getLocationSource()).thenReturn(locMan);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException con
     * messaggio "Illegal null view" al tentativo di costruire un oggetto con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        cp = new ContactsPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException con
     * messaggio "Illegal null activity" al tentativo di costruire un oggetto con
     * activity nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullActivity() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null activity");
        cp = new ContactsPresenter(view, null);
    }

    /**
     * Verifica che il costruttore chiami il metodo attachPresenter su view
     * fornendo come parametro il nuovo oggetto ContactsPresenter creato.
     */

    @Test
    public void constructorShouldCallAttachPresenterWithCorrectParam() {
        cp = new ContactsPresenter(view, activity);
        verify(view).attachPresenter(cp);
    }

    /**
     * Verifica che il metodo resume chiami il metodo attachObserver su
     * locMan (di tipo ILocationManager) fornendo come parametri l'oggetto
     * ContactsPresenter di invocazione e il corretto intervallo di aggiornamento.
     */

    @Test
    public void resumeShouldCallAttachObserverWithCorrectParams() {
        cp = new ContactsPresenter(view, activity);
        cp.resume();
        verify(locMan).attachObserver(cp, UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che il metodo pause chiami il metodo detachObserver su
     * locMan (ILocationManager) fornendo come parametro l'oggetto
     * ContactsPresenter di invocazione.
     */

    @Test
    public void pauseShouldCallDetachObserverWithCorrectParam() {
        cp = new ContactsPresenter(view, activity);
        cp.pause();
        verify(locMan).detachObserver(cp);
    }

    /**
     * Verifica che il metodo onLocationUpdate lanci un'eccezione IllegalArgumentException
     * con messaggio "Illegal null location" quando chiamato con parametro nullo.
     */

    @Test
    public void onLocationUpdateShouldThrowExceptionWhenNullGeoPoint() {
        cp = new ContactsPresenter(view, activity);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        cp.onLocationUpdate(null);
    }
}
