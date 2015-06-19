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
 * Name: ListAdapter.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

import java.util.Iterator;
import java.util.List;

/**
 * Adatta l'interfaccia DirectAccessList all'interfaccia List di Java.
 *
 * @use Viene istanziato passando l'oggetto List da adattare come parametro al costruttore. Viene utilizzato in SerleenaSQLiteDataSource per la creazione di strutture DirectAccessList a partire da oggetti ArrayList.
 * @field list : List<T> Oggetto List<T> da adattare
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @see DirectAccessList
 */
public class ListAdapter<T> implements DirectAccessList<T> {

    private final List<T> list;

    /**
     * Crea un adapter.
     *
     * @param list Lista da adattare.
     */
    public ListAdapter(List<T> list) {
        this.list = list;
    }

    /**
     * Implementa DirectAccessList.size().
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Implementa DirectAccessList.get().
     */
    @Override
    public T get(int index) {
        return list.get(index);
    }

    /**
     * Implementa Iterable.iterator().
     *
     * @return Iteratore.
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

}
