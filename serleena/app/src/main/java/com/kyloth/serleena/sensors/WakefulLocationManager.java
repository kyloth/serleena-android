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
 * Name: WakefulLocationManager
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-12
 *
 * History:
 * Version  Programmer       Date        Changes
 * 1.0.0    Filippo Sestini  2015-05-12  Creazione file e scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;
import android.os.PowerManager;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UnregisteredObserverException;

import java.util.HashMap;
import java.util.Map;

/**
 * Concretizza ILocationManager.
 *
 * Fornisce al codice client la posizione dell'utente utilizzando il modulo
 * GPS del dispositivo con il supporto delle API Android. Assicura la
 * comunicazione dei dati agli observer registrati mantenendo sveglio il
 * processore quando necessario.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class WakefulLocationManager implements ILocationManager {

    private static WakefulLocationManager instance;

    private PowerManager pm;
    private WakeupManager wm;
    private NormalLocationManager nlm;
    private Map<ILocationObserver, IWakeupObserver> observers;
    private IWakeupObserver gpsUpdateObserver;
    private int gpsUpdateInterval;
    private Map<ILocationObserver, Integer> intervals;
    private GeoPoint lastKnownLocation;

    /**
     * Crea un oggetto WakefulLocationManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    private WakefulLocationManager(Context context) {
        wm = WakeupManager.getInstance(context);
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        observers = new HashMap<ILocationObserver, IWakeupObserver>();
        intervals = new HashMap<ILocationObserver, Integer>();

        final WakefulLocationManager me = this;
        class GpsWakeup implements IWakeupObserver, ILocationObserver {
            @Override
            public void onLocationUpdate(GeoPoint loc) {
                lastKnownLocation = loc;
            }
            @Override
            public void onWakeup() {
                me.getSingleUpdate(this, 30);
            }
        }
        gpsUpdateObserver = new GpsWakeup();
    }

    /**
     * Implementa ILocationManager.attachObserver().
     *
     * Notifica l'observer a intervalli regolari secondo il valore del
     * parametro interval. Se il processore è in sleep mode,
     * questo viene svegliato e l'observer notificato.
     *
     * @param observer ILocationObserver da registrare. Se null,
     *                 viene lanciata un'eccezione IllegalArgumentException.
     * @param interval Intervallo di tempo in secondi ogni quanto viene
     *                 notificato l'oserver. Se minore o uguale a zero,
     *                 viene lanciata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    @Override
    public synchronized void attachObserver(final ILocationObserver observer,
                                            int interval)
            throws IllegalArgumentException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (interval <= 0)
            throw new IllegalArgumentException("Illegal interval");

        IWakeupObserver myObserver = new IWakeupObserver() {
            @Override
            public void onWakeup() {
                notifyObserver(observer);
            }
        };

        wm.attachObserver(myObserver, interval, false);
        observers.put(observer, myObserver);
        intervals.put(observer, interval);
        adjustGpsUpdateRate();
    }

    /**
     * Implementa ILocationManager.detachObserver().
     *
     * @param observer ILocationObserver la cui registrazione come "observer" di
     *                 questo oggetto sarà cancellata. Se null, viene lanciata
     *                 un'eccezione IllegalArgumentException. Se non
     *                 precedentemente registrato,
     *                 viene lanciata un'eccezione
     *                 UnregisteredObserverException.
     * @throws UnregisteredObserverException
     * @throws IllegalArgumentException
     */
    @Override
    public synchronized void detachObserver(ILocationObserver observer)
            throws UnregisteredObserverException, IllegalArgumentException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (!observers.containsKey(observer))
            throw new UnregisteredObserverException();

        wm.detachObserver(observers.get(observer));
        observers.remove(observer);
        intervals.remove(observer);
        adjustGpsUpdateRate();
    }

    /**
     * Implementa ILocationManager.getSingleUpdate().
     *
     * Il metodo garantisce che il processore non entri in sleep mode
     * finchè non vengono ottenuti i dati dal modulo GPS,
     * o comunque fino allo scadere di un timeout.
     * L'observer riceve in ogni caso una callback. Nel caso non sia stato
     * possibile ottenere dati aggiornati dal GPS, vengono comunicati gli
     * ultimi disponibili.
     *
     * @param observer Oggetto ILocationObserver a cui comunicare i dati. Se
     *                 null, viene sollevata un'eccezione
     *                 IllegalArgumentException.
     * @param timeout  Timeout in secondi. Se minore o uguale a zero,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    @Override
    public synchronized void getSingleUpdate(final ILocationObserver observer,
                                             int timeout)
            throws IllegalArgumentException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");

        final android.os.PowerManager.WakeLock wakeLock =
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();

        ILocationObserver myObs = new ILocationObserver() {
            @Override
            public void onLocationUpdate(GeoPoint loc) {
                lastKnownLocation = loc;
                notifyObserver(observer);
                wakeLock.release();
            }
        };

        try {
            nlm.getSingleUpdate(myObs, timeout);
        } catch (Exception ex) {
            wakeLock.release();
            throw ex;
        }
    }

    /**
     * Implementa ILocationManager.notifyObserver().
     *
     * @param observer Oggetto ILocationObserver a cui comunicare i dati.
     *                 Se null, viene lanciata un'eccezione
     *                 IllegalArgumentException.
     */
    @Override
    public synchronized void notifyObserver(ILocationObserver observer)
            throws IllegalArgumentException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");

        observer.onLocationUpdate(lastKnownLocation);
    }

    /**
     * Regola la frequenza delle richieste di aggiornamento sulla posizione
     * utente in base agli observer correntemente registrati e le loro
     * esigenze in termini di tempo.
     */
    private synchronized void adjustGpsUpdateRate() {
        try {
            wm.detachObserver(gpsUpdateObserver);
        } catch (UnregisteredObserverException ex) {}

        if (observers.size() == 0) {
            gpsUpdateInterval = Integer.MAX_VALUE;
        } else {
            int minInterval = Integer.MAX_VALUE;

            for (int interval : intervals.values())
                if (interval < minInterval)
                    minInterval = interval;

            gpsUpdateInterval = minInterval;
            wm.attachObserver(gpsUpdateObserver, gpsUpdateInterval, false);
        }
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
    public static WakefulLocationManager getInstance(Context context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");
        if (instance == null)
            instance = new WakefulLocationManager(context);
        return instance;
    }

}
