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
 * Name: GeoPoint.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import android.location.Location;

/**
 * Rappresenta un punto geografico come una coppia di valori "latitudine" e "longitudine".
 *
 * @use Viene utilizzato dalle componenti di persistenza e di presentazione per rappresentare geneerici punti nello spazio geografico.
 * @field latitude : double Latitudine del punto
 * @field longitude : double Longitudine del punto
 * @author  Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0
 */
public class GeoPoint
{
    private double latitude;
    private double longitude;
    public static double MIN_LATITUDE = -90.0;
    public static double MAX_LATITUDE = 90.0;
    public static double MIN_LONGITUDE = -180.0;
    public static double MAX_LONGITUDE = 180.0;

    /**
     * Crea una nuova istanza di GeoPoint da una coppia di coordinate.
     *
     * @param latitude     Latitudine del punto geografico, -90 < x < 90.
     * @param longitude    Longitudine del punto geografico, -180 < x < 180.
     */
    public GeoPoint(double latitude, double longitude) throws IllegalArgumentException {
        if ( latitude < MIN_LATITUDE   || latitude > MAX_LATITUDE ||
            longitude < MIN_LONGITUDE  || longitude >= MAX_LONGITUDE ) {
            // Si: [0, 2pi) No: [0, 2pi]. 2pi mod 2pi == 0.
            throw new IllegalArgumentException();
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Restituisce la latitudine del punto geografico.
     *
     * @return Coordinata latitudinale.
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Restituisce la longitudine del punto geografico.
     *
     * @return Coordinata longitudinale.
     */
    public double longitude() {
        return longitude;
    }

    /**
     * Restituisce la distanza da un altro punto geografico.
     *
     * @param other Punto da cui si vuole calcolare la distanza.
     * @return Distanza in metri tra i due punti.
     */
    public float distanceTo(GeoPoint other) {
        float[] results = new float[1];
        Location.distanceBetween(latitude(), longitude(), other.latitude(),
                other.longitude(), results);
        return results[0];
    }

    /**
     * Overriding del metodo equals() della superclasse Object.
     *
     * @param o Oggetto da comparare.
     * @return  True se entrambi gli oggetti hanno tipo GeoPoint e si riferiscono
     *          al medesimo punto geografico, quindi con uguali valori di
     *          latitudine e longitudine. False altrimenti.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof GeoPoint) {
            GeoPoint other = (GeoPoint) o;
            return this.latitude() == other.latitude() &&
                this.longitude() == other.longitude();
        }
        return false;
    }

    /**
     * Overriding di Object.hashCode()
     *
     * @return Hash dell'oggetto.
     */
    public int hashCode() {
        return (int)(latitude + longitude);
    }

    public Location toLocation() {
        Location newLocation = new Location("");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        return newLocation;
    }

    public float bearingTo(GeoPoint other) {
        return this.toLocation().bearingTo(other.toLocation());
    }

}
