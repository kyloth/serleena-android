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
 * Name: LocationReachedManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-13
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-13   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;
import android.location.Location;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UnregisteredObserverException;

import java.util.HashMap;
import java.util.Map;

/**
 * Concretizza ILocationReachedManager
 *
 * @author Filippo Sestini <sestini.filippo@gmail,com>
 * @version 1.0.0
 */
public class LocationReachedManager implements ILocationReachedManager {

    private static int LOCATION_RADIUS = 15;
    private static LocationReachedManager instance;

    private Map<ILocationReachedObserver, GeoPoint> observers;
    private Map<ILocationReachedObserver, IWakeupObserver> alarms;
    private IWakeupManager wm;
    private ILocationManager locMan;

    /**
     * Crea un oggetto LocationReachedManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    private LocationReachedManager(Context context) {
        wm = WakeupManager.getInstance(context);
        locMan = WakefulLocationManager.getInstance(context);
        observers = new HashMap<ILocationReachedObserver, GeoPoint>();
        alarms = new HashMap<ILocationReachedObserver, IWakeupObserver>();
    }

    /**
     * Implementa ILocationReachedManager.attachObserver().
     *
     * @param observer ILocationReachedObserver da registrare. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @param location Punto geografico il cui raggiungimento deve generare una
     *                 notifica. Se null, viene sollevata un'eccezione
     *                 IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    @Override
    public synchronized void attachObserver(ILocationReachedObserver observer,
                                            GeoPoint location)
            throws IllegalArgumentException {

        observers.put(observer, location);
        IWakeupObserver alarm = new CheckDistanceAlarm(observer, locMan);
        alarms.put(observer, alarm);
        wm.attachObserver(alarm, 0, true);
    }

    /**
     * Implementa ILocationReachedManager.detachObserver().
     *
     * @param observer Oggetto da deregistrare ILocationReachedManager. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     *                 Se non precedentemente registrato,
     *                 viene sollevata un'eccezione
     *                 UnregisteredObserverException.
     * @throws IllegalArgumentException
     * @throws UnregisteredObserverException
     */
    @Override
    public synchronized void detachObserver(ILocationReachedObserver observer)
            throws IllegalArgumentException, UnregisteredObserverException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (!observers.containsKey(observer))
            throw new UnregisteredObserverException();

        observers.remove(observer);
        try {
            wm.detachObserver(alarms.remove(observer));
        } catch (UnregisteredObserverException ex) { }
    }

    /**
     * Verifica, per un particolare observer, la distanza tra la posizione
     * attuale e quella richiesta dall'observer.
     *
     * Se la distanza è ritenuta
     * trascurabile, la destinazione è considerata raggiunta e l'observer
     * viene notificato. Altrimenti, viene pianificato un ulteriore controllo
     * con ritardo di tempo dipendente dalla distanza tra la destinazione e
     * la posizione attuale.
     *
     * @param observer Oggetto ILocationReachedObserver la cui destinazione
     *                 viene controllata.
     * @param currentLocation Posizione attuale dell'utente.
     */
    private synchronized void checkObserverStatus(
            ILocationReachedObserver observer, GeoPoint currentLocation) {
        float[] results = new float[1];
        GeoPoint toReach = observers.get(observer);

        Location.distanceBetween(currentLocation.latitude(),
                currentLocation.longitude(), toReach.latitude(),
                toReach.longitude(), results);

        int distance = Math.round(results[0]);

        if (distance <= LOCATION_RADIUS) {
            observer.onLocationReached();
            observers.remove(observer);
        } else {
            IWakeupObserver alarm = new CheckDistanceAlarm(observer, locMan);
            alarms.remove(observer);
            alarms.put(observer, alarm);
            wm.attachObserver(alarm, distance, true);
        }
    }

    /**
     * Restituisce la singola istanza della classe.
     *
     * Implementa il pattern Singleton.
     *
     * @param context Contesto dell'applicazione.
     * @return Istanza della classe.
     */
    public static LocationReachedManager getInstance(Context context) {
        if (instance == null)
            instance = new LocationReachedManager(context);
        return instance;
    }

}
