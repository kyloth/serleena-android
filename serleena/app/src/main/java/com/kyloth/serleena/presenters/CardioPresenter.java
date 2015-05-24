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
 * Name: CardioPresenter.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 * Date: 2015-05-24
 *
 * History:
 * Version  Programmer        Date        Changes
 * 1.0.0    Filippo Sestini   2015-05-24  Creazione file e scrittura
 *                                        codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import com.kyloth.serleena.presentation.ICardioPresenter;
import com.kyloth.serleena.presentation.ICardioView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.IHeartRateManager;
import com.kyloth.serleena.sensors.IHeartRateObserver;

/**
 * Concretizza ICardioPresenter.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class CardioPresenter implements ICardioPresenter, IHeartRateObserver {

    private IHeartRateManager hrMan;
    private ICardioView view;

    /**
     * Crea un nuovo oggetto CardioPresenter.
     *
     * @param view Vista da associare al Presenter.
     * @param activity Activity a cui è associato il Presenter.
     */
    public CardioPresenter(ICardioView view, ISerleenaActivity activity) {
        this.view = view;
        this.hrMan = activity.getSensorManager().getHeartRateSource();
        view.attachPresenter(this);
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Si registra agli eventi del sensore di battito cardiaco.
     */
    @Override
    public void resume() {
        hrMan.attachObserver(this, 15);
    }

}
