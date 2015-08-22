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
 * Name: TrackPresenter.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file e scrittura del codice e di
 *                              Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.os.AsyncTask;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.AzimuthMagneticNorth;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.sensors.NoActiveTrackException;
import com.kyloth.serleena.sensors.NoSuchCheckpointException;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.IHeadingObserver;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;
import com.kyloth.serleena.sensors.ITrackCrossing;
import com.kyloth.serleena.sensors.ITrackCrossingObserver;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Concretizza ITrackPresenter.
 *
 * Gestisce l'esecuzione del Percorso attivo, monitorando
 * l'attraversamento dei checkpoint, impostando il Tracciamento e mostrando
 * informazioni utili sulla vista associata.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IWeatherView Vista associata al presenter
 * @field activeTrack : ITrack Percorso attivo
 * @field lastKnownLocation : GeoPoint Ultima posizione geografica dell'utente conosciuta
 * @field lastKnownHeading : AzimuthMagneticNorth Ultimo valore di orientamento dell'utente conosciuto
 * @field tc : ITrackCrossing Gestore dell'attraversamento del Percorso
 * @field locMan : ILocationManager Gestore del sensore di posizione
 * @field hMan : IHeadingManager Gestore del sensore di battito cardiaco
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TrackPresenter implements ITrackPresenter, ITrackCrossingObserver,
        ILocationObserver, IHeadingObserver {

    public static int UPDATE_INTERVAL_SECONDS = 60;

    private ITrackView view;

    private GeoPoint lastKnownLocation;
    private AzimuthMagneticNorth lastKnownHeading;

    private final ILocationManager locMan;
    private final ITrackCrossing tc;
    private IHeadingManager hMan;

    /**
     * Crea un nuovo oggetto TrackPresenter.
     *
     * @param view     Vista da associare al Presenter. Se null, viene sollevata
     *                 un'eccezione IllegalArgumentException.
     * @param activity Activity a cui il Presenter fa riferimento. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     */
    public TrackPresenter(ITrackView view, ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        view.attachPresenter(this);

        this.view = view;
        this.tc = activity.getSensorManager().getTrackCrossingManager();
        this.locMan = activity.getSensorManager().getLocationSource();
        try {
            this.hMan = activity.getSensorManager().getHeadingSource();
        } catch (SensorNotAvailableException e) {
            /* Null object pattern */
            this.hMan = new IHeadingManager() {
                @Override
                public void attachObserver(IHeadingObserver observer) {
                    if (observer == null)
                        throw new IllegalArgumentException("Illegal null " +
                                "observer");
                }

                @Override
                public void detachObserver(IHeadingObserver observer)
                        throws IllegalArgumentException {
                    if (observer == null)
                        throw new IllegalArgumentException("Illegal null " +
                                "observer");
                }

                @Override
                public void notifyObservers() {
                }
            };
        }
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Il Presenter si registra ai sensori utilizzati.
     */
    @Override
    public synchronized void resume() {
        tc.attachObserver(this);
        locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
        hMan.attachObserver(this);
        updateView();
    }

    /**
     * Implementa IPresenter.pause().
     *
     * Il Presenter rimuove la registrazione ai sensori utilizzati.
     */
    @Override
    public synchronized void pause() {
        lastKnownHeading = null;
        lastKnownLocation = null;
        locMan.detachObserver(this);
        hMan.detachObserver(this);
        tc.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione attuale
     *            dell'utente.
     */
    @Override
    public synchronized void onLocationUpdate(GeoPoint loc) {
        lastKnownLocation = loc;
        updateDistance();
        updateHeading();
    }

    /**
     * Implementa ITrackPresenter.onHeadingUpdate().
     *
     * In base al valore di orientamento ricevuto dal sensore, e alla
     * posizione dell'utente, calcola il valore in gradi che,
     * sommato algebricamente attuale, restituisce la direzione del prossimo
     * checkpoint da raggiungere. Questo valore è comunicato alla vista.
     *
     * @param heading Orientamento dell'utente, indicato come radianti di
     *                rotazione attorno all'asse azimuth.
     */
    @Override
    public synchronized void onHeadingUpdate(AzimuthMagneticNorth heading) {
        lastKnownHeading = heading;
        updateHeading();
    }

    /**
     * Implementa ITrackPresenter.advanceCheckpoint().
     */
    @Override
    public synchronized void advanceCheckpoint() throws
            NoTrackCrossingException, NoActiveTrackException {
        tc.advanceCheckpoint();
    }

    /**
     * Implementa ITrackCrossingObserver.onCheckpointCrossed().
     *
     * Se il Presenter è attivo, viene programmato un aggiornamento asincrono
     * della vista.
     */
    @Override
    public synchronized void onCheckpointCrossed() {
        updateView();
    }

    @Override
    public synchronized void abortTrack() {
        tc.abort();
    }

    private void updateView() {
        view.clearView();
        try {
            view.setTrackName(tc.getTrack().name());
        } catch (NoActiveTrackException e) { }
        updateCheckpoints();
        updateDistance();
        updateHeading();
        updateStats();
    }

    private void updateStats() {
        view.clearStats();
        try {
            view.setLastPartial(tc.getLastCrossed().partialTime());
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    try {
                        return tc.getLastCrossed().delta();
                    } catch (NoSuchTelemetryException |
                            NoSuchTelemetryEventException |
                            NoSuchCheckpointException |
                            NoActiveTrackException e) {
                        return null;
                    }
                }
                @Override
                protected void onPostExecute(Integer delta) {
                    if (delta != null && delta != 0)
                        view.setDelta(delta);
                }
            }.execute();
        } catch (NoSuchCheckpointException|NoActiveTrackException e) { }
    }

    private void updateHeading() {
        try {
            if (lastKnownHeading != null && lastKnownLocation != null) {
                Checkpoint cp = null;
                    cp = tc.getTrack().getCheckpoints().get(
                            tc.getNextCheckpoint());
                float bearingTo = lastKnownLocation.bearingTo(cp);
                float azimuthTrueNorth =
                        lastKnownHeading.toTrueNorth(lastKnownLocation);
                view.setDirection(azimuthTrueNorth - bearingTo);
            }
        } catch (NoTrackCrossingException|NoActiveTrackException e) { }
    }

    private void updateDistance() {
        try {
            if (lastKnownLocation != null) {
                int next = tc.getNextCheckpoint();
                Checkpoint cp = tc.getTrack().getCheckpoints().get(next);
                int distance = Math.round(lastKnownLocation.distanceTo(cp));
                view.setDistance(distance);
            }
        } catch (NoTrackCrossingException|NoActiveTrackException e) { }
    }

    private void updateCheckpoints() {
        view.clearCheckpoints();
        int checkpointNumber = 0;
        try {
            checkpointNumber = tc.getNextCheckpoint() + 1;
            int total = tc.getTrack().getCheckpoints().size();
            view.setCheckpointNo(checkpointNumber);
            view.setTotalCheckpoints(total);
        } catch (NoTrackCrossingException e) {
            view.displayTrackEnded();
        } catch (NoActiveTrackException ee) {}
    }

}
