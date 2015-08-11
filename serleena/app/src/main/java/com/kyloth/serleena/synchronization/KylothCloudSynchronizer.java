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
 * Name: KylothCloudSynchronizer.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.persistence.IPersistenceDataSource;
import com.kyloth.serleena.synchronization.net.INetProxy;
import com.kyloth.serleena.synchronization.net.SerleenaJSONNetProxy;
import com.kyloth.serleena.synchronization.kylothcloud.InboundRootEntity;
import com.kyloth.serleena.synchronization.kylothcloud.IKylothIdSource;
import com.kyloth.serleena.synchronization.kylothcloud.LocalEnvKylothIdSource;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStreamParser;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudSerleenaSQLiteInboundDumpBuilder;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.SerleenaSQLiteInboundDump;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStreamBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Funge da facade per il sottosistema di sincronizzazione.
 * Realizza il pattern Singleton.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.0.1
 * @use Viene usato dall'Activity come unico entry point per le operazioni di sincronizzazione, dopo avergli fornito un dataSource e un dumpLoader (che possono e tipicamente sono la stesso oggetto)e l'URL del servizio remoto. Si faccia riferimento ai diagrammi di sequenza della ST per i dettagli della procedura di sincronizzazione.
 * @field instance la singola istanza
 */
public class KylothCloudSynchronizer implements IKylothCloudSynchronizer {
    static KylothCloudSynchronizer instance;
    private final static String DEFAULT_URL = "http://localhost:8080";
    IPersistenceDataSink sink;
    IPersistenceDataSource source;
    INetProxy proxy;
    URL url;

    KylothCloudSynchronizer(INetProxy proxy, IPersistenceDataSink sink, IPersistenceDataSource source) {
        this.proxy = proxy;
        this.sink = sink;
        this.source = source;
    }

    static void __reset() {
        instance = null;
    }
    /**
     * Ritorna l'istanza unica di KylothCloudSynchronizer
     */
    public static KylothCloudSynchronizer getInstance(IPersistenceDataSink sink, IPersistenceDataSource source) {
        try {
            return getInstance(
                    new SerleenaJSONNetProxy(
                            new LocalEnvKylothIdSource(),
                            new URL(DEFAULT_URL)
                    ),
                    sink,
                    source
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static KylothCloudSynchronizer getInstance(INetProxy proxy, IPersistenceDataSink sink, IPersistenceDataSource source) {
        if (instance == null) {
            instance = new KylothCloudSynchronizer(proxy, sink, source);
        }
        return instance;
    }

    /**
     * Imposta l'URL del servizio remoto con cui eseguire la sincronizzazione
     *
     * @param url l'URL del servizio remoto
     */
    public void setUrl(URL url) {
        this.url = url;
     * Esegue la preautorizzazione iniziale ottenendo
     * un token dal servizio remoto (cfr. ST).
     */
    String preAuth()  throws AuthException, IOException {
        return proxy.preAuth();
    }

    public void auth()  throws AuthException, IOException {
        proxy.auth();
    }

    private void get() throws AuthException, IOException {
        if (proxy == null) {
            throw new RuntimeException("No INetProxy?");
        }
        CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.get();
        CloudJSONInboundStreamParser sjisp = new CloudJSONInboundStreamParser(in);
        InboundRootEntity root = sjisp.parse();
        if (root == null) {
            throw new IOException("Got null while deserializing?");
        }
        CloudSerleenaSQLiteInboundDumpBuilder builder = new CloudSerleenaSQLiteInboundDumpBuilder(root);
        SerleenaSQLiteInboundDump dump = builder.build();
        sink.load(dump);
        if (!proxy.success()) {
            throw new IOException("Unknown network error");
        }
        proxy.disconnect();
    }

    private void send() throws AuthException, IOException {
        if (proxy == null) {
            throw new RuntimeException("No INetProxy?");
        }
        CloudJSONOutboundStreamBuilder b = new CloudJSONOutboundStreamBuilder();
        for (IExperienceStorage exp : source.getExperiences()) {
            b.addExperience(exp);
        }

        OutboundStream s = proxy.send();
        b.stream(s);
        if (!proxy.success()) {
            throw new IOException("Unknown network error");
        }
        proxy.disconnect();
    }

    /**
     * Richiede la sincronizzazione bidirezionale col servizio remoto.
     */
    public void sync() throws AuthException, IOException {
        send();
        get();
    }
}
