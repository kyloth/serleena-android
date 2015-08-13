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
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.common.TelemetryEvent;

import java.util.ArrayList;

/**
 * Concretizza ITelemetryManager
 *
 * @use Viene istanziato da SerleenaSensorManager e restituito al codice client dietro interfaccia
 * @field wkMan : IWakeupManager Gestore dei wakeup
 * @field events : ArrayList<TelemetryEvent> Lista di eventi campionati al momento
 * @field pm : IPowerManager Gestore dei lock sul processore
 * @field enabled : boolean Valore indicante se il campionamento è abilitato
 * @field startTimestamp : long Istante, in UNIX time, di avvio del Tracciamento al momento avviato
 * @field uuid : String UUID dell'oggetto in quanto IWakeupObserver
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 */
class TelemetryManager
        implements ITelemetryManager, ITrackCrossingObserver {

    public static int SAMPLING_RATE_SECONDS = 60;

    private ITrackCrossing tc;
    private ArrayList<TelemetryEvent> events;
    private boolean enabled;

    /**
     * Crea un oggetto TelemetryManager.
     *
     * Il Manager utilizza altre risorse del dispositivo, passate come
     * parametri al costruttore.
     */
    public TelemetryManager(ITrackCrossing trackCrossing) {
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
        } catch (NoTrackCrossingException|NoActiveTrackException e) {
            enabled = true;
        }
    }

    /**
     * Implementa ITelemetryManager.disable().
     */
    @Override
    public synchronized void disable() {
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
        events.clear();
    }

    /**
     * Implementa ITrackCrossedObserver.onCheckpointCrossed().
     */
    @Override
    public void onCheckpointCrossed() {
        if (enabled) {
            int checkpointIndex = 0;
            try {
                checkpointIndex = tc.getLastCrossed().checkPointIndex();
                if (checkpointIndex == 0)
                    start();
                events.add(new CheckpointReachedTelemetryEvent(
                        tc.getLastCrossed().partialTime(), checkpointIndex+1));

                int total = tc.getTrack().getCheckpoints().size();
                if (checkpointIndex == total - 1) {
                    enabled = false;
                    tc.getTrack().createTelemetry(getEvents());
                }
            } catch (NoSuchCheckpointException|NoActiveTrackException e) { }
        }
    }

}
