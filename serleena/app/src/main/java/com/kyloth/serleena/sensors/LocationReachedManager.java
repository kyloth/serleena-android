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
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concretizza ILocationReachedManager
 *
 * @author Filippo Sestini <sestini.filippo@gmail,com>
 * @version 1.0.0
 */
class LocationReachedManager
        implements ILocationReachedManager, ILocationObserver {

    public static final int LOCATION_RADIUS = 50;

    private final List<LocationReachedRequest> requests;
    private final IBackgroundLocationManager bkgrLocMan;

    /**
     * Crea un oggetto LocationReachedManager.
     *
     * L'oggetto utilizza altre risorse di sensoristica del dispositivo,
     * fornitegli tramite parametri al costruttore.
     *
     * @param bkgrLocMan Gestore di notifiche sulla posizione utente.
     */
    public LocationReachedManager(IBackgroundLocationManager bkgrLocMan) {
        if (bkgrLocMan == null)
            throw new IllegalArgumentException("Illegal location manager");

        this.bkgrLocMan = bkgrLocMan;
        this.requests = new ArrayList<>();
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
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");

        requests.add(new LocationReachedRequest(observer, location));
        if (requests.size() == 1)
            bkgrLocMan.attachObserver(this);
    }

    /**
     * Implementa ILocationReachedManager.detachObserver().
     *
     * @param observer Oggetto da deregistrare ILocationReachedManager. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    @Override
    public synchronized void detachObserver(ILocationReachedObserver observer,
                                            GeoPoint location)
            throws IllegalArgumentException {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");

        LocationReachedRequest request =
                new LocationReachedRequest(observer, location);
        if (requests.contains(request)) {
            requests.remove(request);
            if (requests.size() == 0)
                bkgrLocMan.detachObserver(this);
        }
    }

    @Override
    public synchronized void detachObserver(ILocationReachedObserver observer) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        List<LocationReachedRequest> toRemove = new ArrayList<>();
        for (LocationReachedRequest request : requests)
            if (request.observer().equals(observer))
                toRemove.add(request);
        for (LocationReachedRequest request : toRemove)
            requests.remove(request);
        if (requests.size() == 0)
            bkgrLocMan.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * Viene notificato ogni observer il cui punto geografico obiettivo è
     * stato raggiunto.
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione
     */
    @Override
    public synchronized void onLocationUpdate(GeoPoint loc) {
        List<LocationReachedRequest> toRemove = new ArrayList<>();

        for (LocationReachedRequest request : requests)
            if (request.location().distanceTo(loc) < LOCATION_RADIUS)
                toRemove.add(request);

        for (LocationReachedRequest request : toRemove)
            requests.remove(request);

        if (requests.size() == 0)
            bkgrLocMan.detachObserver(this);

        for (LocationReachedRequest request : toRemove)
            request.observer().onLocationReached();
    }

}
