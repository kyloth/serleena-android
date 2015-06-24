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
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.kyloth.serleena.common.GeoPoint;

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
 * @use Viene istanziato da SerleenaSensorManager e restituito al codice client dietro interfaccia.
 * @field observers : Map<ILocationObserver, LocationListener> Mappa gli Observer ai relativi LocationListener
 * @field locationManager : LocationManager Gestore della posizione di Android
 * @field currentInterval : int Intervallo con cui al momento viene richiesto l'aggiornamento sulla posizione alle API Android
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class SerleenaLocationManager implements ILocationManager {

    private LocationManager locationManager;
    private Map<ILocationObserver, LocationListener> observers;

    /**
     * Crea un oggetto NormalLocationManager.
     *
     * @param locationManager LocationManager da utilizzare per ottenere la
     *                        posizione dell'utente. Se null, viene sollevata
     *                        un'eccezione IllegalArgumentException.
     */
    public SerleenaLocationManager(LocationManager locationManager) {
        if (locationManager == null)
            throw new IllegalArgumentException("Illegal null location manager");
        this.locationManager = locationManager;
        this.observers = new HashMap<>();
    }

    /**
     * Implementa ILocationManager.attachObserver().
     *
     * @param observer Observer da registrare agli eventi dell'istanza.
     * @param interval Intervalli minimo ogni quanto l'Observer deve essere
     *                 notificato.
     */
    @Override
    public void attachObserver(ILocationObserver observer, int interval) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (interval <= 0)
            throw new IllegalArgumentException("Illegal interval");

        SerleenaLocationListener listener = new SerleenaLocationListener(observer);
        this.observers.put(observer, listener);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                interval, 0, listener);
    }

    /**
     * Implementa ILocationManager.detachObserver().
     *
     * @param observer Observer la cui registrazione deve essere cancellata.
     */
    @Override
    public void detachObserver(ILocationObserver observer) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");

        if (observers.containsKey(observer))
            locationManager.removeUpdates(observers.get(observer));
    }

    /**
     * Implementa ILocationManager.getSingleUpdate().
     *
     * @param observer Observer che deve essere notificato.
     */
    @Override
    public void getSingleUpdate(ILocationObserver observer) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");

        SerleenaLocationListener listener = new SerleenaLocationListener(observer);

        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                listener,
                null);
    }

    public static class SerleenaLocationListener implements LocationListener {

        private ILocationObserver observer;

        public SerleenaLocationListener(ILocationObserver observer) {
            this.observer = observer;
        }

        @Override
        public void onLocationChanged(Location location) {
            observer.onLocationUpdate(new GeoPoint(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        { }
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onProviderDisabled(String provider) { }

    }

}
