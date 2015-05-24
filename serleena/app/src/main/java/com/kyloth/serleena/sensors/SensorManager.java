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
 * Name: SensorManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-21
 *
 * History:
 * Version  Programmer        Date        Changes
 * 1.0.0    Filippo Sestini   2015-05-21  Creazione file e scrittura
 *                                        codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;

import java.util.HashMap;

/**
 * Concretizza ISensorManager
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SensorManager implements ISensorManager {

    private static HashMap<Context, SensorManager> instances =
            new HashMap<Context, SensorManager>();

    private ILocationManager locMan;
    private IHeadingManager hMan;
    private IHeartRateManager hrMan;
    private ILocationReachedManager locReaMan;
    private IWakeupManager wMan;
    private ITelemetryManager telMan;

    public static SensorManager getInstance(Context context) {
        if (instances.get(context) == null)
            instances.put(context, new SensorManager(context));
        return instances.get(context);
    }

    /**
     * Crea un gestore della sensoristica a partire dal contesto specificato.
     *
     * @param context Contesto dell'applicazione.
     * @see Context
     */
    private SensorManager(Context context) {
        locMan = new SerleenaLocationManager(context);
        hrMan = new HeartRateManager(context);
        wMan = new WakeupManager(context);
        locReaMan = new LocationReachedManager(wMan, locMan);
        telMan = new TelemetryManager(locMan, hrMan, wMan, SerleenaPowerManager
                .getInstance(context));
        try {
            hMan = new HeadingManager(context);
        } catch (SensorNotAvailableException e) {
            hMan = null;
        }
    }

    /**
     * Implementa ISensorManager.getLocationSource().
     *
     * @return Oggetto ILocationManager.
     */
    @Override
    public ILocationManager getLocationSource() {
        return locMan;
    }

    /**
     * Implementa ISensorManager.getHeadingSource().
     *
     * @return Oggetto IHeadingManager.
     */
    @Override
    public IHeadingManager getHeadingSource()
            throws SensorNotAvailableException {
        if (hMan == null)
            throw new SensorNotAvailableException("Heading sensor not " +
                    "available");
        return hMan;
    }

    /**
     * Implementa ISensorManager.getHeartRateManager().
     *
     * @return Oggetto IHeartRateManager.
     */
    @Override
    public IHeartRateManager getHeartRateSource() {
        return hrMan;
    }

    /**
     * Implementa ISensorManager.getLocationReachedSource().
     *
     * @return Oggetto ILocationReachedManager.
     */
    @Override
    public ILocationReachedManager getLocationReachedSource() {
        return locReaMan;
    }

    /**
     * Implementa ISensorManager.getWakeupSource().
     *
     * @return Oggetto IWakeupManager.
     */
    @Override
    public IWakeupManager getWakeupSource() {
        return wMan;
    }

    /**
     * Implementa ISensorManager.getTelemetryManager().
     *
     * @return Oggetto ITelemetryManager.
     */
    @Override
    public ITelemetryManager getTelemetryManager() {
        return telMan;
    }

}
