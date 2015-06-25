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
 * Name: SerleenaActivity
 * Package: com.kyloth.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura del codice e di
 *                              Javadoc
 */
package com.kyloth.serleena.activity;

import android.app.AlarmManager;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.model.*;
import com.kyloth.serleena.persistence.sqlite.SQLiteDataSourceFactory;
import com.kyloth.serleena.presentation.*;
import com.kyloth.serleena.presenters.*;
import com.kyloth.serleena.sensors.*;
import com.kyloth.serleena.view.fragments.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe che implementa ISerleenaActivity.
 *
 * In questa visuale è possibile selezionare un'esperienza da attivare tra quelle disponibili.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field dataSource : sorgente dati utilizzata dall'activity e dai suoi presenter
 * @field sensorManager : gestore dei sensori utilizzato dall'activity e dai suoi presenter
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.support.v7.app.AppCompatActivity
 */
public class SerleenaActivity extends Activity
        implements ISerleenaActivity, IObjectListObserver {

    private ISerleenaDataSource dataSource;
    private ISensorManager sensorManager;

    private TrackFragment trackFragment;
    private CompassFragment compassFragment;
    private ContactsFragment contactsFragment;
    private TelemetryFragment telemetryFragment;
    private WeatherFragment weatherFragment;
    private MapFragment mapFragment;
    private ExperienceSelectionFragment experienceSelectionFragment;
    private TrackSelectionFragment trackSelectionFragment;
    private ObjectListFragment menuFragment;
    private ObjectListFragment experienceFragment;

    /**
     * Ridefinisce Activity.onCreate().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serleena);

        SQLiteDataSourceFactory factory = SQLiteDataSourceFactory.getInstance();
        dataSource = new SerleenaDataSource(factory.createDataSource(this));
        sensorManager = SerleenaSensorManager.getInstance(this);

        if (findViewById(R.id.main_container) != null) {
            if (savedInstanceState != null)
                return;

            trackFragment = new TrackFragment();
            compassFragment = new CompassFragment();
            contactsFragment = new ContactsFragment();
            experienceSelectionFragment = new ExperienceSelectionFragment();
            trackSelectionFragment = new TrackSelectionFragment();
            telemetryFragment = new TelemetryFragment();
            weatherFragment = new WeatherFragment();
            mapFragment = new MapFragment();
            experienceFragment = new ObjectListFragment() {
                @Override
                public String toString() {
                    return "Esperienza";
                }
            };
            menuFragment = new ObjectListFragment();

            ArrayList<Object> expList = new ArrayList<>();
            expList.add(telemetryFragment);
            expList.add(mapFragment);
            expList.add(experienceSelectionFragment);
            expList.add(trackSelectionFragment);
            expList.add(trackFragment);

            experienceFragment.setList(expList);
            experienceFragment.attachObserver(this);

            ArrayList<Object> menuList = new ArrayList<>();
            menuList.add(experienceFragment);
            menuList.add(weatherFragment);
            menuList.add(contactsFragment);
            menuList.add(compassFragment);

            menuFragment.setList(menuList);
            menuFragment.attachObserver(this);

            new CompassPresenter(compassFragment, this);
            new MapPresenter(mapFragment, this);

            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, menuFragment).commit();
        }
    }

    /**
     * Ridefinisce Activity.onKeyDown().
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, menuFragment).commit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Implementa ISerleenaActivity.getDataSource().
     */
    @Override
    public ISerleenaDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Implementa ISerleenaActivity.getSensorManager().
     */
    @Override
    public ISensorManager getSensorManager() {
        return sensorManager;
    }

    /**
     * Implementa IObjectListObserver.onObjectSelected().
     *
     * @param obj Oggetto selezionato.
     */
    @Override
    public void onObjectSelected(Object obj) {
        Fragment f = (Fragment) obj;
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, f).commit();
    }

}
