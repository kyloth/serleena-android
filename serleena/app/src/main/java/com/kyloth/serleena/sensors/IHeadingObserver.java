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
 * Name: IHeadingObserver.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Gabriele Pozzan   Creazione file e scrittura
 *                                          codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

/**
 * Interfaccia che verrà implementata da un oggetto in grado
 * di osservare un oggetto di tipo IHeadingManager.
 *
 * @use Viene implementata da CompassPresenter e TrackPresenter per ottenere dati sull'orientamento da mostrare sulla vista.
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public interface IHeadingObserver {
    /**
     * Metodo di callback chiamato con l'update dell'oggetto
     * osservato, il quale indica in questo caso la presenza
     * di dati aggiornati dai sensori.
     *
     * @param heading Valore di tipo double che indica la direzione
     *                dell'Escursionista rispetto ai punti cardinali.
     */
    public void onHeadingUpdate(double heading);
}
