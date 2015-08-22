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
 * Name: ObjectListFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione file e scrittura javadoc
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.widget.ListAdapter;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.presentation.IObjectListObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Test di unità per la classe ObjectListFragment
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class ObjectListFragmentTest {

    private ObjectListFragment fragment;
    private Activity activity;
    private ArrayList<Object> testList;
    private IObjectListObserver observer;
    private Object o2;
    private ArrayList<Object> otherTestList;

    @Before
    public void initialize() throws Exception {
        activity = Robolectric.buildActivity(Activity.class)
                .create().start().visible().get();
        fragment = new ObjectListFragment();
        activity.getFragmentManager().beginTransaction()
                .add(fragment, "TEST").commit();

        Object o1 = new Object();
        o2 = new Object();
        Object o3 = new Object();
        testList = new ArrayList<>();
        testList.add(o1);
        testList.add(o2);
        testList.add(o3);

        Object o12 = new Object();
        Object o22 = new Object();
        Object o32 = new Object();
        otherTestList = new ArrayList<>();
        otherTestList.add(o12);
        otherTestList.add(o22);
        otherTestList.add(o32);

        observer = mock(IObjectListObserver.class);
        fragment.attachObserver(observer);
        fragment.setList(testList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fragmentShouldThrowWhenSettingNullList() throws Exception {
        fragment.setList(null);
    }

    /**
     * Verifica che gli elementi della lista mostrata sulla vista
     * corrispondano agli oggetti impostati dal metodo setList().
     */
    @Test
    public void settingListShouldSetAdapter() {
        ListAdapter adapter = fragment.getListAdapter();

        assertEquals(testList.size(), adapter.getCount());
        for (int i = 0; i < testList.size(); i++)
            assertEquals(testList.get(i), adapter.getItem(i));
    }

    /**
     * Verifica che gli observer vengano notificati alla selezione di
     * elementi della lista.
     */
    @Test
    public void observerShouldBeNotifiedWhenSelectingItems() {
        fragment.onListItemClick(null, null, 1, 0);
        verify(observer).onObjectSelected(o2);
    }

    /**
     * Verifica che la vista aggiorni la lista visualizzata in base ai dati
     * impostati con setList() durante onResume().
     */
    @Test
    public void onResumeShouldRefreshListItems() throws Exception {
        activity.getFragmentManager().beginTransaction()
                .remove(fragment).commit();
        fragment.setList(otherTestList);
        activity.getFragmentManager().beginTransaction()
                .add(fragment, "TEST").commit();

        ListAdapter adapter = fragment.getListAdapter();

        assertEquals(testList.size(), adapter.getCount());
        for (int i = 0; i < testList.size(); i++)
            assertEquals(testList.get(i), adapter.getItem(i));

        fragment.onResume();

        adapter = fragment.getListAdapter();

        assertEquals(otherTestList.size(), adapter.getCount());
        for (int i = 0; i < otherTestList.size(); i++)
            assertEquals(otherTestList.get(i), adapter.getItem(i));
    }

}