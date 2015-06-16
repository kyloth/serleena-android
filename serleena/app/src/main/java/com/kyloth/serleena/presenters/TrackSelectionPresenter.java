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
 * Name: TrackSelectionPresenter
 * Package: com.hitchikers.serleena.presenters
 * Author: Filippo Sestini
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Filippo Sestini  Creazione del file
 */

package com.kyloth.serleena.presenters;

import android.os.AsyncTask;

import com.kyloth.serleena.model.IExperience;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.presentation.ISerleenaActivity;
import com.kyloth.serleena.presentation.ITrackSelectionPresenter;
import com.kyloth.serleena.presentation.ITrackSelectionView;
import com.kyloth.serleena.sensors.ITrackCrossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Concretizza ITrackSelectionPresenter.
 *
 * @use Viene utilizzata solamente dall'Activity, che ne mantiene un riferimento. Il Presenter, alla creazione, si registra alla sua Vista, passando se stesso come parametro dietro interfaccia.
 * @field view : IWeatherView Vista associata al presenter
 * @field activity : ISerleenaActivity Activity a cui il presenter appartiene
 * @field tracks : List<ITrack> Percorsi da visualizzare sulla vista
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TrackSelectionPresenter implements ITrackSelectionPresenter {

    private ISerleenaActivity activity;
    private ITrackSelectionView view;
    private List<ITrack> tracks;

    /**
     * Crea un oggetto TrackSelectionPresenter.
     *
     * Si collega alla vista tramite il metodo attachPresenter() esposto
     * dall'interfaccia della vista.
     *
     * @param view Vista da associare al presenter. Se null,
     *             viene sollevata un'eccezione IllegalArgumentException.
     * @param activity Activity che rappresenta l'applicazione in corso e al
     *                 quale il presenter appartiene. Se null,
     *                 viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    public TrackSelectionPresenter(ITrackSelectionView view,
                                   ISerleenaActivity activity)
            throws IllegalArgumentException {
        if (view == null)
            throw new IllegalArgumentException("Illegal null view");
        if (activity == null)
            throw new IllegalArgumentException("Illegal null activity");

        this.activity = activity;
        this.view = view;
        this.tracks = new ArrayList<ITrack>();
        this.view.attachPresenter(this);
    }

    /**
     * Implementa ITrackSelectionPresenter.activateTrack().
     *
     * Il percorso selezionato viene segnalato all'activity, che si occupa di
     * segnalare i restanti presenter e rendere l'attivazione effettiva.
     *
     * @param index Indice della lista di percorsi che rappresenta la
     *              selezione dell'utente. Se minore di zero o non
     *              corrispondente ad un elemento della lista,
     *              viene sollevata un'eccezione IllegalArgumentException.
     * @throws java.lang.IllegalArgumentException
     */
    @Override
    public synchronized void activateTrack(int index)
            throws IllegalArgumentException {
        if (index < 0 || index >= tracks.size())
            throw new IllegalArgumentException("Index out of range");

        ITrackCrossing tc =
            activity.getSensorManager().getTrackCrossingManager();
        tc.startTrack(tracks.get(index));
    }

    /**
     * Implementa IPresenter.resume().
     *
     * Non effettua operazioni, in quanto non vengono utilizzate risorse da
     * acquisire o rilasciare.
     */
    @Override
    public void resume() {

    }

    /**
     * Implementa IPresenter.resume().
     *
     * Non effettua operazioni, in quanto non vengono utilizzate risorse da
     * acquisire o rilasciare.
     */
    @Override
    public void pause() {

    }

    /**
     * Segnala al presenter l'esperienza correntemente attiva.
     *
     * All'attivazione di una nuova esperienza, l'activity segnala al
     * presenter in modo che esso possa aggiornare la vista con i Percorsi
     * corretti.
     * L'aggiornamento viene effettuato creando un flusso di controllo
     * asincrono che enumera i Percorsi associati all'esperienza attivata e
     * li utilizza per popolare la vista.
     *
     * @param experience Esperienza appena attivata. Se null,
     *                   viene sollevata un'eccezione IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    public synchronized void setActiveExperience(final IExperience experience)
            throws IllegalArgumentException {
        if (experience == null)
            throw new IllegalArgumentException("Illegal null experience");

        view.clearList();
        tracks.clear();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<String> names = new ArrayList<String>();
                int i = 0;

                for (ITrack t : experience.getTracks()) {
                    tracks.add(t);
                    names.add("Tracciato " + Integer.toString(i));
                    i++;
                }

                view.setList(names);
                return null;
            }
        };
        task.execute();
    }
}
