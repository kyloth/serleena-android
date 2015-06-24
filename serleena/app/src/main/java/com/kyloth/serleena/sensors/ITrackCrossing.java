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
 * Name: ITrackCrossing.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.model.ITrack;

/**
 * Interfaccia di un oggetto che offre servizi per l'attraversamento di
 * Percorsi, rappresentati da una serie di checkpoint.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface ITrackCrossing {

    /**
     * Avvia il Percorso specificato.
     *
     * @param track Percorso.
     */
    void startTrack(ITrack track);

    /**
     * Restituisce l'indice dell'ultimo checkpoint attraversato.
     *
     * Se non sono ancora stati attraversati checkpoint per il Percorso
     * attuale (o l'ultimo attraversato), viene sollevata un'eccezione
     * NoSuchCheckpointException.
     *
     * @return Indice del checkpoint attraversato.
     * @throws NoSuchCheckpointException
     */
    int getLastCrossed() throws NoSuchCheckpointException;

    /**
     * Restituisce il prossimo checkpoint da attraversare per il Percorso in
     * corso.
     *
     * Se non vi è un Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * @return
     * @throws NoTrackCrossingException
     */
    int getNextCheckpoint() throws NoTrackCrossingException;

    /**
     * Registra un observer agli eventi dell'oggetto.
     *
     * Realizza il pattern Observer.
     *
     * @param observer Observer da registrare.
     */
    void attachObserver(ITrackCrossingObserver observer);

    /**
     * Cancella la registrazione di un observer.
     *
     * Realizza il pattern Observer.
     *
     * @param observer Observer la cui registrazione deve essere cancellata.
     */
    void detachObserver(ITrackCrossingObserver observer);

    /**
     * Notifica gli observer registrati agli eventi dell'oggetto.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * Realizza il pattern Observer.
     *
     * @throws NoTrackCrossingException
     */
    void notifyObservers() throws NoTrackCrossingException;

    /**
     * Causa l'avanzamento del Percorso al prossimo checkpoint.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * @throws NoTrackCrossingException
     */
    void advanceCheckpoint() throws NoTrackCrossingException;

    /**
     * Ottiene il tempo parziale trascorso dall'avvio del Percorso
     * all'ultimo checkpoint attraversato.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * @return Tempo parziale in secondi.
     * @throws NoTrackCrossingException
     */
    int lastPartialTime() throws NoTrackCrossingException;

    /**
     * Restituisce il Percorso in corso o appena conclusosi.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * @return Oggetto ITrack rappresentante il Percorso.
     * @throws NoTrackCrossingException
     */
    ITrack getTrack() throws NoTrackCrossingException;

    /**
     * Restituisce lo stato del Percorso.
     *
     * @return True se il Percorso è in attraversamento.
     */
    boolean isTrackCrossing();

}
