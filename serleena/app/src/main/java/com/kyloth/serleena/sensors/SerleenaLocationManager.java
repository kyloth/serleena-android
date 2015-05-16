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
 * Name: NormalLocationManager.java
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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.UnregisteredObserverException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Concretizza ILocationManager.
 *
 * Fornisce al codice client la posizione dell'utente utilizzando il modulo
 * GPS del dispositivo con il supporto delle API Android.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SerleenaLocationManager implements ILocationManager,
        LocationListener {

    private static SerleenaLocationManager instance;

    private static final long MAX_WINDOW_SECONDS = 30;

    private GeoPoint lastKnownLocation;
    private long lastUpdate;
    private Map<ILocationObserver, Integer> observers;
    private android.location.LocationManager locationManager;
    private Set<ILocationObserver> singleUpdates;
    private int currentInterval;

    /**
     * Crea un oggetto NormalLocationManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    private SerleenaLocationManager(Context context) {
        this.observers = new HashMap<ILocationObserver, Integer>();
        this.singleUpdates = new HashSet<ILocationObserver>();
        this.currentInterval = Integer.MAX_VALUE;

        locationManager = (android.location.LocationManager)
            context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Implementa ILocationManager.attachObserver().
     *
     * L'observer viene notificato ad intervalli regolari indicati dal
     * parametro interval, a meno che il processore non sia in sleep mode.
     * L'intervallo di tempo è indicativo della frequenza di notifica
     * desiderata dall'oggetto "observer".
     *
     * La posizione utente è ottenuta utilizzando le API di Android per il
     * modulo GPS, con frequenza pari a quella dell'observer di frequenza
     * maggiore. L'intervallo di effettivo aggiornamento può pertanto non
     * essere quello indicato, ma, in condizioni di disponibilità del
     * segnale, almeno tale.
     *
     * @param observer ILocationObserver da registrare. Se null,
     *                 viene lanciata un'eccezione IllegalArgumentException.
     * @param interval Intervallo di tempo in secondi.
     *                 Se minore o uguale a zero,
     *                 viene lanciata un'eccezione IllegalArgumentException.
     *
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

        observers.put(observer, interval);
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

        observers.remove(observer);
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
     * @param timeout  Timeout in secondi.
     */
    @Override
    public synchronized void getSingleUpdate(final ILocationObserver observer,
                                             int timeout)
            throws IllegalArgumentException {

        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (timeout <= 0)
            throw new IllegalArgumentException("Illegal timeout");

        if (observers.size() > 0 && ((System.currentTimeMillis() / 1000L) -
                lastUpdate) < MAX_WINDOW_SECONDS)

            notifyObserver(observer);

        else {

            final LocationListener listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    singleUpdates.remove(observer);
                    lastKnownLocation = new GeoPoint(location.getLatitude(),
                            location.getLongitude());
                    lastUpdate = System.currentTimeMillis() / 1000L;
                    notifyObserver(observer);
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override
                public void onProviderEnabled(String s) { }
                @Override
                public void onProviderDisabled(String s) { }
            };

            singleUpdates.add(observer);

            String provider = android.location.LocationManager.GPS_PROVIDER;
            locationManager.requestSingleUpdate(provider, listener, null);

            final Handler timeoutHandler = new Handler();
            final Runnable runnable = new Runnable() {
                public void run() {
                    if (singleUpdates.contains(observer)) {
                        locationManager.removeUpdates(listener);
                        notifyObserver(observer);
                        singleUpdates.remove(observer);
                    }
                }
            };

            timeoutHandler.postDelayed(runnable, timeout * 1000);

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
     * Restituisce la singola istanza della classe.
     *
     * Implementa il pattern Singleton.
     *
     * @param context Contesto dell'applicazione. Se null,
     *                viene sollevata un'eccezione IllegalArgumentException.
     * @return Istanza della classe.
     */
    public static SerleenaLocationManager getInstance(Context context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");
        if (instance == null)
            instance = new SerleenaLocationManager(context);
        return instance;
    }

    @Override
    public synchronized void onLocationChanged(Location location) {
        lastKnownLocation = new GeoPoint(location.getLatitude(),
                location.getLongitude());
        lastUpdate = System.currentTimeMillis() / 1000L;

        for (ILocationObserver observer : observers.keySet())
            notifyObserver(observer);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

    /**
     * Regola la frequenza delle richieste di aggiornamento sulla posizione
     * utente in base agli observer correntemente registrati e le loro
     * esigenze in termini di tempo.
     */
    private synchronized void adjustGpsUpdateRate() {
        if (observers.size() == 0) {
            currentInterval = Integer.MAX_VALUE;
            locationManager.removeUpdates(this);
        } else {

            int minInterval = Integer.MAX_VALUE;
            for (int interval : observers.values())
                if (interval < minInterval)
                    minInterval = interval;

            locationManager.removeUpdates(this);
            locationManager.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, minInterval * 1000, 10, this);
            currentInterval = minInterval;

        }
    }

}
