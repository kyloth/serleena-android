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

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class SyncPresenterTest {

    SerleenaActivity activity;
    ISyncView view;
    SyncPresenter presenter;
    ISynchronizer synchronizer;

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(SerleenaActivity.class)
                   .create().start().resume().visible().get();
        view = mock(ISyncView.class);
        synchronizer = mock(ISynchronizer.class);
        presenter = new SyncPresenter(view, activity);
        presenter.synchronizer = synchronizer;
    }

    @Test
    public void testSynchronizeNotPaired() throws AuthException, IOException {
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.PREAUTHED, presenter.status);
    }

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

    @Test
    public void testSynchronizedPreAuthRejected() throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(AuthException.class);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.REJECTED, presenter.status);
    }

    @Test
    public void testSynchronizedPreAuthAuthfailedPreAuth() throws AuthException, IOException {
        when(synchronizer.preAuth()).thenThrow(IOException.class);
        presenter.synchronize();
        try {
            presenter.syncingThread.join();
        } catch (InterruptedException e) {}
        assertEquals(SyncStatusEnum.AUTHFAILED, presenter.status);
    }

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

    @Test
    public void testSynchronizedAuthAuthFailed() throws AuthException, IOException {
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

    @Test
    public void testSynchronizedSyncAfterAuthFailed() throws AuthException, IOException {
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
