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
 * Name: WeatherFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.model.IWeatherForecast;
import com.kyloth.serleena.presentation.IWeatherPresenter;
import com.kyloth.serleena.presenters.ISerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.view.widgets.MapWidget;
import com.kyloth.serleena.view.widgets.WeatherWidget;

import junit.framework.Assert;

import org.apache.maven.artifact.ant.shaded.cli.Arg;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19, manifest = "src/main/AndroidManifest.xml")
public class WeatherFragmentTest {

    private WeatherFragment fragment;
    private IWeatherPresenter presenter;

    private TextView dateText;
    private TextView noInfoText;
    private TextView morningTempText;
    private TextView afternoonTempText;
    private TextView nightTempText;
    private WeatherWidget morningWidget;
    private WeatherWidget afternoonWidget;
    private WeatherWidget nightWidget;
    private List<View.OnClickListener> listeners;

    /**
     * Inizializza il test.
     *
     */
    @Before
    public void initialize() {
        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        ViewGroup v = mock(ViewGroup.class);

        when(inflater.inflate(
                        eq(R.layout.fragment_weather),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);

        dateText = mock(TextView.class);
        when(v.findViewById(R.id.weather_date_text)).thenReturn(dateText);
        noInfoText = mock(TextView.class);
        when(v.findViewById(R.id.weather_no_info)).thenReturn(noInfoText);

        morningWidget = mock(WeatherWidget.class);
        when(v.findViewById(R.id.morning_widget)).thenReturn(morningWidget);
        afternoonWidget = mock(WeatherWidget.class);
        when(v.findViewById(R.id.afternoon_widget)).thenReturn(afternoonWidget);
        nightWidget = mock(WeatherWidget.class);
        when(v.findViewById(R.id.night_widget)).thenReturn(nightWidget);

        morningTempText = mock(TextView.class);
        when(v.findViewById(R.id.morning_temp_text)).thenReturn(morningTempText);
        afternoonTempText = mock(TextView.class);
        when(v.findViewById(R.id.afternoon_temp_text)).thenReturn(afternoonTempText);
        nightTempText = mock(TextView.class);
        when(v.findViewById(R.id.night_temp_text)).thenReturn(nightTempText);

        when(v.getChildCount()).thenReturn(8);
        when(v.getChildAt(0)).thenReturn(dateText);
        when(v.getChildAt(1)).thenReturn(morningWidget);
        when(v.getChildAt(2)).thenReturn(afternoonWidget);
        when(v.getChildAt(3)).thenReturn(nightWidget);
        when(v.getChildAt(4)).thenReturn(morningTempText);
        when(v.getChildAt(5)).thenReturn(afternoonTempText);
        when(v.getChildAt(6)).thenReturn(nightTempText);
        when(v.getChildAt(7)).thenReturn(noInfoText);

        fragment = new WeatherFragment();
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
        presenter = mock(IWeatherPresenter.class);
        fragment.attachPresenter(presenter);

        ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(dateText).setOnClickListener(captor.capture());
        verify(morningWidget).setOnClickListener(captor.capture());
        verify(afternoonWidget).setOnClickListener(captor.capture());
        verify(nightWidget).setOnClickListener(captor.capture());
        verify(morningTempText).setOnClickListener(captor.capture());
        verify(afternoonTempText).setOnClickListener(captor.capture());
        verify(nightTempText).setOnClickListener(captor.capture());
        verify(noInfoText).setOnClickListener(captor.capture());
        listeners = captor.getAllValues();
    }

    /**
     * Verifica che sia possibile collegare un IContactsPresenter ad un
     * ContactsFragment.
     */
    @Test
    public void testAttachContactsPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    @Test
    public void clearingViewShouldTellUserNoInfoIsAvailable() {
        fragment.clearWeatherInfo();
        verify(noInfoText).setVisibility(View.VISIBLE);
        verify(morningWidget).setVisibility(View.INVISIBLE);
        verify(afternoonWidget).setVisibility(View.INVISIBLE);
        verify(nightWidget).setVisibility(View.INVISIBLE);
        verify(morningTempText).setVisibility(View.INVISIBLE);
        verify(afternoonTempText).setVisibility(View.INVISIBLE);
        verify(nightTempText).setVisibility(View.INVISIBLE);
    }

    /**
     * Verifica che il passaggio di parametro null a setWeatherInfo() sollevi
     * un'eccezione.
     */
    @Test(expected = IllegalArgumentException.class)
    public void setWeatherInfoShouldThrowWhenNullArgument() {
        fragment.setWeatherInfo(null);
    }

    /**
     * Verifica che alla pressione del pulsante centrale venga
     * richiesto il prossimo contatto.
     */
    @Test
    public void testShouldRequestNextContactOnKeyDown() {
        int count = 0;
        for (View.OnClickListener listener : listeners) {
            listener.onClick(mock(View.class));
            verify(presenter, times(count + 1)).advanceDate();
            count++;
        }
    }

    @Test
    public void viewShouldShowTodayDateAsDefault() {
        Map<Integer, String> monthNames = new HashMap<>();
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

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String text = calendar.get(Calendar.DAY_OF_MONTH) + " " + monthNames
                .get(calendar.get(Calendar.MONTH)) + " " + calendar.get
                (Calendar.YEAR);
        verify(dateText).setText(text);
    }

    @Test
    public void viewShouldDisplayTheDateOfWeatherInfo() {
        IWeatherForecast info = mock(IWeatherForecast.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 05, 01);
        when(info.date()).thenReturn(calendar.getTime());
        fragment.setWeatherInfo(info);
        verify(dateText).setText("1 Maggio 2015");
    }

}
