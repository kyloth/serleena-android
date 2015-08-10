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
 * Name: SerleenaJSONNetProxyNotAuthorizedTest.java
 * Package: com.kyloth.serleena.synchronization.net
 * Author: Matteo Lisotto
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Matteo Lisotto    Creazione file
 */

package com.kyloth.serleena.synchronization.net;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.synchronization.AuthException;
import com.kyloth.serleena.synchronization.kylothcloud.IKylothIdSource;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaJSONNetProxyNotAuthorizedTest {

    private SerleenaJSONNetProxy proxy;
    private SerleenaConnectionFactory factory = mock(SerleenaConnectionFactory.class);
    private HttpURLConnection urlConnection = mock(HttpURLConnection.class);
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


    private void initializeMocks () throws IOException {
        String auth = "Rush";
        when(factory.createURLConnection(any(URL.class))).thenReturn(urlConnection);
        when(urlConnection.getOutputStream()).thenReturn(outputStream);
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(urlConnection.getContentType()).thenReturn("text/plain");
        when(urlConnection.getInputStream()).thenReturn(new ByteArrayInputStream(auth.getBytes()));
    }

    public SerleenaJSONNetProxyNotAuthorizedTest() {
        try {
            initializeMocks();
        } catch (IOException e) {}
    }

    @Before
    public void initialize() throws MalformedURLException {
        IKylothIdSource entry = new IKylothIdSource() {
            @Override
            public String getKylothId() {
                return "Geddy";
            }
        };
        proxy = new SerleenaJSONNetProxy(new URL("http://localhost"), entry, factory);
    }

    @Test
    public void preAuthTest() throws AuthException, IOException {
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, "Rush");
    }

    @Test
    public void authTest() throws AuthException, IOException {
        proxy.auth();
    }

    @Test
    public void sendTest() throws AuthException, IOException {
        proxy.send();
        String response = outputStream.toString();
        assertEquals(response, "Data=");
    }
}