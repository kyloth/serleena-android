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
 *
 * History:
 * Version  Programmer   Changes
 * 1.0.0    Tobia Tesan  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */
package com.kyloth.serleena.common;

import android.graphics.Bitmap;

/**
 * Classe che realizza l'interfaccia IQuadrant.
 *
 * @use Viene istanziato da persistence.sqlite.SerleenaSQLiteDataSource e restituito al chiamante dietro interfaccia.
 * @field northWest : GeoPoint Punto a nord-est del rettangolo che individua l'area del quadrante
 * @field southEast : GeoPoint Punto a sud-ovest del rettangolo che individua l'area del quadrante
 * @field raster : Bitmap Immagine raster della porzione di mappa associata al quadrante
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
public class Quadrant extends Region implements IQuadrant {
    private Bitmap raster;

    /**
     * Crea un oggetto Quadrant.
     *
     * @param northWest Punto geografico indicante l'angolo nord-ovest del
     *                  quadrante.
     * @param southEast Punto geografico individua l'angolo sud-est del
     *                  quadrante.
     * @param raster    L'immagine raster della mappa associata al quadrante.
     */
    public Quadrant(GeoPoint northWest, GeoPoint southEast, Bitmap raster) {
        super(northWest, southEast);

        if (raster == null)
            throw new IllegalArgumentException("Illegal null raster");

        this.raster = raster;
    }

    /**
     * Implementa IQuadrant.getRaster().
     */
    @Override
    public Bitmap getRaster() {
        return raster;
    }

}
