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
 * Name: IHeartRateManager.java
 * Package: com.kyloth.serleena.sensors
 * Author: Gabriele Pozzan
 * Date: 2015-05-06
 *
 * History:
 * Version  Programmer        Date        Changes
 * 1.0.0    Gabriele Pozzan   2015-05-06  Creazione file e scrittura
 *                                        codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

/**
 * Interfaccia che verrà implementata da un oggetto in grado di fornire
 * informazioni sul battito cardiaco dell'Escursionista.
 *
 * Tali dati verranno forniti su singola richiesta o comunicati in
 * modalità push a un oggetto IHeartRateObserver registrato agli eventi
 * di IHeartRateManager tramite pattern "Observer".
 *
 * @use Viene utilizzata da SensorManager per restituire ai client il sensore di battito cardiaco, e dal client per accedere ai servizi offerti dal sensore. È utilizzato in particolare da CardioPresenter e TelemetryManager, quest'ultimo per il campionamento di eventi di Tracciamento.
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0.0
 */
public interface IHeartRateManager {
    /**
     * Registra un IHeartRateObserver che, tramite il pattern "Observer"
     * sarà notificato, a un intervallo fissato, dei cambiamenti di stato.
     *
     * @param observer IHeartRateObserver da registrare.
     * @param interval Intervallo di tempo per la notifica all'oggetto "observer".
     */
    public void attachObserver(IHeartRateObserver observer, int interval);
    /**
     * Cancella la registrazione di un IHeartRateObserver.
     *
     * @param observer IHeartRateObserver la cui registrazione come "observer" di
     *                 questo oggetto sarà cancellata.
     */
    public void detachObserver(IHeartRateObserver observer);

    /**
     * Permette di ottenere un aggiornamento dei dati sul battico cardiaco su
     * richiesta esplicita.
     *
     * @return Ritorna un valore intero rappresentante l'heart rate attuale.
     */

    /**
     * Permette di ottenere un aggiornamento dei dati sul battico cardiaco su
     * richiesta esplicita.
     *
     * Poichè la comunicazione con il sensore potrebbe non essere immediata o
     * non avvenire per niente, viene specificato un timeout oltre il quale
     * viene segnalato all'observer il risultato, che potrebbe quindi non
     * essere aggiornato.
     *
     * @param observer IHeartRateObserver destinatario dell'aggiornamento.
     * @param timeout Timeout in secondi oltre il quale il sensore invoca in
     *                ogni caso, il metodo onHeartRateUpdate() dell'osservatore,
     */
    public void getSingleUpdate(IHeartRateObserver observer, int timeout);

    /**
     * Metodo "notify" basato sull'omonimo metodo della classe "Subject" del
     * Design Pattern "Observer".
     *
     * @param observer Oggetto observer da segnalare.
     */
    public void notifyObserver(IHeartRateObserver observer);
}
