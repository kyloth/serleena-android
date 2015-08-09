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


package com.kyloth.serleena.synchronization.kyloth;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;

import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Deserializer custom per deserializzare una TrackEntity dal JSON passato da Kyloth in GSON.
 */
class TrackEntityDeserializer implements JsonDeserializer<TrackEntity> {

    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * the same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public TrackEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TrackEntity te = new TrackEntity();
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
        te.telemetries.add(new TelemetryEntityDeserializer().deserialize(json.getAsJsonObject().get("bestTelemetry").getAsJsonObject(), TelemetryEntity.class, context));
        return te;
    }
}
