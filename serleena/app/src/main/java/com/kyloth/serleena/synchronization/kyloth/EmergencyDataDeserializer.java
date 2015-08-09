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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Quadrant;

import java.lang.reflect.Type;

public class EmergencyDataDeserializer implements JsonDeserializer<EmergencyDataEntity> {
    @Override
    public EmergencyDataEntity deserialize(JsonElement json, Type typeOfEd, JsonDeserializationContext context) throws JsonParseException {
        EmergencyDataEntity ede = new EmergencyDataEntity();
        JsonObject boundingRect = json.getAsJsonObject().get("boundingRect").getAsJsonObject();
        JsonObject topLeft = boundingRect.getAsJsonObject("topLeft");
        JsonObject bottomRight = boundingRect.getAsJsonObject("bottomRight");
        GeoPoint tl = new GeoPoint(topLeft.getAsJsonObject().get("latitude").getAsDouble(), topLeft.get("longitude").getAsDouble());
        GeoPoint br = new GeoPoint(bottomRight.getAsJsonObject().get("latitude").getAsDouble(), bottomRight.get("longitude").getAsDouble());
        ede.rect = new Quadrant(tl, br, null);
        ede.name = json.getAsJsonObject().get("name").getAsString();
        ede.number = json.getAsJsonObject().get("number").getAsString();
        return ede;
    }
}
