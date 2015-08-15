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
 * Name: SerleenaJSONNetProxy.java
 * Package: com.kyloth.serleena.synchronization.net
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.net;

import com.kyloth.serleena.synchronization.AuthException;
import com.kyloth.serleena.synchronization.InboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.IKylothIdSource;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStream;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Concretizza l'interfaccia INetProxy permettendo di dialogare con
 * il servizio KylothCloud.
 *
 * @use Viene utilizzato da KylothCloudSynchronizer, che ne usa una istanzia
 *        per poter dialogare con il servizio KylothCloud utilizzando le
 *        primitive ad alto livello prescritte da INetProxy.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @field baseUrl L'URL del servizio remoto
 * @field auth_token Il token di autorizzazione ricevuto dal servizio remoto al termine
 *                   della procedura di handshaking, valido per l'intera vita
 *                   dell'oggetto.
 */
public class SerleenaJSONNetProxy implements INetProxy {
    URL baseUrl;
    String authToken;
    String tempToken;
    HttpURLConnection urlConnection;
    IKylothIdSource kylothIdSource;
    SerleenaConnectionFactory factory;

    final String AUTH_TOKEN_NAME = "X-AuthToken";
    final String DATA_TOKEN_NAME = "data";
    final String CHARSET = "UTF-8";

    public String getCharset() {
        return CHARSET;
    }
    private String getTempToken() {
        return tempToken;
    }

    private void reset() {
        authToken = null;
        tempToken = null;
        urlConnection = null;
    }

    private URL getAuthUrl() {
        URL authUrl = null;

        try {
            authUrl = new URL(baseUrl.toString()+"users/pair/"+getTempToken());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return authUrl;
    }

    private URL getSyncUrl() {
        URL syncUrl = null;

        try {
            // syncUrl = new URL(baseUrl.toString()+"/users/sync/"+kylothIdSource.getKylothId());
            // TODO: Qual'e' il vero URL? ST o quello che dice Bronsa? Vedi SHCLOUD-34
            syncUrl = new URL(baseUrl.toString()+"data/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return syncUrl;
    }

    private URL getPreAuthUrl() {

        URL preAuthUrl = null;

        try {
            preAuthUrl = new URL(baseUrl.toString()+"tokens/"+kylothIdSource.getKylothId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return preAuthUrl;
    }


    private void connect() throws AuthException, IOException {
        if (authToken == null) {
            throw new AuthException("Not paired yet?");
        } else {
            urlConnection = factory.createURLConnection(getSyncUrl());
            urlConnection.addRequestProperty(AUTH_TOKEN_NAME, authToken);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
        }
    }

    @Override
    public void disconnect() {
        if (urlConnection == null) {
            throw new RuntimeException("No urlConnection?");
        } else {
            try {
                urlConnection.disconnect();
            } finally {
                urlConnection = null;
            }
        }
    }
    /**
     * Costruisce un'istanza di SerleenaJSONNetProxy
     * @param baseUrl L'URL del servizio remoto
     */
    public SerleenaJSONNetProxy(IKylothIdSource kylothIdSource, URL baseUrl) {
        this.kylothIdSource = kylothIdSource;
        this.baseUrl = baseUrl;

        factory = new SerleenaConnectionFactory() {
            public HttpURLConnection createURLConnection(URL url) throws IOException{
                return (HttpURLConnection) url.openConnection();
            }
        };
    }

    /**
     * Costruisce un'istanza di SerleenaJSONNetProxy
     * @param baseUrl L'URL del servizio remoto
     * @param factory Factory che restituirà l'oggeto urlConnection
     */
    public SerleenaJSONNetProxy(URL baseUrl, IKylothIdSource kylothIdSource, SerleenaConnectionFactory factory) {
        this.baseUrl = baseUrl;
        this.factory = factory;
        this.kylothIdSource = kylothIdSource;
    }

    @Override
    public CloudJSONOutboundStream send() throws AuthException, IOException {
        if (urlConnection == null) {
            connect();
            urlConnection.setRequestMethod("POST");
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, CHARSET));
            writer.write(URLEncoder.encode(DATA_TOKEN_NAME, CHARSET) + "=");
            writer.flush();
            return new CloudJSONOutboundStream(os);
        } else {
            throw new RuntimeException("Existing urlConnection? Disconnect first");
        }
    }

    public boolean success() throws IOException, AuthException {
        if (urlConnection == null) {
            throw new RuntimeException("No connection?");
        } else {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            } else if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new AuthException("Unauthorized, got HTTP "+urlConnection.getResponseCode());
            } else  {
                throw new IOException("Network error, status "+urlConnection.getResponseCode());
            }
        }
    }

    @Override
    public InboundStream get() throws AuthException, IOException {
        if (urlConnection == null) {
            connect();
            urlConnection.setRequestMethod("GET");
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                CloudJSONInboundStream in;
                in = new CloudJSONInboundStream(urlConnection.getInputStream());
                return in;
            } else {
                int c = urlConnection.getResponseCode();
                disconnect();
                if (c == 403 || c == 401 || c == 405) {
                    throw new AuthException("Got " + c + " from remote service");
                } else {
                    throw new IOException("Got " + c + " from remote service");
                }
            }
        } else {
            throw new RuntimeException("Existing URL connection? disconnect() first");
        }
    }

    @Override
    public String preAuth() throws AuthException, IOException {
        reset();

        if (urlConnection != null) {
            throw new RuntimeException("Existing urlConnection. Looks like send() or get() attempted. preAuth() must be called first.");
        } else {
            authToken = null;
            HttpURLConnection tempUrlConnection = factory.createURLConnection(getPreAuthUrl());

            if (tempUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String contentType = tempUrlConnection.getContentType();
                if (contentType.contains("text/plain")) {
                    InputStream stream = tempUrlConnection.getInputStream();
                    Scanner s = new Scanner(stream).useDelimiter("\\A");
                    String out = s.hasNext() ? s.next() : "";
                    tempToken = out.trim();
                } else {
                    throw new IOException("Content type was" + contentType);
                }
            } else {
                throw new AuthException("Got " + tempUrlConnection.getResponseCode() + " from remote service");
            }
            tempUrlConnection.disconnect();
            return tempToken;
        }
    }

    /**
     * Richiede di eseguire la procedura di autorizzazione permanente contro
     * il servizio remoto.
     */
    @Override
    public void auth() throws AuthException, IOException {
        authToken = null;
        if (authToken != null) {
            throw new RuntimeException("Existing authtoken? Cannot call auth() twice.");
        }

        if (urlConnection != null) {
            throw new RuntimeException("Existing urlConnection. Looks like send() or get() attempted. auth() must be called first.");
        }

        if (tempToken == null) {
            throw new RuntimeException("No temp token? preauth() must be called first.");
        }

        HttpURLConnection tempUrlConnection = factory.createURLConnection(getAuthUrl());
        if (tempUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String contentType = tempUrlConnection.getContentType();
            if (contentType.contains("text/plain")) {
                InputStream stream = tempUrlConnection.getInputStream();
                Scanner s = new Scanner(stream).useDelimiter("\\A");
                String out = s.hasNext() ? s.next() : "";
                authToken = out.trim();
                tempToken = null;
            } else {
                throw new IOException("Content type was" + contentType);
            }
        } else {
            throw new AuthException("Got " + tempUrlConnection.getResponseCode() + " from remote service");
        }
    }
}
