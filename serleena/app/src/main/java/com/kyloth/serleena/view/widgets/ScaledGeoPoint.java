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
 * Name: ScaledGeoPoint
 * Package: com.kyloth.serleena.view.widgets
 * Author: Filippo Sestini
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Filippo Sestini    Creazione del file, scrittura del codice e di
 *                              Javadoc
 */

package com.kyloth.serleena.view.widgets;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IRegion;

/**
 * Rappresenta un punto geografico scalato in un sistema di coordinate
 * cartesiane arbitrario.
 *
 * @use Viene utilizzato da MapWidget per calcolare le coordinate con cui disporre i punti utente o posizione utente sulla propria area di disegno.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @field width : int Larghezza dell'area su cui scalare il GeoPoint
 * @field height : int Altezza dell'area su cui scalare il GeoPoint
 * @field point : GeoPoint Punto da scalare
 * @field region : IRegion Regione che individua l'area entro cui scalare il punto geografico
 * @version 1.0.0
 */
public class ScaledGeoPoint {

    private final int width;
    private final int height;
    private final GeoPoint point;
    private final IRegion region;

    /**
     * Crea un nuovo oggetto ScaledGeoPoint
     *
     * @param width Larghezza dell'area su cui scalare il GeoPoint
     * @param height Altezza dell'area su cui scalare il GeoPoint
     * @param point Punto da scalare
     * @param region Regione che individua l'area entro cui scalare il punto
     *               geografico
     */
    public ScaledGeoPoint(int width, int height, IRegion region,
                          GeoPoint point) {
        this.width = width;
        this.height = height;
        this.point = point;
        this.region = region;
    }

    /**
     * Ascissa del punto scalato sulla nuova area di riferimento.
     *
     * @return Ascissa.
     */
    public float x() {
        double topLon = region.getNorthWestPoint().longitude();
        double botLon = region.getSouthEastPoint().longitude();
        return (float)(width * (topLon-point.longitude()) / (topLon - botLon));
    }

    /**
     * Ordinata del punto scalato sulla nuova area di riferimento.
     *
     * @return Ordinata.
     */
    public float y() {
        double topLat = region.getNorthWestPoint().latitude();
        double botLat = region.getSouthEastPoint().latitude();
        return (float)(height*(topLat - point.latitude()) / (topLat - botLat));
    }

}
