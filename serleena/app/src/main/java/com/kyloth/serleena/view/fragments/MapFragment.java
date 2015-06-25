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
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.LocationNotAvailableException;
import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;
import com.kyloth.serleena.presentation.IMapView;
import com.kyloth.serleena.view.widgets.MapWidget;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements IMapView,
        View.OnClickListener {

    MapWidget map;
    IMapPresenter presenter;

    /**
     * Crea un nuovo oggetto MapFragment.
     */
    public MapFragment() {
        /* Null object pattern */
        presenter = new IMapPresenter() {
            @Override
            public void newUserPoint() throws NoActiveExperienceException { }
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
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        map = (MapWidget) v.findViewById(R.id.map_widget);
        return v;
    }

    @Override
    public void onClick(View v) {
        try {
            presenter.newUserPoint();
        } catch (NoActiveExperienceException|LocationNotAvailableException e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setUserLocation(GeoPoint point) {
        //map.setUserPosition(point);
        TextView tv = (TextView) getView().findViewById(R.id.map_text);
        tv.setText("Lat: " + point.latitude() + "    Lon: " + point.longitude());
    }

    @Override
    public void displayQuadrant(IQuadrant q) {
        if (q == null)
            throw new IllegalArgumentException("Illegal null quadrant");
        map.setQuadrant(q);
    }

    @Override
    public void displayUP(Iterable<UserPoint> points) {
        if (points == null)
            throw new IllegalArgumentException("Illegal null points");
        map.setUserPoints(points);
    }

    @Override
    public void attachPresenter(IMapPresenter presenter) {
        if (presenter == null)
            throw new IllegalArgumentException("Illegal null presenter");
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        map.setQuadrant(null);
        map.setUserPoints(null);
        map.setUserPosition(null);
    }

    /**
     * Metodo invocato quando il Fragment viene visualizzato.
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Metodo invocato quando il Fragment smette di essere visualizzato.
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    /**
     * Ridefinisce Object.toString().
     */
    @Override
    public String toString() {
        return "Mappa";
    }

}
