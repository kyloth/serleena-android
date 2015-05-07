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

    /**
     * Crea un nuovo oggetto HeadingManager.
     *
     * @param context Oggetto Context in cui viene eseguito l'HeadingManager.
     */
    public HeadingManager(Context context) {
        observers = new HashMap<IHeadingObserver, ScheduledFuture>();
        latestOrientation = 0;
        this.context = context;
    }

    /**
     * Implementazione del pattern Singleton.
     *
     * @return Singola istanza della classe HeadingManager.
     */
    public static HeadingManager getInstance(Context context) {
        if (instance == null)
            instance = new HeadingManager(context);
        return instance;
    }

}
