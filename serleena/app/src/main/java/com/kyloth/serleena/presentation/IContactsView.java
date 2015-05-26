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
 * Name: IContactsView
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
 * Interfaccia implementata da una View in grado di mostrare un
 * contatti di emergenza.
 *
 * @use Viene utilizzato dal Presenter \fixedwidth{ContactsPresenter} per mantenere un riferimento alla vista associata, e comunicare con essa.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since 1.0
 */
public interface IContactsView {

    /**
     * Lega un presenter alla vista.
     *
     * @param presenter Presenter da associare alla vista. Se null,
     *                  viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    public void attachPresenter(IContactsPresenter presenter)
            throws IllegalArgumentException;

	/**
	 * Mostra a schermo un contatto di emergenza.
	 *
	 * @param name Il nome o identificativo del contatto da mostrare
	 *             (es. "Stazione Carabinieri Terni")
	 * @param contact Il contatto in formato leggibile dall'uomo
	 *                (es. "0744 885546")
	 */
	void displayContact(String name, String contact);

    /**
     * Pulisce la vista.
     */
    public void clearView();

}
