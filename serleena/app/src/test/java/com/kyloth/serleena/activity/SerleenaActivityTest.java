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
 * Name: SerleenaActivityTest
 * Package: com.kyloth.serleena.activity
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura di codice e
 *                              Javadoc
 */

package com.kyloth.serleena.activity;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.R;
import com.kyloth.serleena.view.fragments.CompassFragment;
import com.kyloth.serleena.view.fragments.ContactsFragment;
import com.kyloth.serleena.view.fragments.ExperienceSelectionFragment;
import com.kyloth.serleena.view.fragments.MapFragment;
import com.kyloth.serleena.view.fragments.ObjectListFragment;
import com.kyloth.serleena.view.fragments.QuitFragment;
import com.kyloth.serleena.view.fragments.SyncFragment;
import com.kyloth.serleena.view.fragments.TelemetryFragment;
import com.kyloth.serleena.view.fragments.TrackFragment;
import com.kyloth.serleena.view.fragments.TrackSelectionFragment;
import com.kyloth.serleena.view.fragments.WeatherFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19,
        manifest = "src/main/AndroidManifest.xml")
public class SerleenaActivityTest {

    SerleenaActivity activity;
    static final String MENU_TELEMETRY_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_telemetryFragment);
    static final String MENU_TRACK_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_trackFragment);
    static final String MENU_MAP_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_mapFragment);
    static final String MENU_CONTACTS_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_contactsFragment);
    static final String MENU_TRACK_SEL_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_trackSelectionFragment);
    static final String MENU_EXPERIENCE_SEL_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_experienceSelectionFragment);
    static final String MENU_EXPERIENCE_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_experienceFragment);
    static final String MENU_SYNC_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_syncFragment);
    static final String MENU_COMPASS_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_compassFragment);
    static final String MENU_WEATHER_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_weatherFragment);
    static final String MENU_QUIT_LABEL = RuntimeEnvironment.application.getResources().getString(R.string.menu_quitFragment);

    @Before
    public void initialize() {
        activity = Robolectric.buildActivity(SerleenaActivity.class)
                .create().start().visible().get();
        currentFragment().onResume();
    }

    /**
     * Verifica che il primo Fragment visualizzato dall'activity sia il menu
     * principale, e che questo elenchi tutti i contenuti previsti dalla
     * specifica.
     */
    @Test
    public void firstFragmentShouldDisplayACompleteMainMenu() {
        assertTrue(isValidMainMenu(currentFragment()));
    }

    /**
     * Verifica che venga visualizzato il menu principale in seguito alla
     * pressione del bottone fisico "Menu".
     */
    @Test
    public void activityShouldShowMainMenuWhenMenuHardButtonPressed() {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_container, new Fragment()).commit();
        assertFalse(isValidMainMenu(currentFragment()));
        activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
        assertTrue(isValidMainMenu(currentFragment()));
    }

    /**
     * Verifica che non venga cambiato Fragment se il bottone premuto è
     * diverso da "Menu".
     */
    @Test
    public void activityShouldDoNothingWhenButtonOtherThanMenuPressed() {
        activity.getFragmentManager().beginTransaction()
                .replace(R.id.main_container, new Fragment()).commit();
        assertFalse(isValidMainMenu(currentFragment()));
        activity.onKeyDown(KeyEvent.KEYCODE_0, null);
        assertFalse(isValidMainMenu(currentFragment()));
    }

    /**
     * Verifica che l'activity risponda alla selezione di un Fragment da una
     * ListView visualizzandolo in primo piano.
     */
    @Test
    public void activityShouldShowFragmentWhenSelectedFromListView() {
        Fragment testFragment = new Fragment();
        activity.onObjectSelected(testFragment);
        assertEquals(currentFragment(), testFragment);
    }

    /**
     * Verifica che il metodo onCreate() inizializzi l'activity anche se
     * viene passato un oggetto Bundle diverso da null.
     */
    @Test
    public void activityDoesNothingIfBundleNotNull() {
        activity = Robolectric.buildActivity(SerleenaActivity.class).create
                (Bundle.EMPTY).start().visible().get();
        assertNotNull(currentFragment());
    }

    /**
     * Verifica che la schermata Esperienza mostri una lista corretta di
     * visuali, secondo specifica.
     */
    @Test
    public void experienceViewShouldShowACorrectListOfFragments() {
        ObjectListFragment experienceFragment = null;
        ListAdapter menuAdapter =
                ((ListFragment) currentFragment()).getListAdapter();
        for (int i = 0; i < menuAdapter.getCount(); i++)
            if (menuAdapter.getItem(i).toString().equals(MENU_EXPERIENCE_LABEL))
                experienceFragment = (ObjectListFragment)menuAdapter.getItem(i);

        assertNotNull(experienceFragment);
        activity.onObjectSelected(experienceFragment);
        experienceFragment.onResume();
        assertTrue(isValidExperienceFragment(experienceFragment));
    }

    private Fragment currentFragment() {
        return activity.getFragmentManager().findFragmentById(R.id
                .main_container);
    }

    private static boolean isValidExperienceFragment(
            ObjectListFragment fragment) {
        ListAdapter listAdapter = fragment.getListAdapter();

        if (listAdapter.getCount() != 5)
            return false;

        List<Fragment> fragmentsInExp = new ArrayList<>();
        for (int i = 0; i < listAdapter.getCount(); i++)
            fragmentsInExp.add((Fragment) listAdapter.getItem(i));

        return containsFragment(
                        fragmentsInExp,
                        TelemetryFragment.class,
                        MENU_TELEMETRY_LABEL) &&
                containsFragment(
                        fragmentsInExp,
                        MapFragment.class,
                        MENU_MAP_LABEL) &&
                containsFragment(
                        fragmentsInExp,
                        ExperienceSelectionFragment.class,
                        MENU_EXPERIENCE_SEL_LABEL) &&
                containsFragment(
                        fragmentsInExp,
                        TrackSelectionFragment.class,
                        MENU_TRACK_SEL_LABEL) &&
                containsFragment(
                        fragmentsInExp,
                        TrackFragment.class,
                        MENU_TRACK_LABEL);
    }

    private static boolean isValidMainMenu(Fragment fragment) {
        try {
            ListAdapter listAdapter =
                    ((ObjectListFragment) fragment).getListAdapter();

            if (listAdapter.getCount() != 6)
                return false;

            List<Fragment> fragmentsInMenu = new ArrayList<>();
            for (int i = 0; i < listAdapter.getCount(); i++)
                fragmentsInMenu.add((Fragment) listAdapter.getItem(i));

            return containsFragment(
                            fragmentsInMenu,
                            CompassFragment.class,
                            MENU_COMPASS_LABEL) &&
                    containsFragment(
                            fragmentsInMenu,
                            ObjectListFragment.class,
                            MENU_EXPERIENCE_LABEL) &&
                    containsFragment(
                            fragmentsInMenu,
                            WeatherFragment.class,
                            MENU_WEATHER_LABEL) &&
                    containsFragment(
                            fragmentsInMenu,
                            SyncFragment.class,
                            MENU_SYNC_LABEL) &&
                    containsFragment(
                            fragmentsInMenu,
                            QuitFragment.class,
                            MENU_QUIT_LABEL) &&
                    containsFragment(
                            fragmentsInMenu,
                            ContactsFragment.class,
                            MENU_CONTACTS_LABEL);
        } catch (ClassCastException e) {
            return false;
        }
    }

    private static boolean containsFragment(Iterable<Fragment> fragments,
                                            Class fragClass,
                                            String title) {
        boolean result = false;
        for (Fragment f : fragments)
            result |= fragClass.isInstance(f) && f.toString().equals(title);
        return result;
    }
}
