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
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.view.widgets.WeatherWidget;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private Map<Integer, String> monthNames;

    public WeatherFragment() {
        monthNames = new HashMap<>();
        monthNames.put(1, "Gennaio");
        monthNames.put(2, "Febbraio");
        monthNames.put(3, "Marzo");
        monthNames.put(4, "Aprile");
        monthNames.put(5, "Maggio");
        monthNames.put(6, "Giugno");
        monthNames.put(7, "Luglio");
        monthNames.put(8, "Agosto");
        monthNames.put(9, "Settembre");
        monthNames.put(10, "Ottobre");
        monthNames.put(11, "Novembre");
        monthNames.put(12, "Dicembre");

        presenter = new IWeatherPresenter() {
            @Override
            public void advanceDate() { }
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) inflater.inflate(
                R.layout.fragment_weather, container, false);

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
        String text = calendar.get(Calendar.DAY_OF_MONTH) + " " + monthNames
                .get(calendar.get(Calendar.MONTH)) + " " + calendar.get
                (Calendar.YEAR);
        dateText.setText(text);

        return group;
    }

    @Override
    public void attachPresenter(IWeatherPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setWeatherInfo(IWeatherForecast forecast) {
        if (forecast == null)
            throw new IllegalArgumentException("Illegal null forecast");

        noInfoText.setVisibility(View.INVISIBLE);
        morningWidget.setVisibility(View.VISIBLE);
        afternoonWidget.setVisibility(View.VISIBLE);
        nightWidget.setVisibility(View.VISIBLE);
        morningTempText.setVisibility(View.VISIBLE);
        afternoonTempText.setVisibility(View.VISIBLE);
        nightTempText.setVisibility(View.VISIBLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(forecast.date().getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateText.setText(day + " " + monthNames.get(month) + " " + year);
        morningWidget.setCondition(forecast.getMorningForecast());
        afternoonWidget.setCondition(forecast.getAfternoonForecast());
        nightWidget.setCondition(forecast.getNightForecast());
        morningTempText.setText(forecast.getMorningTemperature() + " °C");
        afternoonTempText.setText(forecast.getAfternoonTemperature() + " °C");
        nightTempText.setText(forecast.getNightTemperature() + " °C");
    }

    @Override
    public void clearWeatherInfo() {
        noInfoText.setVisibility(View.VISIBLE);
        morningWidget.setVisibility(View.INVISIBLE);
        afternoonWidget.setVisibility(View.INVISIBLE);
        nightWidget.setVisibility(View.INVISIBLE);
        morningTempText.setVisibility(View.INVISIBLE);
        afternoonTempText.setVisibility(View.INVISIBLE);
        nightTempText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

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
     */
    @Override
    public String toString() {
        return "Meteo";
    }

}
