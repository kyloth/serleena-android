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
 * Name: ITrack
 * Package: com.hitchikers.serleena.model
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 * Date: 2015-05-05
 *
 * History:
 * Version    Programmer    Date        Changes
 * 1.0        Tobia Tesan   2015-05-05  Creazione del file
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.TelemetryEvent;

/**
 * Interfaccia realizzata da oggetti che rappresentano un
 * Percorso
 *
 * @author  Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since   1.0
 */
public interface ITrack {
	/**
	 * @return  Un Iterable che contiene tutti i Tracciamenti disponibili
	 *          per il Percorso rappresentato dall'oggetto.
	 * @version 1.0
	 * @since   1.0
	 */
	Iterable<ITelemetry> getTelemetries();

	/**
	 * @return  Crea e salva un Tracciamento dato un Iterable di oggetti
	 *          TelemetryEvent.
	 * @version 1.0
	 * @since   1.0
	 */
	void createTelemetry(Iterable<TelemetryEvent> events);

    /**
     * Restituisce i Checkpoint che compongono il percorso.
     *
     * @return Insieme di Checkpoint.
     */
    public ImmutableList<Checkpoint> getCheckpoints();

}
