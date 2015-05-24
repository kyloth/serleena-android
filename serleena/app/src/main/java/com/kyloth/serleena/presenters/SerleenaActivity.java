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
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */
package com.kyloth.serleena.presenters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.kyloth.serleena.DummyCardioPresenter;
import com.kyloth.serleena.DummyCompassPresenter;
import com.kyloth.serleena.DummyContactsPresenter;
import com.kyloth.serleena.DummyExpSelPresenter;
import com.kyloth.serleena.DummyMapPresenter;
import com.kyloth.serleena.DummySyncPresenter;
import com.kyloth.serleena.DummyTrackPresenter;
import com.kyloth.serleena.DummyTrackSelPresenter;
import com.kyloth.serleena.DummyWeatherPresenter;
import com.kyloth.serleena.R;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.model.Telemetry;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presentation.ICardioView;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.presentation.IContactsView;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.presentation.IPresenter;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ISyncView;
import com.kyloth.serleena.presentation.ITelemetryView;
import com.kyloth.serleena.presentation.ITrackSelectionPresenter;
import com.kyloth.serleena.presentation.ITrackSelectionView;
import com.kyloth.serleena.presentation.ITrackView;
import com.kyloth.serleena.presentation.IWeatherView;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SensorManager;
import com.kyloth.serleena.view.fragments.CardioFragment;
import com.kyloth.serleena.view.fragments.CompassFragment;
import com.kyloth.serleena.view.fragments.ContactsFragment;
import com.kyloth.serleena.view.fragments.ExperienceSelectionFragment;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.fragments.SyncFragment;
import com.kyloth.serleena.view.fragments.TelemetryFragment;
import com.kyloth.serleena.view.fragments.TrackFragment;
import com.kyloth.serleena.view.fragments.TrackSelectionFragment;
import com.kyloth.serleena.view.fragments.WeatherFragment;

import java.util.HashMap;
import java.util.Map;


/**
 * Classe che implementa ISerleenaActivity.
 *
 * In questa visuale è possibile selezionare un'esperienza da attivare tra quelle disponibili.
 *
 * @field myFrags : Map<String,Fragment> lista dei Fragment istanziati nell'Activity
 * @field myPress : Map<String,IPresenter> lista degli IPresenter istanziati nell'Activity
 * @field myLayoutIds : Map<String,Integer> mappa di corrispondenze tra stringhe e id dei layout
 * @field myMenuItemIds : Map<Integer,String> mappa di corrispondenze tra id dei menu item e visuale richiesta
 * @field curFrag : String stringa contenente il nome della visuale attualmente presente sul display
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.support.v7.app.AppCompatActivity
 */
public class SerleenaActivity extends AppCompatActivity implements ISerleenaActivity {

    /**
     * Lista dei Fragment.
     */
    private Map<String,Fragment> myFrags = new HashMap<>();
    /**
     * Lista dei Presenter.
     */
    private Map<String,IPresenter> myPress = new HashMap<>();
    /**
     * Matrice di corrispondenza per gli id dei layout.
     */
    private Map<String,Integer> myLayoutIds = new HashMap<>();
    /**
     * Matrice di corrispondenza per le voci del menù.
     */
    private Map<Integer,String> myMenuItemIds = new HashMap<>();

    /**
     * Tag del Fragment attualmente visualizzato.
     */
    private String curFrag;

    private boolean showingMenu = false;
    private ISerleenaDataSource dataSource;
    private ISensorManager sensorManager;

    /**
     * Metodo invocato alla creazione dell'Activity. Vengono inizializzate le
     * mappe e viene visualizzato il Fragment iniziale
     *
     * @param savedInstanceState istanza che viene rigenerata dal sistema operativo dopo una terminazione dell'applicazione
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragMap();
        initPresentersMap();
        initLayoutIds();
        initMenuItemIds();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        changeFragment("TELEMETRY");

        dataSource = new SerleenaDataSource(
                new SerleenaSQLiteDataSource(this, new SerleenaDatabase(this, 1)));
        sensorManager = SensorManager.getInstance(this);
    }

    /**
     * Metodo che istanzia i Fragment nell'Activity e li mappa a dei tag.
     */
    private void initFragMap() {
        myFrags.put("TRACK", new TrackFragment());
        myFrags.put("MAP", new MapFragment());
        myFrags.put("TELEMETRY", new TelemetryFragment());
        myFrags.put("EXPLIST", new ExperienceSelectionFragment());
        myFrags.put("TRACKLIST", new TrackSelectionFragment());
        myFrags.put("CONTACTS", new ContactsFragment());
        myFrags.put("WEATHER", new WeatherFragment());
        myFrags.put("CARDIO", new CardioFragment());
        myFrags.put("COMPASS", new CompassFragment());
        myFrags.put("SYNC", new SyncFragment());
    }

