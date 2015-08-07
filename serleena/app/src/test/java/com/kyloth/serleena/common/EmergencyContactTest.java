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
 * Name: EmergencyContactTest.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

import com.kyloth.serleena.persistence.sqlite.TestFixtures;

import org.junit.Test;

import java.lang.Object;
import java.lang.String;
import dalvik.annotation.TestTargetClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Contiene i test di unità per la classe EmergencyContact.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class EmergencyContactTest {

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     *
     * Vengono testati i valori restituiti dai metodi della classe.
     * In particolare, si verifica che i valori passati al costruttore nella
     * creazione dell'oggetto vengano correttamente restituiti dai metodi.
     */
    @Test
    public void testLocationTelemetryEventGetters() {
        String name = TestFixtures.CONTACTS_FIXTURE_1_NAME;
        String value = TestFixtures.CONTACTS_FIXTURE_1_VALUE;
        assertNotEquals(name, TestFixtures.CONTACTS_FIXTURE_2_NAME);
        assertNotEquals(value, TestFixtures.CONTACTS_FIXTURE_2_VALUE);

        EmergencyContact c = new EmergencyContact(name, value);

        assertEquals(c.name(), name);
        assertEquals(c.value(), value);
        assertNotEquals(c.name(), TestFixtures.CONTACTS_FIXTURE_2_NAME);
        assertNotEquals(c.value(), TestFixtures.CONTACTS_FIXTURE_2_VALUE);
    }

    /**
     * Testa la correttezza del metodo equals().
     */
    @Test
    public void testEqualsMethod() {
        String name1 = TestFixtures.CONTACTS_FIXTURE_1_NAME;
        String value1 = TestFixtures.CONTACTS_FIXTURE_1_VALUE;
        assertNotEquals(name1, TestFixtures.CONTACTS_FIXTURE_2_NAME);
        assertNotEquals(value1, TestFixtures.CONTACTS_FIXTURE_2_VALUE);

        EmergencyContact c1 = new EmergencyContact(name1, value1);

        String name2 = TestFixtures.CONTACTS_FIXTURE_1_NAME;
        String value2 = TestFixtures.CONTACTS_FIXTURE_1_VALUE;

        EmergencyContact c2 = new EmergencyContact(name2, value2);

        String name3 = TestFixtures.CONTACTS_FIXTURE_2_NAME;
        String value3 = TestFixtures.CONTACTS_FIXTURE_2_VALUE;
        EmergencyContact c3 = new EmergencyContact(name3, value3);

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, new Object());
    }

}
