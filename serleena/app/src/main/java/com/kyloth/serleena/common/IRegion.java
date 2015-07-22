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
 * Name: IRegion.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

/**
 * Rappresenta l’interfaccia esposta da un oggetto rappresentante una regione geografica
 * rettangolare, individuata da due coordinate.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface IRegion {

    /**
     * Restituisce il punto a nord-est dell'area geografica.
     *
     * @return Primo punto geografico.
     */
    GeoPoint getNorthWestPoint();

    /**
     * Restituisce il punto a sud-ovest dell'area geografica.
     *
     * @return Secondo punto geografico
     */
    GeoPoint getSouthEastPoint();

    /**
     * Restituisce true se il punto geografico specificato è contenuto
     * nell'area rappresentata dall'oggetto. False altrimenti.
     *
     * @param p Punto geografico. Se null, viene sollevata un'eccezione
     *          IllegalArgumentException.
     * @return True se il punto è contenuto nell'area. False altrimenti.
     * @throws IllegalArgumentException
     */
    boolean contains(GeoPoint p) throws IllegalArgumentException;

}
