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
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc.
 * 1.0.1    Filippo Sestini   Refactoring di onSensorChanged
 *                                         e aggiunta di onNewData().
 */

package com.kyloth.serleena.sensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Concretizza IHeadingManager utilizzando le API di gestione della
 * sensoristica di Android.
 *
 * @use Viene istanziato da SerleenaSensorManager e restituito al codice client dietro interfaccia.
 * @field accelerometerValues : float[] Valori ottenuti dall'accelerometro del dispositivo
 * @field magneticFieldValues : float[] Valori ottenuti dal sensore di campo magnetico del dispositivo
 * @field latestOrientation : double Ultimo valore di orientamento disponibile
 * @field observers : List<IHeadingObserver> Lista degli observers registrati agli eventi del manager
 * @field context : Context Contesto dell'applicazione
 * @field magnetometer : Sensor Instanza della classe di Android Sensor associato al magnetometro del dispositivo
 * @field accelerometer : Sensor Instanza della classe di Android Sensor associato all'accelerometro del dispositivo
 * @author Filippo Sestini
 * @version 1.0.0
 */
@TargetApi(19)
class HeadingManager implements IHeadingManager, SensorEventListener {

    private float[] accelerometerValues;
    private float[] magneticFieldValues;
    private double latestOrientation;
    private List<IHeadingObserver> observers;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private SensorManager sm;

    /**
     * Crea un nuovo oggetto HeadingManager associato a un contesto di
     * applicazione specificato.
     *
     * @param sensorManager Gestore dei sensori di sistema.
     */
    public HeadingManager(SensorManager sensorManager) throws
            SensorNotAvailableException {

        this.sm = sensorManager;
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null)
            throw new SensorNotAvailableException("accelerometer");
        if (magnetometer == null)
            throw new SensorNotAvailableException("magnetometer");

        observers = new ArrayList<IHeadingObserver>();
        latestOrientation = 0;
        magneticFieldValues = accelerometerValues = null;
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
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = sensorEvent.values;
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticFieldValues = sensorEvent.values;
        else
            return;

        AsyncTask<Void, Void, Double> t = new AsyncTask<Void, Void, Double>() {
            @Override
            protected Double doInBackground(Void... params) {
                onRawDataReceived(accelerometerValues, magneticFieldValues);
                return null;
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
     */
    @Override
    @TargetApi(19)
    public synchronized void attachObserver(final IHeadingObserver observer) {
        observers.add(observer);
        if (observers.size() == 1) {
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
    public synchronized void detachObserver(IHeadingObserver observer) {
        observers.remove(observer);

        if (observers.size() == 0)
            sm.unregisterListener(this);
    }

    /**
     * Implementazione di IHeadingManager.notifyObservers().
     */
    @Override
    public synchronized void notifyObservers() {
        for (IHeadingObserver o : observers)
            o.onHeadingUpdate(latestOrientation);
    }

    /**
     * Utilizza dati grezzi forniti da sensoristica o fonti esterne relativi
     * al campo magnetico e l'accelerazione del dispositivo per calcolare
     * l'orientamento del dispositivo, che viene comunicato agli observer
     * registrati all'istanza.
     *
     * @param accelerometerValues Valori di accelerazione del dispositivo
     *                            sugli assi x, y, z.
     * @param magnetometerValues Valori di campo magnetico del dispositivo
     *                           sugli assi x, y, z.
     */
    public void onRawDataReceived(float[] accelerometerValues, float[]
            magnetometerValues) {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magnetometerValues);
        SensorManager.getOrientation(R, values);

        latestOrientation = (float) Math.toDegrees(values[0]); // Azimuth
        notifyObservers();
    }

}
