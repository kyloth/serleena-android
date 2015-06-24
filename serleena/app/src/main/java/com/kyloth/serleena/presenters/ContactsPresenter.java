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
 * Name: ContactsPresenter.java
 * Package: com.hitchikers.serleena.presentation
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file
 */

package com.kyloth.serleena.presenters;

import android.os.AsyncTask;

import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

/**
 * Concretizza IContactsPresenter
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IContactsView Vista associata al Presenter
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @field locMan : ILocationManager Gestore del sensore di posizione
 * @field contacts : DirectAccessList<EmergencyContact> Contatti da visualizzare sulla vista
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ContactsPresenter implements IContactsPresenter,
        ILocationObserver {

    private static final int UPDATE_INTERVAL_SECONDS = 180;

    private IContactsView view;
    private ISerleenaActivity activity;
    private ILocationManager locMan;
    private DirectAccessList<EmergencyContact> contacts;
    private int index;

    /**
     * Crea un nuovo oggetto ContactsPresenter.
     *
     * @param view Vista IContactsView associata al Presenter. Se null,
     *             viene sollevata un'eccezione IllegalArgumentException.
     * @param activity Activity dell'applicazione. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     */
    public ContactsPresenter(IContactsView view, ISerleenaActivity activity) {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.view = view;
        this.activity = activity;
        locMan = activity.getSensorManager().getLocationSource();

        view.attachPresenter(this);
        resetView(contacts);
    }

    /**
     * Implementa IContactsPresenter.nextContact().
     *
     * Se non vi sono contatti da visualizzare, il metodo non ha effetto.
     */
    @Override
    public synchronized void nextContact() {
        if (contacts != null && contacts.size() > 0) {
            index = (index + 1) % contacts.size();
            EmergencyContact c = contacts.get(index);
            view.displayContact(c.name(), c.value());
        }
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Si registra al sensore di posizione.
     */
    @Override
    public synchronized void resume() {
        locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Implementa IPresenter.pause().
     *
     * Rilascia le risorse non necessarie annullando la registrazione al sensore
     * di posizione.
     */
    @Override
    public synchronized void pause() {
        locMan.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * Ad ogni aggiornamento sulla posizione, viene creato un flusso di
     * controllo asincrono che si occupa di richiedere alla sorgente dati i
     * contatti di emergenza aggiornati.
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione
     */
    @Override
    public synchronized void onLocationUpdate(final GeoPoint loc) {
        if (loc == null)
            throw new IllegalArgumentException("Illegal null location");

        final ISerleenaDataSource ds = activity.getDataSource();

        AsyncTask<Void, Void, DirectAccessList<EmergencyContact>> task =
                new AsyncTask<Void, Void, DirectAccessList<EmergencyContact>>(){
            @Override
            protected DirectAccessList<EmergencyContact> doInBackground(
                    Void... voids) {
                return ds.getContacts(loc);
            }
            @Override
            protected void onPostExecute(
                    DirectAccessList<EmergencyContact> result) {
                resetView(result);
            }
        };

        task.execute();
    }

    /**
     * Reimposta la vista alla condizione iniziale.
     */
    private synchronized void resetView(
            DirectAccessList<EmergencyContact> contacts) {
        this.contacts = contacts;
        index = 0;

        if (contacts == null || contacts.size() == 0)
            view.clearView();
        else {
            EmergencyContact c = contacts.get(0);
            view.displayContact(c.name(), c.value());
        }
    }

}
