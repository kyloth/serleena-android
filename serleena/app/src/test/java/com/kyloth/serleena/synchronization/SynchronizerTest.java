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
 * Name: SynchronizerTest.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IPersistenceDataSource;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.synchronization.net.INetProxy;

import java.io.IOException;
import java.util.ArrayList;

public class SynchronizerTest {

    INetProxy proxy;
    IPersistenceDataSink sink;
    IPersistenceDataSource source;
    ArrayList<IExperienceStorage> exps;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void initialize() {
        proxy = mock(INetProxy.class);
        sink = mock(IPersistenceDataSink.class);
        source = mock(IPersistenceDataSource.class);
        exps = new ArrayList<IExperienceStorage>();
        IExperienceStorage exp_1 = mock(IExperienceStorage.class);
        IExperienceStorage exp_2 = mock(IExperienceStorage.class);
        exps.add(exp_1);
        exps.add(exp_2);
        when(source.getExperiences()).thenReturn(exps);
        Synchronizer.__reset();
    }

    /**
     * Testa l'unicita' dell'istanza di Synchronizer
     */
    @Test
    public void testGetInstance() {
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        Synchronizer sync2 = Synchronizer.getInstance(proxy, sink, source);
        assertEquals(sync, sync2);
    }

    /**
     * Testa che il Synchronizer ritorni correttamente il token ottenuto dal proxy
     */
    @Test
    public void testPreAuthTokenIsHandedOver() throws AuthException, IOException {
        when(proxy.preAuth()).thenReturn("TOKEN");
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        String s = sync.preAuth();
        assertEquals("TOKEN", s);
    }

    /**
     * Testa che il Synchronizer chiami correttamente il proxy quando chiamato auth
     */
    @Test
    public void testAuthIsForwarded() throws AuthException, IOException {
        when(proxy.preAuth()).thenReturn("TOKEN");
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        sync.auth();
        verify(proxy).auth();
    }

    /**
     * Testa che Synchronizer.preAuth rilanci eccezioni causate dal proxy
     */
    @Test(expected = AuthException.class)
    public void testPreAuthExceptionIsRethrown() throws AuthException, IOException {
        doThrow(AuthException.class).when(proxy).auth();
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        sync.auth();
    }

    /**
     * Testa che Synchronizer.preAuth ritorni eventuali eccezioni causate dal proxy
     */
    @Test(expected = IOException.class)
    public void testPreAuthIOExceptionIsRethrown() throws AuthException, IOException {
        doThrow(IOException.class).when(proxy).auth();
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        sync.auth();
    }

    /**
     * Testa che Synchronizer.auth rilanci eccezioni causate dal proxy
     */
    @Test(expected = AuthException.class)
    public void testAuthExceptionIsRethrown() throws AuthException, IOException {
        doThrow(AuthException.class).when(proxy).auth();
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        sync.auth();
    }

    /**
     * Testa che Synchronizer.auth ritorni eventuali eccezioni causate dal proxy
     */
    @Test(expected = IOException.class)
    public void testIOExceptionIsRethrown() throws AuthException, IOException {
        doThrow(IOException.class).when(proxy).auth();
        Synchronizer sync = Synchronizer.getInstance(proxy, sink, source);
        sync.auth();
    }

    /**
     * Testa che il Synchronizer ritorni una RunTimeException se chiamato senza un proxy
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testSyncNullProxyResultsInRuntimeException() throws Exception {
        Synchronizer sync = Synchronizer.getInstance(null, sink, source);
        sync.sync();
    }
}
