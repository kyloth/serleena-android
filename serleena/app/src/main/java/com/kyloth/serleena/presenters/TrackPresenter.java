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
 * Name: TrackPresenter.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file e scrittura del codice e di
 *                              Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.annotation.TargetApi;
import android.os.AsyncTask;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.IHeadingObserver;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;
import com.kyloth.serleena.sensors.ILocationReachedManager;
import com.kyloth.serleena.sensors.ILocationReachedObserver;
import com.kyloth.serleena.sensors.ITelemetryManager;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Concretizza ITrackPresenter.
 *
 * Gestisce l'esecuzione del Percorso attivo, monitorando
 * l'attraversamento dei checkpoint, impostando il Tracciamento e mostrando
 * informazioni utili sulla vista associata.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TrackPresenter implements ITrackPresenter,
        ILocationReachedObserver, ILocationObserver, IHeadingObserver {

    private static int UPDATE_INTERVAL_SECONDS = 60;

    private ITrackView view;
    private ISerleenaActivity activity;

    private ITrack activeTrack;
    private int checkpointToReach;
    private boolean telemetry;
    private long trackStartFullTime;

    private ITelemetryManager telMan;
    private ILocationManager locMan;
    private ILocationReachedManager lrMan;
    private IHeadingManager hMan;

}
