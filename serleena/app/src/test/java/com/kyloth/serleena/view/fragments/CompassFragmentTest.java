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
 * Author: Sebastiano Valle
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Sebastiano Valle Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.view.KeyEvent;

import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presenters.SerleenaActivity;
import com.kyloth.serleena.view.fragments.CompassFragment;
import com.kyloth.serleena.view.widgets.CompassWidget;

import junit.framework.Assert;

import org.junit.Test;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Throwable;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.CompassFragment
 */
public class CompassFragmentTest
        extends ActivityUnitTestCase<SerleenaActivity> {

    private SerleenaActivity activity;
    private CompassFragment fragment;
    private ICompassPresenter presenter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Costruttore che passa alla superclasse il tipo di Activity da
     * istanziare.
     *
     */
    public CompassFragmentTest() {
        super(SerleenaActivity.class);
    }

    /**
     * Override di setUp della superclasse.
     *
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
        Intent i = new Intent("android.intent.action.MAIN");
        startActivity(i, null, null);
        testAttachCompassPresenter();
        //testClearView();
        testSetHeading();
        testShouldDoNothingOnKeyDown();
    }

    /**
     * Verifica che sia possibile collegare un ICompassPresenter ad un
     * CompassFragment.
     *
     */
    @Test
    public void testAttachCompassPresenter() {
        fragment = new CompassFragment();
        presenter = mock(ICompassPresenter.class);
        fragment.attachPresenter(presenter);
        fragment.onResume();
        fragment.onPause();
        verify(presenter).resume();
        verify(presenter).pause();
    }

    /**
     * Verifica che sia possibile collegare un ICompassPresenter ad un
     * CompassFragment.
     *
     */
    @Test
    public void testSetHeading() {
        fragment = new CompassFragment();
        activity = new SerleenaActivity();
        CompassWidget wid = mock(CompassWidget.class);
        //fragment.setHeading(20);
    }

    /**
     * Verifica che sia possibile smettere di visualizzare la bussola.
     *
     */
    @Test
    public void testClearView() {
        fragment = new CompassFragment();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("View not cleared");
        fragment.clearView();
    }

    /**
     * Verifica che alla pressione del pulsante centrale non avvenga
     * nulla.
     *
     */
    @Test
    public void testShouldDoNothingOnKeyDown() {
        fragment = new CompassFragment();
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("View not cleared");
        fragment.keyDown(0, new KeyEvent(1,KeyEvent.KEYCODE_ENTER));
    }
}
