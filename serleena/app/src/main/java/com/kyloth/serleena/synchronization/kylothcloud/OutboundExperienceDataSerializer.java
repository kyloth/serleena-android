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
 * Name: OutboundExperienceDataSerializer.java
 * Package: com.hitchikers.serleena.synchronization.kylothcloud
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0.0      Tobia Tesan  Creazione del file
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import java.util.Iterator;

/**
 * Serializer custom per serializzare una OutboundExperienceData in JSON
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */

class OutboundExperienceDataSerializer implements JsonSerializer<OutboundExperienceDataEntity> {
    @Override
    public JsonElement serialize(OutboundExperienceDataEntity or, Type typeOfOr, JsonSerializationContext context) {
        JsonObject outboundData = new JsonObject();
        outboundData.addProperty("experience", or.experience.toString());
        JsonArray userPoints = new JsonArray();
        Iterator<UserPointEntity> i_userpoints = or.userPoints.iterator();
        while(i_userpoints.hasNext()) {
            userPoints.add(new UserPointSerializer().serialize(i_userpoints.next(), UserPointEntity.class, context));
        }
        outboundData.add("userPoints", userPoints);
        JsonArray telemetryData = new JsonArray();
        Iterator<OutboundTelemetryEntity> i_telemetry = or.telemetryData.iterator();
        while(i_telemetry.hasNext()) {
            telemetryData.add(new OutboundTelemetryEntitySerializer().serialize(i_telemetry.next(), OutboundTelemetryEntity.class, context));
        }
        outboundData.add("telemetryData", telemetryData);

        return outboundData;
    }

}
