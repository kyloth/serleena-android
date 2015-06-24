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
 * 2.0.0    Gabriele Pozzan  Aggiunta integrazione con gli altri package,
 *                                       incrementata copertura
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

import java.util.List;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.sensors.SerleenaSensorManager;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;

/**
 * Contiene test per la classe ContactsPresenter.
 * In particolare vengono integrate le componenti dei package sensors,
 * common, model e persistence; vengono utilizzati degli stub per
 * il package presentation.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class ContactsPresenterTest {

    SQLiteDatabase db;
    SerleenaDatabase serleenaDB;
    SerleenaSQLiteDataSource serleenaSQLDS;
    SerleenaDataSource dataSource;

    IContactsView view;
    ISerleenaActivity activity;
    SerleenaSensorManager sm;
    int UPDATE_INTERVAL_SECONDS = 180;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        view = mock(IContactsView.class);
        activity = mock(ISerleenaActivity.class);
        sm = SerleenaSensorManager.getInstance(RuntimeEnvironment.application);
        when(activity.getSensorManager()).thenReturn(sm);

        serleenaDB = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDB.getWritableDatabase();
        serleenaDB.onConfigure(db);
        serleenaDB.onUpgrade(db, 1, 2);

        serleenaSQLDS = new SerleenaSQLiteDataSource(RuntimeEnvironment.application, serleenaDB);
        dataSource = new SerleenaDataSource(serleenaSQLDS);

        String insert_contact_1 = "INSERT INTO contacts " +
                                  "(contact_id, contact_name, contact_value, " +
                                  "contact_ne_corner_latitude, contact_ne_corner_longitude, " +
                                  "contact_sw_corner_latitude, contact_sw_corner_longitude) " +
                                  "VALUES (1, 'Contact_1', '1', 0, 0, 2, 2)";
        String insert_contact_2 = "INSERT INTO contacts " +
                                  "(contact_id, contact_name, contact_value, " +
                                  "contact_ne_corner_latitude, contact_ne_corner_longitude, " +
                                  "contact_sw_corner_latitude, contact_sw_corner_longitude) " +
                                  "VALUES (2, 'Contact_2', '2', 0, 0, 2, 2)";
        db.execSQL(insert_contact_1);
        db.execSQL(insert_contact_2);

        when(activity.getDataSource()).thenReturn(dataSource);

    }

    /**
     * Verifica che il costruttore lanci un'eccezione IllegalArgumentException con
     * messaggio "Illegal null view" al tentativo di costruire un oggetto con view nulla.
     */

    @Test
    public void constructorShouldThrowExceptionWhenNullView() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null view");
        ContactsPresenter cp = new ContactsPresenter(null, activity);
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
        ContactsPresenter cp = new ContactsPresenter(view, null);
    }

    /**
     * Verifica che il metodo resume chiamato con oggetti del
     * package sensors non sollevi eccezioni e in generale non
     * causi errori.
     */

    @Test
    public void testResume() {
        ContactsPresenter presenter = new ContactsPresenter(view, activity);
        presenter.resume();
    }

    /**
     * Verifica che il metodo pause chiamato con oggetti del
     * package sensors non sollevi eccezioni e in generale
     * non causi errori.
     */

    @Test
    public void testPause() {
        ContactsPresenter presenter = new ContactsPresenter(view, activity);
        presenter.resume();
        presenter.pause();
    }

    /**
     * Verifica che il metodo onLocationUpdate lanci un'eccezione
     * di tipo IllegalArgumentException con messaggio "Illegal
     * null location" se invocato con un GeoPoint nullo.
     */

    @Test
    public void testOnLocationUpdateExceptionNullLocation() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null location");
        ContactsPresenter presenter = new ContactsPresenter(view, activity);
        presenter.onLocationUpdate(null);
    }

    /**
     * Verifica che il metodo nextContact aggiorni correttamente
     * la view, nello specifico controlla che i contatti vengano
     * mostrati come in una lista circolare.
     */

    @Test
    public void testNextContact() {
        ContactsPresenter presenter = new ContactsPresenter(view, activity);
        presenter.nextContact();
        presenter.onLocationUpdate(new GeoPoint(1, 1));
        ArgumentCaptor<String> strCaptor = ArgumentCaptor.forClass(String.class);
        presenter.nextContact();
        presenter.nextContact();
        verify(view, timeout(200).times(3)).displayContact(strCaptor.capture(), any(String.class));
        List<String> capturedStrings = strCaptor.getAllValues();
        assertTrue(capturedStrings.get(0).equals("Contact_1"));
        assertTrue(capturedStrings.get(1).equals("Contact_2"));
        assertTrue(capturedStrings.get(2).equals("Contact_1"));
    }

}
