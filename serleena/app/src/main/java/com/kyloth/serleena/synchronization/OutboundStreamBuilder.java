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
 * Name: OutboundStreamBuilder.java
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
 * Interfaccia implementata da un oggetto in grado di prendere
 * oggetti di tipo *Storage contenenti dati raccolti sul dispositivo e
 * e produrre un OutboundStream da inviare al servizio remoto attraverso
 * un opportuno proxy.
 *
 * @use Viene usato da KylothCloudSynchronizer, che vi carica le Esperienze e i Tracciamenti raccolti sul dispositivo da inviare al cloud, somministrando l'OutboundStream risultante a un idoneo proxy.
 */
public interface OutboundStreamBuilder {
	/**
	 * Aggiunge un'Esperienza (e di conseguenza i suoi Punti Utente
	 * e eventuali altri dati raccolti localmente) all'OutboundStream
	 * da costruire.
	 *
	 * @param e Un'Esperienza le cui componenti raccolte sul dispositivo
	 *          si vogliono  inviare al servizio remoto.
	 */
	void addExperience(IExperienceStorage e);

	/**
	 * Restituisce un OutboundStream con i dati da inviare al servizio remoto
	 * pronti per essere consumati da un idoneo proxy.
	 */
	void stream(OutboundStream s) throws IOException;
}
