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
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Gabriele Pozzan  Creazione file scrittura
 *                                       codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import android.app.PendingIntent;

import com.kyloth.serleena.sensors.IWakeupObserver;
import com.kyloth.serleena.sensors.WakeupSchedule;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Contiene i test di unità per la classe WakeupSchedule.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class WakeupScheduleTest {
    Map<String, IWakeupObserver> uuidMap;
    Map<IWakeupObserver, PendingIntent> obsMap;
    Map<PendingIntent, String> intentMap;
    Map<IWakeupObserver, Boolean> onetimeMap;
    String uuid;
    IWakeupObserver observer;
    PendingIntent alarmIntent;
    WakeupSchedulePlaceholder wsp;

    /**
     * Classe di utilità che permette di iniettare in WakeupSchedule
     * delle mappe tracciabili.
     */
    class WakeupSchedulePlaceholder extends WakeupSchedule {
        public WakeupSchedulePlaceholder() {
            super();
        }
        private void setUuidMap(Map<String, IWakeupObserver> uuidMap) {
            this.uuidMap = uuidMap;
        }
        private void setObsMap(Map<IWakeupObserver, PendingIntent> obsMap) {
            this.obsMap = obsMap;
        }
        private void setIntentMap(Map<PendingIntent, String> intentMap) {
            this.intentMap = intentMap;
        }
        private void setOneTimeMap(Map<IWakeupObserver, Boolean> onetimeMap) {
            this.onetimeMap = onetimeMap;
        }
    }

    /**
     * Inizializza i campi dati necessari all'esecuzione dei test.
     */
    @Before
    public void initialize() {
        uuidMap = (Map<String, IWakeupObserver>) mock(Map.class);
        obsMap = (Map<IWakeupObserver, PendingIntent>) mock(Map.class);
        intentMap = (Map<PendingIntent, String>) mock(Map.class);
        onetimeMap = (Map<IWakeupObserver, Boolean>) mock(Map.class);
        wsp = new WakeupSchedulePlaceholder();
        wsp.setUuidMap(uuidMap);
        wsp.setObsMap(obsMap);
        wsp.setIntentMap(intentMap);
        wsp.setOneTimeMap(onetimeMap);
        uuid = "uuid";
        observer = mock(IWakeupObserver.class);
        alarmIntent = mock(PendingIntent.class);
    }
    /**
     * Testa la correttezza del metodo "add" della classe.
     */
    @Test
    public void testAdd() {
        when(uuidMap.put(uuid, observer)).thenReturn(observer);
        when(obsMap.put(observer, alarmIntent)).thenReturn(alarmIntent);
        wsp.add(uuid, observer, alarmIntent, true);
        verify(uuidMap).put(uuid, observer);
        verify(obsMap).put(observer, alarmIntent);
        verify(intentMap).put(alarmIntent, uuid);
        verify(onetimeMap).put(observer, true);
        IWakeupObserver new_observer = mock(IWakeupObserver.class);
        wsp.add("new_uuid", new_observer, alarmIntent, false);
        verify(onetimeMap).put(new_observer, false);
    }

    /**
     * Testa la correttezza del metodo "remove" della classe.
     */
    @Test
    public void testRemove() {
        when(obsMap.remove(observer)).thenReturn(alarmIntent);
        when(intentMap.remove(alarmIntent)).thenReturn(uuid);
        wsp.add(uuid, observer, alarmIntent, true);
        wsp.remove(observer);
        verify(uuidMap).remove(uuid);
        verify(obsMap).remove(observer);
        verify(intentMap).remove(alarmIntent);
        verify(onetimeMap).remove(observer);
    }

    /**
     * Testa la correttezza dei metodi "getter" della classe.
     */
    @Test
    public void testGetters() {
        wsp.add(uuid, observer, alarmIntent, true);
        wsp.getIntent(observer);
        verify(obsMap).get(observer);
        wsp.getObserver(uuid);
        verify(uuidMap).get(uuid);
    }

    /**
     * Testa la correttezza del metodo "isOneTimeOnly" della classe.
     */
    @Test
    public void testIsOneTimeOnly() {
        IWakeupObserver new_observer = mock(IWakeupObserver.class);
        WakeupSchedule new_wsp = new WakeupSchedule();
        new_wsp.add("uuid_1", observer, alarmIntent, true);
        new_wsp.add("uuid_2", new_observer, alarmIntent, false);
        assertTrue(new_wsp.isOneTimeOnly(observer));
        assertTrue(!(new_wsp.isOneTimeOnly(new_observer)));
    }

    /**
     * Testa la correttezza del metodo "containsObserver" della classe.
     */
    @Test
    public void testContainsObserver() {
        IWakeupObserver obs = mock(IWakeupObserver.class);
        IWakeupObserver obs2 = mock(IWakeupObserver.class);
        WakeupSchedule sched = new WakeupSchedule();
        sched.add("uuid1", obs, alarmIntent, false);
        assertTrue(sched.containsObserver(obs));
        assertTrue(!sched.containsObserver(obs2));
    }
}
