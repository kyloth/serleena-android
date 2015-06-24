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
 * Name: IExperienceSelectionPresenter
 * Package: com.hitchikers.serleena.presentation
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 * 
 * History: 
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.presentation;

import com.kyloth.serleena.model.IExperience;

/**
 * E' l'interfaccia realizzata dal Presenter della schermata di selezione
 * dell'esperienza.
 *
 * @use Interfaccia utilizzata dalla vista ExperienceSelectionFragment per mantenere un riferimento al Presenter associato, e comunicare con esso.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 * @since 1.0
 */
public interface IExperienceSelectionPresenter extends IPresenter {

    /**
	 * Attiva un'esperienza identificata per numero tra quelle mostrati dalla
	 * vista di selezione.
     *
     * @param experience Esperienza selezionata dall'utente e che deve essere
     *                   attivata. Se null, viene sollevata un'eccezione
     *                   IllegalArgumentException.
     */
    void activateExperience(IExperience experience)
            throws IllegalArgumentException;

}
