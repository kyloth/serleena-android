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

import java.util.Date;

public class WeatherFragment extends Fragment
        implements IWeatherView, View.OnClickListener{

    IWeatherPresenter presenter;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) inflater.inflate(
                R.layout.fragment_weather, container, false);
        for (int i = 0; i < group.getChildCount(); i++)
            group.getChildAt(i).setOnClickListener(this);
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
    }

    @Override
    public void clearWeatherInfo() {

    }

    @Override
    public void setDate(Date date) {
        if (date == null)
            throw new IllegalArgumentException("Illegal null date");
        TextView tv = (TextView) getView().findViewById(R.id.weather_date_text);
        tv.setText(date.toString());
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
