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
 * Name: CompassFragmentTest.java
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
import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.view.widgets.CompassWidget;

import org.robolectric.RobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class CompassFragmentTest {

    private CompassFragment fragment;
    private ICompassPresenter presenter;
    private CompassWidget compassWiget;

    @Before
    public void initialize() {
        fragment = new CompassFragment();
        presenter = mock(ICompassPresenter.class);
        fragment.attachPresenter(presenter);

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        compassWiget = mock(CompassWidget.class);

        when(inflater.inflate(
                        eq(R.layout.fragment_compass),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.compass_widget)).thenReturn(compassWiget);
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
    }

    @Test
    public void testAttachCompassPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che setHeading imposti correttamente l'orientamento sul
     * CompassWidget.
     */
    @Test
    public void setHeadingShouldSetCompassWidgetCorrectly() {
        fragment.setHeading(50);
        verify(compassWiget).setOrientation(50);
    }

    /**
     * Verifica che clearView() reimposti il CompassWidget.
     */
    @Test
    public void clearViewShouldResetCompassWidget() {
        fragment.clearView();
        verify(compassWiget).reset();
    }

    /**
     * Verifica che toString() restituisca "Bussola".
     */
    @Test
    public void toStringShouldReturnCorrectResult() {
        assertTrue(fragment.toString().equals("Bussola"));
    }

}
