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
 * Name: SerleenaSQLiteInboundDumpBuilder.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

/**
 * Concretizza InboundDumpBuilder in modo da restituire un InboundDump
 * compatibile con il formato del database interno dell'orologio.
 *
 * @usage Viene usato da KylothCloudSynchronizer per trasformare una collezione di IDataEntity provenienti da un InboundStreamParser in un dump idoneo a essere caricato nel database dell'orologio.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.1
 */
public class SerleenaSQLiteInboundDumpBuilder implements InboundDumpBuilder {
	/**
	 * Aggiunge al'InboundDump in preparazione un
	 * Tracciamento.
	 *
	 * @param telemetry Il Tracciamento da aggiungere
	 */
	@Override
	public void addTelemetry(IDataEntity telemetry) {
		// TODO
	}

	/**
	 * Aggiunge una cella di dati meteo.
	 */
	@Override
	public void addWeather(IDataEntity weatherCell) {
		// TODO
	}

	/**
	 * Aggiunge un'Esperienza.
	 */
	@Override
	public void addExperience(IDataEntity experience) {
		// TODO
	}

	/**
	 * Aggiunge le informazioni per un quadrante di
	 * mappa.
	 */
	@Override
	public void addMapQuadrant(IDataEntity mapQuadrant) {
		// TODO
	}

	/**
	 * Restituisce un InboundDump idoneo a essere caricato nel database
	 * interno con i dati finora inseriti.
	 */
	@Override
	public InboundDump build() {
		// TODO
		return null;
	}
}
