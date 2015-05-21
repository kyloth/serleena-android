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


package com.kyloth.serleena;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.presentation.IExperienceSelectionPresenter;
import com.kyloth.serleena.presentation.IExperienceSelectionView;
import com.kyloth.serleena.presentation.ISerleenaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe di stub per IExperienceSelectionPresenter
 */
public class DummyExpSelPresenter implements IExperienceSelectionPresenter {

    private List<IExperience> experiences;
    private ISerleenaActivity activity;
    private IExperienceSelectionView view;

    public DummyExpSelPresenter(IExperienceSelectionView view, ISerleenaActivity activity) {
        this.activity = activity;
        this.view = view;
        this.experiences = new ArrayList<>();
        List<String> names = new ArrayList<>();

        names.add("CORTINA");
        names.add("ASIAGO");
        names.add("TORRE ARCHIMEDE");

        view.setList(names);
        view.attachPresenter(this);
    }

    @Override
    public void activateExperience(int index) throws IllegalArgumentException {

    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }
}
