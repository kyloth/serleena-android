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
 * Name: InboundStreamParser.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

/**
 * Interfaccia per un oggetto in grado di leggere un InboundStream ricevuto
 * dal servizio remoto attraverso un opportuno proxy e restituire una
 * collezione di IDataEntity indipendenti dal formato di rappresentazione
 * originario.
 *
 * @use E' usato da KylothCloudSynchronizer che fornisce un InboundStream prodotto da un idoneo proxy e ne raccoglie l'output per passarlo a un InboundDumpBuilder.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
public interface InboundStreamParser {
    /**
     * Dato un InboundStream contenente dati di sincronizzazione, fornisce
     * una rappresentazione intermedia agnostica.
     *
     * @return Una collezione di IDataEntity che costituiscono una
     *         rappresentazione agnostica dei dati forniti dal servizio.
     */
    IDataEntity parse();
}
