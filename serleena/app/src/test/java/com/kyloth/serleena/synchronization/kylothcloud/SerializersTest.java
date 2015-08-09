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
 * Name: DataEntityStructSmokeTest.java
 * Package: com.kyloth.serleena.synchronization.kyloth.inbound;
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Tobia Tesan      Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.synchronization.kylothcloud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.ArrayList;


/**
 * Smoke test per verificare che i JSON prodotti da Kyloth mappino
 * correttamente sulle struct definite attraverso GSON.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerializersTest {
    final static String SAMPLES_DIR = "../../common/samples/";
    final static float GEO_TOLERANCE = 0.00001f;
    @Test
    public void trackEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(TrackEntity.class, new TrackEntityDeserializer()).create();

        BufferedReader r;
        FileReader in = new FileReader(SAMPLES_DIR + "partial/track.json");
        BufferedReader br = new BufferedReader(in);
        TrackEntity e = gson.fromJson(br, TrackEntity.class);
        Assert.assertEquals(e.name, "Track_1");
        Assert.assertNotEquals(e.name, "asdfasdfasdf");
        Assert.assertEquals(e.checkpoints.size(), 2);
        Iterator<CheckpointEntity> i = e.checkpoints.iterator();
        Assert.assertTrue(i.hasNext());
        CheckpointEntity c = i.next();
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals(c.id, 0);
        Assert.assertEquals(c.point.latitude(), new GeoPoint(45.279032, 11.655213).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(c.point.longitude(), new GeoPoint(45.279032, 11.655213).longitude(), GEO_TOLERANCE);
        TelemetryEntity t = e.telemetries.iterator().next();
        Iterator<Long> it = t.events.iterator();
        long ee = it.next();
        Assert.assertEquals(ee, 1437505820522L);
        ee = it.next();
        Assert.assertEquals(ee, 1438441982647L);
    }

    @Test
    public void weatherForecastSmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(WeatherDataEntity.class, new WeatherEntityDeserializer()).create();
        BufferedReader r;
        FileReader in = new FileReader(SAMPLES_DIR + "partial/weatherforecast.json");
        BufferedReader br = new BufferedReader(in);
        WeatherDataEntity e = gson.fromJson(br, WeatherDataEntity.class);

        Assert.assertEquals(e.morning.forecast, WeatherForecastEnum.Stormy);
        Assert.assertNotEquals(e.morning.forecast, WeatherForecastEnum.Rainy);
        Assert.assertEquals(e.afternoon.forecast, WeatherForecastEnum.Cloudy);
        Assert.assertNotEquals(e.afternoon.forecast, WeatherForecastEnum.Rainy);
        Assert.assertEquals(e.night.forecast, WeatherForecastEnum.Sunny);
        Assert.assertNotEquals(e.night.forecast, WeatherForecastEnum.Rainy);
        Assert.assertEquals(e.boundingRect.getNorthWestPoint().latitude(), new GeoPoint(45.276257, 11.654297).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.boundingRect.getNorthWestPoint().longitude(), new GeoPoint(45.276257, 11.654297).longitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.boundingRect.getSouthEastPoint().latitude(), new GeoPoint(45.146557, 11.954498).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.boundingRect.getSouthEastPoint().longitude(), new GeoPoint(45.146557, 11.954498).longitude(), GEO_TOLERANCE);
        Assert.assertNotEquals(e.boundingRect.getSouthEastPoint(), new GeoPoint(12, -34));
        Assert.assertEquals(e.morning.temperature, -2, 0.001);
        Assert.assertEquals(e.afternoon.temperature, 0, 0.001);
        Assert.assertEquals(e.night.temperature, 2, 0.001);
        Assert.assertEquals(e.date, 1437436800000L);
    }

    @Test
    public void emergencyContactSmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(EmergencyDataEntity.class, new EmergencyDataDeserializer()).create();
        BufferedReader r;
        FileReader in = new FileReader(SAMPLES_DIR + "partial/emergencycontact.json");
        BufferedReader br = new BufferedReader(in);
        EmergencyDataEntity e = gson.fromJson(br, EmergencyDataEntity.class);

        Assert.assertEquals(e.rect.getNorthWestPoint(), new GeoPoint(45.276257, 11.654297));
        Assert.assertEquals(e.rect.getSouthEastPoint(), new GeoPoint(45.146557, 11.954498));
        Assert.assertNotEquals(e.rect.getNorthWestPoint(), new GeoPoint(67, 15.44));
        Assert.assertEquals(e.name, "Emergency_1");
        Assert.assertEquals(e.number, "800977354");
    }

    @Test
    public void checkpointEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(CheckpointEntity.class, new CheckpointEntityDeserializer()).create();
        FileReader in = new FileReader(SAMPLES_DIR + "partial/checkpoint.json");
        BufferedReader br = new BufferedReader(in);
        CheckpointEntity e = gson.fromJson(br, CheckpointEntity.class);

        Assert.assertEquals(e.point, new GeoPoint(2.0, 2.0));
        Assert.assertEquals(e.id, 0);
    }

    @Test
    public void experienceEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(ExperienceEntity.class, new ExperienceEntityDeserializer()).create();
        FileReader in = new FileReader(SAMPLES_DIR + "partial/experience.json");
        BufferedReader br = new BufferedReader(in);
        ExperienceEntity e = gson.fromJson(br, ExperienceEntity.class);

        Assert.assertEquals(e.name, "Experience_1");
        Assert.assertEquals(e.region.getNorthWestPoint().latitude(), new GeoPoint(45.276257, 11.654297).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.region.getNorthWestPoint().longitude(), new GeoPoint(45.276257, 11.654297).longitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.region.getSouthEastPoint().latitude(), new GeoPoint(45.146557, 11.954498).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(e.region.getSouthEastPoint().longitude(), new GeoPoint(45.146557, 11.954498).longitude(), GEO_TOLERANCE);
        Iterator<UserPointEntity> i_up = e.userPoints.iterator();
        UserPointEntity up = i_up.next();
        Assert.assertEquals(up.point.latitude(), new GeoPoint(45.277573, 11.654908).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(up.point.longitude(), new GeoPoint(45.277573, 11.654908).longitude(), GEO_TOLERANCE);
        up = i_up.next();
        Assert.assertEquals(up.point.latitude(), new GeoPoint(45.276413, 11.65555).latitude(), GEO_TOLERANCE);
        Assert.assertEquals(up.point.longitude(), new GeoPoint(45.276413, 11.65555).longitude(), GEO_TOLERANCE);
        Iterator<TrackEntity> i_t = e.tracks.iterator();
        Assert.assertEquals(i_t.next().name, "Track_1");
    }

    @Test
    public void telemetryEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(TelemetryEntity.class, new TelemetryEntityDeserializer()).create();
        FileReader in = new FileReader(SAMPLES_DIR + "partial/telemetry.json");
        BufferedReader br = new BufferedReader(in);
        TelemetryEntity e = gson.fromJson(br, TelemetryEntity.class);

        Iterator<Long> i_e = e.events.iterator();
        long ee = i_e.next();
        Assert.assertEquals(ee, 1437505820522L);
        ee = i_e.next();
        Assert.assertEquals(ee, 1438441982647L);
    }

    @Test
    public void rootEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(InboundRootEntity.class, new RootEntityDeserializer()).create();
        FileReader in = new FileReader(SAMPLES_DIR + "kylothCloud-get-data.js");
        BufferedReader br = new BufferedReader(in);
        InboundRootEntity e = gson.fromJson(br, InboundRootEntity.class);
        Assert.assertEquals(e.experiences.size(), 1);
    }

    @Test
    public void userPointEntitySmokeTest() throws FileNotFoundException {
        Gson gson = new GsonBuilder().registerTypeAdapter(UserPointEntity.class, new UserPointDeserializer()).create();
        FileReader in = new FileReader(SAMPLES_DIR + "partial/userPoint.json");
        BufferedReader br = new BufferedReader(in);
        UserPointEntity up = gson.fromJson(br, UserPointEntity.class);
        Assert.assertEquals(up.point, new GeoPoint(4.0, 4.0));
        Assert.assertEquals(up.name, "UP1");
    }

    @Test
    public void userPointEntitySerializerTest() {
        Gson gsonSerializer = new GsonBuilder().registerTypeAdapter(UserPointEntity.class, new UserPointSerializer()).create();
        Gson gsonDeserializer = new GsonBuilder().registerTypeAdapter(UserPointEntity.class, new UserPointDeserializer()).create();
        UserPointEntity up = new UserPointEntity();
        up.point = new GeoPoint(66.6, 6.66);
        up.name = "UP_1";
        String userPoint = gsonSerializer.toJson(up, UserPointEntity.class);
        UserPointEntity upD = gsonDeserializer.fromJson(userPoint, UserPointEntity.class);
        Assert.assertEquals(up.point, upD.point);
        Assert.assertEquals(up.name, upD.name);
    }

    @Test
    public void telemetryEntitySerializerTest() {
        Gson gsonSerializer = new GsonBuilder().registerTypeAdapter(TelemetryEntity.class, new TelemetryEntitySerializer()).create();
        Gson gsonDeserializer = new GsonBuilder().registerTypeAdapter(TelemetryEntity.class, new TelemetryEntityDeserializer()).create();
        TelemetryEntity t = new TelemetryEntity();
        t.events = new ArrayList<Long>();
        Long e = new Long(66);
        t.events.add(e);
        String telemetry = gsonSerializer.toJson(t, TelemetryEntity.class);
        TelemetryEntity tD = gsonDeserializer.fromJson(telemetry, TelemetryEntity.class);
        Long eD = tD.events.iterator().next();
        Assert.assertEquals(e, eD);
    }

    @Test
    public void outboundRootSerializerTest() {
        Gson gsonSerializer = new GsonBuilder().registerTypeAdapter(OutboundRootEntity.class, new OutboundRootSerializer()).create();
        Gson gsonDeserializer = new GsonBuilder().registerTypeAdapter(OutboundRootEntity.class, new OutboundRootDeserializer()).create();
        OutboundRootEntity ore = new OutboundRootEntity();
        ore.data = new ArrayList<OutboundDataEntity>();
        OutboundDataEntity ode = new OutboundDataEntity();
        ode.experience = "ExperienceOutbound";
        ode.userPoints = new ArrayList<UserPointEntity>();
        UserPointEntity up = new UserPointEntity();
        up.point = new GeoPoint(6.66, 66.6);
        up.name = "UP";
        ode.userPoints.add(up);
        ode.telemetryData = new ArrayList<TelemetryEntity>();
        TelemetryEntity t = new TelemetryEntity();
        t.events = new ArrayList<Long>();
        Long e = new Long(66);
        t.events.add(e);
        ode.telemetryData.add(t);
        ore.data.add(ode);
        String root = gsonSerializer.toJson(ore, OutboundRootEntity.class);
        OutboundRootEntity oreD = gsonDeserializer.fromJson(root, OutboundRootEntity.class);
        OutboundDataEntity odeD = oreD.data.iterator().next();
        Assert.assertEquals(ode.experience, odeD.experience);
        UserPointEntity upD = odeD.userPoints.iterator().next();
        Assert.assertEquals(up.name, upD.name);
        Assert.assertEquals(up.point, upD.point);
        TelemetryEntity tD = odeD.telemetryData.iterator().next();
        Long eD = tD.events.iterator().next();
        Assert.assertEquals(e, eD);
    }
}
