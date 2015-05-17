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
 * Date: 2015-05-15
 *
 * History:
 * Version    Programmer       Date        Changes
 * 1.0        Filippo Sestini  2015-05-15  Creazione del file
 */

package com.kyloth.serleena.presenters;

import android.os.AsyncTask;

import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.ListAdapter;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IContactsPresenter;
import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

import java.util.ArrayList;

/**
 * Concretizza IContactsPresenter
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class ContactsPresenter implements IContactsPresenter,
        ILocationObserver {

    private static int UPDATE_INTERVAL_SECONDS = 180;

    private IContactsView view;
    private ISerleenaActivity activity;
    private ILocationManager locMan;
    private ImmutableList<EmergencyContact> contacts;
    int index;

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
        resetView();
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Si registra al sensore di posizione.
     */
    @Override
    public void resume() {
        locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
    }

}
