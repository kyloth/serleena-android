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
 * Name: SerleenaPowerManagerTest.java
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
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.os.PowerManager;

/**
 * Contiene i test di unità per la classe SerleenaPowerManager.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */

public class SerleenaPowerManagerTest {

    Context context;
    PowerManager pm;
    PowerManager.WakeLock wl;
    String lockName;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Inizializza i campi dati necessari alla conduzione dei test.
     */

    @Before
    public void initialize() {
        lockName = "lockName";
        context = mock(Context.class);
        pm = mock(PowerManager.class);
        wl = mock(PowerManager.WakeLock.class);
        when(context.getSystemService(Context.POWER_SERVICE)).thenReturn(pm);
        when(pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockName)).thenReturn(wl);
    }

    /**
     * Verifica che il metodo getInstance sollevi un'eccezione di tipo
     * IllegalArgumentException con messaggio "Illegal null context" se
     * invocato con Context nullo.
     */

    @Test
    public void getInstanceShouldThrowExceptionWhenNullContext() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Illegal null context");
        SerleenaPowerManager spm = SerleenaPowerManager.getInstance(null);
    }

    /**
     * Verifica che il metodo getInstance fornisca sempre la stessa istanza
     * di SerleenaPowerManager rispettando il design pattern Singleton.
     */

    @Test
    public void getInstanceShouldReturnSameInstance() {
        SerleenaPowerManager spm_1 = SerleenaPowerManager.getInstance(context);
        SerleenaPowerManager spm_2 = SerleenaPowerManager.getInstance(context);
        assertTrue(spm_1 == spm_2);
    }

    /**
     * Verifica che il metodo unlock sollevi un'eccezione di tipo
     * UnregisteredLockException se invocato con parametro una stringa
     * cui non sia associato nessun lock.
     */

    @Test
    public void unlockShouldThrowExceptionWhenUnregisteredLock() {
        SerleenaPowerManager spm = SerleenaPowerManager.getInstance(context);
        exception.expect(UnregisteredLockException.class);
        spm.unlock("unregistered_lock");
    }

    /**
     * Verifica che il metodo lock chiami il metodo newWakeLock sul
     * PowerManager passando come parametri PowerManager.PARTIAL_WAKE_LOCK
     * e il suo stesso parametro di invocazione.
     */

    @Test
    public void lockShouldForwardCorrectParams() {
        SerleenaPowerManager spm = SerleenaPowerManager.getInstance(context);
        spm.lock(lockName);
        verify(pm).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockName);
    }
}
