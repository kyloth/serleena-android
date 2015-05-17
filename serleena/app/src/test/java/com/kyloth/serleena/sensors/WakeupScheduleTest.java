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
    String onetime_uuid;
    String not_onetime_uuid;
    IWakeupObserver observer_onetime;
    IWakeupObserver observer_not_onetime;
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
        observer_onetime = mock(IWakeupObserver.class);
        observer_not_onetime = mock(IWakeupObserver.class);
        alarmIntent = mock(PendingIntent.class);
        onetime_uuid = "onetime_uuid";
        not_onetime_uuid = "not_onetime_uuid";
        uuidMap = (Map<String, IWakeupObserver>) mock(Map.class);
        obsMap = (Map<IWakeupObserver, PendingIntent>) mock(Map.class);
        when(uuidMap.put(onetime_uuid, observer_onetime)).thenReturn(observer_onetime);
        when(obsMap.put(observer_onetime, alarmIntent)).thenReturn(alarmIntent);
        when(obsMap.remove(observer_onetime)).thenReturn(alarmIntent);
        intentMap = (Map<PendingIntent, String>) mock(Map.class);
        when(intentMap.remove(alarmIntent)).thenReturn(onetime_uuid);
        onetimeMap = (Map<IWakeupObserver, Boolean>) mock(Map.class);
        when(onetimeMap.get(observer_onetime)).thenReturn(true);
        when(onetimeMap.get(observer_not_onetime)).thenReturn(false);
        wsp = new WakeupSchedulePlaceholder();
        wsp.setUuidMap(uuidMap);
        wsp.setObsMap(obsMap);
        wsp.setIntentMap(intentMap);
        wsp.setOneTimeMap(onetimeMap);
    }

    /**
     * Verifica che il metodo add chiami i metodi put sulle diverse
     * HashMap di WakeupSchedule con i corretti parametri.
     */

    @Test
    public void addShouldForwardCorrectParams() {
        wsp.add(onetime_uuid, observer_onetime, alarmIntent, true);
        verify(uuidMap).put(onetime_uuid, observer_onetime);
        verify(obsMap).put(observer_onetime, alarmIntent);
        verify(intentMap).put(alarmIntent, onetime_uuid);
        verify(onetimeMap).put(observer_onetime, true);
        wsp.add(not_onetime_uuid, observer_not_onetime, alarmIntent, false);
        verify(onetimeMap).put(observer_not_onetime, false);
    }

    /**
     * Verifica che il metodo remove chiami i metodi remove sulle diverse
     * HashMap di WakeupSchedule con i corretti parametri.
     */

    @Test
    public void removeShouldForwardCorrectParams() {
        wsp.add(onetime_uuid, observer_onetime, alarmIntent, true);
        wsp.remove(observer_onetime);
        verify(uuidMap).remove(onetime_uuid);
        verify(obsMap).remove(observer_onetime);
        verify(intentMap).remove(alarmIntent);
        verify(onetimeMap).remove(observer_onetime);
    }

    /**
     * Verifica che il metodo isOneTimeOnly chiami il metodo get
     * su onetimeMap fornendo il suo stesso parametro.
     */

    @Test
    public void isOneTimeOnlyShouldForwardCorrectParam() {
        wsp.add(onetime_uuid, observer_onetime, alarmIntent, true);
        wsp.isOneTimeOnly(observer_onetime);
        verify(onetimeMap).get(observer_onetime);
        wsp.add(not_onetime_uuid, observer_not_onetime, alarmIntent, false);
        wsp.isOneTimeOnly(observer_not_onetime);
        verify(onetimeMap).get(observer_not_onetime);
    }

    /**
     * Verifica che il metodo containsObserver chiami il metodo
     * containsKey su onetimeMap fornendo il suo stesso parametro.
     */

    @Test
    public void containsObserverShouldForwardCorrectParam() {
        wsp.add(onetime_uuid, observer_onetime, alarmIntent, true);
        wsp.containsObserver(observer_onetime);
        verify(onetimeMap).containsKey(observer_onetime);
    }
}
