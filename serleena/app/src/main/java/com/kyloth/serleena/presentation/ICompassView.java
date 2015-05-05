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
 * Name: ICompassView
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
 * E' l'interfaccia propria delle classi View in un contesto MVP che sono
 * in grado di visualizzare informazioni sull'orientamento rispetto ai punti
 * cardinali.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @since 1.0
 * @version 1.0
 */
public interface ICompassView {
	/**
	 * Cambia l'orientamento mostrato dalla vista.
	 *
	 * @param heading Orientamento espresso in gradi (0.0 ... 360.0),
	 *                modulo 360.0.
	 * @since 1.0
	 */
	void setHeading(float heading);

	/**
	 * Lega un presenter alla vista.
	 *
	 * @since 1.0
	 */
	void attachPresenter(ICompassPresenter presenter);
}