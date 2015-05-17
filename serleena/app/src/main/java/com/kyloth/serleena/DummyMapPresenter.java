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
import android.graphics.Bitmap;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.presentation.IMapPresenter;

import java.util.ArrayList;

/**
 * Stub per mockup
 */
public class DummyMapPresenter implements IMapPresenter {
    private MapFragment myMap;
    private int cnt;
    private ArrayList<UserPoint> myUps = new ArrayList<>();

    public DummyMapPresenter(Fragment fragment) {
        if(fragment instanceof MapFragment)
            myMap = (MapFragment) fragment;
    }

    @Override
    public void newUserPoint() {
        switch(cnt) {
            case 0:
                myUps.add(new UserPoint(240,240));
                break;
            case 1:
                myUps.add(new UserPoint(120,640));
                break;
            default:
                myUps.add(new UserPoint(500,500));
        }
        cnt++;
    }

    @Override
    public void resume() {
        myMap.displayQuadrant(new IQuadrant() {
            @Override
            public Bitmap getRaster() {
                return null;
            }

            @Override
            public GeoPoint getFirstPoint() {
                return null;
            }

            @Override
            public GeoPoint getSecondPoint() {
                return null;
            }
        });
        myMap.setUserLocation(new GeoPoint(640,120));
        myMap.displayUP(myUps);
    }

    @Override
    public void pause() {

    }
}
