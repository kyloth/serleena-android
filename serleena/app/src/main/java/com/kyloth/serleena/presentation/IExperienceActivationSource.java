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
 * Name: IExperienceActivationSource.java
 * Package: com.hitchikers.serleena.presentation
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer        Changes
 * 1.0        Filippo Sestini   Creazione del file
 */

package com.kyloth.serleena.presentation;

import com.kyloth.serleena.common.NoActiveExperienceException;
import com.kyloth.serleena.model.IExperience;

/**
 * Rappresenta l'interfaccia di un oggetto da cui è possibile ottenere
 * l'Esperienza correntemente attiva.
 *
 * @use Viene implementata da ExperienceSelectionFragment, che si occupa di gestire l'attivazione delle Esperienze. MapPresenter e TrackSelectionPresenter mantendono un riferimento a questa interfaccia per ottenere l'Esperienza attiva.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public interface IExperienceActivationSource {

    /**
     * Restituisce l'Esperienza correntemente attiva.
     *
     * Se non è attiva alcuna Esperienza, viene sollevata un'eccezione
     * NoActiveExperienceException.
     *
     * @return Esperienza correntemente attiva.
     */
    IExperience activeExperience() throws NoActiveExperienceException;

}
