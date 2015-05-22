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
 * Name: TrackFragment
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 */
package com.kyloth.serleena.view.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.presentation.ITrackPresenter;
import com.kyloth.serleena.presentation.ITrackView;


/**
 * Classe che implementa la visuale “Percorso” della schermata “Esperienza”.
 *
 * In questa visuale vengono visualizzate le informazioni riguardanti il percorso attivo.
 *
 * @field presenter : ITrackPresenter presenter collegato a un TrackFragment
 * @field direction : Float direzione del prossimo checkpoint
 * @field totalCheckpoints : Integer numero di checkpoint totali
 * @field currentCheckpoint : Integer checkpoint a cui è arrivato l'utente
 * @field distance : Integer distanza dal prossimo checkpoint
 * @field gainVsGhost : Integer vantaggio netto sulla prestazione del ghost
 * @field elapsedTime : Integer tempo rilevato all'ultimo checkpoint
 * @field partialTW : TextView View dove è visualizzato il confronto tra checkpoint attraversati e totali
 * @field nextTW : TextView View dove sono visualizzate informazioni sul successivo checkpoint
 * @field elapsedTimeTW : TextView View dove è visualizzato il tempo parziale rilevato all'ultimo checkpoint
 * @field ghostTimeTW : TextView View dove è visualizzato il guadagno rispetto alla prestazione migliore
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.0.0
 * @see android.app.Fragment
 */
public class TrackFragment extends Fragment implements ITrackView {

    /**
     * Presenter collegato a un TrackFragment
     */
    private ITrackPresenter presenter;

    private Float direction;
    private Integer totalCheckpoints = 0;
    private Integer currentCheckpoint = 0;
    private Integer distance;
    private Integer gainVsGhost;
    private Integer elapsedTime = 0;

    private TextView partialTW;
    private TextView nextTW;
    private TextView elapsedTimeTW;
    private TextView ghostTimeTW;

    /**
     * Questo metodo viene invocato ogni volta che un TrackFragment viene collegato ad un'Activity.
     *
     * @param activity Activity che ha appena terminato una transazione in cui viene aggiunto il corrente Fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        partialTW = (TextView) activity.findViewById(R.id.track_checkpoint_partial);
        nextTW = (TextView) activity.findViewById(R.id.track_checkpoint_next);
        elapsedTimeTW = (TextView) activity.findViewById(R.id.track_time_elapsed);
        ghostTimeTW = (TextView) activity.findViewById(R.id.track_ghost_time);
        presenter.resume();
    }

    /**
     * Questo metodo viene invocato ogni volta che un TrackFragment viene rimosso da un'Activity
     * tramite una transazione. Viene cancellato il riferimento all'Activity a cui era legato.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        presenter.pause();
    }

    /**
     * Metodo per eseguire un'operazione di subscribe relativa ad un ITrackPresenter.
     *
     * @param presenter oggetto collegato che verrà notificato da questo Fragment
     */
    @Override
    public void attachPresenter(ITrackPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Metodo che rimuove le informazioni sullo schermo.
     */
    @Override
    public void clearView() {
        partialTW.setText("");
        nextTW.setText("NESSUN DATO");
        elapsedTimeTW.setText("DA VISUALIZZARE");
        ghostTimeTW.setText("");
        direction = null;
        gainVsGhost = null;
    }

    /**
     * Metodo che visualizza sullo schermo dello smartwatch le informazioni disponibili sul percorso.
     */
    private void displayInfo() {
        String text = currentCheckpoint + "/" + totalCheckpoints ;
        partialTW.setText(text);
        text = getDirection();
        nextTW.setText(text);
        text = getTime(elapsedTime);
        elapsedTimeTW.setText(text);
        text = "";
        if(gainVsGhost != null) {
            if(gainVsGhost < 0) {
                text = "-";
                gainVsGhost = gainVsGhost * (-1);
            }
            text = text + getTime(gainVsGhost);
        }
        ghostTimeTW.setText(text);
    }

    /**
     * Metodo che restituisce l'ora scritta in un formato convenzionale.
     *
     * @param time tempo (in secondi) da convertire in HH:MM:SS
     */
    private String getTime(Integer time) {
        Integer hoursI = (time / 3600);
        String hours = hoursI.toString();
        if(hoursI < 10) hours = "0" + hours;
        Integer minutesI = ((time % 3600) / 60);
        String minutes = minutesI.toString();
        if(minutesI < 10) minutes = "0" + minutes;
        Integer secondsI = (elapsedTime % 60);
        String seconds = secondsI.toString();
        if(secondsI < 10) seconds = "0" + seconds;
        return (hours + ":" + minutes + ":" + seconds);
    }

    /**
     * Metodo che restituisce una stringa contenente le informazioni sul prossimo checkpoint.
     */
    private String getDirection() {
        if(distance == null && direction == null) return "";
        if(direction == null) return (distance + "m");
        String s = "DRITTO";
        if(direction > 45)
            s = "SX";
        if(direction > 135)
            s = "GIRATI";
        if(direction > 225)
            s = "DX";
        if(direction > 315)
            s = "DRITTO";
        if (distance != null)
            s = s + " - " + distance + "m";
        return s;
    }

    /**
     * Metodo per impostare la direzione del prossimo checkpoint
     *
     * @param heading Direzione del prossimo checkpoint
     */
    @Override
    public void setDirection(float heading) {
        direction = heading;
        displayInfo();
    }

    /**
     * Metodo per impostare la distanza dal prossimo checkpoint.
     *
     * @param distance Distanza dal prossimo checkpoint
     */
    @Override
    public void setDistance(int distance) {
        this.distance = distance;
        displayInfo();
    }

    /**
     * Metodo per impostare il tempo di attraversamento dell'ultimo checkpoint.
     *
     * @param seconds Secondi impiegati per raggiungere l'ultimo checkpoint attraversato
     */
    @Override
    public void setLastPartial(int seconds) {
        elapsedTime = seconds;
        displayInfo();
    }

    /**
     * Metodo per impostare il vantaggio rispetto alla miglior prestazione.
     *
     * @param seconds Secondi di scarto dalla miglior prestazione
     */
    @Override
    public void setDelta(int seconds) {
        gainVsGhost = seconds;
        displayInfo();
    }

    /**
     * Metodo per impostare l'ultimo checkpoint attraversato.
     *
     * @param n Numero che rappresenta quale checkpoint è stato attraversato
     */
    @Override
    public void setCheckpointNo(int n) {
        currentCheckpoint = n;
        displayInfo();
    }

    /**
     * Metodo per impostare il numero totale di checkpoint.
     *
     * @param n Numero totale di checkpoint
     */
    @Override
    public void setTotalCheckpoints(int n) {
        totalCheckpoints = n;
        displayInfo();
    }

    /**
     * Metodo per impostare il tracciamento attivo.
     *
     * @param b Booleano vero sse il tracciamento è attivo.
     */
    @Override
    public void telemetryEnabled(boolean b) {
        displayInfo();
    }
}
