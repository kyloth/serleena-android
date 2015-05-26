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
 * Name: IPowerManager.java
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

/**
 * Rappresenta un'interfaccia centralizzata e semplificata per l'acquisizione
 * di lock del processore.
 *
 * @use Viene realizzata da \fixedwidth{SerleenaPowerManager} e utilizzata da \fixedwidth{TelemetryManager}, per evitare che il processore entri il \foreignword{sleep} durante il campionamento.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface IPowerManager {

    /**
     * Acquisisce un lock del processore.
     *
     * @param lockName Stringa identificativa del lock.
     */
    public void lock(String lockName);

    /**
     * Rilascia il lock del processore associato alla stringa identificativa
     * specificata.
     *
     * @param lockName Stringa identificativa del lock da rilasciare. Se non
     *                 esiste un lock associato a questa stringa,
     *                 viene sollevata un'eccezione UnregisteredLockException.
     * @throws com.kyloth.serleena.sensors.UnregisteredLockException
     */
    public void unlock(String lockName) throws UnregisteredLockException;

}
