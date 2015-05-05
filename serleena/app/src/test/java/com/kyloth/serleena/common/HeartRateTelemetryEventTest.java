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
 * Name: HeartRateTelemetryEventTest.java
 * Package: com.kyloth.serleena.common
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice e documentazione
 *                                          in Javadoc.
 */

package com.kyloth.serleena.common;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.HeartRateTelemetryEvent;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import org.junit.Test;
import java.util.Date;
import dalvik.annotation.TestTargetClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Contiene i test di unità per la classe HeartRateTelemetryEvent.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 * @see com.kyloth.serleena.common.HeartRateTelemetryEvent
 */
public class HeartRateTelemetryEventTest {

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     *
     * Vengono testati i valori restituiti dai metodi della classe. In particolare, si verifica che
     * i valori passati al costruttore nella creazione dell'oggetto vengano correttamente restituiti
     * dai metodi.
     */
    @Test
    public void testHeartRateTelemetryEventGetters() {
        Date date = new Date(2015, 5, 5, 2, 27);
        int rate = 34;
        HeartRateTelemetryEvent event = new HeartRateTelemetryEvent(date, rate);

        org.junit.Assert.assertTrue(event.timestamp().equals(date));
        org.junit.Assert.assertTrue(event.heartRate() == rate);

        org.junit.Assert.assertTrue(!(event.timestamp().equals(new Date(2014, 5, 5, 2, 27))));
        org.junit.Assert.assertTrue(!(event.heartRate() == 50));
    }

    /**
     * Testa la correttezza del metodo equals().
     */
    @Test
    public void testEqualsMethod() {
        Date date1 = new Date(2015, 5, 5, 2, 27);
        int rate1 = 33;
        HeartRateTelemetryEvent event1 = new HeartRateTelemetryEvent(date1, rate1);

        Date date2 = new Date(2015, 5, 5, 2, 27);
        int rate2 = 33;
        HeartRateTelemetryEvent event2 = new HeartRateTelemetryEvent(date2, rate2);

        Date date3 = new Date(2014, 5, 5, 2, 27);
        int rate3 = 33;
        HeartRateTelemetryEvent event3 = new HeartRateTelemetryEvent(date3, rate3);

        org.junit.Assert.assertTrue(event1.equals(event2));
        org.junit.Assert.assertTrue(!(event1.equals(event3)));
    }

}
