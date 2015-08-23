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
 * Name: SyncPresenterTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file, scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.io.IOException;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.common.SyncStatusEnum;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.synchronization.ISynchronizer;
import com.kyloth.serleena.synchronization.AuthException;
import com.kyloth.serleena.presentation.ISyncView;

/**
 * Test di unità per la classe SyncPresenter.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class SyncPresenterTest {

    private SerleenaActivity activity;
    private ISyncView view;
    private SyncPresenter presenter;
    private ISynchronizer synchronizer;

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(SerleenaActivity.class)
                   .create().start().resume().visible().get();
        view = mock(ISyncView.class);
        synchronizer = mock(ISynchronizer.class);
        presenter = new SyncPresenter(view, activity);
        presenter.synchronizer = synchronizer;
    }

    /**
     * Verifica che una prima richiesta di sincronizzazione andata a buon fine
     * termini in uno stato preautenticato.
     */
    @Test
    public void testSynchronizeNotPaired() throws AuthException, IOException {
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
    }

    /**
     * Verifica che due chiamate consecutive e sequenziali a synchronize()
     * risultino in uno stato autenticato.
     */
    @Test
    public void testSynchronizedPreAuthed() throws AuthException, IOException {
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHED, presenter.status);
    }

    /**
     * Verifica che un'eccezione AuthException in fase di preautenticazione
     * risulti in uno stato del presenter che indichi che la richiesta
     * è stata respinta dal server.
     */
    @Test
    public void testSynchronizedPreAuthRejected() throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(AuthException.class);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.REJECTED, presenter.status);
    }

    /**
     * Verifica che un'eccezione IOException in fase di preautenticazione
     * risulti in uno stato del presenter che indichi il fallimento
     * dell'autenticazione.
     */
    @Test
    public void testSynchronizedPreAuthAuthfailedPreAuth() throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(IOException.class);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHFAILED, presenter.status);
    }

    /**
     * Verifica che, in seguito ad una preautenticazione andata a buon fine,
     * un'eccezione AuthException in fase di autenticazione
     * risulti in uno stato del presenter che indichi che la richiesta
     * è stata respinta dal server.
     */
    @Test
    public void testSynchronizedAuthRejected() throws AuthException, IOException {
        doThrow(AuthException.class).when(synchronizer).auth();
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.REJECTED, presenter.status);
    }

    /**
     * Verifica che, in seguito ad una preautenticazione andata a buon fine,
     * un'eccezione IOException in fase di autenticazione
     * risulti in uno stato del presenter che indichi l'errore di
     * autenticazione.
     */
    @Test
    public void testSynchronizedAuthAuthFailed()
            throws AuthException, IOException {
        doThrow(IOException.class).when(synchronizer).auth();
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHFAILED, presenter.status);
    }

    /**
     * Verifica che, a seguito di preautenticazione e autenticazione andate a
     * buon fine, una terza chiamata a synchronize() risulti in uno stato che
     * indichi la sincronizzazione avvenuta correttamente.
     */
    @Test
    public void testSynchronizedSync() throws AuthException, IOException {
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.SYNCED, presenter.status);
    }

    /**
     * Verifica che, in seguito a preautenticazione e autenticazione andate a
     * buon fine, un'eccezione AuthException in fase di sincronizzazione
     * risulti in uno stato del presenter che indichi che la richiesta
     * è stata respinta dal server.
     */
    @Test
    public void testSynchronizedSyncRejected() throws AuthException, IOException {
        doThrow(AuthException.class).when(synchronizer).sync();
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.REJECTED, presenter.status);
    }

    /**
     * Verifica che, in seguito a preautenticazione e autenticazione andate a
     * buon fine, un'eccezione IOException in fase di sincronizzazione
     * risulti in uno stato del presenter che indichi impossibilità di
     * comunicare correttamente col server.
     */
    @Test
    public void testSynchronizedSyncFailed() throws AuthException, IOException {
        doThrow(IOException.class).when(synchronizer).sync();
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.FAILED, presenter.status);
    }

    /**
     * Verifica che il presenter segnali correttamente lo stato della
     * sincronizzazione nel caso in cui si riesca ad effettuare con successo
     * la preautenticazione a seguito di un primo tentativo fallito.
     */
    @Test
    public void testSynchronizedSyncAfterRejected() throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(AuthException.class).thenReturn("OK");
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.REJECTED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
    }

    /**
     * Verifica che il presenter segnali correttamente lo stato della
     * sincronizzazione nel caso in cui si riesca ad effettuare con successo
     * la preautenticazione dopo una preautenticazione terminata con successo,
     * ma seguita da un fallimento nella autenticazione.
     */
    @Test
    public void testSynchronizedSyncAfterAuthFailed()
            throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(IOException.class).thenReturn("OK");
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHFAILED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
    }

    /**
     * Verifica che, effettuate con successo preautenticazione e autenticazione,
     * il presenter segnali correttamente lo stato della
     * sincronizzazione nel caso in cui si riesca ad effettuare correttamente
     * una sincronizzazione dopo un primo tentativo fallito.
     */
    @Test
    public void testSynchronizedSyncAfterFailed() throws AuthException, IOException {
        doThrow(IOException.class).doNothing().when(synchronizer).sync();
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.FAILED, presenter.status);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.SYNCED, presenter.status);
    }
}
