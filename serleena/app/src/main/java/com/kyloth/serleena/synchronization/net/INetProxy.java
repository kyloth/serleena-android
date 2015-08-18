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
 * Name: INetProxy.java
 * Package: com.kyloth.serleena.synchronization.net
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.net;

import com.kyloth.serleena.synchronization.AuthException;
import com.kyloth.serleena.synchronization.InboundStream;
import com.kyloth.serleena.synchronization.OutboundStream;

import java.io.IOException;

/**
 * Fornisce un'interfaccia ad alto livello verso il
 * servizio remoto.
 *
 * @use Viene utilizzato da KylothCloudSynchronizer, che ne usa una istanzia
 *        per poter dialogare con un servizio remoto utilizzando primitive ad
 *        alto livello.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.0.1
 */
public interface INetProxy {
    /**
     * Ritorna un OutboundStream in cui scrivere per inviare dati al servizio remoto.
     *
     * @return Un OutboundStream in cui scrivere i dati per il servizio remoto.
     */
    OutboundStream send() throws AuthException, IOException;

    /**
     * Richiede i dati di sincronizzazione dal servizio remoto.
     *
     * @return Un InboundStream da cui leggere i dati in forma grezza provenienti
     *         dal servizio remoto.
     */
    InboundStream get() throws IOException, AuthException;

    /**
     * Richiede la preautorizzazione con il servizio remoto.
     *
     * @return La stringa con il token temporaneo da visualizzare che
     *         l'utente dovra' poi confermare sull'interfaccia cloud..
     */
    String preAuth() throws IOException, AuthException;

    /**
     * Richiede di eseguire la procedura di autorizzazione permanente contro
     * il servizio remoto.
     */
    void auth() throws IOException, AuthException;

    /**
     * Disconnette dal servizio remoto
     */
    void disconnect() throws NotConnectedException;

    /**
     * Verifica se l'ultima operazione di send() o get() e' andata a buon fine
     * @return true se e' andata a buon fine, altrimenti false oppure solleva eccezione
     * @throws IOException
     * @throws AuthException
     */
    boolean success() throws IOException, AuthException;
}
