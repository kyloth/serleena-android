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
 * Name: WeatherFragment
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */
package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Classe che implementa la schermata “Meteo”, in cui vengono mostrate informazioni metereologiche
 * relative ad un giorno
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia IWeatherPresenter.
 * @field presenter : IWeatherPresenter presenter collegato a un WeatherFragment
 * @field now : Calendar data da visualizzare
 * @field weatherMap : HashMap<WeatherForecastEnum,Integer> mappa di corrispondenze tra condizioni meteo e immagini da visualizzare
 * @field image : ImageView immagine su cui disegnare le condizioni metereologiche fornite
 * @field tempTxt : TextView casella di testo dove inserire il valore della temperatura fornito
 * @field dayTxt : TextView casella di testo dove visualizzare la data fornita
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class WeatherFragment extends Fragment implements IWeatherView {

    private IWeatherPresenter presenter;

    private Calendar now;

    private HashMap<WeatherForecastEnum,Integer> weatherMap = new HashMap<>();

    private ImageView image;
    private TextView tempTxt;
    private TextView dayTxt;

    /**
     * Questo metodo viene invocato ogni volta che un WeatherFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initFragment();
    }

    /**
     * Metodo che inizializza il Fragment, ovvero la mappa e i riferimenti alle View utilizzate.
     */
    private void initFragment() {
        weatherMap.put(WeatherForecastEnum.Cloudy, R.drawable.cloud);
        weatherMap.put(WeatherForecastEnum.Rainy,R.drawable.rain);
        weatherMap.put(WeatherForecastEnum.Snowy, R.drawable.snow);
        weatherMap.put(WeatherForecastEnum.Stormy, R.drawable.storm);
        weatherMap.put(WeatherForecastEnum.Sunny, R.drawable.sun);
        image = (ImageView) getActivity().findViewById(R.id.weather_image);
        dayTxt = (TextView) getActivity().findViewById(R.id.weather_day);
        tempTxt = (TextView) getActivity().findViewById(R.id.weather_temperature);
    }

    /**
     * Questo metodo viene invocato ogni volta che un WeatherFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Metodo utilizzato per collegare un presenter a un WeatherFragment
     *
     * @param presenter Presenter a cui viene collegato un WeatherFragment
     */
    @Override
    public void attachPresenter(IWeatherPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Metodo utilizzato per impostare e visualizzare delle previsioni meteorologiche.
     *
     * @param forecast Oggetto contenente delle previsioni
     */
    @Override
    public void setWeatherInfo(IWeatherForecast forecast) {
        image.setVisibility(ImageView.VISIBLE);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        Integer temp = 0;
        WeatherForecastEnum weatherId = null;
        if(hour >= 12 && hour < 20) {
            weatherId = forecast.getAfternoonForecast();
            temp = forecast.getAfternoonTemperature();
        }
        if(hour < 12 && hour >= 4) {
            weatherId = forecast.getMorningForecast();
            temp = forecast.getMorningTemperature();
        }
        if(hour < 4 ||  hour >= 20) {
            weatherId = forecast.getNightForecast();
            temp = forecast.getNightTemperature();
        }
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),weatherMap.get(weatherId));
        bmp = Bitmap.createScaledBitmap(bmp,130,130,false);
        image.setImageBitmap(bmp);
        String day = new Integer(now.get(Calendar.DAY_OF_MONTH)).toString();
        if(now.get(Calendar.DAY_OF_MONTH) < 10) day = "0" + day;
        String month = new Integer(now.get(Calendar.MONTH)).toString();
        if(now.get(Calendar.MONTH) < 10) month = "0" + month;
        dayTxt.setText(day + "/" + month + "/" + now.get(Calendar.YEAR));
        tempTxt.setText(temp.toString() + "°C");
    }

    /**
     * Metodo utilizzato per rimuovere le informazioni visualizzate
     */
    @Override
    public void clearWeatherInfo() {
        image.setVisibility(ImageView.INVISIBLE);
        tempTxt.setText("NESSUNA INFORMAZIONE DISPONIBILE");
        dayTxt.setText("");
    }

    /**
     * Metodo utilizzato per impostare una data su un WeatherFragment
     *
     * @param date Data impostata
     */
    @Override
    public void setDate(Date date) {
        now = Calendar.getInstance();
        now.setTime(date);
    }

    /**
     * Metodo che visualizza le previsioni successive.
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     */
    public void keyDown(int keyCode, KeyEvent event) {
        presenter.advanceDate();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.pause();
    }
}
