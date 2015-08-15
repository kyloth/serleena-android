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


package com.kyloth.serleena.synchronization;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSink;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.TestFixtures;
import com.kyloth.serleena.synchronization.net.SerleenaJSONNetProxy;
import com.kyloth.serleena.synchronization.kylothcloud.CheckpointEntity;
import com.kyloth.serleena.synchronization.kylothcloud.IKylothIdSource;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


/**
 * Crea un server HTTP fittizio per verificare che lo stack di gestione del protocollo
 * funzioni correttamente.
 */
@RunWith(RobolectricTestRunner.class)
public class DummyServerIntegrationTest {

    final static String SAMPLES_DIR = "../../common/samples/";
    final String AUTH_TOKEN_NAME = "X-AuthToken";
    final String DATA_TOKEN_NAME = "data";
    String JSON_OUTPUT;

    SerleenaDatabase sh;
    SQLiteDatabase db;
    SerleenaSQLiteDataSource source;
    SerleenaSQLiteDataSink sink;
    Server server;
    URL url;

    private static void copyInputStream( InputStreamReader in, PrintWriter out )      throws IOException
    {
        char[] buffer=new char[1024];
        int len;
        while ( ( len=in.read(buffer) ) >= 0 )
        {
            out.write(buffer, 0, len);
        }
    }

    /**
     * Prova ad eseguire una sincronizzazione completa verso il server dummy
     * che i sample JSON come risposta.
     *
     * @throws Exception
     */
    @Test(timeout=5000)
    public void syncSanityTest() throws Exception {
        // TODO: Fare altri test, in particolare gli edge case
        FileReader in = new FileReader(SAMPLES_DIR + "partial/track.json");
        BufferedReader r = new BufferedReader(in);
        final Pattern preauthP = Pattern.compile("/tokens/([\\w|::]+)");
        final Pattern authP = Pattern.compile("/users/pair/([\\w|::]+)");
        final Pattern syncP = Pattern.compile("/data/([\\w|::]*)");
        final String kylothId = "foo";
        final String PREAUTH_TOKEN = "123456";
        final String AUTH_TOKEN = "654321";
        final String PREAUTH_FAIL = "NACK";
        final String AUTH_FAIL = "NACK";

        IKylothIdSource iKylothIdSource = new IKylothIdSource() {
            @Override
            public String getKylothId() {
                return kylothId;
            }
        };
        AbstractHandler h = new AbstractHandler() {

            public int counter = 0;

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                String path = baseRequest.getRequestURI();
                Matcher preauthM = preauthP.matcher(path);
                Matcher authM = authP.matcher(path);
                // TODO: La ST dice che e' /data, Bronsa dice che e' diverso
                Matcher syncM = syncP.matcher(path);
                // TODO: Controllare bene regexp con bronsa
                if (preauthM.matches()) {
                    response.setContentType("text/plain; charset=utf-8");
                    assertEquals(preauthM.group(1), kylothId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    out.println(PREAUTH_TOKEN);
                    assertEquals(counter, 0);
                    counter++;
                    baseRequest.setHandled(true);
                } else if (authM.matches()) {
                    if (request.getMethod() == "GET") {
                        response.setContentType("text/plain; charset=utf-8");
                        assertEquals(authM.group(1), PREAUTH_TOKEN);
                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter out = response.getWriter();
                        out.println(AUTH_TOKEN);
                        assertEquals(counter, 1);
                        counter++;
                        baseRequest.setHandled(true);
                    } else {
                        assertTrue(false);
                    }
                } else if (syncM.matches()) {
                    if (request.getMethod() == "POST") {
                        response.setContentType("text/json; charset=utf-8");
                        response.setStatus(HttpServletResponse.SC_OK);
                        assertEquals(baseRequest.getHeader(AUTH_TOKEN_NAME), AUTH_TOKEN);
                        String res = request.getParameter(DATA_TOKEN_NAME);
                        JsonParser parser = new JsonParser();
                        JsonElement o1 = parser.parse(res);
                        JsonElement o2 = parser.parse(JSON_OUTPUT);
                        assertEquals(o1, o2);
                        baseRequest.setHandled(true);
                    } else if (request.getMethod() == "GET") {
                        FileReader in = new FileReader(SAMPLES_DIR + "kylothCloud-get-data.js");
                        copyInputStream(in, response.getWriter());
                        baseRequest.setHandled(true);
                    }
                } else {
                    throw new RuntimeException(path);
                }
            }
        };

        KylothCloudSynchronizer.__reset();
        KylothCloudSynchronizer s = KylothCloudSynchronizer.getInstance(
                new SerleenaJSONNetProxy(iKylothIdSource, url),
                sink, source);
        server.setHandler(h);
        server.start();
        String token = s.preAuth();
        assertEquals(token, PREAUTH_TOKEN);
        s.auth();
        s.sync();
        Iterable<IExperienceStorage> a = source.getExperiences();
        Iterator<IExperienceStorage> it = a.iterator();
        int i = 0;
        while(it.hasNext()) { it.next(); i++;}
        assertTrue(i > 0);

    }

    @Before
    public void setup() throws Exception {
        server = new Server(8081);
        sh = new SerleenaDatabase(RuntimeEnvironment.application, "sample.db", null, 1);
        db = sh.getWritableDatabase();
        ContentValues values = TestFixtures.pack(TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1);
        db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
        JSON_OUTPUT = "{\"data\":[{\"experience\":\"Experience_1\"}]}";
        url = new URL("http://localhost:8081/");
        sink = new SerleenaSQLiteDataSink(RuntimeEnvironment.application, sh);
        source = new SerleenaSQLiteDataSource(sh);

    }

    @After
    public void teardown() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


