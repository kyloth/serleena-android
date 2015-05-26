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
 * Name: WakeupScheduleTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 * 1.1.0    Tobia Tesan      Rewrite senza WakeupSchedulePlaceholder
 */

package com.kyloth.serleena.sensors;


import android.app.PendingIntent;
import android.content.Intent;

import com.kyloth.serleena.BuildConfig;
import com.kyloth.serleena.sensors.IWakeupObserver;
import com.kyloth.serleena.sensors.WakeupSchedule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Contiene i test di unità per la classe WakeupSchedule.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.1.0
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class WakeupScheduleTest {
    IWakeupObserver goodObs;
    IWakeupObserver badObs;
    WakeupSchedule schedule;
    Intent goodIntent;
    Intent badIntent;
    PendingIntent pGoodIntent;
    PendingIntent pBadIntent;

    @Before
    public void setUp() {
        schedule = new WakeupSchedule();

        goodObs = new IWakeupObserver() {
            String uuid = UUID.randomUUID().toString();
            @Override
            public void onWakeup() {

            }

            @Override
            public String getUUID() {
                return uuid;
            }
        };

        badObs = new IWakeupObserver() {
            String uuid = UUID.randomUUID().toString();
            @Override
            public void onWakeup() {

            }

            @Override
            public String getUUID() {
                return uuid;
            }
        };

        goodIntent = new Intent();
        goodIntent.putExtra("FOO", "BAR");
        pGoodIntent = PendingIntent.getActivity(RuntimeEnvironment.application, 1, goodIntent, 0);
        assertNotNull(pGoodIntent);

        badIntent = new Intent();
        badIntent.putExtra("BAZ", "BANG");
        pBadIntent = PendingIntent.getActivity(RuntimeEnvironment.application, 1, badIntent, 0);
        assertNotNull(pBadIntent);
    }

    /**
     * Controlla che dopo l'inserimento sia possibile recuperare correttamente
     * l'intent inserito (e non un altro).
     */
    @Test
    public void testAddAndRetrieveIntent() {
        schedule.add(goodObs, pGoodIntent, false);
        PendingIntent retrieved = schedule.getIntent(goodObs);
        assertTrue(retrieved != null);
        assertTrue(retrieved == pGoodIntent);
        assertFalse(retrieved == pBadIntent);
    }

    /**
     * Controlla che dopo l'inserimento sia possibile recuperare correttamente
     * l'Observer inserito (e non un altro).
     */
    @Test
    public void testAddAndRetrieveObserver() {
        schedule.add(goodObs, pGoodIntent, false);
        IWakeupObserver retrieved = schedule.getObserver(goodObs.getUUID());
        assertTrue(retrieved != null);
        assertTrue(retrieved == goodObs);
        assertFalse(retrieved == badObs);
    }

    /**
     * Controlla che dopo l'inserimento containsObserver dia risultato
     * positivo per l'observer inserito (ma non un altro)
     */
    @Test
    public void testAddAndContains() {
        schedule.add(goodObs, pGoodIntent, false);
        assertTrue(schedule.containsObserver(goodObs));
        assertFalse(schedule.containsObserver(badObs));
    }

    /**
     * Controlla che dopo l'inserimento isOneTimeOnly sia true
     * sse il parametro passato a add() era true.
     */
    @Test
    public void testOneTimeIsOneTime() {
        schedule.add(goodObs, pGoodIntent, true);
        schedule.add(badObs, pBadIntent, false);
        assertTrue(schedule.isOneTimeOnly(goodObs) == true);
        assertTrue(schedule.isOneTimeOnly(badObs) == false);
    }

    /**
     * Controlla che dopo la rimozione di un observer containsObserver
     * restituisca false.
     */
    @Test
    public void testRemove() {
        schedule.add(goodObs, pGoodIntent, false);
        PendingIntent retrieved = schedule.getIntent(goodObs);
        assertTrue(schedule.containsObserver(goodObs));
        schedule.remove(goodObs);
        assertFalse(schedule.containsObserver(goodObs));
    }

    /**
     * Controlla che il get di un Intent non esistente sollevi una
     * NoSuchElementException.
     */
    @Test(expected = NoSuchElementException.class)
    public void testGetIntentThrowsNoSuchElement() {
        schedule.getIntent(goodObs);
    }

    /**
     * Controlla che il get di un observer non esistente sollevi una
     * NoSuchElementException.
     */
    @Test(expected = NoSuchElementException.class)
    public void testGetObserverThrowsNoSuchElement() {
        schedule.getObserver("123456789");
    }

    /**
     * Controlla che il get di un intent null sollevi una
     * IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetIntentNoNull() {
        schedule.getIntent(null);
    }

    /**
     * Controlla che il get di un observer null sollevi una
     * IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetObserverNoNull() {
        schedule.getObserver(null);
    }

    /**
     * Controlla che add con un UUID null e OneTime == false
     * sollevi IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoNull2() {
        schedule.add(null, pGoodIntent, false);
    }

    /**
     * Controlla che add con un intent null e OneTime == false
     * sollevi IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoNull3() {
        schedule.add(goodObs, null, false);
    }

    /**
     * Controlla che add con un Observer null e OneTime == true
     * sollevi IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoNull2A() {
        schedule.add(null, pGoodIntent, true);
    }

    /**
     * Controlla che add con un Intent null e OneTime == true
     * sollevi IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoNull3A() {
        schedule.add(goodObs, null, true);
    }
}
