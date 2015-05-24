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

/**
 * Rappresenta i wakeup pianificati utilizzando WakeupManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
class WakeupSchedule {

    Map<String, IWakeupObserver> uuidMap;
    Map<IWakeupObserver, PendingIntent> obsMap;
    Map<PendingIntent, String> intentMap;
    Map<IWakeupObserver, Boolean> onetimeMap;

    public WakeupSchedule() {
        uuidMap = new HashMap<String, IWakeupObserver>();
        obsMap = new HashMap<IWakeupObserver, PendingIntent>();
        intentMap = new HashMap<PendingIntent, String>();
        onetimeMap = new HashMap<IWakeupObserver, Boolean>();
    }

    public PendingIntent getIntent(IWakeupObserver observer) {
        return obsMap.get(observer);
    }

    public IWakeupObserver getObserver(String uuid) {
        return uuidMap.get(uuid);
    }

    public void add(String uuid, IWakeupObserver observer,
                    PendingIntent alarmIntent, boolean oneTimeOnly) {
        uuidMap.put(uuid, observer);
        obsMap.put(observer, alarmIntent);
        intentMap.put(alarmIntent, uuid);
        onetimeMap.put(observer, oneTimeOnly);
    }

    public void remove(IWakeupObserver observer) {
        uuidMap.remove(intentMap.remove(obsMap.remove(observer)));
        onetimeMap.remove(observer);
    }

    public boolean isOneTimeOnly(IWakeupObserver observer) {
        return onetimeMap.get(observer);
    }

    public boolean containsObserver(IWakeupObserver observer) {
        return onetimeMap.containsKey(observer);
    }

}
