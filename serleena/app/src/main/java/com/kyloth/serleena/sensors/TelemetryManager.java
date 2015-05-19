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
 * Date: 2015-05-21
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-21   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;

import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Concretizza ITelemetryManager
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 */
public class TelemetryManager implements ITelemetryManager,
        ILocationObserver, IHeartRateObserver, IWakeupObserver {

    private static int SAMPLING_RATE_SECONDS = 60;
    private static TelemetryManager instance;

    private ILocationManager locMan;
    private IHeartRateManager hrMan;
    private IWakeupManager wkMan;
    private ArrayList<TelemetryEvent> events;
    private SerleenaPowerManager pm;
    private boolean sampling;
    private long startTimestamp;

    /**
     * Crea un oggetto TelemetryManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    private TelemetryManager(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");

        this.events = new ArrayList<TelemetryEvent>();
        this.sampling = false;
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
     * Implementa ITelemetryManager.start().
     */
    @Override
    public synchronized void start() {
        if (!sampling) {
            wkMan.attachObserver(this, SAMPLING_RATE_SECONDS, false);
            sampling = true;
        }
        events.clear();
        startTimestamp = System.currentTimeMillis() / 1000L;
    }

    /**
     * Implementa ITelemetryManager.stop().
     */
    @Override
    public synchronized void stop() {
        if (sampling) {
            wkMan.detachObserver(this);
            sampling = false;
        }
    }

    /**
     * Implementa ITelemetryManager.signalEvent().
     *
     * @param event Evento di Tracciamento da registrare.
     */
    @Override
    public void signalEvent(TelemetryEvent event) {
        events.add(event);
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
        pm.unlock("HeartRateTelemetryLock");
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * Rilascia il lock del processore acquisito in onWaleup() per il sensore
     * di posizione. Vedi onWakeup().
     *
     * @param loc Posizione geografica dell'utente.
     */
    @Override
    public void onLocationUpdate(GeoPoint loc) {
        long now = System.currentTimeMillis() / 1000L;
        int partial = (int)(now - startTimestamp);
        events.add(new LocationTelemetryEvent(partial, loc));
        pm.unlock("LocationTelemetryLock");
    }

    /**
     * Implementa IWakeupObserver.onWakeup().
     *
     * Il metodo viene invocato periodicamente da un IWakeupManager,
     * per permettere all'applicazione di registrare eventi di Tracciamento
     * anche in periodi di sleep del processore.
     * In attesa della risposta dei sensori, il metodo acquisisce un lock sul
     * processore per ogni sensore, evitando che esso torni in modalità sleep
     * prima del corretto campionamento dell'evento.
     */
    @Override
    public void onWakeup() {
        pm.lock("LocationTelemetryLock");
        pm.lock("HeartRateTelemetryLock");
        locMan.getSingleUpdate(this, 20);
        hrMan.getSingleUpdate(this, 20);
    }

    /**
     * Restituisce la singola istanza della classe.
     *
     * Implementa il pattern Singleton.
     *
     * @param context Contesto dell'applicazione. Se null,
     *                viene sollevata un'eccezione IllegalArgumentException.
     * @return Istanza della classe.
     */
    public static TelemetryManager getInstance(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");

        if (instance == null)
            instance = new TelemetryManager(context);
        return instance;
    }

}