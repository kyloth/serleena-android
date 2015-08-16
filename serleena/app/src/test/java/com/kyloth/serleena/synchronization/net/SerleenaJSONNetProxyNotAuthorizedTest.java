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
import static org.junit.Assert.fail;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class SerleenaJSONNetProxyNotAuthorizedTest {

    private SerleenaJSONNetProxy proxy;
    private SerleenaConnectionFactory factory = mock(SerleenaConnectionFactory.class);

    final String PREAUTH_TOKEN = "Foo";
    final String KYLOTH_ID = "Bar";

    final URL BASE_URL;
    {URL BASE_URL1;
        try {
            BASE_URL1 = new URL("http://localhost");
        } catch (MalformedURLException e) {
            BASE_URL1 = null;
        }
        BASE_URL = BASE_URL1;
    }

    private HttpURLConnection urlConnection = mock(HttpURLConnection.class);
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


    private void initializeMocks () throws IOException {
        when(factory.createURLConnection(any(URL.class))).thenReturn(urlConnection);
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
                return KYLOTH_ID;
            }
        };
        proxy = new SerleenaJSONNetProxy(new URL("http://localhost"), entry, factory);
    }

    @Test
    public void preAuthTest() throws AuthException, IOException {
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, PREAUTH_TOKEN);
    }

    /**
     * Verifica che auth senza preauth sollevi una RuntimeException
     */
    @Test(expected = RuntimeException.class)
    public void testAuthWithoutPreauthNotAllowed() throws AuthException, IOException {
        proxy.auth();
    }

    /**
     * Verifica che chiamare auth due volte sollevi una RuntimeException
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = RuntimeException.class)
    public void testTwiceAuthNotAllowed() throws AuthException, IOException {
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, PREAUTH_TOKEN);
        proxy.auth();
        proxy.auth();
    }

    /**
     * Verifica che sia possibile ripetere preauth (ottenendo il reset).
     * @throws AuthException
     * @throws IOException
     */
    @Test
    public void testMultiplePreauthAllowed() throws AuthException, IOException {
        String preAuth = proxy.preAuth();
        assertEquals(preAuth, PREAUTH_TOKEN);
        proxy.auth();
        preAuth = proxy.preAuth();
        assertEquals(preAuth, PREAUTH_TOKEN);
        proxy.auth();
        proxy.send();
    }

    /**
     * Verifica che chiamare send() senza auth risulti in una AuthException.
     * @throws AuthException
     * @throws IOException
     */
    @Test(expected = AuthException.class)
    public void testSendWithoutAuthNotAllowed() throws AuthException, IOException {
        proxy.send();
    }

    /**
     * Verifica che se error 500 preauth sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testPreauthAuthExceptionOn500() throws AuthException, IOException {
        when(urlConnection.getResponseCode()).thenReturn(500);
        proxy.preAuth();
    }

    /**
     * Verifica che se error 403 preauth sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testPreauthAuthExceptionOn403() throws AuthException, IOException {
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        proxy.preAuth();
    }

    /**
     * Verifica che se urlConnection == null disconnect sollevi RuntimeException
     */
    @Test(expected = RuntimeException.class)
    public void testDisconnectRuntimeExcpetion () throws RuntimeException {
        proxy.disconnect();
    }

    /**
     * Verifica che se urlConnection != null get sollevi RuntimeException
     */
    @Test(expected = RuntimeException.class)
    public void testGetAuthException () throws IOException, AuthException {
        proxy.preAuth();
        proxy.auth();
        proxy.get();
        proxy.get();
    }

    /**
     * Verifica che se contentType != text/plain preAuth sollevi IOException
     */

    @Test(expected = IOException.class)
    public void testPreAuthExcpetionOnWrongContentType () throws IOException, AuthException {
        when(urlConnection.getContentType()).thenReturn("text/hidden");
        proxy.preAuth();
    }

    /**
     * Verifica che non sia possibile autenticarsi dopo una get
     */

    @Test(expected = RuntimeException.class)
    public void testAuthAfterGet () throws AuthException, IOException {
        proxy.preAuth();
        proxy.auth();
        proxy.get();
        proxy.auth();
    }

    /**
     * Verifica che non sia possibile autenticarsi dopo una send
     */

    @Test(expected = RuntimeException.class)
    public void testAuthAfterSend () throws AuthException, IOException {
        proxy.preAuth();
        proxy.auth();
        proxy.send();
        proxy.auth();
    }

    /**
     * Verifica che se contentType != text/plain auth sollevi IOException
     */

    @Test(expected = IOException.class)
    public void testAuthExceptionOnBadContentType() throws AuthException, IOException {
        when(urlConnection.getContentType()).thenReturn("text/hidden");
        proxy.preAuth();
        proxy.auth();
    }

    /**
     * Verifica che se error 500 auth sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testAuthAuthExceptionOn500() throws AuthException, IOException {
        proxy.preAuth();
        when(urlConnection.getResponseCode()).thenReturn(500);
        proxy.auth();
    }

    /**
     * Verifica che se error 401 per get il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testGetAuthExceptionOn401() throws AuthException, IOException {
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        String text = "Test401Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
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
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
            String text = "Test401IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
            in.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 401");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 403 per get il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testGetAuthExceptionOn403() throws AuthException, IOException {
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        String text = "Test403Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
        in.close();
        proxy.success();
    }

    /**
     * Verifica che se error 403 per get il proxy non sollevi IOException
     */
    @Test
    public void testGetNotIOExceptionOn403() throws AuthException, IOException {
        try {
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
            String text = "Test403IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
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
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
        String text = "Test405Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
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
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
            String text = "Test405IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
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
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        String text = "Test401Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
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
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
            String text = "Test401IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
            out.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 401");
        } catch (AuthException ae) {}
    }

    /**
     * Verifica che se error 403 per send il proxy sollevi AuthException
     */
    @Test(expected = AuthException.class)
    public void testSendAuthExceptionOn403() throws AuthException, IOException {
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
        String text = "Test403Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
        CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
        out.close();
        proxy.success();
    }

    /**
     * Verifica che se error 403 per send il proxy non sollevi IOException
     */
    @Test
    public void testSendNotIOExceptionOn403() throws AuthException, IOException {
        try {
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);
            String text = "Test403IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
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
        when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
        String text = "Test405Auth";
        when(urlConnection.getInputStream()).thenReturn(new CloudJSONInboundStream(new ByteArrayInputStream(text.getBytes("UTF-8"))));
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
            when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_METHOD);
            String text = "Test405IO";
            ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
            streamOut.write(text.getBytes("UTF-8"));
            when(urlConnection.getOutputStream()).thenReturn(new CloudJSONOutboundStream(streamOut));
            CloudJSONOutboundStream out = (CloudJSONOutboundStream) proxy.send();
            out.close();
            proxy.success();
        } catch (IOException ioe) {
            fail("IOException thrown on 405");
        } catch (AuthException ae) {}
    }
}
