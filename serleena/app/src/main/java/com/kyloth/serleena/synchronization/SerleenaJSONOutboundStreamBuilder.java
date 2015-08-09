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
 * Name: SerleenaJSONOutboundStreamBuilder.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;

import java.io.IOException;

/**
 * Concretizza OutboundStreamBuilder in modo da poter costruire stream JSON
 * nel formato che KylothCloud si attende.
 *
 * @use Viene usato da KylothCloudSynchronizer per costruire un OutboundStream nel formato idoneo ad essere passato a SerleenaJSONNetProxy per l'invio a KylothCloud
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 */
public class SerleenaJSONOutboundStreamBuilder implements OutboundStreamBuilder {
	/**
	 * Aggiunge un'Esperienza (e di conseguenza i suoi Punti Utente
	 * e eventuali altri dati raccolti localmente) all'OutboundStream
	 * da costruire.
	 *
	 * @param e Un'Esperienza le cui componenti raccolte sul dispositivo
	 *          si vogliono  inviare al servizio remoto.
	 */
	@Override
	public void addExperience(IExperienceStorage e) {
		// TODO
	}

	@Override
	public void stream(OutboundStream s) throws IOException {
		// TODO
	}

	/**
	 * Restituisce uno SerleenaJSONOutboundStream con i dati da inviare al
	 * servizio remoto pronti per essere consumati da un idoneo proxy.
	 */
	public SerleenaJSONOutboundStream build() {
		// TODO
		return null;
	}
}
