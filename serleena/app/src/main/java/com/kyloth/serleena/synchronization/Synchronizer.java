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
 * Name: Synchronizer.java
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
import com.kyloth.serleena.synchronization.net.NotConnectedException;
import com.kyloth.serleena.synchronization.kylothcloud.InboundRootEntity;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStream;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudJSONInboundStreamParser;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.CloudSerleenaSQLiteInboundDumpBuilder;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.SerleenaSQLiteInboundDump;
import com.kyloth.serleena.synchronization.kylothcloud.outbound.CloudJSONOutboundStreamBuilder;

import java.io.IOException;

/**
 * Funge da facade per il sottosistema di sincronizzazione.
 * Realizza il pattern Singleton.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.0.1
 */
public class Synchronizer implements ISynchronizer {
    static Synchronizer instance;
    IPersistenceDataSink sink;
    IPersistenceDataSource source;
    INetProxy proxy;

    private Synchronizer(INetProxy proxy, IPersistenceDataSink sink, IPersistenceDataSource source) {
        this.proxy = proxy;
        this.sink = sink;
        this.source = source;
    }

    // Metodo di utilita' per poter resettare lo stato del singleton negli unit test del package
    static void __reset() { instance = null; }

    /**
     * Ritorna l'istanza unica di Synchronizer
     *
     * @param proxy L'INetProxy con cui connettersi al servizio remoto
     * @param sink L'IPersistenceDataSink in cui riversare i dati raccolti
     * @param source L'IPersistenceDataSource da cui prelevare i dati per l'invio al servizio remoto
     */
    public static Synchronizer getInstance(INetProxy proxy, IPersistenceDataSink sink, IPersistenceDataSource source) {
        if (instance == null) {
            instance = new Synchronizer(proxy, sink, source);
        }
        return instance;
    }

    /**
     * Esegue la preautorizzazione iniziale ottenendo
     * un token dal servizio remoto (cfr. ST).
     *
     * @return Il token di conferma da fornire manualmente al servizio remoto
     * @throws AuthException Se il servizio remoto nega il permesso di preautenticare
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    @Override
    public String preAuth()  throws AuthException, IOException {
        return proxy.preAuth();
    }

    /**
     * Esegue l'autenticazione col servizio remoto
     * @throws RuntimeException Se erroneamente chiamato dal programmatore senza prima effettuare con successo preauth()
     * @throws AuthException Se il servizio remoto nega l'autenticazione
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    @Override
    public void auth()  throws AuthException, IOException {
        proxy.auth();
    }

    private void get() throws AuthException, IOException {
        if (proxy == null) {
            throw new RuntimeException("No INetProxy?");
        }
        CloudJSONInboundStream in = (CloudJSONInboundStream) proxy.read();
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

        try {
            proxy.disconnect();
        } catch (NotConnectedException e) {
            throw new IOException("Connection already lost?");
        }

    }

    private void send() throws AuthException, IOException {
        if (proxy == null) {
            throw new RuntimeException("No INetProxy?");
        }
        CloudJSONOutboundStreamBuilder b = new CloudJSONOutboundStreamBuilder();
        for (IExperienceStorage exp : source.getExperiences()) {
            b.addExperience(exp);
        }

        OutboundStream s = proxy.write();
        try {
            b.stream(s);
        } catch (IllegalArgumentException e) {
            throw new IOException("Unable to parse stream");
        }
        if (!proxy.success()) {
            throw new IOException("Unknown network error");
        }

        try {
            proxy.disconnect();
        } catch (NotConnectedException e) {
            throw new IOException("Connection already lost?");
        }
    }

    /**
     * Richiede la sincronizzazione bidirezionale col servizio remoto.
     *
     * @throws AuthException Se il servizio remoto nega il permesso di sincronizzare
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    @Override
    public void sync() throws AuthException, IOException {
        try {
            send();
            get();
        } catch (Exception e) {
            try {
                proxy.disconnect();
            } catch (NotConnectedException nce) {
                // noop
            }
            throw e;
        }
    }
}
