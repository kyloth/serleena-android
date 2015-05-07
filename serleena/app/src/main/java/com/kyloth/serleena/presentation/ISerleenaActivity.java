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
 * Name: ISerleenaActivity.java
 * Package: com.kyloth.serleena.presentation
 * Author: Filippo Sestini
 * Date: 2015-05-07
 *
 * History:
 * Version  Programmer        Date         Changes
 * 1.0.0    Filippo Sestini   2015-05-07   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.presentation;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.sensors.ISensorManager;

/**
 * Interfaccia dell'Activity dell'applicazione serleena.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface ISerleenaActivity {

    /**
     * Imposta l'esperienza attiva.
     *
     * Il metodo è chiamato dal Presenter che si occupa
     * dell'impostazione dell'Esperienza attiva. Tale
     * informazione viene segnalata dall'activity a tutti i
     * presenter che ne hanno bisogno.
     *
     * @param experience Esperienza da attivare.
     */
    public void setActiveExperience(IExperience experience);

    /**
     * Imposta il percorso attivo.
     *
     * Il metodo è chiamato dal Presenter che si occupa
     * dell'impostazione del Percorso attivo. Tale
     * informazione viene segnalata dall'activity a tutti i
     * presenter che ne hanno bisogno.
     *
     * @param track Percorso da attivare.
     */
    public void setActiveTrack(ITrack track);

    /**
     * Abilita il Tracciamento per il Percorso attivo.
     *
     * Il metodo è chiamato dal Presenter che si occupa
     * dell'abilitazione e disabilitazione del Tracciamento. Tale
     * informazione viene segnalata dall'activity a tutti i
     * presenter che ne hanno bisogno. Se non è attivo alcun Percorso,
     * il metodo non ha effetto.
     */
    public void enableTelemetry();

    /**
     * Disabilita il Tracciamento per il Percorso attivo.
     *
     * Il metodo è chiamato dal Presenter che si occupa
     * dell'abilitazione e disabilitazione del Tracciamento. Tale
     * informazione viene segnalata dall'activity a tutti i
     * presenter che ne hanno bisogno. Se non è attivo alcun Percorso,
     * il metodo non ha effetto.
     */
    public void disableTelemetry();

    /**
     * Restituisce il data source dell'applicazione.
     *
     * @return Data source dell'applicazione.
     */
    public ISerleenaDataSource getDataSource();

    /**
     * Restituisce il gestore dei sensori dell'applicazione,
     * dietro interfaccia ISensorManager.
     *
     * @return Gestore dei sensori dell'applicazione.
     */
    public ISensorManager getSensorManager();

}
