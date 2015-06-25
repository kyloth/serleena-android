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


package com.kyloth.serleena.view.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ICompassPresenter;
import com.kyloth.serleena.presentation.ICompassView;
import com.kyloth.serleena.view.widgets.CompassWidget;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompassFragment extends Fragment implements ICompassView {

    private ICompassPresenter presenter;
    CompassWidget compass;

    /**
     * Crea un nuovo oggetto CompassFragment.
     */
    public CompassFragment() {
        presenter = new ICompassPresenter() {
            @Override
            public void resume() { }
            @Override
            public void pause() { }
        };
    }

    /**
     * Ridefinisce Fragment.onCreateView().
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = (View) inflater.inflate(
                R.layout.fragment_compass,
                container,
                false);
        compass = (CompassWidget) v.findViewById(R.id.compass_widget);
        return v;
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Bussola";
    }

    @Override
    public void setHeading(double heading) {
        compass.setOrientation((float) heading);
    }

    @Override
    public void attachPresenter(ICompassPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }
}
