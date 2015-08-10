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
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaJSONNetProxyAuthorizedTest {

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

    public SerleenaJSONNetProxyAuthorizedTest() {
        try {
            initializeMocks();
        } catch (IOException e) {}
    }

    @Before
    public void initialize() throws IOException, AuthException {
        IKylothIdSource entry = new IKylothIdSource() {
            @Override
            public String getKylothId() {
                return "Geddy";
            }
        };
        proxy = new SerleenaJSONNetProxy(new URL("http://localhost"), entry, factory);
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, "Rush");
        proxy.auth();
    }

    /**
     * Verifica che i dati vengano scritti correttamente nell'outputstream da send();
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test
    public void testSendOk() throws AuthException, IOException {
        CloudJSONOutboundStream s = proxy.send();
        s.write('A');
        s.write('B');
        s.write('C');
        s.flush();
        s.close();
        String response = outputStream.toString();
        assertEquals(response, "Data=ABC");
        assertTrue(proxy.success());
        proxy.disconnect();
    }

    /**
     * Verifica che uno status 403 risulti in una AuthException nel chiamare success() dopo send
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = AuthException.class)
    public void testSendAuthExceptionOn403() throws AuthException, IOException {
        CloudJSONOutboundStream s = proxy.send();
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        proxy.success();
        proxy.disconnect();
    }

    /**
     * Verifica che uno status 500 risulti in una IOException nel chiamare success() dopo send
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void testSendIOExceptionOn500() throws AuthException, IOException {
        CloudJSONOutboundStream s = proxy.send();
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        proxy.success();
        proxy.disconnect();
    }

    /**
     * Verifica che uno status 403 risulti in una AuthException nel chiamare success() dopo get
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = AuthException.class)
    public void testGetAuthExceptionOn403() throws AuthException, IOException {
        proxy.get();
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        proxy.success();
        proxy.disconnect();
    }

    /**
     * Verifica che uno status 500 risulti in una IOException nel chiamare success() dopo get
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void testGetIOExceptionOn500() throws AuthException, IOException {
        proxy.get();
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        proxy.success();
        proxy.disconnect();
    }

    /**
     * Controlla che chiamare get o send senza disconnect() dopo una send risulti in una RuntimeException
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected=RuntimeException.class)
    public void testMustDisconnectAfterSend() throws AuthException, IOException {
        proxy.send();
        proxy.get();
    }

    /**
     * Controlla che chiamare get o send senza disconnect() dopo una get risulti in una RuntimeException
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected=RuntimeException.class)
    public void testMustDisconnectAfterGet() throws AuthException, IOException {
        proxy.send();
        proxy.get();
    }
}