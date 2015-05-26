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
 * Name: KylothJSONNetProxy.java
 * Package: com.kyloth.serleena.synchronization.net
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.net;

import com.kyloth.serleena.synchronization.InboundStream;
import com.kyloth.serleena.synchronization.OutboundStream;

/**
 * Concretizza l'interfaccia ISerleenaNetProxy permettendo di dialogare con
 * il servizio KylothCloud.
 *
 * @usage Viene utilizzato da KylothCloudSynchronizer, che ne usa una istanzia
 *        per poter dialogare con il servizio KylothCloud utilizzando le
 *        primitive ad alto livello prescritte da ISerleenaNetProxy.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @field url L'URL del servizio remoto
 * @field auth_token Il token di autorizzazione ricevuto dal servizio remoto al termine
 *                   della procedura di handshaking, valido per l'intera vita
 *                   dell'oggetto.
 */
public class KylothJSONNetProxy implements ISerleenaNetProxy {
	String url;
	String auth_token;

	/**
	 * Costruisce un'istanza di KylothJSONNetProxy
	 * @param url L'URL del servizio remoto
	 */
	KylothJSONNetProxy(String url) {
		this.url = url;
	}
	/**
	 * Invia al servizio remoto un OutboundStream con i dati raccolti localmente.
	 *
	 * @param stream Un OutboundStream con i dati raccolti localmente da inviare
	 */
	@Override
	public void send(OutboundStream stream) {
		//TODO
	}

	/**
	 * Richiede i dati di sincronizzazione dal servizio remoto.
	 *
	 * @return Un InboundStream contenente i dati in forma grezza provenienti
	 *         dal servizio remoto.
	 */
	@Override
	public InboundStream get() {
		//TODO
		return null;
	}

	/**
	 * Richiede la preautorizzazione con il servizio remoto.
	 *
	 * @return La stringa con il token temporaneo da visualizzare che
	 *         l'utente dovra' poi confermare sull'interfaccia cloud..
	 */
	@Override
	public String preAuth() {
		//TODO
		return null;
	}

	/**
	 * Richiede di eseguire la procedura di autorizzazione permanente contro
	 * il servizio remoto.
	 */
	@Override
	public void auth() {
		//TODO
	}
}
