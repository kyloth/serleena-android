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
 * Name: Quadrant.java
 * Package: com.kyloth.serleena.common
 * Author: Tobia Tesan
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Tobia Tesan         2015-05-05  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */
package com.kyloth.serleena.common;

import android.graphics.Bitmap;

/**
 * Classe che realizza l'interfaccia IQuadrant.
 *
 * @use Viene istanziato da \fixedwidth{persistence.sqlite.SerleenaSQLiteDataSource} e restituito al chiamante dietro interfaccia.
 * @field northEast : GeoPoint Punto a nord-est del rettangolo che individua l'area del quadrante
 * @field southWest : GeoPoint Punto a sud-ovest del rettangolo che individua l'area del quadrante
 * @field raster : Bitmap Immagine raster della porzione di mappa associata al quadrante
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
public class Quadrant implements IQuadrant {
    private GeoPoint northEast;
    private GeoPoint southWest;
    private Bitmap raster;

    /**
     * Crea un oggetto Quadrant
     *
     * @param northEast
     * @param southWest
     * @param raster L'immagine raster della mappa per il quadrante. Puo'
     *               essere null.
     * @throws IllegalArgumentException E' lanciata quando northEast o
     *               southEast sono null
     */
    public Quadrant(GeoPoint northEast, GeoPoint southWest, Bitmap raster) throws IllegalArgumentException {
        if (northEast == null || southWest == null) {
            throw new IllegalArgumentException();
        }
        this.northEast = northEast;
        this.southWest = southWest;
        this.raster = raster;
    }

    /**
     * Implementa IQuadrant.getRaster().
     */
    @Override
    public Bitmap getRaster() {
        return raster;
    }

    /**
     * Implementa IQuadrant.getNorthEastPoint().
     */
    @Override
    public GeoPoint getNorthEastPoint() {
        return northEast;
    }

    /**
     * Implementa IQuadrant.getSouthWestPoint().
     */
    @Override
    public GeoPoint getSouthWestPoint() {
        return southWest;
    }

    /**
     * Verifica se un punto e' contenuto nel Quadrant.
     *
     * @param p Punto geografico. Se null, viene sollevata un'eccezione
     *          IllegalArgumentException.
     * @return True se p e' contenuto nel quadrante
     * @throws IllegalArgumentException se p e' null
     */
    @Override
    public boolean contains(GeoPoint p) throws IllegalArgumentException {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return (
                northEast.latitude() <= p.latitude() &&
                p.latitude() <= southWest.latitude() &&
                northEast.longitude() <= p.longitude() &&
                p.longitude() <= southWest.longitude()
        );
    }

}
