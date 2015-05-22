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


package com.kyloth.serleena;

import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presentation.IWeatherView;

import java.util.Calendar;
import java.util.Date;

/**
 * Classe stub per IWeatherPresenter
 */
public class DummyWeatherPresenter implements IWeatherPresenter {

    private ISerleenaActivity activity;
    private IWeatherView view;

    public DummyWeatherPresenter(IWeatherView view, ISerleenaActivity activity) {
        this.activity = activity;
        this.view = view;

        view.attachPresenter(this);
    }

    @Override
    public void advanceDate() {

    }

    @Override
    public void resume() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2018);
        cal.set(Calendar.MONTH,6);
        cal.set(Calendar.DAY_OF_MONTH,8);
        cal.set(Calendar.HOUR_OF_DAY,23);
        view.setDate(cal.getTime());
        view.setWeatherInfo(new IWeatherForecast() {
            @Override
            public WeatherForecastEnum getMorningForecast() {
                return WeatherForecastEnum.Stormy;
            }

            @Override
            public WeatherForecastEnum getAfternoonForecast() {
                return WeatherForecastEnum.Sunny;
            }

            @Override
            public WeatherForecastEnum getNightForecast() {
                return WeatherForecastEnum.Snowy;
            }

            @Override
            public int getMorningTemperature() {
                return 14;
            }

            @Override
            public int getAfternoonTemperature() {
                return 20;
            }

            @Override
            public int getNightTemperature() {
                return 5;
            }

            @Override
            public Date date() {
                return null;
            }
        });
    }

    @Override
    public void pause() {

    }
}
