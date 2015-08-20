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
 * Name: WeatherPresenterIntegrationTest.java
 * Package: com.kyloth.serleena.presenters
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.presenters;

import android.app.Application;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.TextView;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.TestDB;
import com.kyloth.serleena.activity.SerleenaActivity;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.WeatherForecastEnum;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.view.fragments.WeatherFragment;
import com.kyloth.serleena.view.widgets.WeatherWidget;
import com.jayway.awaitility.Awaitility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class WeatherPresenterIntegrationTest {

    private WeatherFragment fragment;
    private TestActivity activity;
    private TextView dateText;
    private TextView noInfoText;
    private TextView morningTempText;
    private TextView afternoonTempText;
    private TextView nightTempText;
    private WeatherWidget morningWidget;
    private WeatherWidget afternoonWidget;
    private WeatherWidget nightWidget;
    private ShadowLocationManager slm;
    private Map<Integer, String> monthNames;
    private SQLiteDatabase db;

    private WeatherForecastEnum[] morningConditions =
            new WeatherForecastEnum[7];
    private WeatherForecastEnum[] afternoonConditions =
            new WeatherForecastEnum[7];
    private WeatherForecastEnum[] nightConditions =
            new WeatherForecastEnum[7];
    private int[] morningTemps = new int[7];
    private int[] afternoonTemps = new int[7];
    private int[] nightTemps = new int[7];
    private int[] days = new int[7];
    private int[] months = new int[7];
    private int[] years = new int[7];
    private int[] timestamps = new int[7];
    final String MENU_WEATHER_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_weatherFragment);

    @Before
    public void initialize() {
        monthNames = new HashMap<>();
        monthNames.put(0, "Gennaio");
        monthNames.put(1, "Febbraio");
        monthNames.put(2, "Marzo");
        monthNames.put(3, "Aprile");
        monthNames.put(4, "Maggio");
        monthNames.put(5, "Giugno");
        monthNames.put(6, "Luglio");
        monthNames.put(7, "Agosto");
        monthNames.put(8, "Settembre");
        monthNames.put(9, "Ottobre");
        monthNames.put(10, "Novembre");
        monthNames.put(11, "Dicembre");

        Application app = RuntimeEnvironment.application;
        LocationManager lm = (LocationManager)
                app.getSystemService(Context.LOCATION_SERVICE);
        slm = Shadows.shadowOf(lm);

        activity = Robolectric.buildActivity(TestActivity.class).
                create().start().resume().visible().get();

        SerleenaDatabase serleenaDatabase = TestDB.getEmptyDatabase();
        db = serleenaDatabase.getWritableDatabase();
        activity.setDataSource(
                new SerleenaDataSource(
                        new SerleenaSQLiteDataSource(
                                serleenaDatabase)));

        ListFragment menu = (ListFragment) activity.getFragmentManager()
                .findFragmentById(R.id.main_container);
        for (int i = 0; i < menu.getListAdapter().getCount(); i++) {
            Fragment f = (Fragment) menu.getListAdapter().getItem(i);
            if (f.toString().equals(MENU_WEATHER_LABEL))
                fragment = (WeatherFragment) f;
        }

        activity.onObjectSelected(fragment);
        fragment.onResume();

        View v = fragment.getView();
        dateText = (TextView) v.findViewById(R.id.weather_date_text);
        noInfoText = (TextView) v.findViewById(R.id.weather_no_info);
        morningWidget = (WeatherWidget) v.findViewById(R.id.morning_widget);
        afternoonWidget = (WeatherWidget) v.findViewById(R.id.afternoon_widget);
        nightWidget = (WeatherWidget) v.findViewById(R.id.night_widget);
        morningTempText = (TextView) v.findViewById(R.id.morning_temp_text);
        afternoonTempText = (TextView) v.findViewById(R.id.afternoon_temp_text);
        nightTempText = (TextView) v.findViewById(R.id.night_temp_text);
        initDB();
    }

    /**
     * Verifica che le informazioni presenti nel database vengano
     * correttamente rappresentate dalla schermata in base alla data e alla
     * posizione attuale.
     */
    @Test
    public void viewShouldShowInfoAccordingToDataInDatabase() {
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(3);
        l.setLongitude(3);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(l);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return noInfoText.getVisibility() != View.VISIBLE;
            }
        });

        assertView(days[0], months[0], years[0], morningConditions[0],
                afternoonConditions[0], nightConditions[0],
                morningTemps[0], afternoonTemps[0], nightTemps[0]);

        l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(10);
        l.setLongitude(10);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(l);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return noInfoText.getVisibility() == View.VISIBLE;
            }
        });

        assertEquals(View.VISIBLE, noInfoText.getVisibility());

        l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(8);
        l.setLongitude(8);
        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(l);

        Awaitility.await().until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return noInfoText.getVisibility() != View.VISIBLE;
            }
        });

        assertView(days[0],  months[0], years[0], WeatherForecastEnum.Snowy,
                WeatherForecastEnum.Rainy, WeatherForecastEnum.Sunny,
                17, 20, 16);
    }

    /**
     * Verifica che il click sulla schermata faccia ruotare il giorno da
     * visualizzare nell'arco di sette giorni.
     */
    @Test
    public void clickingShouldScrollAllSevenDaysForTheCurrentLocation() {
        Location l = new Location(LocationManager.GPS_PROVIDER);
        l.setLatitude(3);
        l.setLongitude(3);

        for (LocationListener listener : slm.getRequestLocationUpdateListeners())
            listener.onLocationChanged(l);

        String last = "Nessuna informazione disponibile";

        for (int i = 0; i < 8; i ++) {
            final String lll = last;
            Awaitility.await().until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !dateText.getText().equals(lll);
                }
            });

            assertView(days[i % 7], months[i % 7], years[i % 7],
                    morningConditions[i % 7], afternoonConditions[i % 7],
                    nightConditions[i % 7], morningTemps[i % 7],
                    afternoonTemps[i % 7], nightTemps[i % 7]);
            last = days[i % 7] + " " + monthNames.get(months[i % 7]) + " " +
                    years[i % 7];

            dateText.callOnClick();
        }
    }

    private void initDB() {
        Random rand = new Random();
        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());
        today.set(Calendar.HOUR_OF_DAY, 00);
        today.set(Calendar.MINUTE, 00);
        today.set(Calendar.SECOND, 00);
        today.set(Calendar.MILLISECOND, 00);

        for (int i = 0; i < 7; i++) {
            morningConditions[i] =
                    WeatherForecastEnum.values()[rand.nextInt(
                            WeatherForecastEnum.values().length)];
            afternoonConditions[i] =
                    WeatherForecastEnum.values()[rand.nextInt(
                            WeatherForecastEnum.values().length)];
            nightConditions[i] =
                    WeatherForecastEnum.values()[rand.nextInt(
                            WeatherForecastEnum.values().length)];
            morningTemps[i] = rand.nextInt(20) + 10;
            afternoonTemps[i] = rand.nextInt(20) + 10;
            nightTemps[i] = rand.nextInt(20) + 10;

            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MILLISECOND, 00);
            calendar.add(Calendar.DAY_OF_YEAR, i);
            timestamps[i] = (int) (calendar.getTime().getTime() / 1000L);
            days[i] = calendar.get(Calendar.DAY_OF_MONTH);
            months[i] = calendar.get(Calendar.MONTH);
            years[i] = calendar.get(Calendar.YEAR);

            TestDB.forecastQuery(db, i, timestamps[i], morningConditions[i],
                    afternoonConditions[i], nightConditions[i],
                    morningTemps[i], afternoonTemps[i], nightTemps[i],
                    5, 0, 0, 5);
        }
        TestDB.forecastQuery(db, 7, timestamps[0],
                WeatherForecastEnum.Snowy, WeatherForecastEnum.Rainy,
                WeatherForecastEnum.Sunny, 17, 20, 16, 9, 7, 7, 9);

    }

    private void assertView(int day, int month, int year,
                            WeatherForecastEnum morningCondition,
                            WeatherForecastEnum afternoonCondition,
                            WeatherForecastEnum nightCondition,
                            int morningTemperature,
                            int afternoonTemperature, int nightTemperature) {

        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        g.set(Calendar.DAY_OF_MONTH, day);
        g.set(Calendar.MONTH, month);
        g.set(Calendar.YEAR, year);
        g.set(Calendar.HOUR_OF_DAY, 0);
        g.set(Calendar.MINUTE, 0);
        g.set(Calendar.SECOND, 0);
        g.set(Calendar.MILLISECOND, 0);

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String text = df.format(g.getTime());

        assertEquals(text,
                dateText.getText());
        assertEquals(morningCondition, morningWidget.getCondition());
        assertEquals(afternoonCondition, afternoonWidget.getCondition());
        assertEquals(nightCondition, nightWidget.getCondition());
        assertEquals(morningTemperature + " °C", morningTempText.getText());
        assertEquals(afternoonTemperature + " °C", afternoonTempText.getText());
        assertEquals(nightTemperature + " °C", nightTempText.getText());
    }

    private static class TestActivity extends SerleenaActivity {
        private ISerleenaDataSource dataSource;

        public TestActivity() {
            super();
        }

        public void setDataSource(ISerleenaDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public ISerleenaDataSource getDataSource() {
            return this.dataSource;
        }
    }
}
