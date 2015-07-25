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
 * Name: SyncFragmentTest.java
 * Package: com.kyloth.serleena.view.fragments
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file e scrittura di codice e
 *                           documentazione in Javadoc.
 */

package com.kyloth.serleena.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kyloth.serleena.R;
import com.kyloth.serleena.common.SyncStatusEnum;
import com.kyloth.serleena.presentation.ISyncPresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

@RunWith(RobolectricTestRunner.class)
public class SyncFragmentTest {

    TextView fakeToken;
    TextView fakeInfo;
    ImageButton fakeButton;
    SyncFragment fragment;
    ISyncPresenter presenter;

    @Before
    public void initialize() {
        presenter = mock(ISyncPresenter.class);
        fragment = new SyncFragment();
        fragment.attachPresenter(presenter);

        fakeInfo = mock(TextView.class);
        fakeToken = mock(TextView.class);
        fakeButton = new ImageButton(RuntimeEnvironment.application);

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);

        when(inflater.inflate(
                        eq(R.layout.fragment_sync_screen),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.textview_token)).thenReturn(fakeToken);
        when(v.findViewById(R.id.textview_info)).thenReturn(fakeInfo);
        when(v.findViewById(R.id.button_sync)).thenReturn(fakeButton);
        fragment.onCreateView(inflater, vg, mock(Bundle.class));
    }

    /**
     * Verifica che i metodi resume() e pause() del presenter associato alla
     * vista vengano chiamati in corrispondenza di onPause() e onResume()
     * della vista.
     */
    @Test
    public void presenterShouldBeNotifiedOfResumeAndPause() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che il click del bottone di sync della vista segnali al
     * presenter di avviare la sincronizzazione.
     */
    @Test
    public void clickingButtonShouldTellPresenterToStartSynching() {
        fakeButton.callOnClick();
        verify(presenter).synchronize();
    }

    /**
     * Verifica che il metodo displayToken() imposti correttamente il widget
     * con il token.
     */
    @Test
    public void setTokenShouldSetTextViewAccordingly() {
        fragment.displayToken("token");
        verify(fakeToken).setText("token");
    }

    /**
     * Verifica che il metodo setSyncStatus() imposti correttamente il widget
     * con indicazioni sullo stato della sincronizzazione in corso.
     */
    @Test
    public void setSyncStatusShouldSetInfoTextViewAccordingly() {
        fragment.setSyncStatus(SyncStatusEnum.COMPLETE);
        verify(fakeInfo).setText("FATTO");
        fragment.setSyncStatus(SyncStatusEnum.FAILED);
        verify(fakeInfo).setText("ERRORE");
        fragment.setSyncStatus(SyncStatusEnum.INACTIVE);
        verify(fakeInfo).setText("SINCRONIZZAZIONE NON ATTIVA");
        fragment.setSyncStatus(SyncStatusEnum.INPUT_REQUIRED);
        verify(fakeInfo).setText("IN ATTESA DI CONFERMA...");
        fragment.setSyncStatus(SyncStatusEnum.PREAUTH);
        verify(fakeInfo).setText("STO RICEVENDO IL TOKEN...");
        fragment.setSyncStatus(SyncStatusEnum.REJECTED);
        verify(fakeInfo).setText("INSERIMENTO NON CORRETTO");
        fragment.setSyncStatus(SyncStatusEnum.SYNCING);
        verify(fakeInfo).setText("SINCRONIZZANDO...");
    }

    /**
     * Verifica che la vista cancelli la visualizzazione del token se lo
     * stato della sincronizzazione è diverso da INPUT_REQUIRED.
     */
    @Test
    public void setSyncStatusShouldClearTokenWhenInputNotRequired() {
        fragment.setSyncStatus(SyncStatusEnum.COMPLETE);
        verify(fakeToken).setText("");
        fragment.setSyncStatus(SyncStatusEnum.FAILED);
        verify(fakeToken, times(2)).setText("");
        fragment.setSyncStatus(SyncStatusEnum.INACTIVE);
        verify(fakeToken, times(3)).setText("");
        fragment.setSyncStatus(SyncStatusEnum.INPUT_REQUIRED);
        verify(fakeToken, times(3)).setText("");
        fragment.setSyncStatus(SyncStatusEnum.PREAUTH);
        verify(fakeToken, times(4)).setText("");
        fragment.setSyncStatus(SyncStatusEnum.REJECTED);
        verify(fakeToken, times(5)).setText("");
        fragment.setSyncStatus(SyncStatusEnum.SYNCING);
        verify(fakeToken, times(6)).setText("");
    }

}
