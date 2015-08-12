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


package com.kyloth.serleena;

import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.sqlite.IRasterSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class PersistenceModelIntegrationTest {

    SQLiteDatabase db;
    SerleenaDataSource dataSource;

    private String getExperienceQuery(int id, String name) {
    return "INSERT INTO experiences (experience_id, experience_name) VALUES (" +
        String.valueOf(id) + ", '" +  name + "')";
    }

    private String getTrackQuery(int id, String name, int experience) {
        return "INSERT INTO tracks (track_id, track_name, track_experience) " +
            "VALUES (" + String.valueOf(id) + ", '" + name + "', " + 
            String.valueOf(experience) + ")";
    }

    private String getTelemetryQuery(int id, int track) {
        return "INSERT INTO telemetries (telem_id, telem_track) VALUES (" +
            String.valueOf(id) + ", " + String.valueOf(track) + ")";
    }

    private String getCheckpointTelemetryEventQuery(int id, int timestamp,
                                                    int telemetry, int checkp) {
        return "INSERT INTO telemetry_events_checkp (eventc_id, " +
            "eventc_timestamp, eventc_value, eventc_telem) " +
            "VALUES (" + String.valueOf(id) + ", " + String.valueOf(timestamp) +
            ", " + String.valueOf(checkp) + ", " +
            String.valueOf(telemetry) + ")";
    }

    private String getCheckpointQuery(int id, int num, double lat, double lon,
                                     int track) {
        return "INSERT INTO checkpoints (checkpoint_id, checkpoint_num, " + 
            "checkpoint_latitude, checkpoint_longitude, checkpoint_track) " +
            "VALUES (" + String.valueOf(id) + ", " + String.valueOf(num) + ", " +
            String.valueOf(lat) + ", " + String.valueOf(lon) + ", "
            + String.valueOf(track) + ")";
    }

    private String getUserPointQuery(int id, double lat, double lon,
                                    int experience) {
        return "INSERT INTO user_points (userpoint_id, userpoint_x, " +
                "userpoint_y, " +
            "userpoint_experience) VALUES (" + String.valueOf(id) + ", " +
            String.valueOf(lat) + ", " + String.valueOf(lon) + ", " + 
            String.valueOf(experience) + ")";
    }

    private <T> boolean containsEquals(Iterable<T> elements, T element) {
        for (T t : elements)
            if (t.equals(element))
                return true;
        return false;
    }

    @Before
    public void initialize() {
        SerleenaDatabase serleenaDb = new SerleenaDatabase(
                RuntimeEnvironment.application, "sample.db", null, 1);
        db = serleenaDb.getWritableDatabase();
        serleenaDb.onConfigure(db);
        serleenaDb.onUpgrade(db, 1, 2);

        SerleenaSQLiteDataSource sqlDataSource = new SerleenaSQLiteDataSource(
                RuntimeEnvironment.application,
                serleenaDb,
                mock(IRasterSource.class));
        dataSource = new SerleenaDataSource(sqlDataSource);
    }


    @Test
    public void experienceShouldReturnHerName() {
        db.execSQL(getExperienceQuery(1, "myExperience"));

        Iterator<IExperience> iterator = dataSource.getExperiences().iterator();
        assertEquals("myExperience", iterator.next().getName());
    }

    @Test
    public void experienceShouldReturnItsUserPoints() {
        db.execSQL(getExperienceQuery(1, "myExperience"));
        db.execSQL(getUserPointQuery(1, 3, 4, 1));
        db.execSQL(getUserPointQuery(2, 5, 7, 1));

        assertTrue(containsEquals(
                dataSource.getExperiences().iterator().next().getUserPoints(),
                new UserPoint(3, 4)));
        assertTrue(containsEquals(
                dataSource.getExperiences().iterator().next().getUserPoints(),
                new UserPoint(5, 7)));
    }

    @Test
    public void userPointShouldBeAddedCorrectly() {
        db.execSQL(getExperienceQuery(1, "myExperience"));
        IExperience experience = dataSource.getExperiences().iterator().next();
        assertTrue(!experience.getUserPoints().iterator().hasNext());

        experience.addUserPoints(new UserPoint(3, 4));
        assertTrue(containsEquals(experience.getUserPoints(),
                new UserPoint(3, 4)));
    }

    @Test
    public void trackShouldReturnItsEvents() {
        db.execSQL(getExperienceQuery(1, "myExperience"));
        db.execSQL(getTrackQuery(1, "myTrack", 1));
        db.execSQL(getTelemetryQuery(1, 1));
        db.execSQL(getCheckpointTelemetryEventQuery(1, 300, 1, 0));
        db.execSQL(getCheckpointTelemetryEventQuery(2, 300, 1, 1));

        IExperience experience = dataSource.getExperiences().iterator().next();
        ITrack track = experience.getTracks().iterator().next();
        ITelemetry telemetry = track.getTelemetries().iterator().next();

        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 0)));
        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 0)));
    }

    @Test
    public void createTelemetryShouldWorkCorrectly() {
        db.execSQL(getExperienceQuery(1, "myExperience"));
        db.execSQL(getTrackQuery(1, "myTrack", 1));

        IExperience experience = dataSource.getExperiences().iterator().next();
        ITrack track = experience.getTracks().iterator().next();

        List<TelemetryEvent> list = new ArrayList<>();
        list.add(new CheckpointReachedTelemetryEvent(300, 0));
        list.add(new CheckpointReachedTelemetryEvent(300, 1));

        track.createTelemetry(list);

        ITelemetry telemetry = track.getTelemetries().iterator().next();

        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 0)));
        assertTrue(containsEquals(telemetry.getEvents(),
                new CheckpointReachedTelemetryEvent(300, 1)));
    }

}
