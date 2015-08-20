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

public class InboundRootEntityDeserializer implements JsonDeserializer<InboundRootEntity> {
    @Override
    public InboundRootEntity deserialize(JsonElement json, Type typeOfR, JsonDeserializationContext context) throws JsonParseException {
        InboundRootEntity re = new InboundRootEntity();
        re.experiences = new ArrayList<ExperienceEntity>();
        JsonArray experiences = json.getAsJsonObject().get("experiences").getAsJsonArray();
        for(JsonElement experience : experiences) {
            re.experiences.add(new ExperienceEntityDeserializer().deserialize(experience, ExperienceEntity.class, context));
        }
        re.emergencyData = new ArrayList<EmergencyDataEntity>();
        JsonArray emergencyData = json.getAsJsonObject().get("emergencyData").getAsJsonArray();
        for(JsonElement emergency : emergencyData) {
            re.emergencyData.add(new EmergencyDataDeserializer().deserialize(emergency, EmergencyDataEntity.class, context));
        }
        re.weatherData = new ArrayList<WeatherDataEntity>();
        JsonArray weatherData = json.getAsJsonObject().get("weatherData").getAsJsonArray();
        for(JsonElement weather : weatherData) {
            re.weatherData.add(new WeatherEntityDeserializer().deserialize(weather, WeatherDataEntity.class, context));
        }

        return re;
    }
}
