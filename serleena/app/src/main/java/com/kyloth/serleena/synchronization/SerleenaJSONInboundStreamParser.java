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
 * Name: SerleenaJSONInboundStreamParser.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization;

/**
 * Concretizza InboundStreamParser in modo da poter consumare
 * stream JSON in arrivo da KylothCloud.
 *
 * @use Viene usato da KylothCloudSynchronizer per trasformare i dati in arrivo da KylothCloud, raccolti in un InboundStream da un INetProxy, in un formato intermedio somministrabile a un InboundDumpBuilder.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 */
public class SerleenaJSONInboundStreamParser implements InboundStreamParser {
	/**
	 * Dato un InboundStream contenente dati di sincronizzazione, fornisce
	 * una rappresentazione intermedia agnostica.
	 *
	 * @return Una collezione di IDataEntity che costituiscono una
	 * rappresentazione agnostica dei dati forniti dal servizio.
	 */

	@Override
	public IDataEntity parse() {
		return null;
	}
}
