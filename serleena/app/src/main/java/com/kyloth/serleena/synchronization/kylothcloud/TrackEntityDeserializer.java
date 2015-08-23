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


package com.kyloth.serleena.synchronization.kylothcloud;
/**
 * Name: TrackEntityDeserializer.java
 * Package: com.hitchikers.serleena.synchronization.kyloth
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0.0      Tobia Tesan  Creazione del file
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Deserializer custom per deserializzare una TrackEntity dal JSON passato da Kyloth in GSON.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
class TrackEntityDeserializer implements JsonDeserializer<TrackEntity> {
    @Override
    public TrackEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TrackEntity te = new TrackEntity();
        String idString = json.getAsJsonObject().get("id").getAsString();
        te.uuid = UUID.fromString(idString);
        te.name = json.getAsJsonObject().get("name").getAsString();
        te.checkpoints = new LinkedList<CheckpointEntity>();
        JsonArray cpoints = json.getAsJsonObject().get("checkPoints").getAsJsonArray();
        for (JsonElement cpoint : cpoints) {
            CheckpointEntity cp = new CheckpointEntity();
            cp.id = cpoint.getAsJsonObject().get("id").getAsInt();
            cp.point = new GeoPoint(
                cpoint.getAsJsonObject().get("latitude").getAsFloat(),
                cpoint.getAsJsonObject().get("longitude").getAsFloat()
            );
            te.checkpoints.add(cp);
        }
        te.telemetries = new LinkedList<TelemetryEntity>();
        JsonElement bestTele = json.getAsJsonObject().get("bestTelemetry");

        if (!bestTele.isJsonNull())
            te.telemetries.add(new TelemetryEntityDeserializer().deserialize(bestTele.getAsJsonObject(), TelemetryEntity.class, context));
        return te;
    }
}
