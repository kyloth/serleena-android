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
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaJSONNetProxyAuthorizedTest {

    private SerleenaJSONNetProxy proxy;
    private SerleenaConnectionFactory factory = mock(SerleenaConnectionFactory.class);

    final String PREAUTH_TOKEN = "Foo";
    final String KYLOTH_ID = "Bar";
    final String IN_DATA = "IN DATA";
    final String OUT_DATA = "OUT DATA";

    final URL BASE_URL;
    {URL BASE_URL1;
        try {
            BASE_URL1 = new URL("http://localhost/");
        } catch (MalformedURLException e) {
            BASE_URL1 = null;
        }
        BASE_URL = BASE_URL1;
    }

    final URL DATA_URL;

    {URL DATA_URL;
        try {
            DATA_URL = new URL(BASE_URL + "data/");
            // Lo dice la ST
        } catch (MalformedURLException e) {
            DATA_URL = null;
            e.printStackTrace();
        }
        this.DATA_URL = DATA_URL;
    }

    private HttpURLConnection urlConnection = mock(HttpURLConnection.class);
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private HttpURLConnection urlConnectionData = mock(HttpURLConnection.class);
    private ByteArrayOutputStream outputStreamData = new ByteArrayOutputStream();


    private void initializeMocks() throws IOException {
        when(factory.createURLConnection(any(URL.class))).thenReturn(urlConnection);
        when(factory.createURLConnection(eq(DATA_URL))).thenReturn(urlConnectionData);

        when(urlConnection.getOutputStream()).thenReturn(outputStream);
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(urlConnection.getContentType()).thenReturn("text/plain");
        when(urlConnection.getInputStream()).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Object mock = invocation.getMock();
                        return new ByteArrayInputStream(PREAUTH_TOKEN.getBytes());
                    }
                });

        when(urlConnectionData.getOutputStream()).thenReturn(outputStreamData);
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(urlConnectionData.getContentType()).thenReturn("application/json");
        when(urlConnectionData.getInputStream()).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Object mock = invocation.getMock();
                        return new ByteArrayInputStream(IN_DATA.getBytes());
                    }
                });
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
                return KYLOTH_ID;
            }
        };
        proxy = new SerleenaJSONNetProxy(BASE_URL, entry, factory);
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, PREAUTH_TOKEN);
        proxy.auth();
    }

    /**
     * Verifica che i dati vengano scritti correttamente nell'outputstream da send();
     *
     * @throws AuthException
     * @throws IOException
     */
    @Test
    public void testSendOk() throws AuthException, IOException, NotConnectedException {
        CloudJSONOutboundStream s = proxy.send();
        s.write('A');
        s.write('B');
        s.write('C');
        s.flush();
        s.close();
        String response = outputStreamData.toString();
        assertEquals(response, "data=ABC");
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
    public void testSendAuthExceptionOn403() throws AuthException, IOException, NotConnectedException {
        CloudJSONOutboundStream s = proxy.send();
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
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
    public void testSendIOExceptionOn500() throws AuthException, IOException, NotConnectedException {
        CloudJSONOutboundStream s = proxy.send();
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
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
    public void testGetAuthExceptionOn403() throws AuthException, IOException, NotConnectedException {
        proxy.get();
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
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
    public void testGetIOExceptionOn500() throws AuthException, IOException, NotConnectedException {
        proxy.get();
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
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

    /**
     * Verifica che se error 401 per get il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testGetAuthExceptionOn401() throws AuthException, IOException {
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        String text = "Test401Auth";
        when(urlConnectionData.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
        in.close();
        proxy.success();
    }

    /**
     * Verifica che se error 401 per get il proxy non sollevi IOException
     */
    @Test
    public void testGetNotIOExceptionOn401() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
            String text = "Test401IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
            in.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 401");
        } catch (AuthException ae) {}
    }


    /**
     * Verifica che se error 403 per get il proxy non sollevi IOException
     */
    @Test
    public void testGetNotIOExceptionOn403() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
            String text = "Test403IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
            in.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 403");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 405 per get il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testGetAuthExceptionOn405() throws AuthException, IOException {
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
        String text = "Test405Auth";
        when(urlConnectionData.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
        in.close();
        proxy.success();
    }

    /**
     * Verifica che se error 405 per get il proxy non sollevi IOException
     */
    @Test
    public void testGetNotIOExceptionOn405() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
            String text = "Test405IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
            in.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 405");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 401 per send il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testSendAuthExceptionOn401() throws AuthException, IOException {
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        String text = "Test401Auth";
        when(urlConnectionData.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
        out.close();
        proxy.success();
    }

    /**
     * Verifica che se error 401 per send il proxy non sollevi IOException
     */
    @Test
    public void testSendNotIOExceptionOn401() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
            String text = "Test401IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
            out.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 401");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 403 per send il proxy non sollevi IOException
     */
    @Test
    public void testSendNotIOExceptionOn403() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
            String text = "Test403IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
            out.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 403");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 405 per send il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testSendAuthExceptionOn405() throws AuthException, IOException {
        when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
        String text = "Test405Auth";
        when(urlConnectionData.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
        out.close();
        proxy.success();
    }

    /**
     * Verifica che se error 405 per send il proxy non sollevi IOException
     */
    @Test
    public void testSendNotIOExceptionOn405() throws AuthException, IOException {
        try {
            when(urlConnectionData.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
            String text = "Test405IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnectionData.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
            out.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 405");
        } catch (AuthException ae) {}
    }
}