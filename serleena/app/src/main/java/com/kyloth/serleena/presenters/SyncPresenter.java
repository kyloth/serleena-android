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
 * Name: SyncPresenter.java
 * Package: com.hitchikers.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file
 */

package com.kyloth.serleena.presenters;

import android.app.Activity;
import com.kyloth.serleena.common.SyncStatusEnum;
import com.kyloth.serleena.presentation.ISyncPresenter;
import com.kyloth.serleena.presentation.ISyncView;
import com.kyloth.serleena.synchronization.AuthException;
import com.kyloth.serleena.synchronization.ISynchronizer;
import com.kyloth.serleena.synchronization.Synchronizer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Concretizza ISyncPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : ISyncView Vista associata al Presenter
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SyncPresenter implements ISyncPresenter {

    private ISyncView view;
    private ISerleenaActivity activity;
    ISynchronizer synchronizer;
    private static final URL KYLOTH_PORTAL_URL;
    Thread syncingThread;

    static {URL kylothPortalUrl1;
        try {
            kylothPortalUrl1 = new URL("http://api.kyloth.info");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            kylothPortalUrl1 = null;
        }
        KYLOTH_PORTAL_URL = kylothPortalUrl1;
    }
    SyncStatusEnum status;

    /**
     * Crea un nuovo oggetto SyncPresenter.
     *
     * @param view Vista da associare al Presenter. Se null, viene sollevata
     *             un'eccezione IllegalArgumentException.
     * @param activity Activity a cui il Presenter fa riferimento. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    public SyncPresenter(ISyncView view, ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.view = view;
        this.activity = activity;
        this.synchronizer = Synchronizer.getInstance(null, null, null);
        // Questo arriva gia' inizializzato dall'activity.
        this.status = SyncStatusEnum.NOTPAIRED;

        this.view.attachPresenter(this);
    }

    /**
     * Implementa ISyncPresenter.synchronize().
     */
    @Override
    public void synchronize() {
        try {
            // Se c'e' un thread che sta facendo qualcosa lo aborta
            syncingThread.interrupt();
        } catch (NullPointerException e) {
            // Altrimenti ok
        }
        syncingThread = new Thread(){
            public void run() {
                synchronized(status) {
                    switch (status) {
                        case NOTPAIRED:
                            // Se premo il bottone quando non preauthato inizio a pairare.
                            status = SyncStatusEnum.PREAUTHING;
                            refresh();
                            preAuth();
                            break;
                        case PREAUTHING:
                            // Se premo il bottone quando sta preauthando interrupto il thread e ritorno allo stato precedente.
                            // un preauth vale sempre, anche ce l'avesse fatta
                            status = SyncStatusEnum.NOTPAIRED;
                            refresh();
                            break;
                        case PREAUTHED:
                            // Se premo il bottone quando ha preauthato parto con l'auth vera
                            status = SyncStatusEnum.AUTHING;
                            refresh();
                            auth();
                            break;
                        case AUTHING:
                            // Se premo il bottone quando sta authando aborto tutto
                            // un preauth vale sempre, tanto
                            status = SyncStatusEnum.NOTPAIRED;
                            refresh();
                            break;
                        case AUTHED:
                            // Se premo il bottone quando e' authato parto col sync vero
                            status = SyncStatusEnum.SYNCING;
                            refresh();
                            sync();
                            break;
                        case SYNCING:
                            // Se premo il bottone quando sta syncando aborto e torno a authed.
                            status = SyncStatusEnum.AUTHED;
                            refresh();
                            break;
                        case SYNCED:
                            status = SyncStatusEnum.SYNCING;
                            refresh();
                            sync();
                            break;
                        case REJECTED:
                            // Se premo il bottone quando sono stato negato riprovo da zero.
                            // tanto preauth vale sempre
                            status = SyncStatusEnum.PREAUTHING;
                            refresh();
                            preAuth();
                            break;
                        case FAILED:
                            // Se premo il bottone quando e' fallita la sync riprovo
                            status = SyncStatusEnum.SYNCING;
                            refresh();
                            sync();
                            break;
                        case AUTHFAILED:
                            // Se premo il bottone quando e' fallita l'auth riprovo
                            status = SyncStatusEnum.PREAUTHING;
                            refresh();
                            preAuth();
                            break;
                    }
                };
            }
        };

        syncingThread.start();

    }

    private void refreshToken(final String s) {
        // TODO: Sostituire con un handler
        if (activity instanceof Activity) {
            ((Activity) activity).runOnUiThread(new Runnable() {
                final String finalS = s;
                @Override
                public void run() {
                    // TODO: Rimuovere
                    System.out.println(finalS);
                    view.setSyncStatus(status);
                    view.displayToken(finalS);
                }
            });
        }
    }

    private void refresh() {
        // TODO: Sostituire con un handler
        if (activity instanceof Activity) {
            ((Activity) activity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    view.setSyncStatus(status);
                }
            });
        }
    }

    private void preAuth() {
        String s = "ERROR";
        // You should never see this
        try {
            s = synchronizer.preAuth();
            status = SyncStatusEnum.PREAUTHED;
        } catch (AuthException e) {
            status = SyncStatusEnum.REJECTED;
        } catch (IOException e) {
            status = SyncStatusEnum.AUTHFAILED;
        }
        refreshToken(s);
    }

    private void auth() {
        try {
            synchronizer.auth();
            status = SyncStatusEnum.AUTHED;
        } catch (AuthException e) {
            status = SyncStatusEnum.REJECTED;
        } catch (IOException e) {
            status = SyncStatusEnum.AUTHFAILED;
        }
        refresh();
    }

    private void sync() {
        try {
            synchronizer.sync();
            status = SyncStatusEnum.SYNCED;
        } catch (AuthException e) {
            status = SyncStatusEnum.REJECTED;
        } catch (IOException e) {
            status = SyncStatusEnum.FAILED;
        }
        refresh();
    }

    /**
     * Implementa ISyncPresenter.resume().
     */
    @Override
    public void resume() {
        refresh();
    }

    /**
     * Implementa ISyncPresenter.pause().
     */
    @Override
    public void pause() {
        // NOOP?
    }

}

