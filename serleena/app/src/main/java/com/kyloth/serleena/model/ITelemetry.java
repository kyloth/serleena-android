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
 * Name: ITelemetry
 * Package: com.hitchikers.serleena.model
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.model;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.TelemetryEvent;

/**
 * Interfaccia realizzata da oggetti che rappresentano un
 * Tracciamento
 *
 * @use Utilizzata dal Model e dalla parte di presentazione. L'uso principale viene fatto da TrackPresenter, che utilizza i Tracciamenti dell'esperienza attiva per confrontarli con l'attività in corso.
 * @author  Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since   1.0
 */
public interface ITelemetry {

    /**
     * Restituisce gli eventi che costituiscono il Tracciamento.
     *
     * @return  Un Iterable che contiene tutti gli eventi del Tracciamento.
     * @version 1.0
     */
    Iterable<TelemetryEvent> getEvents();

    /**
     * Restituisce gli eventi che soddisfano il predicato specificato.
     *
     * @param predicate Predicato.
     * @return Insieme enumerabile di eventi che soddisfano il predicato.
     */
    Iterable<TelemetryEvent> getEvents(Predicate<TelemetryEvent>
                                               predicate)
            throws NoSuchTelemetryEventException;

    /**
     * Restituisce la durata totale del Tracciamento.
     *
     * @return  Durata temporale in secondi del Tracciamento.
     * @version 1.0
     */
    int getDuration();

    /**
     * Restituisce l'UNIX timestamp di inizio del Tracciamento.
     *
     * @return UNIX timestamp in millisecondi
     */
    long startTimestamp();

}
