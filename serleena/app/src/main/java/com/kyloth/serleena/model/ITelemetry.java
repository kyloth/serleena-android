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
 * Date: 2015-05-05
 *
 * History:
 * Version    Programmer    Date        Changes
 * 1.0        Tobia Tesan   2015-05-05  Creazione del file
 */

package com.kyloth.serleena.model;

import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.LocationTelemetryEvent;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;

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
    public Iterable<TelemetryEvent> getEvents();

    /**
     * Restituisce gli eventi che costituiscono il Tracciamento, filtrati
     * secondo un intervallo di tempo specificato.
     *
     * @return  Insieme enumerabile che contiene tutti gli eventi del
     *          Tracciamento compresi nell'intervallo di secondi specificato.
     * @param from L'inizio dell'intervallo, comprensivo degli
     *             eventi da restituire, espresso in secondi dall'avvio del
     *             Tracciamento.
     *             Se from > to, viene sollevata un'eccezione
     *             IllegalArgumentException.
     * @param to L'inizio dell'intervallo, comprensivo degli
     *           eventi da restituire, espresso in secondi dall'avvio del
     *           Tracciamento.
     *           Se from > to, viene sollevata un'eccezione
     *           IllegalArgumentException.
     * @version 1.0
     */
    public Iterable<TelemetryEvent> getEvents(int from, int to)
            throws NoSuchTelemetryEventException, IllegalArgumentException;

    /**
     * Restituisce gli eventi che costituiscono il Tracciamento, filtrati in
     * base a uno specifico tipo di evento.
     *
     * @return  Insieme enumerabile che contiene tutti e soli gli eventi del
     *          Tracciamento di tipo specificato.
     * @param type Tipo di eventi da restituire.
     * @return Insieme enumerabile di eventi di Tracciamento.
     */
    public Iterable<TelemetryEvent> getEvents(TelemetryEventType type)
            throws NoSuchTelemetryEventException;

    /**
     * Restituisce l'evento del Tracciamento registrato in prossimità dellal
     * posizione specificata, secondo una tolleranza.
     * Se non è possibile trovare un evento che soddisfi i valori
     * specificati, viene sollevata un'eccezione NoSuchTelemetryEventException.
     *
     * @param loc Posizione campionata dall'evento che si vuole ottenere. Se
     *            null, viene sollevata un'eccezione IllegalArgumentException.
     * @param tolerance Tolleranza, in metri, indicante quanto la posizione
     *                  registrata dall'evento restituito può discostarsi dal
     *                  valore richiesto. Se < 0, viene sollevata
     *                  un'eccezione IllegalOperationException.
     * @return Evento di Tracciamento.
     * @throws NoSuchTelemetryEventException
     * @throws IllegalArgumentException
     */
    public LocationTelemetryEvent getEventAtLocation(GeoPoint loc,
                                                     int tolerance)
            throws NoSuchTelemetryEventException, IllegalArgumentException;

    /**
     * Restituisce la durata totale del Tracciamento.
     *
     * @return  Durata temporale in secondi del Tracciamento.
     * @version 1.0
     */
    public int getDuration();

}
