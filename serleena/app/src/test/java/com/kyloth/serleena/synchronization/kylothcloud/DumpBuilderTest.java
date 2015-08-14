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


package com.kyloth.serleena.synchronization.kylothcloud;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.sqlite.IRasterSource;
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

import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class DumpBuilderTest {
    @Test
    public void dumpBuilderSanityTest() throws IOException {
        // TODO: Fare altri test
        SerleenaDatabase sh = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        SQLiteDatabase db = sh.getWritableDatabase();
        ContentValues values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        String JSON_OUTPUT = "{\"data\":[{\"experience\":\"Experience_1\"}]}";

        class Foo extends ByteArrayOutputStream implements JSONOutboundStream {
            Foo (int i) { super (i); }
        }

        Foo f = new Foo(4096);

        CloudJSONOutboundStreamBuilder b = new CloudJSONOutboundStreamBuilder();
        SerleenaSQLiteDataSource source = new SerleenaSQLiteDataSource(sh);
        for (IExperienceStorage exp : source.getExperiences()) {
            b.addExperience(exp);
        }

        b.stream(f);
        JsonParser parser = new JsonParser();

        try {
            String res = new String(f.toByteArray(),"UTF-8");
            JsonElement o1 = parser.parse(res);
            JsonElement o2 = parser.parse(JSON_OUTPUT);
            Assert.assertEquals(o1, o2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }
        String res = new String(f.toByteArray(),"UTF-8");
        JsonElement o1 = parser.parse(b.build());
        JsonElement o2 = parser.parse(JSON_OUTPUT);
        Assert.assertEquals(o1, o2);
    }
}
