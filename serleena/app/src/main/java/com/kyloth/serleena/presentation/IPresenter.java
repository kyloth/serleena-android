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
 * Name: IPresenter
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
 * IPresenter e' l'interfaccia di base di un Presenter.
 *
 * @use Alcuni servizi del Presenter, come l'ascolto di sensori, sono richiesti solamente quando la vista associata &#232; effettivamente visibile e mostrata a schermo. &#200; quindi fondamentale che il Presenter e la vista associata siano sincronizzati, evitando che il Presenter consumi risorse per aggiornare una vista che non &#232; visibile. Per questo motivo, l'interfaccia espone metodi per mettere in pausa e riprendere alcune attivit&#224; del Presenter in base al ciclo di vita della vista a cui &#232; associato. Ogni vista chiama questi metodi sul Presenter associato ad essa, per segnalare il suo stato di visibilità o meno.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since 1.0
 */
public interface IPresenter {
	/**
	 * Riprende tutte le attivita' del Presenter
         *
	 * @since 1.0
	 */
	void resume();

	/**
	 * Mette in pausa alcune attivita' del Presenter in modo da non consumare
	 * inutilmente risorse quando la sua vista non e' a schermo.
	 *
	 * @since 1.0
	 */
	void pause();
}
