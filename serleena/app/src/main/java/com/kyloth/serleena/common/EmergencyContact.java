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
 * Name: EmergencyContact.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

/**
 * Rappresenta un contatto di un’autorità locale, identificato dal contatto e
 * dal nome dell'autorità.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public class EmergencyContact {

    private String name;
    private String value;

    /**
     * Crea un oggetto di tipo EmergencyContact.
     *
     * @param name      Nome dell'autorità locale.
     * @param value     Valore del contatto.
     */
    public EmergencyContact(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Restituisce il nome dell'autorità locale.
     *
     * @return Nome dell'autorità locale.
     */
    public String name() {
        return name;
    }

    /**
     * Restituisce il valore del contatto.
     *
     * @return Valore del contatto.
     */
    public String value() {
        return value;
    }

    /**
     * Overriding del metodo equals() della superclasse Object.
     *
     * @param o Oggetto da comparare.
     * @return  True se entrambi gli oggetti hanno tipo EmergencyContact e si
     *          riferiscono al medesimo contatto, quindi con uguale nome e
     *          valore del contatto. False altrimenti.
     */
    public boolean equals(Object o) {
        if (o != null && o instanceof EmergencyContact) {
            EmergencyContact other = (EmergencyContact) o;
            return this.name.equals(other.name) &&
                this.value.equals(other.value);
        }
        return false;
    }
}