    /**
     * Metodo che istanzia i Presenter nell'Activity e li mappa a dei tag.
     */
    private void initPresentersMap() {
        myPress.put("MAP", new DummyMapPresenter((IMapView) myFrags.get("MAP")));
        myPress.put("TRACKLIST", new DummyTrackSelPresenter((ITrackSelectionView) myFrags.get("TRACKLIST"), this));
        myPress.put("EXPLIST", new DummyExpSelPresenter((IExperienceSelectionView) myFrags.get("EXPLIST"), this));
        myPress.put("CARDIO", new DummyCardioPresenter((ICardioView) myFrags.get("CARDIO"), this));
        myPress.put("TELEMETRY", new TelemetryPresenter((ITelemetryView) myFrags.get("TELEMETRY"), this));
        myPress.put("CONTACTS", new DummyContactsPresenter((IContactsView) myFrags.get("CONTACTS"), this));
        myPress.put("TRACK", new DummyTrackPresenter((ITrackView) myFrags.get("TRACK"), this));
        myPress.put("WEATHER", new DummyWeatherPresenter((IWeatherView) myFrags.get("WEATHER"),this));
        myPress.put("SYNC", new DummySyncPresenter((ISyncView) myFrags.get("SYNC"),this));
        myPress.put("COMPASS", new DummyCompassPresenter((ICompassView) myFrags.get("COMPASS"),this));
    }

    /**
     * Metodo che mappa i tag ai layout da visualizzare.
     */
    private void initLayoutIds() {
        myLayoutIds.put("TRACK", R.layout.fragment_track);
        myLayoutIds.put("MAP", R.layout.fragment_map);
        myLayoutIds.put("TELEMETRY", R.layout.fragment_telemetry);
        myLayoutIds.put("EXPLIST", R.layout.fragment_exp_selection_list);
        myLayoutIds.put("TRACKLIST", R.layout.fragment_track_selection_list);
        myLayoutIds.put("CONTACTS", R.layout.fragment_contacts);
        myLayoutIds.put("WEATHER", R.layout.fragment_weather);
        myLayoutIds.put("CARDIO",R.layout.fragment_cardio);
        myLayoutIds.put("COMPASS", R.layout.fragment_compass_screen);
        myLayoutIds.put("SYNC", R.layout.fragment_sync_screen);
    }

    /**
     * Metodo che mappa le voci dei menù ai tag delle varie visuali e schermate.
     */
    private void initMenuItemIds() {
        myMenuItemIds.put(R.id.screen_menu_exp,"TRACK");
        myMenuItemIds.put(R.id.screen_menu_contact,"CONTACTS");
        myMenuItemIds.put(R.id.screen_menu_meteo,"WEATHER");
        myMenuItemIds.put(R.id.screen_menu_cardio,"CARDIO");
        myMenuItemIds.put(R.id.screen_menu_compass,"COMPASS");
        myMenuItemIds.put(R.id.screen_menu_sync, "SYNC");
    }

