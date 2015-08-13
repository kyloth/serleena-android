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
 * Insieme a ITrackCrossingObserver, realizza il pattern Observer.
 *
 * @use Viene implementata da TrackCrossing, che si occupa di gestire l'attraversamento del Percorso da parte dell'utente. Viene poi riferito da TrackPresenter e TelemetryManager, che lo utilizzano rispettivamente per mostrare informazioni sul Percorso e gestire il Tracciamento.
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
     * Restituisce un oggetto CheckpointCrossed rappresentante l'ultimo
     * attraversamento effettuato.
     *
     * Se non sono ancora stati attraversati checkpoint per il Percorso
     * attuale (o il Percorso appena terminato), viene sollevata un'eccezione
     * NoSuchCheckpointException.
     *
     * @return Oggetto CheckpointCrossing rappresentante l'ultimo
     * attraversamento.
     * @throws NoSuchCheckpointException
     */
    CheckpointCrossing getLastCrossed()
            throws NoSuchCheckpointException, NoActiveTrackException;

    /**
     * Restituisce il prossimo checkpoint da attraversare per il Percorso in
     * corso.
     *
     * Se non vi è un Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     */
    int getNextCheckpoint()
            throws NoActiveTrackException, NoTrackCrossingException;

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
    void notifyObservers() throws NoTrackCrossingException, NoActiveTrackException;

    /**
     * Causa l'avanzamento del Percorso al prossimo checkpoint.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoTrackCrossingException.
     *
     * @throws NoTrackCrossingException
     */
    void advanceCheckpoint() throws NoTrackCrossingException, NoActiveTrackException;

    /**
     * Restituisce il Percorso in corso o appena conclusosi.
     *
     * Se non vi è alcun Percorso in corso, viene sollevata un'eccezione
     * NoActiveTrackException.
     *
     * @return Oggetto ITrack rappresentante il Percorso.
     */
    ITrack getTrack() throws NoActiveTrackException;

    /**
     * Restituisce lo stato del Percorso.
     *
     * @return True se vi è un Percorso attivo, e questo è attualmente in
     * attraversamento. False altrimenti.
     */
    boolean isTrackCrossing();

    /**
     * Arresta il Percorso in esecuzione.
     */
    void abort();

}
