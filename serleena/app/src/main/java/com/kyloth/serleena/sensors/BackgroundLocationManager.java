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
 * Name: BackgroundLocationManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.kyloth.serleena.common.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementa IBackgroundLocationManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class BackgroundLocationManager extends WakefulBroadcastReceiver
        implements IBackgroundLocationManager, ServiceResultReceiver.Receiver {

    private ServiceResultReceiver myResultReceiver;
    private AlarmManager am;
    private Context context;
    private Map<ILocationObserver, Integer> observers;

    private PendingIntent pendingIntent;
    private GeoPoint location;

    /**
     * Crea un nuovo oggetto BackgroundLocationManager.
     *
     * @param context Contesto dal quale creare l'oggetto.
     * @param am Gestore degli allarmi di Android.
     */
    public BackgroundLocationManager(Context context, AlarmManager am) {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");
        if (am == null)
            throw new IllegalArgumentException("Illegal null alarm manager");

        myResultReceiver = new ServiceResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);
        this.am = am;
        this.context = context;
        this.observers = new HashMap<>();
    }

    /**
     * Implementa IBackgroundLocationManager.attachObserver().
     *
     * Se vi è almeno un Observer registrato, viene avviato l'aggiornamento
     * sulla posizione.
     *
     * @param observer Observer da registrare. Se null, viene sollevata
     *                 un'eccezione IllegalArgumentException.
     */
    @Override
    public synchronized void attachObserver(ILocationObserver observer,
                                            int interval) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (interval <= 0)
            throw new IllegalArgumentException("Illegal interval");

        if (!observers.containsKey(observer)) {
            this.observers.put(observer, interval);
            if (observers.size() == 1)
                context.registerReceiver(this, new IntentFilter("SERLEENA_ALARM"));
            restart();
        }
    }

    /**
     * Implementa IBackgroundLocationManager.detachObserver().
     *
     * Se non vi è alcun observer registrato, l'aggiornamento viene disattivato.
     *
     * @param observer Observer la cui registrazione deve essere cancellata.
     *                 Se null, viene sollevata un'eccezione
     *                 IllegalArgumentException.
     */
    @Override
    public synchronized void detachObserver(ILocationObserver observer) {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");

        if (observers.size() == 1 && this.observers.containsKey(observer)) {
            am.cancel(pendingIntent);
            pendingIntent = null;
            context.stopService(new Intent(context, LocationService.class));
            context.unregisterReceiver(this);
        }

        this.observers.remove(observer);
    }

    /**
     * Implementa IBackgroundLocationManager.notifyObservers().
     */
    @Override
    public void notifyObservers() {
        if (location != null)
            for (ILocationObserver o : this.observers.keySet())
                o.onLocationUpdate(location);
    }

    /**
     * Ridefinisce WakefulBroadcastReceiver.onReceive().
     *
     * Viene chiamato dal sistema ad ogni tick dell'allarme, svegliando il
     * dispositivo dalla sleep mode se necessario.
     *
     * Ad ogni evento di allarme, viene avviato un servizio per il recupero
     * in background della posizione utente.
     *
     * @param context Contesto nel quale avviene la ricezione.
     * @param intent Intent ricevuto.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LocationService.class);
        service.putExtra("receiverTag", myResultReceiver);
        startWakefulService(context, service);
    }

    /**
     * Implementa ServiceResultReceiver.Receiver.onReceiveResult().
     *
     * Viene utilizzato dal servizio avviato per segnalare i risultati
     * relativi alla posizione utente. Questi dati vengono segnalari poi agli
     * observer.
     *
     * @param resultCode Codice del risultato.
     * @param resultData Dati del risultato.
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        this.location = new GeoPoint(
                resultData.getDouble("latitude"),
                resultData.getDouble("longitude")
        );
        notifyObservers();
    }

    private void restart() {
        int minInterval = Integer.MAX_VALUE;
        for (int interval : observers.values())
            if (interval < minInterval)
                minInterval = interval;
        int alarmType = AlarmManager.RTC_WAKEUP;
        Intent intentToFire = new Intent("SERLEENA_ALARM");

        if (pendingIntent != null)
            am.cancel(pendingIntent);

        pendingIntent = PendingIntent.getBroadcast(
                context,
                new Random().nextInt(),
                intentToFire,
                PendingIntent.FLAG_CANCEL_CURRENT);

        am.setInexactRepeating(
                alarmType, 10, minInterval * 1000, pendingIntent);
    }

}