    /**
     * Metodo che aggiunge un menù all'ActionBar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_serleena, menu);
        showingMenu = true;
        return true;
    }

    /**
     * Metodo invocato all'apertura di un menù.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        showingMenu = true;
        return true;
    }

    /**
     * Metodo invocato alla chiusura di un menù.
     */
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        showingMenu = false;
    }

    /**
     * Metodo che gestisce la selezione di voci nel menù presente nella ActionBar.
     *
     * Tale metodo ha il compito di alternare i vari Fragment in base
     * all'opzione scelta dall'utente.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String newFrag = myMenuItemIds.get(id);
        if(newFrag.equals("MAP")) {
            try {
                ((IMapPresenter) myPress.get("MAP")).newUserPoint();
                ((IMapPresenter) myPress.get("MAP")).newUserPoint();
            } catch(NoActiveExperienceException e) {}
        }

        if(newFrag.equals(curFrag)) return true;
        changeFragment(newFrag);
        return true;
    }

    /**
     * Metodo utilizzato per rimpiazzare un Fragment (o aggiungerne uno se non
     * vi è alcun Fragment visualizzato) sull'Activity.
     *
     * Inizialmente il Fragment precedente viene rimosso, per poi aggiungere
     * il Fragment richiesto dall'utente.
     */
    private void changeFragment(String newFrag) {
        if(curFrag != null)
            removeFragment();
        curFrag = newFrag;
        getFragmentManager().
                beginTransaction()
                .add(myFrags.get(curFrag), curFrag)
                .commit();
        setContentView(myLayoutIds.get(curFrag));
    }

    /**
     * Metodo utilizzato per rimuovere un Fragment dal FragmentManager
     * dell'Activity.
     */
    private void removeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment old = getFragmentManager().findFragmentByTag(curFrag);
        ft.remove(old);
        ft.commit();
    }

    /**
     * Metodo richiamato
     *
     * @param keyCode tasto premuto
     * @param event KeyEvent avvenuto
     * @return true se l'evento viene gestito correttamente
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
            if(showingMenu)
                closeOptionsMenu();
            else
                openOptionsMenu();
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if(curFrag.equals("EXPLIST")) changeFragment("TRACKLIST");
            if(curFrag.equals("TRACKLIST")) changeFragment("TRACK");
            if(curFrag.equals("TRACK")) changeFragment("MAP");
            if(curFrag.equals("MAP")) changeFragment("TELEMETRY");
            if(curFrag.equals("TELEMETRY")) changeFragment("EXPLIST");
        }
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            Fragment frag = myFrags.get(curFrag);
            if(frag instanceof MapFragment)
                ((MapFragment) frag).keyDown(keyCode,event);
            if(frag instanceof TelemetryFragment)
                ((TelemetryFragment) frag).keyDown(keyCode,event);
            if(frag instanceof WeatherFragment)
                ((WeatherFragment) frag).keyDown(keyCode,event);
            if(frag instanceof ContactsFragment)
                ((ContactsFragment) frag).keyDown(keyCode,event);
            if(frag instanceof SyncFragment)
                ((SyncFragment) frag).keyDown(keyCode,event);
        }
        return true;
    }

    /**
     * Implementazione di ISerleenaActivity.setActiveExperience().
     *
     * Segnala all'activity l'attivazione di un'Esperienza. L'activity si
     * occupa di inoltrare l'informazione agli altri presenter che
     * ne necessitano.
     *
     * @param experience Esperienza attivata.
     */
    @Override
    public void setActiveExperience(IExperience experience) {
        TrackSelectionPresenter tsPres =
                (TrackSelectionPresenter) myPress.get("TRACKLIST");
        tsPres.setActiveExperience(experience);
        MapPresenter mPres =
                (MapPresenter) myPress.get("MAP");
        mPres.setActiveExperience(experience);
    }

    /**
     * Implementazione di ISerleenaActivity.setActiveTrack().
     *
     * Segnala all'activity l'attivazione di un Percorso. L'activity si
     * occupa di inoltrare l'informazione agli altri presenter che
     * ne necessitano.
     */
    @Override
    public void setActiveTrack(ITrack track) {
        TrackPresenter presenter = (TrackPresenter) myPress.get("TRACK");
        presenter.setActiveTrack(track);
    }

    /**
     * Metodo che implementa l'abilitazione del tracciamento per il percorso
     * attivo.
     */
    @Override
    public void enableTelemetry() {

    }

    /**
     * Metodo che implementa la disabilitazione del tracciamento per il
     * percorso attivo.
     */
    @Override
    public void disableTelemetry() {

    }

    /**
     * Metodo che implementa getDataSource() di SerleenaActivity.
     */
    @Override
    public ISerleenaDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Metodo che restituisce il gestore dei sensori dell'applicazione.
     */
    @Override
    public ISensorManager getSensorManager() {
        return sensormanager;
    }
}
