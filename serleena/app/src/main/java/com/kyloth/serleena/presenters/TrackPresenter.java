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
import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.AsyncTask;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.IHeadingObserver;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;
import com.kyloth.serleena.sensors.ILocationReachedManager;
import com.kyloth.serleena.sensors.ILocationReachedObserver;
import com.kyloth.serleena.sensors.ITelemetryManager;
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
 * @field activity : ISerleenaActivity Activity a cui il presenter appartiene
 * @field activeTrack : ITrack Percorso attivo
 * @field checkpointToReach : int Indice del prossimo checkpoint da raggiungere
 * @field telemetry : boolean Indicazione dello stato di abilitazione o disabilitazione del Tracciamento
 * @field trackStartFullTime : long Istante di inizio dell'attraversamento dek Percorso
 * @field lastKnownLocation : GeoPoint Ultima posizione geografica conosciuta dell'utente
 * @field telMan : ITelemetryManager Gestore dei Tracciamenti
 * @field locMan : ILocationManager Gestore del sensore di posizione
 * @field lrMan : ILocationReachedManager Gestore del sensore di raggiungimento posizione
 * @field hMan : IHeadingManager Gestore del sensore di battito cardiaco
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TrackPresenter implements ITrackPresenter,
        ILocationReachedObserver, ILocationObserver, IHeadingObserver {

    private static int UPDATE_INTERVAL_SECONDS = 60;

    private ITrackView view;
    private ISerleenaActivity activity;

    private ITrack activeTrack;
    private int checkpointToReach;
    private boolean telemetry;
    private long trackStartFullTime;
    private GeoPoint lastKnownLocation;

    private ITelemetryManager telMan;
    private ILocationManager locMan;
    private ILocationReachedManager lrMan;
    private IHeadingManager hMan;

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

        this.view = view;
        this.activity = activity;
        this.telMan = activity.getSensorManager().getTelemetryManager();
        this.locMan = activity.getSensorManager().getLocationSource();
        this.lrMan = activity.getSensorManager().getLocationReachedSource();
        view.attachPresenter(this);
    }

    /**
     * Implementa ITrackPresenter.advanceCheckpoint().
     *
     * Viene annullato la rilevazione automatica dell'attraversamento di
     * checkpoint con sensori, e effettuato un avanzamento manuale.
     */
    @Override
    public synchronized void advanceCheckpoint() {
        lrMan.detachObserver(this);
        nextCheckpoint();
    }

    /**
     * Fa avanzare il Percorso attivo al prossimo Checkpoint, segnalando al
     * gestore del Tracciamento il superamento del Checkpoint.
     */
    private void nextCheckpoint() {
        long now = System.currentTimeMillis() / 1000L;

        if (checkpointToReach == 0)
            trackStartFullTime = now;

        int partial = (int)(now - trackStartFullTime);

        if (telemetry) {
            CheckpointReachedTelemetryEvent event = new
                    CheckpointReachedTelemetryEvent(partial, checkpointToReach);
            telMan.signalEvent(event);
        }

        checkpointToReach++;
        if (checkpointToReach < activeTrack.getCheckpoints().size()) {
            view.setLastPartial(partial);
            view.setCheckpointNo(checkpointToReach + 1);

            Checkpoint c = activeTrack.getCheckpoints().get(checkpointToReach);
            lrMan.attachObserver(this, c);
        } else {
            view.clearView();
            if (telemetry) {
                telMan.stop();
                activeTrack.createTelemetry(telMan.getEvents());
            }
        }
    }

    /**
     * Implementa IPresenter.resume().
     */
    @Override
    public synchronized void resume() {
        locMan.attachObserver(this, UPDATE_INTERVAL_SECONDS);
        try {
            hMan = activity.getSensorManager().getHeadingSource();
            hMan.attachObserver(this);
        } catch (SensorNotAvailableException e) {
            hMan = null;
        }
    }

    /**
     * Implementa IPresenter.pause().
     */
    @Override
    public synchronized void pause() {
        locMan.detachObserver(this);
        if (hMan != null)
            hMan.detachObserver(this);
    }

    /**
     * Segnala al presenter l'abilitazione del Tracciamento.
     */
    public void onTelemetryEnabled() {
        telemetry = true;
    }

    /**
     * Segnala al presenter la disabilitazione del Tracciamento.
     */
    public void onTelemetryDisabled() {
        telemetry = false;
        telMan.stop();
    }

    /**
     * Segnala al presenter il Precorso attivo.
     *
     * @param track Percorso attivo.
     */
    public void setActiveTrack(ITrack track) {
        this.activeTrack = track;
        view.setTotalCheckpoints(track.getCheckpoints().size());
        view.setCheckpointNo(1);
    }

    /**
     * Implementa ILocationReachedObserver.onLocationObserver().
     */
    @Override
    public void onLocationReached() {
        nextCheckpoint();
    }

    /**
     * Implementa ILocationObserver.onLocationReached().
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione attuale
     *            dell'utente.
     */
    @Override
    @TargetApi(19)
    public void onLocationUpdate(final GeoPoint loc) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                lastKnownLocation = loc;
                updateView(lastKnownLocation);
                return null;
            }
        };
        task.execute();
    }

    /**
     * Aggiorna le informazioni mostrate dalla vista associata al presenter,
     * in base alla posizione utente specificata.
     *
     * Vengono aggiornate le indicazioni relative alla distanza dal prossimo
     * checkpoint e la differenza di tempo rispetto alle precedenti
     * esecuzioni del Percorso.
     *
     * @param loc Posizione attuale dell'utente.
     */
    public void updateView(GeoPoint loc) {
        int distance = Math.round(loc.distanceTo(
                activeTrack.getCheckpoints().get(checkpointToReach)));
        view.setDistance(distance);

        try {
            int delta = computeDelta(activeTrack.getBestTelemetry(),
                    loc, (int)(System.currentTimeMillis() / 1000 -
                            trackStartFullTime));
            view.setDelta(delta);
        } catch (NoSuchTelemetryException e) {
            view.setDelta(0);
        } catch (NoSuchTelemetryEventException ee) {
            view.setDelta(0);
        }
    }

    /**
     * Calcola la differenza di tempo tra la posizione specificata e quella
     * di un Tracciamento specificato.
     *
     * Se il Tracciamento specificato non contiene eventi comparabili con la
     * posizione specificata, viene sollevata un'eccezione
     * NoSuchTelemetryEventException.
     *
     * @param bestTelemetry Tracciamento su cui effettuare il confronto. Se
     *                      null, viene sollevata un'eccezione
     *                      IllegalArgumentException.
     * @param loc Posizione attuale dell'utente. Se null, viene sollevata
     *            un'eccezione IllegalArgumentException.
     * @param now Tempo parziale attuale dell'utente,
     *            espresso in secondi dall'avvio del Percorso. Se < 0, viene
     *            sollevata un'eccezione IllegalArgumentException.
     * @return  Secondi di differenza tra la posizione specificata e il
     *          Tracciamento specificato.
     * @throws NoSuchTelemetryEventException
     */
    private static int computeDelta(ITelemetry bestTelemetry, GeoPoint loc,
                                    int now)
            throws NoSuchTelemetryEventException {
        if (bestTelemetry == null)
            throw new IllegalArgumentException("Illegal null telemetry");
        if (loc == null)
            throw new IllegalArgumentException("Illegal null location");
        if (now < 0)
            throw new IllegalArgumentException("Illegal time below zero");

        LocationTelemetryEvent event =
                bestTelemetry.getEventAtLocation(loc, 30);
        return now - event.timestamp();
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
        Location userLocation = new Location("");
        userLocation.setLatitude(lastKnownLocation.latitude());
        userLocation.setLongitude(lastKnownLocation.longitude());
        Location destinationObj = new Location("");
        Checkpoint c = activeTrack.getCheckpoints().get(checkpointToReach);
        destinationObj.setLatitude(c.latitude());
        destinationObj.setLongitude(c.longitude());

        float azimuth = (float) heading;

        GeomagneticField geoField =
                new GeomagneticField(
                       Double.valueOf(userLocation.getLatitude()).floatValue(),
                       Double.valueOf(userLocation.getLongitude()).floatValue(),
                       Double.valueOf(userLocation.getAltitude()).floatValue(),
                       System.currentTimeMillis());

        float azimuthTrueNorth = azimuth - geoField.getDeclination();
        float bearTo = userLocation.bearingTo(destinationObj);

        if (bearTo < 0)
            bearTo = bearTo + 360;

        float direction = bearTo - azimuthTrueNorth;

        if (direction < 0)
            direction = direction + 360;

        view.setDirection(direction);
    }

}
