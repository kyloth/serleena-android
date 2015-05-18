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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.presentation.IPresenter;
import com.kyloth.serleena.view.fragments.CardioFragment;
import com.kyloth.serleena.view.fragments.CompassFragment;
import com.kyloth.serleena.view.fragments.ContactsFragment;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.fragments.SyncFragment;
import com.kyloth.serleena.view.fragments.TrackFragment;
import com.kyloth.serleena.view.fragments.WeatherFragment;

import java.util.HashMap;
import java.util.Map;


public class SerleenaActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    private int old_id;
    private Map<String,Fragment> myFrags = new HashMap<>();
    private Map<String,IPresenter> myPress = new HashMap<>();
    private Map<String,Integer> myLayoutIds = new HashMap<>();
    private Map<Integer,String> myMenuItemIds = new HashMap<>();

    private String curFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragMap();
        initPresentersMap();
        initLayoutIds();
        initMenuItemIds();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        changeFragment("TRACK");
    }

    private void initFragMap() {
        myFrags.put("TRACK", new TrackFragment());
        myFrags.put("MAP", new MapFragment());
        myFrags.put("CONTACTS", new ContactsFragment());
        myFrags.put("WEATHER", new WeatherFragment());
        myFrags.put("CARDIO",new CardioFragment());
        myFrags.put("COMPASS", new CompassFragment());
        myFrags.put("SYNC", new SyncFragment());
    }

    private void initPresentersMap() {
        myPress.put("MAP",new DummyMapPresenter(myFrags.get("MAP")));
        ((IMapView) myFrags.get("MAP")).attachPresenter((IMapPresenter) myPress.get("MAP"));
    }

    private void initLayoutIds() {
        myLayoutIds.put("TRACK",R.layout.fragment_track);
        myLayoutIds.put("MAP", R.layout.fragment_map);
        myLayoutIds.put("CONTACTS", R.layout.fragment_contacts);
        myLayoutIds.put("WEATHER", R.layout.fragment_weather);
        myLayoutIds.put("CARDIO",R.layout.fragment_cardio);
        myLayoutIds.put("COMPASS", R.layout.fragment_compass_screen);
        myLayoutIds.put("SYNC", R.layout.fragment_sync_screen);
    }

    private void initMenuItemIds() {
        myMenuItemIds.put(R.id.screen_menu_exp,"MAP");
        myMenuItemIds.put(R.id.screen_menu_contact,"CONTACTS");
        myMenuItemIds.put(R.id.screen_menu_meteo,"WEATHER");
        myMenuItemIds.put(R.id.screen_menu_cardio,"CARDIO");
        myMenuItemIds.put(R.id.screen_menu_compass,"COMPASS");
        myMenuItemIds.put(R.id.screen_menu_sync,"SYNC");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serleena, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String newFrag = myMenuItemIds.get(id);
        if(newFrag.equals("MAP")) {
            ((IMapPresenter) myPress.get("MAP")).newUserPoint();
            ((IMapPresenter) myPress.get("MAP")).newUserPoint();
        }

        if(newFrag.equals(curFrag)) return true;
        changeFragment(newFrag);
        return true;

        //return super.onOptionsItemSelected(item);
    }

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

    private void removeFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment old = getFragmentManager().findFragmentByTag(curFrag);
        ft.remove(old);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
