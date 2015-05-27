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
 * Name: KylothCloudSynchronizer.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

import com.kyloth.serleena.model.ISerleenaDataSource;

/**
 * Funge da facade per il sottosistema di sincronizzazione.
 * Realizza il pattern Singleton.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.0.1
 * @use Viene usato dall'Activity come unico entry point per le operazioni di sincronizzazione, dopo avergli fornito un dataSource e un dumpLoader (che possono e tipicamente sono la stesso oggetto)e l'URL del servizio remoto. Si faccia riferimento ai diagrammi di sequenza della ST per i dettagli della procedura di sincronizzazione.
 * @field instance la singola istanza
 */
public class KylothCloudSynchronizer {
	static KylothCloudSynchronizer instance;

	/**
	 * Ritorna l'istanza unica di KylothCloudSynchronizer
	 */
	public static KylothCloudSynchronizer getInstance() {
		//TODO
		return instance;
	};

	/**
	 * Imposta l'URL del servizio remoto con cui eseguire la sincronizzazione
	 *
	 * @param url l'URL del servizio remoto
	 */
	public void setUrl(String url) {
		//TODO
	}

	/**
	 * Esegue la preautorizzazione iniziale ottenendo
	 * un token dal servizio remoto (cfr. ST).
	 */
	public void preAuth() {
		//TODO
	}

	/**
	 * Richiede la sincronizzazione bidirezionale col servizio remoto.
	 */
	public void sync() {
		//TODO
	}

	/**
	 * Imposta un datasource da cui prelevare i dati per la sincronizzazione verso il cloud
	 */
	public void setDataSource(ISerleenaDataSource dataSource) {
		//TODO
	}

	/**
	 * Imposta un DumpLoader in cui caricare i dati ricevuti dal cloud.
	 */
	public void setDumpLoader(ISerleenaDumpLoader dumpLoader) {
		//TODO
	}
}
