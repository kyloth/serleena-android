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


package com.kyloth.serleena.sensors;

import android.app.PendingIntent;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Rappresenta i wakeup pianificati utilizzando WakeupManager.
 *
 * @use Viene utilizzata da SerleenaSensorManager per restituire ai client il sensore di orientamento, e dai client per accedere ai servizi offerti dal sensore. È utilizzato in particolare da CompassPresenter e TrackPresenter.
 * @field uuidMap : Map<String, IWakeupObserver> Mappa degli UUID e relativi Observer
 * @field obsMap : Map<IWakeupObserver, PendingIntent> Mappa degli Observer e relativi Intent
 * @field intentMap : Map<PendingIntent, String> Mappa degli Intent e relativi UUID
 * @field onetimeMap : Map<IWakeupObserver, Boolean> Mappa che associa ogni Observer a un valore che indica se l'Observer il wakeup deve avvenire una sola volta o ripetuto
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class WakeupSchedule {

    private Map<String, IWakeupObserver> uuidMap;
    private Map<IWakeupObserver, PendingIntent> obsMap;
    private Map<PendingIntent, String> intentMap;
    private Map<IWakeupObserver, Boolean> onetimeMap;

    public WakeupSchedule() {
        uuidMap = new HashMap<String, IWakeupObserver>();
        obsMap = new HashMap<IWakeupObserver, PendingIntent>();
        intentMap = new HashMap<PendingIntent, String>();
        onetimeMap = new HashMap<IWakeupObserver, Boolean>();
    }

    public PendingIntent getIntent(IWakeupObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException();
        }

        if (!obsMap.containsKey(observer)) {
            throw new NoSuchElementException();
        }

        return obsMap.get(observer);
    }

    public IWakeupObserver getObserver(String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException();
        }

        if (!uuidMap.containsKey(uuid)) {
            throw new NoSuchElementException();
        }

        return uuidMap.get(uuid);
    }

    public void add(IWakeupObserver observer, PendingIntent alarmIntent,
            boolean oneTimeOnly) {
        if (observer == null || alarmIntent == null) {
            throw new IllegalArgumentException();
        }
        uuidMap.put(observer.getUUID(), observer);
        obsMap.put(observer, alarmIntent);
        intentMap.put(alarmIntent, observer.getUUID());
        onetimeMap.put(observer, oneTimeOnly);
    }

    public void remove(IWakeupObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException();
        }

        uuidMap.remove(intentMap.remove(obsMap.remove(observer)));
        onetimeMap.remove(observer);
    }

    public boolean isOneTimeOnly(IWakeupObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException();
        }

        if (!onetimeMap.containsKey(observer)) {
            throw new NoSuchElementException();
        }

        return onetimeMap.get(observer);
    }

    public boolean containsObserver(IWakeupObserver observer) {
        return onetimeMap.containsKey(observer);
    }

}
