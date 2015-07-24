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


package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.GeoPoint;

public class LocationReachedRequest {
    private ILocationReachedObserver observer;
    private GeoPoint location;

    public LocationReachedRequest(
            ILocationReachedObserver observer, GeoPoint location) {
        this.observer = observer;
        this.location = location;
    }

    public ILocationReachedObserver observer() {
        return observer;
    }

    public GeoPoint location() {
        return location;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof LocationReachedRequest) {
            LocationReachedRequest other = (LocationReachedRequest) o;
            return observer.equals(other.observer) &&
                    location.equals(other.location);
        }
        return false;
    }

    public int hashCode() {
        return (int)(location.latitude() + location.longitude());
    }

}
