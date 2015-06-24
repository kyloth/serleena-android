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
 * Name: TelemetryManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.common.TelemetryEvent;

import java.util.ArrayList;

/**
 * Concretizza ITelemetryManager
 *
 * @use Viene istanziato da SerleenaSensorManager e restituito al codice client dietro interfaccia
 * @field locMan : ILocationManager Gestore del sensore della posizione
 * @field hrMan : IHeadingManager Gestore del sensore di battito cardiaco
 * @field wkMan : IWakeupManager Gestore dei wakeup
 * @field events : ArrayList<TelemetryEvent> Lista di eventi campionati al momento
 * @field pm : IPowerManager Gestore dei lock sul processore
 * @field enabled : boolean Valore indicante se il campionamento è abilitato
 * @field startTimestamp : long Istante, in UNIX time, di avvio del Tracciamento al momento avviato
 * @field uuid : String UUID dell'oggetto in quanto IWakeupObserver
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 */
class TelemetryManager
        implements ITelemetryManager, IHeartRateObserver,
        ITrackCrossingObserver, ILocationObserver {

    public static int SAMPLING_RATE_SECONDS = 60;

    private IBackgroundLocationManager bkgrLocMan;
    private IHeartRateManager hrMan;
    private ITrackCrossing tc;
    private ArrayList<TelemetryEvent> events;
    private boolean enabled;
    private long startTimestamp;
    /**
     * Crea un oggetto TelemetryManager.
     *
     * Il Manager utilizza altre risorse del dispositivo, passate come
     * parametri al costruttore.
     */
    public TelemetryManager(IBackgroundLocationManager bkgrLocMan,
                            IHeartRateManager hrMan,
                            ITrackCrossing trackCrossing) {
        this.bkgrLocMan = bkgrLocMan;
        this.hrMan = hrMan;
        this.tc = trackCrossing;

        this.tc.attachObserver(this);
        this.events = new ArrayList<TelemetryEvent>();
        this.enabled = false;
    }

    /**
     * Implementa ITelemetryManager.getEvents().
     *
     * @return Insieme enumerabile di eventi di Tracciamento.
     */
    @Override
    public Iterable<TelemetryEvent> getEvents() {
        return (Iterable<TelemetryEvent>) events.clone();
    }

    /**
     * Implementa ITelemetryManager.enable().
     *
     * Se vi è un percorso già iniziato e non è pertanto possibile abilitare il
     * Tracciamento in corso d'opera, viene sollevata un'eccezione
     * TrackAlreadyStartedException.
     */
    @Override
    public synchronized void enable()
            throws TrackAlreadyStartedException {
        try {
            if (tc.getNextCheckpoint() == 0)
                enabled = true;
            else
                throw new TrackAlreadyStartedException();
        } catch (NoTrackCrossingException e) {
            enabled = true;
        }
    }

    /**
     * Implementa ITelemetryManager.disable().
     */
    @Override
    public synchronized void disable() {
        stop();
        enabled = false;
    }

    /**
     * Implementa ITelemetryManager.isEnabled().
     *
     * @return True se il Tracciamento è abilitato.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private void start() {
        bkgrLocMan.attachObserver(this, SAMPLING_RATE_SECONDS);
        hrMan.attachObserver(this, SAMPLING_RATE_SECONDS);
        events.clear();
        startTimestamp = System.currentTimeMillis() / 1000L;
    }

    private void stop() {
        bkgrLocMan.detachObserver(this);
    }

    /**
     * Implementa IHeartRateObserver.onHeartRateUpdate().
     *
     * Registra un evento HeartRateTelemetryEvent all'ottenimento di dati
     * aggiornati da parte del monitor di battito cardiaco.
     * Rilascia il lock del processore acquisito in onWakeup() per il sensori di
     * battito cardiaco. Vedi onWakeup().
     *
     * @param rate Valore intero indicante il BPM.
     */
    @Override
    public void onHeartRateUpdate(int rate) {
        long now = System.currentTimeMillis() / 1000L;
        int partial = (int)(now - startTimestamp);
        events.add(new HeartRateTelemetryEvent(partial, rate));
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * @param loc Posizione geografica dell'utente.
     */
    @Override
    public void onLocationUpdate(GeoPoint loc) {
        long now = System.currentTimeMillis() / 1000L;
        int partial = (int)(now - startTimestamp);
        events.add(new LocationTelemetryEvent(partial, loc));
    }

    /**
     * Implementa ITrackCrossedObserver.onCheckpointCrossed().
     *
     * @param checkpointNumber Indice del checkpoint appena attraversato.
     */
    @Override
    public void onCheckpointCrossed(int checkpointNumber) {
        if (enabled) {
            if (checkpointNumber == 0)
                start();
            try {
                events.add(new CheckpointReachedTelemetryEvent(
                        tc.lastPartialTime(),
                        checkpointNumber
                ));

                int total = tc.getTrack().getCheckpoints().size();
                if (checkpointNumber == total - 1) {
                    stop();
                    enabled = false;
                    tc.getTrack().createTelemetry(getEvents());
                }
            } catch (NoTrackCrossingException e) {}
        }

    }

}
