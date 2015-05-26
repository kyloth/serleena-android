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
 * Name: WakeupManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kyloth.serleena.common.UnregisteredObserverException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Concretizza IWakeupManager.
 *
 * @use Viene utilizzata da SensorManager per restituire ai client il gestore dei wakeup, e dal client per accedere ai servizi offerti dal sensore. È utilizzato in particolare da LocationReachedManager e TelemetryManager.
 * @field context : Context Contesto dell'applicazione
 * @field schedule : WakeupSchedule Oggetto che incapsula e rappresenta la pianificazione dei wakeup
 * @field alarmManager : Gestore degli allarmi di Android
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class WakeupManager extends BroadcastReceiver implements IWakeupManager {

    private static final String ALARM_UUID = "ALARM_UUID";

    private Context context;
    private WakeupSchedule schedule;
    private AlarmManager alarmManager;

    /**
     * Crea un oggetto WakeupManager.
     *
     * @param context Contesto dell'applicazione.
     * @param alarmManager Un alarmManager diverso da quello di default; 
     *        utile per testing.
     */
    public WakeupManager(Context context, AlarmManager alarmManager) {
        if (alarmManager == null) {
            this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        } else {
            this.alarmManager = alarmManager;
        }
        this.context = context;
        this.schedule = new WakeupSchedule();
    }

    /**
     * Crea un oggetto WakeupManager.
     *
     * @param context Contesto dell'applicazione.
     */
    public WakeupManager(Context context) {
        this(context, null);
    }

    /**
     * Implementa IWakeupManager.attachObserver().
     *
     * @param observer      IWakeupObserver da registrare. Se null, viene
     *                      sollevata un'eccezione IllegalArgumentException.
     * @param interval      Intervallo di tempo per la notifica all'oggetto
     *                      "observer". Se < 0, viene sollevata un'eccezione
     *                      IllegalArgumentException.
     * @param oneTimeOnly   True se il wakeup avviene una sola volta,
     *                      false se il wakeup è ripetuto.
     */
    @TargetApi(19)
    @Override
    public synchronized void attachObserver(IWakeupObserver observer,
                                            int interval, boolean oneTimeOnly)
            throws IllegalArgumentException {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (interval < 0)
            throw new IllegalArgumentException("Illegal interval");

        int alarmType = AlarmManager.RTC_WAKEUP;
        int millis = interval * 1000;
        if (millis == 0)
            millis = 1;
        String uuid = observer.getUUID();

        Intent intentToFire = new Intent(context, WakeupManager.class);
        intentToFire.putExtra(ALARM_UUID, uuid);

        PendingIntent alarmIntent =
                PendingIntent.getBroadcast(context, 0, intentToFire, 0);

        if (oneTimeOnly)
            alarmManager.set(alarmType, millis, alarmIntent);
        else
            alarmManager.setInexactRepeating(alarmType, millis, millis,
                    alarmIntent);

        schedule.add(observer, alarmIntent, oneTimeOnly);
    }

    /**
     * Implementa IWakeupManager.detachObserver().
     *
     * @param observer IWakeupObserver la cui registrazione come "observer" di
     */
    @Override
    public synchronized void detachObserver(IWakeupObserver observer)
            throws IllegalArgumentException, UnregisteredObserverException {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        if (!schedule.containsObserver(observer))
            throw new UnregisteredObserverException();

        alarmManager.cancel(schedule.getIntent(observer));
        schedule.remove(observer);
    }

    /**
     * Implementa IWakeupManager.notifyObserver().
     *
     * @param observer Oggetto "observer" da notificare.
     */
    @Override
    public synchronized void notifyObserver(IWakeupObserver observer)
            throws IllegalArgumentException {
        if (observer == null)
            throw new IllegalArgumentException("Illegal null observer");
        try {
            if (schedule.isOneTimeOnly(observer))
                detachObserver(observer);
            observer.onWakeup();
        } catch (UnregisteredObserverException ex) { }
    }

    /**
     * Ridefinisce BroadcastReceiver.onReceive().
     *
     * @param context Contesto dell'applicazione.
     * @param intent Intent oggetto dell'evento.
     */
    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        String uuid = intent.getStringExtra(ALARM_UUID);
        if (uuid == null)
            throw new IllegalArgumentException("Intent not recognized");

        notifyObserver(schedule.getObserver(uuid));
    }
}
