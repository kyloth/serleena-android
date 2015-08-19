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


package com.kyloth.serleena.synchronization.kylothcloud.inbound;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Region;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IWeatherStorage;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSink;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.synchronization.InboundDump;
import com.kyloth.serleena.synchronization.kylothcloud.EmergencyDataEntity;
import com.kyloth.serleena.synchronization.kylothcloud.ExperienceEntity;
import com.kyloth.serleena.synchronization.kylothcloud.InboundRootEntity;
import com.kyloth.serleena.synchronization.kylothcloud.WeatherDataEntity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import static org.mockito.Mockito.mock;

/**
 * Verifica che sia possibile passare delle strutture al dump builder e richiamarle
 * integre dal database in un secondo tempo.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class DumpBuilderIntegrationTest {
    SerleenaSQLiteDataSink sink;
    SerleenaSQLiteDataSource src;

    @Test
    public void testBuildEmergencyContacts() {
        InboundRootEntity r = new InboundRootEntity();
        EmergencyDataEntity ede = new EmergencyDataEntity();
        ede.name = "FOO";
        ede.number = "123456789";
        ede.rect = new Region(
                new GeoPoint(3,2),
                new GeoPoint(1,4)
        );
        r.emergencyData = new LinkedList<EmergencyDataEntity>();
        r.emergencyData.add(ede);
        CloudSerleenaSQLiteInboundDumpBuilder b = new CloudSerleenaSQLiteInboundDumpBuilder(r);
        InboundDump d = b.build();
        Assert.assertTrue(d.toString().length() > 0);
        sink.load(d);
        DirectAccessList<EmergencyContact> a = src.getContacts(new GeoPoint(2, 3));
        Assert.assertEquals(a.size(), 1);
        Assert.assertEquals(a.get(0).value(), "123456789");
    }

    @Test
    public void testBuildExperiences() {
        InboundRootEntity r = new InboundRootEntity();
        ExperienceEntity e = new ExperienceEntity();
        e.uuid = UUID.randomUUID();
        e.name = "FOO";
        e.region = new Region(
                new GeoPoint(3,2),
                new GeoPoint(1,4)
        );
        r.experiences.add(e);
        CloudSerleenaSQLiteInboundDumpBuilder b = new CloudSerleenaSQLiteInboundDumpBuilder(r);
        InboundDump d = b.build();
        Assert.assertTrue(d.toString().length() > 0);
        sink.load(d);
        Iterable<IExperienceStorage> exp = src.getExperiences();
        int i = 0;
        for (IExperienceStorage n : exp) {
            i++;
        }
        Assert.assertEquals(i, 1);
    }
    
    @Test
    public void testBuildWeatherData() throws NoSuchWeatherForecastException {
        InboundRootEntity r = new InboundRootEntity();
        WeatherDataEntity w = new WeatherDataEntity();
        w.morning.forecast = WeatherForecastEnum.Stormy;
        w.morning.temperature = -2;
        w.afternoon.forecast = WeatherForecastEnum.Cloudy;
        w.afternoon.temperature = 0;
        w.night.forecast = WeatherForecastEnum.Sunny;
        w.night.temperature = 2;
        w.boundingRect = new Region(new GeoPoint(2, -20), new GeoPoint(-2, 20));
        GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        date.set(Calendar.YEAR, 2015);
        date.set(Calendar.MONTH, Calendar.JANUARY);
        date.set(Calendar.DAY_OF_MONTH, 10);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        w.date = date.getTimeInMillis();
        r.weatherData.add(w);
        CloudSerleenaSQLiteInboundDumpBuilder b = new CloudSerleenaSQLiteInboundDumpBuilder(r);
        InboundDump d = b.build();
        Assert.assertTrue(d.toString().length() > 0);
        sink.load(d);
        IWeatherStorage wea = src.getWeatherInfo(new GeoPoint(0, 0), new Date(date.getTimeInMillis()));
        Assert.assertEquals(wea.date(), new Date(date.getTimeInMillis()));
    }


    @Before
    public void setup() throws URISyntaxException {
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, null, null, 1);
        sink = new SerleenaSQLiteDataSink(RuntimeEnvironment.application, sh);
        src =  new SerleenaSQLiteDataSource(sh);
    }
}
