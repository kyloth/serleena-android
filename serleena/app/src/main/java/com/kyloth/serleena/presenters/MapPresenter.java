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
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.persistence.NoSuchQuadrantException;
import com.kyloth.serleena.presentation.IExperienceActivationSource;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Concretizza IMapPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IMapView Vista associata al Presenter
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @field activeExperience : IExperience Esperienza attiva
 * @field currentPosition : GeoPoint Posizione geografica attuale dell'utente
 * @field locMan : ILocationManager Gestore del sensore di posizione
 * @field currentQuadrant : IQuadrant Quadrante attuale
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class MapPresenter implements IMapPresenter, ILocationObserver {

    private static final int UPDATE_INTERVAL_SECONDS = 30;

    private IMapView view;
    private ISerleenaActivity activity;
    private GeoPoint currentPosition;
    private ILocationManager locMan;
    private IQuadrant currentQuadrant;
    private IExperienceActivationSource expActivationSource;

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
    public MapPresenter(IMapView view, ISerleenaActivity activity,
                        IExperienceActivationSource experienceActivationSource)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");
        if (experienceActivationSource == null)
            throw new IllegalArgumentException(
                    "Illegal null experience source");

        this.activity = activity;
        this.view = view;
        this.expActivationSource = experienceActivationSource;

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
    public synchronized void newUserPoint()
            throws NoActiveExperienceException, LocationNotAvailableException {
        final IExperience activeExperience =
                expActivationSource.activeExperience();
        if (currentPosition == null)
            throw new LocationNotAvailableException();
        if (currentQuadrant == null)
            throw new LocationNotAvailableException();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                activeExperience.addUserPoints(new UserPoint(
                        currentPosition.latitude(),
                        currentPosition.longitude()));
                return null;
            }
        }.execute();
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
        view.clear();
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
     */
    @Override
    public synchronized void onLocationUpdate(final GeoPoint loc) {
        if (loc == null)
            throw new IllegalArgumentException("Illegal null location");

        currentPosition = loc;
        view.setUserLocation(loc);

        IExperience activeExperience = null;
        try {
            activeExperience = expActivationSource.activeExperience();
            updateQuadrant(activeExperience, loc);
            updateUserPoints(activeExperience);
        } catch (NoActiveExperienceException e) { }
    }

    /**
     * Segnala al presenter di mostrare sulla vista da esso gestita un elenco di
     * Punti Utente.
     *
     * @param points Punti Utente da mostrare sulla vista associata.
     */
    public void displayUserPoints(Iterable<UserPoint> points) {
        if (points == null)
            throw new IllegalArgumentException("Illegal null user points");

        if (currentQuadrant != null) {
            List<UserPoint> list = new ArrayList<>();
            for (UserPoint point : points)
                if (currentQuadrant.contains(point))
                    list.add(point);
            view.displayUP(list);
        }
    }

    private void updateUserPoints(final IExperience activeExperience) {
        new AsyncTask<Void, Void, Iterable<UserPoint>>() {
            @Override
            protected Iterable<UserPoint> doInBackground(Void... params) {
                return activeExperience.getUserPoints();
            }
            @Override
            protected void onPostExecute(Iterable<UserPoint> userPoints) {
                displayUserPoints(userPoints);
            }
        }.execute();
    }

    private void updateQuadrant(final IExperience activeExperience,
                                final GeoPoint loc) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    currentQuadrant = activeExperience.getQuadrant(loc);
                } catch (NoSuchQuadrantException e) {
                    currentQuadrant = null;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void v) {
                if (currentQuadrant != null)
                    view.displayQuadrant(currentQuadrant);
                else
                    view.clear();
            }
        }.execute();
    }

}
