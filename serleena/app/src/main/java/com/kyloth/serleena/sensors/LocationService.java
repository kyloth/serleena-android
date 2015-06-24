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
 * Name: LocationService.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;

import java.util.Stack;

/**
 * Servizio incaricato di ricavare informazioni sulla posizione utente in
 * background, evitando che il dispositivo entri in sleep mode.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class LocationService extends Service implements LocationListener {

    private PowerManager pm;
    private LocationManager lm;
    private Stack<Intent> intents;
    private PowerManager.WakeLock wl;
    private Handler timeout;
    private Runnable runnable;

    /**
     * Ridefinisce Service.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        pm = (PowerManager) this.getSystemService(POWER_SERVICE);
        lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        intents = new Stack<Intent>();
        wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "LocationServiceWakeLock"
        );
        wl.acquire();
        lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);

        timeout = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        };
        timeout.postDelayed(runnable, 60000);
    }

    /**
     * Ridefinisce Service.onStartCommand().
     *
     * Avvia il servizio, acquisendo un lock al processore per evitare che
     * questo entri in sleep mode, e richiede un aggiornamento sulla
     * posizione al LocationManager di Android.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        intents.add(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Implementa LocationListener.onLocationChanged().
     *
     * Alla ricezione dei dati sulla posizione, questi vengono segnalati al
     * Receiver registrato. Viene poi rilasciato il lock al processore e
     * interrotto il servizio.
     *
     * @param location Posizione rilevata e notificata dal LocationManager.
     */
    @Override
    public void onLocationChanged(Location location) {
        for (Intent intent : intents) {
            ResultReceiver rec = intent.getParcelableExtra("receiverTag");
            Bundle b = new Bundle();
            b.putDouble("latitude", location.getLatitude());
            b.putDouble("longitude", location.getLongitude());
            rec.send(0, b);
        }
        timeout.removeCallbacks(runnable);
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Intent intent : intents)
            BackgroundLocationManager.completeWakefulIntent(intent);
        wl.release();
        lm.removeUpdates(this);
    }

    /**
     * Implementa Service.onBind().
     */
    @Override
    public IBinder onBind(Intent intent) { return null; }

    /**
     * Implementa LocationListener.onStatusChanged().
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    /**
     * Implementa LocationListener.onProviderEnabled().
     */
    @Override
    public void onProviderEnabled(String provider) { }

    /**
     * Implementa LocationListener.onProviderDisabled().
     */
    @Override
    public void onProviderDisabled(String provider) { }

}
