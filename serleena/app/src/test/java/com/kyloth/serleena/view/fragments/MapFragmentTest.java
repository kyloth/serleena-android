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
 * Name: MapFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.view.widgets.MapWidget;

import org.robolectric.RobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test di unità per la classe MapFragment
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class MapFragmentTest {

    private MapFragment fragment;
    private IMapPresenter presenter;
    private MapWidget mapWidget;
    private IQuadrant q;
    private GeoPoint location;
    private Iterable<UserPoint> userPoints;

    @Before
    public void initialize() {
        userPoints = new ArrayList<>();
        q = mock(IQuadrant.class);
        location = mock(GeoPoint.class);
        fragment = new MapFragment();
        presenter = mock(IMapPresenter.class);
        fragment.attachPresenter(presenter);

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        mapWidget = mock(MapWidget.class);

        when(inflater.inflate(
                eq(R.layout.fragment_map),
                eq(vg),
                any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.map_widget)).thenReturn(mapWidget);
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
    }

    /**
     * Verifica che i metodi resume() e pause() del presenter vengano
     * chiamati correttamente.
     */
    @Test
    public void testAttachMapPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che attachPresenter() sollevi un'eccezione quando gli vengono
     * passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void attachPresenterShouldThrowWhenNullArgument() {
        fragment.attachPresenter(null);
    }

    /**
     * Verifica che displayQuadrant() sollevi un'eccezione quando gli vengono
     * passati parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayQuadrantShouldThrowWhenNullArgument() {
        fragment.displayQuadrant(null);
    }

    /**
     * Verifica che displayUP() sollevi un'eccezione quando gli vengono passati
     * parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void displayUPShouldThrowWhenNullArgument() {
        fragment.displayUP(null);
    }

    /**
     * Verifica che il metodo displayQuadrant() imposti il MapWidget con il
     * quadrante.
     */
    @Test
    public void displayQuadrantShouldSetWidgetCorrectly() {
        fragment.displayQuadrant(q);
        verify(mapWidget).setQuadrant(q);
    }

    /**
     * Verifica che setUserLocation() imposti correttamente il MapWidget.
     */
    @Test
    public void setUserLocationShouldSetWidgetCorrectly() {
        fragment.setUserLocation(location);
        verify(mapWidget).setUserPosition(location);
    }

    /**
     * Verifica che displayUP() imposti correttamente il MapWidget.
     */
    @Test
    public void displayUPShouldSetWidgetCorrectly() {
        fragment.displayUP(userPoints);
        verify(mapWidget).setUserPoints(userPoints);
    }

    /**
     * Verifica che clear() pulisca correttamente MapWidget.
     */
    @Test
    public void clearShouldCrealWidgetToo() {
        fragment.clear();
        verify(mapWidget).clear();
    }

    /**
     * Verifica che la chiamata di onClick() notifichi il Presenter della
     * volontà di creare un nuovo punto utente.
     */
    @Test
    public void userClickShouldAskPresenterForNewUserPoint()
            throws NoActiveExperienceException, LocationNotAvailableException {
        fragment.onClick(mock(View.class));
        verify(presenter).newUserPoint();
    }

    /**
     * Verifica che toString() ritorni correttamente "Mappa".
     */
    @Test
    public void toStringShouldReturnTheCorrectValue() {
        assertTrue(fragment.toString().equals("Mappa"));
    }

}
