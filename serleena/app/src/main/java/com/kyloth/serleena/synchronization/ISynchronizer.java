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
 * Name: ISynchronizer.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

import java.io.IOException;

/**
 * Espone un'interfaccia generica per sincronizzare il dispositivo con un servizio remoto.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.0.1
 */
public interface ISynchronizer {

    /**
     * Esegue la preautorizzazione iniziale ottenendo
     * un token dal servizio remoto (cfr. ST).
     *
     * @return Il token di conferma da fornire manualmente al servizio remoto
     * @throws AuthException Se il servizio remoto nega il permesso di preautenticare
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    String preAuth()  throws AuthException, IOException;

    /**
     * Esegue l'autenticazione col servizio remoto
     * @throws RuntimeException Se erroneamente chiamato dal programmatore senza prima effettuare con successo preauth()
     * @throws AuthException Se il servizio remoto nega l'autenticazione
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    void auth()  throws AuthException, IOException;

    /**
     * Richiede la sincronizzazione bidirezionale col servizio remoto.
     *
     * @throws AuthException Se il servizio remoto nega il permesso di sincronizzare
     * @throws IOException Se comunicazione impossibile col servizio remoto
     */
    void sync() throws AuthException, IOException;
}
