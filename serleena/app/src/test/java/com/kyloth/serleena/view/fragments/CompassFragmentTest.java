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
import android.support.v7.app.AppCompatActivity;

import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presenters.CompassPresenter;
import com.kyloth.serleena.presenters.SerleenaActivity;
import com.kyloth.serleena.view.fragments.CompassFragment;
import org.junit.Test;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.view.fragments.CompassFragment
 */
public class CompassFragmentTest {

    private ISerleenaActivity activity = mock(ISerleenaActivity.class);
    private ICompassView fragment = mock(ICompassView.class);
    private ICompassPresenter presenter = mock(ICompassPresenter.class);

    /**
     * Verifica che sia possibile creare un CompassFragment.
     */

    @Test
    public void shouldCreateCompassFragment() {
        fragment = new CompassFragment();
    }

    /**
     * Verifica che sia possibile collegare un ICompassPresenter ad un
     * CompassFragment.
     *
     */

    @Test
    public void shouldAttachCompassPresenter() {
        fragment = new CompassFragment();
        presenter = mock(ICompassPresenter.class);
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che sia possibile collegare un'Activity ad un
     * CompassFragment.
     *
     */

    @Test
    public void shouldAttachActivity() {
        fragment = new CompassFragment();
        ((Fragment) fragment).onAttach(new SerleenaActivity());
    }
}
