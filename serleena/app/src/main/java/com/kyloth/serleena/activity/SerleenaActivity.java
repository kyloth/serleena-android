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
 * Package: com.kyloth.serleena.activity
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura del codice e di
 *                              Javadoc
 */
package com.kyloth.serleena.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;

import com.kyloth.serleena.R;
import com.kyloth.serleena.model.*;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.persistence.IPersistenceDataSource;
import com.kyloth.serleena.persistence.sqlite.CachedSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSink;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.*;
import com.kyloth.serleena.presenters.*;
import com.kyloth.serleena.sensors.*;
import com.kyloth.serleena.synchronization.KylothCloudSynchronizer;
import com.kyloth.serleena.synchronization.net.INetProxy;
import com.kyloth.serleena.synchronization.net.SerleenaJSONNetProxy;
import com.kyloth.serleena.synchronization.kylothcloud.LocalEnvKylothIdSource;
import com.kyloth.serleena.view.fragments.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Classe che implementa ISerleenaActivity.
 *
 * Rappresenta l'unica Activity dell'applicazione, il punto di accesso
 * principale alle sue risorse. Si occupa di creare le viste e i presenter, e
 * agganciarli le une agli altri.
 *
 * @use Ogni presenter dell'applicazione mantiene un riferimento all'Activity dietro interfaccia ISerleenaActivity.
 * @field application : ISerleenaApplication Applicazione serleena
 * @field trackFragment : TrackFragment Visuale Percorso
 * @field compassFragment : CompassFragment Schermata Bussola
 * @field contactsFragment : ContactsFragment Schermata Autorita` locali
 * @field telemetryFragment : TelemetryFragment Visuale Tracciamento
 * @field weatherFragment : WeatherFragment Schermata Meteo
 * @field mapFragment : MapFragment Visuale Mappa
 * @field experienceSelectionFragment : ExperienceSelectionFragment Imposta Esperienza
 * @field trackSelectionFragment : TrackSelectionFragment Visuale Imposta Percorso
 * @field menuFragment : ObjectListFragment Schermata menu` principale
 * @field experienceFragment : ObjectListFragment Schermata Esperienza
 * @field syncFragment : SyncFragment Schermata Sincronizza
 * @author Filippo Sestini <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.support.v7.app.AppCompatActivity
 */
public class SerleenaActivity extends Activity
        implements ISerleenaActivity, IObjectListObserver {

    private ISerleenaApplication application;

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
    private SyncFragment syncFragment;

    /**
     * Ridefinisce Activity.onCreate().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serleena);

        application = (ISerleenaApplication) getApplication();

        if (findViewById(R.id.main_container) != null) {
            if (savedInstanceState != null)
                return;

            initFragments();
            initPresenters();

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
     *
     * Inoltra l'oggetto ISerleenaDataSource restituito dall'applicazione
     * ISerleenaApplication.
     */
    @Override
    public ISerleenaDataSource getDataSource() {
        return application.getDataSource();
    }

    /**
     * Implementa ISerleenaActivity.getSensorManager().
     *
     * Inoltra l'oggetto ISensorManager restituito dall'applicazione
     * ISerleenaApplication.
     */
    @Override
    public ISensorManager getSensorManager() {
        return application.getSensorManager();
    }

    /**
     * Implementa ISerleenaActivity.getDataSink().
     *
     * Inoltra l'oggetto IPersistenceDataSink restituito dall'applicazione
     * ISerleenaApplication.
     */
    @Override
    public IPersistenceDataSink getDataSink() {
        return application.getDataSink();
    }

    /**
     * Implementa IObjectListObserver.onObjectSelected().
     *
     * Riceve eventi di selezione da parte delle schermate Esperienza e dal
     * menu principale. Il Fragment selezionato viene visualizzato in primo
     * piano.
     *
     * @param obj Oggetto selezionato.
     */
    @Override
    public void onObjectSelected(Object obj) {
        Fragment f = (Fragment) obj;
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, f).addToBackStack("fragment")
                .commit();
    }

    /**
     * Inizializza i Fragment.
     */
    private void initFragments() {
        QuitFragment quitFragment = new QuitFragment();
        trackFragment = new TrackFragment();
        compassFragment = new CompassFragment();
        contactsFragment = new ContactsFragment();
        experienceSelectionFragment = new ExperienceSelectionFragment();
        trackSelectionFragment = new TrackSelectionFragment();
        telemetryFragment = new TelemetryFragment();
        weatherFragment = new WeatherFragment();
        mapFragment = new MapFragment();
        syncFragment = new SyncFragment();
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
        menuList.add(syncFragment);
        menuList.add(quitFragment);

        menuFragment.setList(menuList);
        menuFragment.attachObserver(this);

        quitFragment.setOnYesClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSensorManager().getTrackCrossingManager().abort();
                finish();
            }
        });
        quitFragment.setOnNoClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyDown(KeyEvent.KEYCODE_MENU, null);
            }
        });
    }

    /**
     * Inizializza i Presenter.
     */
    private void initPresenters() {
        new CompassPresenter(compassFragment, this);
        new ContactsPresenter(contactsFragment, this);
        ExperienceSelectionPresenter esp =
                new ExperienceSelectionPresenter(
                        experienceSelectionFragment, this);
        new MapPresenter(mapFragment, this, esp);
        new TrackSelectionPresenter(trackSelectionFragment, this, esp);
        new WeatherPresenter(weatherFragment, this);
        new TrackPresenter(trackFragment, this);
        new TelemetryPresenter(telemetryFragment, this);
        new SyncPresenter(syncFragment, this);

    }

}
