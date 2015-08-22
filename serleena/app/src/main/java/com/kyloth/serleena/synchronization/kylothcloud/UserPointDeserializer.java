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
 * Name: UserPointDeserializer.java
 * Package: com.hitchikers.serleena.synchronization.kylothcloud
 * Author: Tobia Tesan
 *
 * History:
 * Version    Programmer       Changes
 * 1.0        Tobia Tesan      Creazione del file
 */

package com.kyloth.serleena.synchronization.kylothcloud;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import com.kyloth.serleena.common.GeoPoint;

/**
 * Deserializer custom per deserializzare una UserPointEntity dal JSON passato
 * da Kyloth in GSON.
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0
 */
class UserPointDeserializer implements JsonDeserializer<UserPointEntity> {

    @Override
    public UserPointEntity deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        UserPointEntity up = new UserPointEntity();
        up.point = new GeoPoint(json.getAsJsonObject().get("latitude").getAsDouble(), json.getAsJsonObject().get("longitude").getAsDouble());
        up.name = json.getAsJsonObject().get("name").getAsString();

        return up;
    }

}
