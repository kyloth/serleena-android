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
 * Name: TelemetryFragmentTest.java
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
import com.kyloth.serleena.presentation.ITelemetryPresenter;
import com.kyloth.serleena.sensors.TrackAlreadyStartedException;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.junit.Test;
import org.junit.Before;

import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe CompassFragment.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public class TelemetryFragmentTest {

    private TelemetryFragment fragment;
    private ITelemetryPresenter presenter;
    private ImageButton button;
    private TextView text;
    private View.OnClickListener listener;

    @Before
    public void initialize() {
        fragment = new TelemetryFragment();

        LayoutInflater inflater = mock(LayoutInflater.class);
        ViewGroup vg = mock(ViewGroup.class);
        View v = mock(View.class);
        button = mock(ImageButton.class);
        text = mock(TextView.class);

        when(inflater.inflate(
                        eq(R.layout.fragment_telemetry),
                        eq(vg),
                        any(Boolean.class))
        ).thenReturn(v);
        when(v.findViewById(R.id.telemetry_image)).thenReturn(button);
        when(v.findViewById(R.id.telemetry_status)).thenReturn(text);
        fragment.onCreateView(inflater, vg, mock(Bundle.class));

        ArgumentCaptor<View.OnClickListener> captor =
                ArgumentCaptor.forClass(View.OnClickListener.class);
        verify(button).setOnClickListener(captor.capture());
        listener = captor.getValue();

        presenter = mock(ITelemetryPresenter.class);
        fragment.attachPresenter(presenter);
    }

    /**
     * Verifica che i metodi resume() e pause() del presenter vengano
     * chiamati correttamente.
     */
    @Test
    public void testAttachCompassPresenter() {
        fragment.onResume();
        verify(presenter).resume();
        fragment.onPause();
        verify(presenter).pause();
    }

    /**
     * Verifica che la distruzione del Fragment interrompa il Tracciamento,
     * che altrimenti continuerebbe anche dopo il termine dell'applicazione.
     */
    @Test
    public void destroyingFragmentShouldDisableTelemetry() {
        fragment.onDestroy();
        verify(presenter).disableTelemetry();
    }

    /**
     * Verifica che inoltrata al presenter la richiesta di
     * abilitare/disabilitare il tracciamento, iin base all'interazione
     * dell'utente con la vista.
     */
    @Test
    public void presenterShouldBeNotifiedWhenUserClicks() throws
            TrackAlreadyStartedException {
        when(text.getText()).thenReturn("OFF");
        listener.onClick(button);
        verify(presenter).enableTelemetry();

        when(text.getText()).thenReturn("ON");
        listener.onClick(button);
        verify(presenter).disableTelemetry();

        when(text.getText()).thenReturn("OFF");
        listener.onClick(button);
        verify(presenter, times(2)).enableTelemetry();

        when(text.getText()).thenReturn("ON");
        listener.onClick(button);
        verify(presenter, times(2)).disableTelemetry();
    }

    /**
     * Verifica che la vista visualizzi un errore quando si tenta di
     * abilitare il tracciamento per un percorso già avviato.
     */
    @Test
    public void shouldDisplayErrorIfTrackAlreadyStarted()
            throws TrackAlreadyStartedException {
        Mockito.doThrow(TrackAlreadyStartedException.class)
                .when(presenter).enableTelemetry();
        when(text.getText()).thenReturn("OFF");
        listener.onClick(button);
        verify(text).setText("ERRORE: Percorso già avviato");
    }

}
