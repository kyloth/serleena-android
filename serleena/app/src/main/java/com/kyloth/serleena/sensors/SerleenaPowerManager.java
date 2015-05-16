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
 * Name: SerleenaPowerManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 * Date: 2015-05-16
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-16   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.content.Context;
import android.os.PowerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Concretizza IPowerManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class SerleenaPowerManager implements IPowerManager {

    private static SerleenaPowerManager instance;

    private Map<String, PowerManager.WakeLock> locks;
    private PowerManager pm;

    /**
     * Crea un oggetto SerleenaPowerManager.
     *
     * Il costruttore è privato per realizzare correttamente il pattern
     * Singleton, forzando l'accesso alla sola istanza esposta dai
     * metodi Singleton e impedendo al codice client di costruire istanze
     * arbitrariamente.
     *
     * @param context Contesto dell'applicazione.
     */
    public SerleenaPowerManager(Context context) {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        locks = new HashMap<>();
    }

    /**
     * Implementa IPowerManager.lock().
     *
     * @param lockName Stringa identificativa del lock da acquisire.
     */
    public void lock(String lockName) {
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager
                .PARTIAL_WAKE_LOCK, lockName);
        locks.put(lockName, wl);
        wl.acquire();
    }

    /**
     * Implementa IPowerManager.unlock().
     *
     * @param lockName Stringa identificativa del lock da rilasciare. Se non
     *                 esiste un lock associato a questa stringa,
     *                 viene sollevata un'eccezione UnregisteredLockException.
     * @throws com.kyloth.serleena.sensors.UnregisteredLockException
     */
    public void unlock(String lockName)
            throws UnregisteredLockException {
        if (locks.containsKey(lockName)) {
            locks.remove(lockName).release();
        } else
            throw new UnregisteredLockException();
    }

    /**
     * Restituisce la singola istanza della classe.
     *
     * Implementa il pattern Singleton.
     *
     * @param context Contesto dell'applicazione. Se null,
     *                viene sollevata un'eccezione IllegalArgumentException.
     * @return Istanza della classe.
     */
    public static SerleenaPowerManager getInstance(Context context)
            throws IllegalArgumentException {
        if (context == null)
            throw new IllegalArgumentException("Illegal null context");

        if (instance == null)
            instance = new SerleenaPowerManager(context);
        return instance;
    }

}
