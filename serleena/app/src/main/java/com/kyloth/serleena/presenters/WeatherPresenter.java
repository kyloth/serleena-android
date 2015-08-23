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
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.sensors.ILocationManager;
import com.kyloth.serleena.sensors.ILocationObserver;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Concretizza IWeatherPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IWeatherView Vista associata al presenter
 * @field activity : ISerleenaActivity Activity a cui il presenter appartiene
 * @field daysPastNow : int Giorni successivi a quello corrente la cui data corrispondente deve essere visualizzata sulla vista
 * @field locMan : ILocationManager Sensore di posizione
 * @field lastKnownLocation : GeoPoint Ultima posizione geografica nota dell'utente
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class WeatherPresenter implements IWeatherPresenter, ILocationObserver {

    public static int LOCATION_UPDATE_INTERVAL_SECONDS = 60;

    private IWeatherView view;
    private ISerleenaActivity activity;
    private int daysPastNow;
    private ILocationManager locMan;
    private ISerleenaDataSource ds;
    private GeoPoint lastKnownLocation;

    /**
     * Crea un nuovo oggetto WeatherPresenter.
     *
     * @param view Vista da associare al Presenter.
     * @param activity Activity a cui il Presenter fa riferimento.
     */
    public WeatherPresenter(IWeatherView view, ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.view = view;
        this.activity = activity;
        this.view.attachPresenter(this);
        this.locMan = activity.getSensorManager().getLocationSource();
        daysPastNow = 0;
    }

    /**
     * Implementa IWeatherPresenter.advanceDate().
     */
    @Override
    public synchronized void advanceDate() {
        daysPastNow = (daysPastNow + 1) % 7;
        Calendar localTime = new GregorianCalendar(Locale.getDefault());
        localTime.setTimeInMillis(currentDate().getTime());
        view.setDate(localTime.getTime());
        view.clearWeatherInfo();

        if (lastKnownLocation != null)
            present();
    }

    /**
     * Implementa IPresenter.resume().
     */
    @Override
    public synchronized void resume() {
        Calendar localTime = new GregorianCalendar(Locale.getDefault());
        localTime.setTimeInMillis(currentDate().getTime());
        view.setDate(localTime.getTime());
        locMan.attachObserver(this, LOCATION_UPDATE_INTERVAL_SECONDS);
    }

    /**
     * Implementa IPresenter.pause().
     */
    @Override
    public synchronized void pause() {
        locMan.detachObserver(this);
    }

    /**
     * Implementa ILocationObserver.onLocationUpdate().
     *
     * @param loc Valore di tipo GeoPoint che indica la posizione
     *            dell'Escursionista.
     */
    @Override
    public synchronized void onLocationUpdate(GeoPoint loc)
            throws IllegalArgumentException {
        if (loc == null)
            throw new IllegalArgumentException("Illegal null location");

        lastKnownLocation = loc;
        present();
    }

    private Date currentDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT"));
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, daysPastNow);
        return c.getTime();
    }

    private void present() throws
            IllegalArgumentException {
        AsyncTask<Void, Void, IWeatherForecast> task =
                new AsyncTask<Void, Void, IWeatherForecast>() {
                    @Override
                    protected IWeatherForecast doInBackground(Void... params) {
                        try {
                            return activity.getDataSource().getWeatherInfo(
                                    lastKnownLocation, currentDate());
                        } catch (NoSuchWeatherForecastException ex) {
                            return null;
                        }
                    }
                    @Override
                    protected void onPostExecute(IWeatherForecast result) {
                        if (result != null)
                            view.setWeatherInfo(result);
                        else
                            view.clearWeatherInfo();
                    }
                };

        task.execute();
    }


}
