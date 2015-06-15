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

import android.annotation.TargetApi;
import android.os.AsyncTask;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.AzimuthMagneticNorth;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.sensors.NoSuchCheckpointException;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.IHeadingObserver;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;
import com.kyloth.serleena.sensors.ITrackCrossing;
import com.kyloth.serleena.sensors.ITrackCrossingObserver;
import com.kyloth.serleena.sensors.NoTrackCrossingException;
import com.kyloth.serleena.sensors.SensorNotAvailableException;
import com.kyloth.serleena.sensors.TrackEndedException;

/**
 * Concretizza ITrackPresenter.
 *
 * Gestisce l'esecuzione del Percorso attivo, monitorando
 * l'attraversamento dei checkpoint, impostando il Tracciamento e mostrando
 * informazioni utili sulla vista associata.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IWeatherView Vista associata al presenter
 * @field activity : ISerleenaActivity Activity a cui il presenter appartiene
 * @field activeTrack : ITrack Percorso attivo
 * @field telemetry : boolean Indicazione dello stato di abilitazione o disabilitazione del Tracciamento
 * @field lastKnownLocation : GeoPoint Ultima posizione geografica conosciuta dell'utente
 * @field telMan : ITelemetryManager Gestore dei Tracciamenti
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
    private double lastKnownHeading;

    private final ILocationManager locMan;
    private final ITrackCrossing tc;
    private IHeadingManager hMan;

    private boolean active;

    /**
     * Crea un nuovo oggetto TrackPresenter.
     *
     * @param view Vista da associare al Presenter. Se null, viene sollevata
     *             un'eccezione IllegalArgumentException.
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
                public void notifyObservers() { }
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
        active = true;

        AsyncTask<Void, Void, Void> task =
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        updateCheckpoints();
                        updateDelta();
                        return null;
                    }
                };
        task.execute();

        if (tc.isTrackCrossing()) {
            locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
            hMan.attachObserver(this);
        }
    }

    /**
     * Implementa IPresenter.pause().
     *
     * Il Presenter rimuove la registrazione ai sensori utilizzati.
     */
    @Override
    public synchronized void pause() {
        active = false;
        locMan.detachObserver(this);
        hMan.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione attuale
     *            dell'utente.
     */
    @Override
    @TargetApi(19)
    public void onLocationUpdate(GeoPoint loc) {
        lastKnownLocation = loc;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    updateDirection(lastKnownLocation, lastKnownHeading);
                } catch (NoTrackCrossingException e) {}
                try {
                    updateDistance(lastKnownLocation);
                } catch (NoTrackCrossingException ee) {}
                return null;
            }
        };

        task.execute();
    }

    /**
     * Aggiorna le informazioni sulla direzione dell'utente rispetto al
     * checkpoint da raggiiungere, mostrate dalla vista associata al presenter,
     * in base alla posizione utente specificata.
     *
     * @param loc Posizione attuale dell'utente.
     */
    public void updateDirection(GeoPoint loc, double heading)
            throws NoTrackCrossingException {
        Checkpoint cp =
                tc.getTrack().getCheckpoints().get(tc.getNextCheckpoint());
        float bearingTo = loc.bearingTo(cp);
        float azimuthTrueNorth =
                new AzimuthMagneticNorth((float)heading).toTrueNorth(loc);
        view.setDirection(bearingTo - azimuthTrueNorth);
    }

    /**
     * Aggiorna le informazioni sulla distanza dell'utente rispetto al
     * checkpoint da raggiiungere, mostrate dalla vista associata al presenter,
     * in base alla posizione utente specificata.
     *
     * @param here Posizione attuale dell'utente.
     */
    public void updateDistance(GeoPoint here)
            throws NoTrackCrossingException {
        if (here == null)
            throw new IllegalArgumentException("Illegal null location");

        int next = tc.getNextCheckpoint();
        Checkpoint cp = tc.getTrack().getCheckpoints().get(next);
        int distance = Math.round(here.distanceTo(cp));
        view.setDistance(distance);
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
    public void onHeadingUpdate(double heading) {
        lastKnownHeading = heading;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    updateDirection(lastKnownLocation, lastKnownHeading);
                } catch (NoTrackCrossingException e) {}
                return null;
            }
        };

        task.execute();
    }

    /**
     * Implementa ITrackPresenter.advanceCheckpoint().
     */
    @Override
    public void advanceCheckpoint() throws NoTrackCrossingException {
        tc.advanceCheckpoint();
    }

    /**
     * Aggiorna le informazioni sull'ultimo checkpoint attraversato, mostrate
     * dalla vista associata al presenter.
     *
     * Nel caso si sia raggiunto l'ultimo percorso, l'informazione viene
     * comunicata alla vista.
     */
    public synchronized void updateCheckpoints() {
        try {
            int checkpointNumber;
            try {
                checkpointNumber = tc.getLastCrossed();
            } catch (NoSuchCheckpointException e) {
                checkpointNumber = -1;
            }

            int total = tc.getTrack().getCheckpoints().size();
            if (checkpointNumber == total - 1)
                view.displayTrackEnded();
            else
                view.setCheckpointNo(checkpointNumber + 2);

            view.setLastPartial(tc.lastPartialTime());
        } catch (NoTrackCrossingException ee) {
            view.clearView();
        }
    }

    /**
     * Aggiorna la vista con la differenza tra il tempo parziale e la
     * prestazione migliore registrata per lo stesso Percorso.
     */
    public synchronized void updateDelta() {
        try {
            final int checkpointNumber = tc.getLastCrossed();
            ITelemetry best = tc.getTrack().getBestTelemetry();
            Predicate<TelemetryEvent> p = new Predicate<TelemetryEvent>() {
                @Override
                public boolean apply(TelemetryEvent telemetryEvent) {
                    return telemetryEvent.getType() ==
                            TelemetryEventType.CheckpointReached &&
                            ((CheckpointReachedTelemetryEvent) telemetryEvent)
                                    .checkpointNumber() == checkpointNumber;
                }
            };
            Iterable<TelemetryEvent> events = best.getEvents(p);
            TelemetryEvent event = events.iterator().next();
            view.setDelta(tc.lastPartialTime() - event.timestamp());
        } catch (NoSuchTelemetryException|NoSuchTelemetryEventException|
                NoTrackCrossingException|NoSuchCheckpointException e) {
            view.clearDelta();
        }
    }

    /**
     * Implementa ITrackCrossingObserver.onCheckpointCrossed().
     *
     * Se il Presenter è attivo, viene programmato un aggiornamento asincrono
     * della vista.
     */
    @Override
    public void onCheckpointCrossed(final int checkpointNumber) {
        if (active) {
            AsyncTask<Void, Void, Void> task =
                    new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    updateCheckpoints();
                    updateDelta();
                    return null;
                }
            };
            task.execute();
        }
    }

}
