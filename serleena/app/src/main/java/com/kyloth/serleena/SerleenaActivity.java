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


public class SerleenaActivity extends ActionBarActivity implements OnFragmentInteractionListener {

    private int old_id;

    private String oldFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment f = new TrackFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(f,"Percorso");
        oldFrag = "Percorso";
        ft.commit();
        setContentView(R.layout.fragment_track);
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
        removeOldFragment();
        Fragment f = null;

        switch(id) {
            case R.id.screen_menu_exp:
                f = new MapFragment();
                oldFrag = "MAP";
                setContentView(R.layout.fragment_map);
                break;
            case R.id.screen_menu_contact:
                f = new ContactsFragment();
                oldFrag = "CONTACTS";
                setContentView(R.layout.fragment_contacts);
                break;
            case R.id.screen_menu_meteo:
                f = new WeatherFragment();
                oldFrag = "WEATHER";
                setContentView(R.layout.fragment_weather);
                break;
            case R.id.screen_menu_cardio:
                f = new CardioFragment();
                oldFrag = "CARDIO";
                setContentView(R.layout.fragment_cardio);
                break;
            case R.id.screen_menu_compass:
                f = new CompassFragment();
                oldFrag = "COMPASS";
                setContentView(R.layout.fragment_compass_screen);
                break;
            case R.id.screen_menu_sync:
                f = new SyncFragment();
                oldFrag = "SYNC";
                setContentView(R.layout.fragment_sync_screen);
                break;
        }
        getFragmentManager().
                beginTransaction()
                .add(f,oldFrag)
                .commit();
        return true;

        //return super.onOptionsItemSelected(item);
    }

    private void removeOldFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment old = getFragmentManager().findFragmentByTag(oldFrag);
        ft.remove(old);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
