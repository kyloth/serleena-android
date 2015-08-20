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
 * Name: CheckpointReachedTelemetryEventTest.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import com.kyloth.serleena.common.*;
import org.junit.Test;

import java.lang.Object;
import java.util.Date;
import dalvik.annotation.TestTargetClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Contiene i test di unità per la classe CheckpointReachedTelemetryEvent.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @see com.kyloth.serleena.common.CheckpointReachedTelemetryEvent
 */
public class CheckpointReachedTelemetryEventTest {

    /**
     * Verifica che il costruttore sollevi un'eccezione se viene passato un
     * numero di checkpoint nullo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfNullOrNegativeCheckpointNumber1() {
        new CheckpointReachedTelemetryEvent(1000, 0);
    }

    /**
     * Verifica che il costruttore sollevi un'eccezione se viene passato un
     * numero di checkpoint negativo.
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfNullOrNegativeCheckpointNumber2() {
        new CheckpointReachedTelemetryEvent(1000, -1);
    }

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     *
     * Vengono testati i valori restituiti dai metodi della classe. In
     * particolare, si verifica che i valori passati al costruttore nella
     * creazione dell'oggetto vengano correttamente restituiti dai metodi.
     */
    @Test
    public void testCheckpointReachedTelemetryEventGetters() {
        int time = 1648521;
        int checkp = 34;
        CheckpointReachedTelemetryEvent event =
                new CheckpointReachedTelemetryEvent(time, checkp);

        org.junit.Assert.assertTrue(event.timestamp() == time);
        org.junit.Assert.assertTrue(event.checkpointNumber() == checkp);

        org.junit.Assert.assertTrue(!(event.timestamp() == 98568));
        org.junit.Assert.assertTrue(!(event.checkpointNumber() == 50));
    }

    /**
     * Testa la correttezza del metodo equals().
     */
    @Test
    public void testEqualsMethod() {
        int date1 = 123456;
        int checkp1 = 33;
        CheckpointReachedTelemetryEvent event1 =
            new CheckpointReachedTelemetryEvent(date1, checkp1);

        int date2 = 123456;
        int checkp2 = 33;
        CheckpointReachedTelemetryEvent event2 =
            new CheckpointReachedTelemetryEvent(date2, checkp2);

        int date3 = 321654;
        int checkp3 = 33;
        CheckpointReachedTelemetryEvent event3 =
            new CheckpointReachedTelemetryEvent(date3, checkp3);

        org.junit.Assert.assertTrue(event1.equals(event2));
        org.junit.Assert.assertTrue(!(event1.equals(event3)));
        org.junit.Assert.assertTrue(!(event1.equals(new Object())));
        org.junit.Assert.assertTrue(!(event1.equals(null)));
    }

}
