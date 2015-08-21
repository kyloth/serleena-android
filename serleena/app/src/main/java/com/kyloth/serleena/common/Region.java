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
 * Name: Region.java
 * Package: com.kyloth.serleena.common
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer   Changes
 * 1.0.0    Tobia Tesan  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

/**
 * Classe che realizza l'interfaccia IRegion
 *
 * @use TODO
 * @field northWest : GeoPoint Punto a nord-est del rettangolo che individua l'area del quadrante
 * @field southEast : GeoPoint Punto a sud-ovest del rettangolo che individua l'area del quadrante
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */

public class Region implements IRegion {
    private GeoPoint northWest;
    private GeoPoint southEast;

    /**
     * Crea un oggetto Region
     *
     * @param northWest
     * @param southEast
     * @throws IllegalArgumentException E' lanciata quando northWest o
     *               southEast sono null
     */
    public Region(GeoPoint northWest, GeoPoint southEast) throws IllegalArgumentException {
        if (northWest == null || southEast == null) {
            throw new IllegalArgumentException();
        }
        // Nord = 90'. Polo sud = -90'.
        if (northWest.latitude() < southEast.latitude()
                ) {
            throw new IllegalArgumentException();
        }
        this.northWest = northWest;
        this.southEast = southEast;
    }

    /**
     * Restituisce il punto a nord-est dell'area geografica.
     *
     * @return Primo punto geografico.
     */
    @Override
    public GeoPoint getNorthWestPoint() {
        return northWest;
    }

    /**
     * Restituisce il punto a sud-ovest dell'area geografica.
     *
     * @return Secondo punto geografico
     */
    @Override
    public GeoPoint getSouthEastPoint() {
        return southEast;
    }

    /**
     * Verifica se un punto e' contenuto nella Region.
     *
     * @param p Punto geografico. Se null, viene sollevata un'eccezione
     *          IllegalArgumentException.
     * @return True se p e' contenuto nella regione
     * @throws IllegalArgumentException se p e' null
     */
    @Override
    public boolean contains(GeoPoint p) throws IllegalArgumentException {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return (
                // 0,0 e' contenuto in (-10,10), (10,-10).
                northWest.latitude() >= p.latitude() &&
                        p.latitude() >= southEast.latitude() &&
                        northWest.longitude() <= p.longitude() &&
                        p.longitude() <= southEast.longitude()
        );
    }

}
