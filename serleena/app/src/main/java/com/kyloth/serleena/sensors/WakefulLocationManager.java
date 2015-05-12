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
                me.getSingleUpdate(this);
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
    public void attachObserver(final ILocationObserver observer, int interval) {

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

}
