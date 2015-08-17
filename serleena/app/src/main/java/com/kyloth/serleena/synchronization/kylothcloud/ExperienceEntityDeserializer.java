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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Region;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/**
 * Deserializer custom per deserializzare una ExperienceEntity dal JSON passato da Kyloth in GSON.
 */
class ExperienceEntityDeserializer implements JsonDeserializer<ExperienceEntity> {


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
    public ExperienceEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ExperienceEntity ee = new ExperienceEntity();
        String idString = json.getAsJsonObject().get("id").getAsString();
        ee.uuid = UUID.fromString(idString);
        ee.name = json.getAsJsonObject().get("name").getAsString();
        ee.tracks = new ArrayList<TrackEntity>();
        JsonArray tracks = json.getAsJsonObject().get("tracks").getAsJsonArray();
        for (JsonElement track : tracks) {
            ee.tracks.add(new TrackEntityDeserializer().deserialize(track, TrackEntity.class, context));
        };

        GeoPoint nw = new GeoPoint(
            json.getAsJsonObject().get("boundingRect").getAsJsonObject().get("topLeft").getAsJsonObject().get("latitude").getAsFloat(),
            json.getAsJsonObject().get("boundingRect").getAsJsonObject().get("topLeft").getAsJsonObject().get("longitude").getAsFloat()
        );

        GeoPoint se = new GeoPoint(
            json.getAsJsonObject().get("boundingRect").getAsJsonObject().get("bottomRight").getAsJsonObject().get("latitude").getAsFloat(),
            json.getAsJsonObject().get("boundingRect").getAsJsonObject().get("bottomRight").getAsJsonObject().get("longitude").getAsFloat()
        );

        ee.region = new Region(nw, se);

        Iterator<JsonElement> i = json.getAsJsonObject().get("rasterData").getAsJsonArray().iterator();

        ee.rasterData = new ArrayList<RasterDataEntity>();
        RasterDataEntityDeserializer s = new RasterDataEntityDeserializer();
        while(i.hasNext()) {
            ee.rasterData.add(s.deserialize(i.next(), RasterDataEntity.class, context));
        }

        ee.userPoints = new ArrayList<UserPointEntity>();
        JsonArray upoints = json.getAsJsonObject().get("user_points").getAsJsonArray();
        for (JsonElement upoint : upoints) {
            UserPointEntity up = new UserPointEntity();
            up.point = new GeoPoint(
                upoint.getAsJsonObject().get("latitude").getAsFloat(),
                upoint.getAsJsonObject().get("longitude").getAsFloat()
            );
            up.name = upoint.getAsJsonObject().get("name").getAsString();
            ee.userPoints.add(up);
        }

        return ee;
    }
}
