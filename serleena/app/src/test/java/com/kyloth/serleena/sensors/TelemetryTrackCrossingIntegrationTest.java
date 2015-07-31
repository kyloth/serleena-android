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
 * Name: TelemetryTrackCrossingIntegrationTest.java
 * Package: com.hitchikers.serleena
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file
 */

package com.kyloth.serleena.sensors;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Iterator;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Test di integrazione tra le classi TelemetryManager e TrackCrossing, che
 * verifica i risultati delle interazioni tra le due unità.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class TelemetryTrackCrossingIntegrationTest {

    ISerleenaDataSource dataSource;
    TrackCrossing tc;
    TelemetryManager tm;
    ITrack track;
    LocationReachedManager lrm;

    @Before
    public void initialize() {
        SerleenaDatabase serleenaDb = TestDB.getEmptyDatabase();
        SQLiteDatabase db = serleenaDb.getWritableDatabase();
        TestDB.experienceQuery(db, 1, "experience");
        TestDB.trackQuery(db, 1, "track", 1);
        TestDB.checkpointQuery(db, 1, 1, 1, 1, 1);
        TestDB.checkpointQuery(db, 2, 2, 2, 2, 1);

        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(
                        RuntimeEnvironment.application,
                        serleenaDb));
        track = dataSource.getExperiences().iterator()
                .next().getTracks().iterator().next();

        Application app = RuntimeEnvironment.application;
        lrm = new LocationReachedManager(mock(BackgroundLocationManager.class));
        tc = new TrackCrossing(lrm);
        tm = new TelemetryManager(mock(BackgroundLocationManager.class), tc);
    }

    /**
     * Verifica che l'interazione generi correttamente il Tracciamento
     * relativo a uno specifico Percorso.
     */
    @Test
    public void telemetryWithTrackCrossingShouldBeCreatedCorrectly() throws
            TrackAlreadyStartedException, NoSuchTelemetryEventException {
        tm.enable();
        tc.startTrack(track);

        lrm.onLocationUpdate(new GeoPoint(1, 1));
        lrm.onLocationUpdate(new GeoPoint(2, 2));

        await().until(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return track.getTelemetries().iterator().hasNext();
            }
        });

        ITelemetry telemetry = track.getTelemetries().iterator().next();
        Iterator<TelemetryEvent> iterator = telemetry.getEvents().iterator();
        CheckpointReachedTelemetryEvent e1 =
                (CheckpointReachedTelemetryEvent) iterator.next();
        CheckpointReachedTelemetryEvent e2 =
                (CheckpointReachedTelemetryEvent) iterator.next();
        assertTrue((e1.checkpointNumber() == 0 && e2.checkpointNumber() == 1) ||
                   (e1.checkpointNumber() == 1 && e2.checkpointNumber() == 0));
    }

}
