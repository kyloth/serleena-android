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
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SerleenaSensorManager;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;

/**
 * Contiene i test di unità per la classe ContactsPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class ContactsPresenterTest {

    private ContactsPresenter presenter;
    private ISerleenaActivity activity;
    private IContactsView view;
    private ILocationManager locMan;

    @Before
    public void initialize() {
        view = mock(IContactsView.class);
        locMan = mock(ILocationManager.class);
        activity = mock(ISerleenaActivity.class);
        ISensorManager sensorManager = mock(ISensorManager.class);
        when(activity.getSensorManager()).thenReturn(sensorManager);
        when(sensorManager.getLocationSource()).thenReturn(locMan);
        presenter = new ContactsPresenter(view, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione IllegalArgumentException
     * se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfNullArguments1() {
        new ContactsPresenter(null, activity);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione IllegalArgumentException
     * se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfNullArguments2() {
        new ContactsPresenter(view, null);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione IllegalArgumentException
     * se vengono passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ctorShouldThrowIfNullArguments3() {
        new ContactsPresenter(null, null);
    }

    /**
     * Verifica che il presenter si registri agli eventi del sensore di
     * posizione durante resume().
     */
    @Test
    public void shouldRegisterItselfToSensorsOnResume() {
        presenter.resume();
        verify(locMan).attachObserver(
                presenter,
                ContactsPresenter.UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Verifica che il presenter si deregistri agli eventi del sensore di
     * posizione durante pause().
     */
    @Test
    public void shouldUnregisterItselfToSensorsOnPause() {
        presenter.pause();
        verify(locMan).detachObserver(presenter);
    }

    /**
     * Verifica che i contatti vengano mostrati dal presenter sulla vista
     * secondo un buffer circolare, mostrando in modo sequenziale dal primo
     * all'ultimo, e successivamente di nuovo il primo.
     */
    @Test
    public void presenterShouldSetViewWithContactsLikeACircularBuffer() {
        EmergencyContact ec1 = mock(EmergencyContact.class);
        when(ec1.name()).thenReturn("Name1");
        when(ec1.value()).thenReturn("Value1");
        EmergencyContact ec2 = mock(EmergencyContact.class);
        when(ec2.name()).thenReturn("Name2");
        when(ec2.value()).thenReturn("Value2");
        List<EmergencyContact> list = new ArrayList<>();
        list.add(ec1);
        list.add(ec2);
        DirectAccessList<EmergencyContact> dList = new ListAdapter<>(list);

        presenter.displayContacts(dList);
        verify(view).displayContact("Name1", "Value1");
        presenter.nextContact();
        verify(view).displayContact("Name2", "Value2");
        presenter.nextContact();
        verify(view, times(2)).displayContact("Name1", "Value1");
    }

    /**
     * Verifica che displayContacts() sollevi un'eccezione
     * IllegalArgumentException se chiamato con parametro null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayContactsShouldThrowWhenNullArguments() {
        presenter.displayContacts(null);
    }

    /**
     * Verifica che la richiesta di avanzare al prossimo contatto non sollevi
     * eccezioni se non vi sono contatti da visualizzare.
     */
    @Test
    public void nextContactShouldNotThrowIfNoContacts() {
        presenter.nextContact();
    }

    /**
     * Verifica che la vista venga pulita se la lista di contatti di
     * emergenza passa da una posizione geografica con un elenco di contatti
     * non vuoto a una con zero contatti.
     */
    @Test
    public void displayContactsShouldClearViewIfContactListBecomesEmpty() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(mock(EmergencyContact.class));
        presenter.displayContacts(new ListAdapter<EmergencyContact>(list));
        presenter.displayContacts(
                new ListAdapter<EmergencyContact>(
                        new ArrayList<EmergencyContact>()));
        verify(view).clearView();
    }

    /**
     * Verifica che la vista non venga modificata se viene richiesto
     * ripetutamente di visualizzare la stessa lista di contatti.
     */
    @Test
    public void gettingSameContactListShouldNotChangeView() {
        List<EmergencyContact> contacts = new ArrayList<>();
        EmergencyContact contact = mock(EmergencyContact.class);
        when(contact.name()).thenReturn("");
        when(contact.value()).thenReturn("");
        contacts.add(mock(EmergencyContact.class));

        presenter.displayContacts(new ListAdapter<EmergencyContact>(contacts));
        verify(view, times(0)).clearView();
        verify(view, times(1)).displayContact(
                any(String.class), any(String.class));

        presenter.displayContacts(new ListAdapter<EmergencyContact>(contacts));
        verify(view, times(0)).clearView();
        verify(view, times(1)).displayContact(
                any(String.class), any(String.class));
    }

}
