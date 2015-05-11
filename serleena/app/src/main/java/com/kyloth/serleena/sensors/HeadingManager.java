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
 * Name: HeadingManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-07
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-07   Creazione file e scrittura
 *                                         codice e documentazione Javadoc.
 */

package com.kyloth.serleena.sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Concretizza IHeadingManager utilizzando le API di gestione della
 * sensoristica di Android.
 *
 * @author Filippo Sestini
 * @version 1.0.0
 */
@TargetApi(19)
public class HeadingManager implements IHeadingManager, SensorEventListener {

    private static HeadingManager instance;

    private float[] accelerometerValues;
    private float[] magneticFieldValues;
    private double latestOrientation;
    private Map<IHeadingObserver, ScheduledFuture> observers;
    private ScheduledThreadPoolExecutor scheduledPool;
    private Context context;
    private Sensor magnetometer;
    private Sensor accelerometer;

    /**
     * Crea un nuovo oggetto HeadingManager.
     *
     * @param context Oggetto Context in cui viene eseguito l'HeadingManager.
     */
    private HeadingManager(Context context) throws SensorNotAvailableException {
        SensorManager sm = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null)
            throw new SensorNotAvailableException("accelerometer");
        if (magnetometer == null)
            throw new SensorNotAvailableException("magnetometer");

        observers = new HashMap<IHeadingObserver, ScheduledFuture>();
        latestOrientation = 0;
        this.context = context;
    }

    /**
     * Implementazione di SensorEventListener.onSensorChanged().
     *
     * Il metodo viene chiamato ogni volta che sono disponibili dati aggiornati
     * dai sensori a cui l'oggetto si è registrato. Questi dati vengono
     * prelevati dai sensori e elaborati in modo asincrono.
     *
     * @param sensorEvent Evento di un sensore.
     * @see android.hardware.SensorEventListener
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = sensorEvent.values;
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticFieldValues = sensorEvent.values;
        else return;

        AsyncTask<Void, Void, Double> t = new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                return computeOrientation();
            }
            @Override
            protected void onPostExecute(Double result) {
                latestOrientation = result;
            }
        };

        t.execute();
    }

    /**
     * Implementazione di SensorEventListener.onAccuracyChanged().
     *
     * @param sensor Sensore la cui precisione è variata.
     * @param accuracy Nuovo valore di precisione del sensore.
     * @see android.hardware.SensorEventListener
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Implmentazione di IHeadingManager.attachObserver().
     *
     * @param observer IHeadingObserver da registrare.
     * @param interval Intervallo di tempo, in secondi,
     *                 ogni qual volta si vuole notificare l'observer.
     */
    @Override
    @TargetApi(19)
    public void attachObserver(final IHeadingObserver observer, int interval) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                notifyObserver(observer);
            }
        };

        ScheduledFuture sf = scheduledPool.schedule(task, interval,
                TimeUnit.SECONDS);
        observers.put(observer, sf);

        if (observers.size() == 1) {
            SensorManager sm = (SensorManager)
                    context.getSystemService(Context.SENSOR_SERVICE);
            sm.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_UI);
            sm.registerListener(this, magnetometer,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Implementazione di IHeadingManager.detachObserver().
     *
     * @param observer IHeadingObserver la cui registrazione come "observer" di
     *                 questo oggetto sarà cancellata.
     */
    @Override
    @TargetApi(19)
    public void detachObserver(IHeadingObserver observer) {
        ScheduledFuture sf = observers.get(observer);
        sf.cancel(true);
        observers.remove(observer);

        if (observers.size() == 0) {
            SensorManager sm = (SensorManager)
                    context.getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);
        }
    }

    /**
     * Implementazione di IHeadingManager.getSingleUpdate().
     *
     * @return Orientamento in gradi rispetto ai punti cardinali.
     */
    @Override
    public double getSingleUpdate() {
        return latestOrientation;
    }

    /**
     * Implementazione di IHeadingManager.notifyObserver().
     *
     * @param observer Oggetto IHeadingObserver da notificare.
     */
    @Override
    public void notifyObserver(IHeadingObserver observer) {
        observer.onHeadingUpdate(latestOrientation);
    }

    /**
     * Calcola l'orientamento del dispositivo.
     *
     * Utilizza i dati raw forniti dai sensori accelerometro e campo magnetico
     * per calcolare l'orientamento del dispositivo.
     *
     * @return Gradi di rotazione sull'asse azimuth.
     */
    @TargetApi(19)
    private double computeOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);

        values[0] = (float) Math.toDegrees(values[0]); // Azimuth
        values[1] = (float) Math.toDegrees(values[1]); // Pitch
        values[2] = (float) Math.toDegrees(values[2]); // Roll

        return values[0];
    }

    /**
     * Implementazione del pattern Singleton.
     *
     * @return Singola istanza della classe HeadingManager.
     */
    public static HeadingManager getInstance(Context context)
            throws SensorNotAvailableException {
        if (instance == null)
            instance = new HeadingManager(context);
        return instance;
    }

}
