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
 * Name: StreamBuilderTest.java
 * Package: com.kyloth.serleena.synchronization.kyloth.inbound;
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Tobia Tesan      Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.synchronization.kylothcloud;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;
import com.kyloth.serleena.synchronization.JSONOutboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStreamBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.mockito.Mockito.mock;

/**
 * Test di unità per classi StreamBuilder
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class StreamBuilderTest {

    /**
     * Verifica che oggetti CloudJSONOutboundStreamBuilder costruiscano
     * correttamente una codifica JSON del contenuto presente nel livello di
     * persistenza.
     */
    @Test
    public void streamBuilderSanityTest()
            throws IOException, UnsupportedEncodingException {
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        SQLiteDatabase db = sh.getWritableDatabase();
        ContentValues values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
        values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_USERPOINT_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_USER_POINTS, null, values);
        TestDB.telemetryQuery(db, 1, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_UUID);
        TestDB.checkPointEventQuery(db, 0, 123456, 1, 1);
        TestDB.checkPointEventQuery(db, 1, 654321, 2, 1);

        TestDB.telemetryQuery(db, 2, TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_UUID);
        TestDB.checkPointEventQuery(db, 2, 56789, 1, 2);
        TestDB.checkPointEventQuery(db, 3, 98765, 2, 2);


        String JSON_OUTPUT = "[{\"experience\":\"b989daae-9102-409b-abac-e428afe38baf\",\"userPoints\":[{\"latitude\":13.0,\"longitude\":73.0,\"name\":\"Punto personalizzato #0\"}],\"telemetryData\":[{\"events\":[123456000,654321000],\"track\":\"af024d00-e2d5-4fae-8bad-8b16f823a2cc\"}, {\"events\":[56789000,98765000],\"track\":\"af024d00-e2d5-4fae-8bad-8b16f823a2cc\"}]}]";

        class Foo extends ByteArrayOutputStream implements JSONOutboundStream {
            Foo (int i) { super (i); }
        }

        Foo f = new Foo(8192);

        CloudJSONOutboundStreamBuilder b = new CloudJSONOutboundStreamBuilder();
        SerleenaSQLiteDataSource source = new SerleenaSQLiteDataSource(sh);
        for (IExperienceStorage exp : source.getExperiences()) {
            b.addExperience(exp);
        }

        b.stream(f);
        JsonParser parser = new JsonParser();

        String res = new String(f.toByteArray(),"UTF-8");
        res = URLDecoder.decode(res,"UTF-8");
        JsonElement o1 = parser.parse(res);
        JsonElement o2 = parser.parse(JSON_OUTPUT);
        Assert.assertEquals(o1, o2);

        res = new String(f.toByteArray(),"UTF-8");
        o1 = parser.parse(b.build());
        o2 = parser.parse(JSON_OUTPUT);
        Assert.assertEquals(o1, o2);
    }
}
