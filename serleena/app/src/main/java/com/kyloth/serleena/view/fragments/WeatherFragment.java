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


package com.kyloth.serleena.view.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.view.widgets.WeatherWidget;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeatherFragment extends Fragment
        implements IWeatherView, View.OnClickListener{

    private IWeatherPresenter presenter;
    private TextView dateText;
    private TextView noInfoText;
    private TextView morningTempText;
    private TextView afternoonTempText;
    private TextView nightTempText;
    private WeatherWidget morningWidget;
    private WeatherWidget afternoonWidget;
    private WeatherWidget nightWidget;
    private LinearLayout morningLayout;
    private LinearLayout afternoonLayout;
    private LinearLayout nightLayout;

    /**
     * Crea un nuovo oggetto WeatherFragment.
     */
    public WeatherFragment() {
        presenter = new IWeatherPresenter() {
            @Override
            public void advanceDate() { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce Fragment.onCreateView().
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) inflater.inflate(
                R.layout.fragment_weather, container, false);

        morningLayout = (LinearLayout) group.findViewById(R.id.morning_layout);
        afternoonLayout = (LinearLayout) group.findViewById(R.id.afternoon_layout);
        nightLayout = (LinearLayout) group.findViewById(R.id.night_layout);
        dateText = (TextView) group.findViewById(R.id.weather_date_text);
        noInfoText = (TextView) group.findViewById(R.id.weather_no_info);
        morningWidget = (WeatherWidget) group.findViewById(R.id.morning_widget);
        afternoonWidget =
                (WeatherWidget) group.findViewById(R.id.afternoon_widget);
        nightWidget = (WeatherWidget) group.findViewById(R.id.night_widget);
        morningTempText = (TextView) group.findViewById(R.id.morning_temp_text);
        afternoonTempText =
                (TextView) group.findViewById(R.id.afternoon_temp_text);
        nightTempText = (TextView) group.findViewById(R.id.night_temp_text);

        for (int i = 0; i < group.getChildCount(); i++)
            group.getChildAt(i).setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String text = df.format(calendar.getTime());
        dateText.setText(text);

        return group;
    }

    /**
     * Implementa IWeatherView.attachPresenter().
     */
    @Override
    public void attachPresenter(IWeatherPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Implementa IWeatherView.setWeatherInfo().
     */
    @Override
    public void setWeatherInfo(IWeatherForecast forecast) {
        if (forecast == null)
            throw new IllegalArgumentException("Illegal null forecast");

        noInfoText.setVisibility(View.INVISIBLE);
        morningLayout.setVisibility(View.VISIBLE);
        afternoonLayout.setVisibility(View.VISIBLE);
        nightLayout.setVisibility(View.VISIBLE);

        morningWidget.setCondition(forecast.getMorningForecast());
        afternoonWidget.setCondition(forecast.getAfternoonForecast());
        nightWidget.setCondition(forecast.getNightForecast());
        morningTempText.setText(forecast.getMorningTemperature() + " °C");
        afternoonTempText.setText(forecast.getAfternoonTemperature() + " °C");
        nightTempText.setText(forecast.getNightTemperature() + " °C");
    }

    /**
     * Implementa IWeatherView.setDate().
     */
    @Override
    public void setDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String text = df.format(calendar.getTime());
        dateText.setText(text);
    }

    /**
     * Implementa IWeatherView.clearWeatherInfo().
     */
    @Override
    public void clearWeatherInfo() {
        noInfoText.setVisibility(View.VISIBLE);
        morningLayout.setVisibility(View.INVISIBLE);
        afternoonLayout.setVisibility(View.INVISIBLE);
        nightLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Ridefinisce Fragment.onResume().
     *
     * Chiama il rispettivo metodo resume() del Presenter.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinisce Fragment.onPause().
     *
     * Chiama il rispettivo metodo pause() del Presenter.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce View.OnClickListener.onClick().
     *
     * @param v Vista che ha scatenato l'evento.
     */
    @Override
    public void onClick(View v) {
        presenter.advanceDate();
    }

    /**
     * Ridefinisce Object.toString().
     *
     * Restituisce il nome della vista.
     */
    @Override
    public String toString() {
        return "Meteo";
    }

}
