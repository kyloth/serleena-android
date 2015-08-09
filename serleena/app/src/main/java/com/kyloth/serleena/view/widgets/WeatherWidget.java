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


package com.kyloth.serleena.view.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

public class WeatherWidget extends ImageView {

    private WeatherForecastEnum condition;
    /**
     * Crea un nuovo oggetto WeatherWidget.
     */
    public WeatherWidget(Context context) {
        super(context);
        this.condition = WeatherForecastEnum.Sunny;
        this.setBackgroundResource(R.drawable.sun);
    }

    /**
     * Crea un nuovo oggetto WeatherWidget.
     */
    public WeatherWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundResource(R.drawable.sun);
    }

    /**
     * Crea un nuovo oggetto WeatherWidget.
     */
    public WeatherWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setBackgroundResource(R.drawable.sun);
    }

    /**
     * Restituisce la condizione atmosferica impostata nel widget.
     *
     * @return Enumerazione della condizione atmosferica.
     */
    public WeatherForecastEnum getCondition() {
        return this.condition;
    }

    /**
     * Imposta la condizione atmosferica da mostrare.
     *
     * @param condition Enumerazione della condizione atmosferica.
     */
    public void setCondition(WeatherForecastEnum condition) {
        this.condition = condition;
        switch (condition) {
            case Sunny:
                this.setBackgroundResource(R.drawable.sun);
                break;
            case Cloudy:
                this.setBackgroundResource(R.drawable.cloud);
                break;
            case Rainy:
                this.setBackgroundResource(R.drawable.rain);
                break;
            case Stormy:
                this.setBackgroundResource(R.drawable.storm);
                break;
            case Snowy:
                this.setBackgroundResource(R.drawable.snow);
                break;
        }
    }

}