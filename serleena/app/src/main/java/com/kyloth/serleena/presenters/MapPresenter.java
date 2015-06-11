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
 * Name: MapPresenter.java
 * Package: com.hitchikers.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file
 */

package com.kyloth.serleena.presenters;

import android.os.AsyncTask;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

/**
 * Concretizza IMapPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IMapView Vista associata al Presenter
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @field activeExperience : IExperience Esperienza attiva
 * @field currentPosition : GeoPoint Posizione geografica attuale dell'utente
 * @field locMan : ILocationManager Gestore del sensore di posizione
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class MapPresenter implements IMapPresenter, ILocationObserver {

    private static final int UPDATE_INTERVAL_SECONDS = 30;

    private IMapView view;
    private ISerleenaActivity activity;
    private IExperience activeExperience;
    private GeoPoint currentPosition;
    private ILocationManager locMan;

    /**
     * Crea un oggetto MapPresenter.
     *
     * Si collega alla vista tramite il metodo attachPresenter() esposto
     * dall'interfaccia della vista.
     *
     * @param view Vista da associare al presenter. Se null,
     *             viene sollevata un'eccezione IllegalArgumentException.
     * @param activity Activity che rappresenta l'applicazione in corso e al
     *                 quale il presenter appartiene. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public MapPresenter(IMapView view, ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.activity = activity;
        this.view = view;
        locMan = activity.getSensorManager().getLocationSource();
        view.attachPresenter(this);
    }

    /**
     * Implementa IMapPresenter.newUserPoint().
     *
     * Se non vi è un'esperienza attiva, viene sollevata un'eccezione
     * NoActiveExperienceException.
     *
     * @throws NoActiveExperienceException
     */
    @Override
    public synchronized void newUserPoint() throws NoActiveExperienceException {
        if (activeExperience == null)
            throw new NoActiveExperienceException();

        ILocationObserver observer = new ILocationObserver() {
            @Override
            public void onLocationUpdate(GeoPoint loc) {
                currentPosition = loc;
                activeExperience.addUserPoints(
                        new UserPoint(currentPosition.latitude(),
                                currentPosition.longitude()));
            }
        };

        locMan.getSingleUpdate(observer, 30);
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Si registra ai servizi di sensoristica per ottenere aggiornamenti
     * sulla posizione dell'utente.
     */
    @Override
    public synchronized void resume() {
        locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Implementa IPresenter.pause().
     *
     * Cancella la registrazione ai servizi di sensoristica,
     * evitando di utilizzare la risorsa quando la vista non è visibile.
     */
    @Override
    public synchronized void pause() {
        locMan.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * Avvia un aggiornamento della vista a seguito di dati aggiornati sulla
     * posizione provenienti dai sensori.
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione attuale
     *            dell'utente rilevata dai sensori. Se null,
     *            viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    @Override
    public synchronized void onLocationUpdate(final GeoPoint loc)
            throws IllegalArgumentException {
        if (loc == null)
            throw new IllegalArgumentException("Illegal null GeoPoint");

        currentPosition = loc;
        updateView(currentPosition);
    }

    /**
     * Segnala al presenter l'esperienza correntemente attiva.
     *
     * All'attivazione di una nuova esperienza, l'activity segnala al
     * presenter in modo che esso possa aggiornare la vista con gli elementi
     * di mappa corretti.
     *
     * @param experience Esperienza appena attivata. Se null,
     *                   viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    public synchronized void setActiveExperience(IExperience experience) {
        this.activeExperience = experience;
        view.clear();
    }

    /*
     * Aggiorna la vista in base alla posizione e all'esperienza correnti.
     *
     * Crea un flusso di controllo asincrono che si occupa di ottenere e
     * comunicare alla vista gli elementi della mappa senza bloccare il
     * thread dela UI.
     */
    public void updateView(final GeoPoint location) {
        final ISerleenaDataSource ds = activity.getDataSource();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                IQuadrant quadrant = ds.getQuadrant(location);
                view.displayQuadrant(quadrant);
                Iterable<UserPoint> ups = activeExperience.getUserPoints();
                view.displayUP(ups);
                view.setUserLocation(location);
                return null;
            }
        };

        task.execute();
    }

}

