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

import com.kyloth.serleena.presentation.ISyncPresenter;
import com.kyloth.serleena.presentation.ISyncView;

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
    }

    /**
     * Implementa ISyncPresenter.synchronize().
     */
    @Override
    public void synchronize() {

    }

    /**
     * Implementa ISyncPresenter.resume().
     */
    @Override
    public void resume() {

    }

    /**
     * Implementa ISyncPresenter.pause().
     */
    @Override
    public void pause() {

    }

}

