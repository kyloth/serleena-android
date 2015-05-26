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
 * Name: CompassPresenter.java
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 * Date: 2015-05-07
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-07   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.presenters;

import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.sensors.IHeadingManager;
import com.kyloth.serleena.sensors.IHeadingObserver;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SensorNotAvailableException;

/**
 * Implementa il Presenter associato alla schermata "Bussola".
 *
 * Si occupa di presentare alla vista informazioni sull'orientamento
 * dell'utente utilizzando i servizi di sensoristica del package Sensors.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : ICompassView Vista associata al Presenter
 * @field activity : ISerleenaActivity Activity a cui il Presenter appartiene
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.presentation.ICompassPresenter
 * @see com.kyloth.serleena.sensors.IHeadingObserver
 */
public class CompassPresenter implements ICompassPresenter, IHeadingObserver {

    private static final int UPDATE_INTERVAL = 5;

    private ICompassView view;
    private ISerleenaActivity activity;

    /**
     * Crea un nuovo oggetto CompassPresenter.
     *
     * @param view Vista ICompassView associata al Presenter.
     * @param activity Activity dell'applicazione.
     */
    public CompassPresenter(ICompassView view, ISerleenaActivity activity) {
        this.view = view;
        this.activity = activity;

        view.attachPresenter(this);
    }

    /**
     * Implementa IPresenter.resume().
     *
     * @see com.kyloth.serleena.presentation.IPresenter
     */
    @Override
    public void resume() {
        try {
            ISensorManager sm = activity.getSensorManager();
            IHeadingManager hm = sm.getHeadingSource();
            hm.attachObserver(this, UPDATE_INTERVAL);
        } catch (SensorNotAvailableException ex) {
            view.clearView();
        }
    }

    /**
     * Implementa IPresenter.pause().
     *
     * @see com.kyloth.serleena.presentation.IPresenter
     */
    @Override
    public void pause() {
        try {
            ISensorManager sm = activity.getSensorManager();
            IHeadingManager hm = sm.getHeadingSource();
            hm.detachObserver(this);
        } catch (SensorNotAvailableException ex) {
            view.clearView();
        }
    }

    /**
     * Implementa IHeadingObserver.onHeadingUpdate().
     *
     * @param heading Valore di tipo double che indica la direzione
     *                dell'Escursionista rispetto ai punti cardinali.
     * @see com.kyloth.serleena.sensors.IHeadingObserver
     */
    @Override
    public void onHeadingUpdate(double heading) {
        view.setHeading(heading);
    }

}
