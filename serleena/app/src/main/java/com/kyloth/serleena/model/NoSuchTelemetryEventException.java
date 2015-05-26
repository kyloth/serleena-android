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
 * Name: NoSuchTelemetryEventException.java
 * Package: com.kyloth.serleena.model
 * Author: Filippo Sestini
 * Date: 2015-05-18
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-18  Creazione file e scrittura di
 *                                          codice e documentazione in Javadoc.
 */

package com.kyloth.serleena.model;

/**
 * Segnala una situazione eccezionale in cui non è possibile ottenere un
 * evento di Tracciamento richiesto.
 *
 * @use Viene sollevato da oggetti ITelemetry in getEvents() (e vari overloading), per segnalare l'assenza di eventi di Tracciamento che soddisfino la richiesta descritta dai parametri.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class NoSuchTelemetryEventException extends Exception {

}
