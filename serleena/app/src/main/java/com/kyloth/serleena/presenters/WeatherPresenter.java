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

import android.os.AsyncTask;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.model.NoSuchWeatherForecastException;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

import java.util.Calendar;
import java.util.Date;

/**
 * Concretizza IWeatherPresenter.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class WeatherPresenter implements IWeatherPresenter, ILocationObserver {

    private static int LOCATION_UPDATE_INTERVAL_SECONDS = 60;

    private IWeatherView view;
    private ISerleenaActivity activity;
    private int daysPastNow;
    private ILocationManager locMan;
    private ISerleenaDataSource ds;
    private GeoPoint lastKnownLocation;

    public WeatherPresenter(IWeatherView view, ISerleenaActivity activity) {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        view.attachPresenter(this);
        locMan = activity.getSensorManager().getLocationSource();
        ds = activity.getDataSource();
        daysPastNow = 0;
    }

    /**
     * Implementa IWeatherPresenter.advanceDate().
     */
    @Override
    public synchronized void advanceDate() {
        daysPastNow = (daysPastNow + 1) % 6;
        if (lastKnownLocation != null) {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    present(daysPastNow, lastKnownLocation);
                    return null;
                }
            };
            task.execute();
        }
    }

    /**
     * Presenta alla vista associata le previsioni metereologiche del giorno
     * specificato, relative alla posizione geografica specificata.
     *
     * @param daysPastNow Specifica la data delle previsioni da visualizzare,
     *                    calcolata sommando alla data corrente il numero di
     *                    giorni indicati dal parametro.
     * @param location Posizione geografica delle previsioni da visualizzare.
     *                 Se null, viene sollevata un'eccezione
     *                 IllegalArgumentException.
     */
    public void present(int daysPastNow, GeoPoint location) throws
            IllegalArgumentException {
        if (location == null)
            throw new IllegalArgumentException("Illegal null location");

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, daysPastNow);
        Date d = c.getTime();

        view.setDate(d);

        try {
            IWeatherForecast info =
                    ds.getWeatherInfo(location, d);
            view.setDate(d);
            view.setWeatherInfo(info);
        } catch (NoSuchWeatherForecastException ex) {
            view.clearWeatherInfo();
        }
    }

    /**
     * Implementa IPresenter.resume().
     */
    @Override
    public synchronized void resume() {
        locMan.attachObserver(this, LOCATION_UPDATE_INTERVAL_SECONDS);
    }

}
