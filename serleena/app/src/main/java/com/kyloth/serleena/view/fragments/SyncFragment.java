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
 * Name: SyncFragment
 * Package: com.kyloth.serleena.view.fragments
 * Author: Sebastiano Valle
 *
 * History:
 * Version   Programmer         Changes
 * 1.0.0     Sebastiano Valle   Creazione del file, scrittura del codice e di Javadoc
 * 1.1.0     Filippo Sestini    Leggero refactoring
 */
package com.kyloth.serleena.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.SyncStatusEnum;
import com.kyloth.serleena.presentation.ISyncPresenter;
import com.kyloth.serleena.presentation.ISyncView;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementa la schermata “Sincronizzazione”, in cui è possibile richiedere la
 * sincronizzazione con la piattaforma Cloud.
 *
 * @use Viene istanziata e utilizzata dall'Activity per la visualizzazione della schermata. Comunica con il Presenter associato attraverso l'interfaccia ISyncPresenter.
 * @field presenter : ISyncPresenter presenter collegato a un SyncFragment
 * @field info : TextView casella di testo dove verranno visualizzate le informazioni di stato
 * @field token : TextView casella di testo dove verranno visualizzate le informazioni sul token per l'accoppiamento
 * @field states : HashMap<SyncStatusEnum,String> mappa di corrispondeze tra oggetti di tipo enumerativo e stringhe
 * @author Sebastiano Valle <valle.sebastiano93@gmail.com>
 * @version 1.1.0
 * @see android.app.Fragment
 */
public class SyncFragment extends Fragment implements ISyncView {

    private ISyncPresenter presenter;
    private TextView info;
    private TextView token;
    Map<SyncStatusEnum, String> states;

    public SyncFragment() {
        super();
        states = new HashMap<>();
        states.put(SyncStatusEnum.COMPLETE,"FATTO");
        states.put(SyncStatusEnum.FAILED,"ERRORE");
        states.put(SyncStatusEnum.INACTIVE,"SINCRONIZZAZIONE NON ATTIVA");
        states.put(SyncStatusEnum.INPUT_REQUIRED, "IN ATTESA DI CONFERMA...");
        states.put(SyncStatusEnum.PREAUTH, "STO RICEVENDO IL TOKEN...");
        states.put(SyncStatusEnum.REJECTED, "INSERIMENTO NON CORRETTO");
        states.put(SyncStatusEnum.SYNCING, "SINCRONIZZANDO...");
        /* Null object pattern */
        presenter = new ISyncPresenter() {
            @Override public void synchronize() { }
            @Override public void resume() { }
            @Override public void pause() { }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.fragment_sync_screen,
                container,
                false);
        info = (TextView) v.findViewById(R.id.textview_info);
        token = (TextView) v.findViewById(R.id.textview_token);
        ImageButton button = (ImageButton) v.findViewById(R.id.button_sync);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.synchronize();
            }
        });
        return v;
    }

    /**
     * Visualizza un token per l'accoppiamento.
     *
     * @param token Token da visualizzare.
     */
    @Override
    public void displayToken(String token) {
        this.token.setText(token);
    }

    /**
     * Metodo utilizzato per visualizzare sul display dello smartwatch lo stato
     * della sincronizzazione
     *
     * @param status Stato da visualizzare
     */
    @Override
    public void setSyncStatus(SyncStatusEnum status) {
        if(status != SyncStatusEnum.INPUT_REQUIRED)
            token.setText("");
        info.setText(states.get(status));
    }

    /**
     * Aggancia un Presenter alla vista.
     *
     * @param presenter Presenter da agganciare.
     */
    @Override
    public void attachPresenter(ISyncPresenter presenter) {
        this.presenter = presenter;
    }

    /**
     * Ridefinisce Fragment.onResume().
     *
     * Chiama il metodo resume() del presenter associato alla vista.
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    /**
     * Ridefinisce Fragment.onPause().
     *
     * Chiama il metodo pause() del presenter associato alla vista.
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

}
