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

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.UUID;

class OutboundDataDeserializer implements JsonDeserializer<OutboundDataEntity> {

    @Override
    public OutboundDataEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        OutboundDataEntity ode = new OutboundDataEntity();
        ode.experience = UUID.fromString(json.getAsJsonObject().get("experience").getAsString());
        ode.userPoints = new ArrayList<UserPointEntity>();
        JsonArray userPoints = json.getAsJsonObject().get("userPoints").getAsJsonArray();
        for (JsonElement up : userPoints) {
            ode.userPoints.add(new UserPointDeserializer().deserialize(up, UserPointEntity.class, context));
        }
        ode.telemetryData = new ArrayList<OutboundTelemetryEntity>();
        JsonArray telemetryData = json.getAsJsonObject().get("telemetryData").getAsJsonArray();
        for (JsonElement t : telemetryData) {
        }

        return ode;
    }
}
