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
 * Name: ITrackView
 * Package: com.hitchikers.serleena.presentation
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 * Date: 2015-05-01
 * 
 * History: 
 * Version    Programmer    Date        Changes
 * 1.0        Tobia Tesan   2015-05-01  Creazione del file
 */

package com.kyloth.serleena.presentation;

/**
 * E' l'interfaccia del Presenter della schermata Percorso, che mostra
 * indicazioni di percorrenza e, se disponibili, informazioni sulla propria
 * performance registrata sul tracciato (tempi parziali, ecc...) e eventuale
 * confronto con il ghost.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since 1.0
 */
public interface ITrackView {
	/**
	 * Lega un Presenter alla vista.
	 *
	 * @since 1.0
	 */
	void attachPresenter(ITrackPresenter presenter);

	/**
	 * Pulisce la vista.
	 *
	 * @since 1.0
	 */
	void clearView();

	/**
	 * Imposta l'orientamento suggerito - l'orientamento ossia
	 * verso cui dirigersi per incontrare il successivo checkpoint.
	 *
	 * @param heading Orientamento espresso in gradi (0.0 ... 360.0),
	 *                modulo 360.0.
	 * @since 1.0
	 */
	void setDirection(float heading);

	/**
	 * Imposta la distanza dal successivo checkpoint.
	 *
	 * @param distance distanza espressa in metri
	 * @since 1.0
	 */
	void setDistance(int distance);

	/**
	 * Imposta il tempo parziale rilevato all'ultimo checkpoint
	 * per visualizzarlo a schermo.
	 *
	 * @since 1.0
	 */
	void setLastPartial(int seconds);

	/**
	 * Imposta il guadagno netto sul ghost rilevato all'ultimo checkpoint.
	 *
	 * @since 1.0
	 */
	void setDelta(int seconds);

	/**
	 * Imposta il numero del prossimo checkpoint per la visualizzazione a video.
	 *
	 * @since 1.0
	 */
	void setCheckpointNo(int n);

	/**
	 * Informa la vista se il Tracciamento e' abilitato o meno.
	 *
	 * @since 1.0
	 */
	void telemetryEnabled(boolean b);
}
