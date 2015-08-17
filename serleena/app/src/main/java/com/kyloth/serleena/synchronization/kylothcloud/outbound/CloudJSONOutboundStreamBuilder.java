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
 * Name: CloudJSONOutboundStreamBuilder.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.kylothcloud.outbound;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.ITelemetryStorage;
import com.kyloth.serleena.persistence.ITrackStorage;
import com.kyloth.serleena.synchronization.JSONOutboundStream;
import com.kyloth.serleena.synchronization.OutboundStream;
import com.kyloth.serleena.synchronization.OutboundStreamBuilder;
import com.kyloth.serleena.synchronization.kylothcloud.OutboundDataEntity;
import com.kyloth.serleena.synchronization.kylothcloud.OutboundRootEntity;
import com.kyloth.serleena.synchronization.kylothcloud.OutboundTelemetryEntity;
import com.kyloth.serleena.synchronization.kylothcloud.OutboundRootSerializer;
import com.kyloth.serleena.synchronization.kylothcloud.TrackEntity;
import com.kyloth.serleena.synchronization.kylothcloud.UserPointEntity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Concretizza OutboundStreamBuilder in modo da poter costruire e scrivere stream JSON
 * nel formato che KylothCloud si attende.
 *
 * @use Viene usato da KylothCloudSynchronizer per costruire un OutboundStream nel formato idoneo ad essere passato a SerleenaJSONNetProxy per l'invio a KylothCloud
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 */
public class CloudJSONOutboundStreamBuilder implements OutboundStreamBuilder {
    OutboundRootEntity root;
    final Gson gson = new GsonBuilder().registerTypeAdapter(OutboundRootEntity.class,
                                                                 new OutboundRootSerializer()).create();
    
    public CloudJSONOutboundStreamBuilder() {
        root = new OutboundRootEntity();
        root.data = new ArrayList<OutboundDataEntity>();
    }

    /**
     * Aggiunge un'Esperienza (e di conseguenza i suoi Punti Utente
     * e eventuali altri dati raccolti localmente) all'OutboundStream
     * da costruire.
     *
     * @param exp Un'Esperienza le cui componenti raccolte sul dispositivo
     *          si vogliono  inviare al servizio remoto.
     */
    @Override
    public void addExperience(IExperienceStorage exp) {
        OutboundDataEntity e = new OutboundDataEntity();
        e.experience = exp.getUUID();
        int userPointCounter = 0;
        for (UserPoint p : exp.getUserPoints()) {
            UserPointEntity pe = new UserPointEntity();
            pe.point = p;
            pe.name = "Point "+userPointCounter++;
            e.userPoints.add(pe);
        }
        for (ITrackStorage t : exp.getTracks()) {
            TrackEntity te = new TrackEntity();
            te.name = "";
            // TODO: Noi non ce l'abbiamo?
            te.uuid = t.getUUID();
            int i = 0;
            for (ITelemetryStorage ts : t.getTelemetries()) {
                OutboundTelemetryEntity tse = new OutboundTelemetryEntity();
                for (TelemetryEvent tsv : ts.getEvents()) {
                    long ee;
                    ee = tsv.timestamp();
                    tse.events.add(ee);
                }
                tse.track = te.uuid;
                e.telemetryData.add(tse);
            }
        }
        root.data.add(e);
    }

    public String build() {
        return gson.toJson(root, OutboundRootEntity.class);
    }
    /**
     * Scrive nell'OutboundStream fornito i dati preaprati in formato JSON
     */
    @Override
    public void stream(OutboundStream s) throws IOException {
        if (s instanceof JSONOutboundStream) {
            JsonWriter writer = null;
            try {
                URLEncodedWriter urlw = new URLEncodedWriter(
                        new OutputStreamWriter(
                                (OutputStream) s,
                                "UTF-8")
                );
                writer = new JsonWriter(urlw);
                writer.setIndent("  ");

                gson.toJson(root, OutboundRootEntity.class, writer);
                writer.flush();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
