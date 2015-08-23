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
 * Name: WeatherEntityDeserializer.java
 * Package: com.hitchikers.serleena.synchronization.kylothcloud
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0.0      Tobia Tesan  Creazione del file
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Region;
import com.kyloth.serleena.persistence.WeatherForecastEnum;

import java.lang.reflect.Type;

import static com.kyloth.serleena.synchronization.kylothcloud.WeatherDataEntity.*;

/**
 * Deserializer custom per deserializzare una WeatherEntity dal JSON passato da Kyloth in GSON.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
public class WeatherEntityDeserializer  implements JsonDeserializer<WeatherDataEntity> {
    @Override
    public WeatherDataEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        WeatherDataEntity we =  new WeatherDataEntity();
        we.morning = parseForecast(json.getAsJsonObject().get("morning"));
        we.afternoon = parseForecast(json.getAsJsonObject().get("afternoon"));
        we.night = parseForecast(json.getAsJsonObject().get("night"));

        we.date = json.getAsJsonObject().get("time").getAsLong();

        JsonObject boundingRect = json.getAsJsonObject().get("boundingRect").getAsJsonObject();
        final float nwLatitude = boundingRect.getAsJsonObject().get("topLeft").getAsJsonObject().get("latitude").getAsFloat();
        final float nwLongitude = boundingRect.getAsJsonObject().get("topLeft").getAsJsonObject().get("longitude").getAsFloat();
        final float seLatitude = boundingRect.getAsJsonObject().get("bottomRight").getAsJsonObject().get("latitude").getAsFloat();
        final float seLongitude = boundingRect.getAsJsonObject().get("bottomRight").getAsJsonObject().get("longitude").getAsFloat();

        we.boundingRect = new Region(new GeoPoint(nwLatitude, nwLongitude), new GeoPoint(seLatitude, seLongitude));

        return we;
    }

    private ForecastTuple parseForecast(JsonElement json) {
        ForecastTuple t;
        t = new ForecastTuple();
        t.temperature = json.getAsJsonObject().get("temperature").getAsFloat();
        switch (json.getAsJsonObject().get("forecast").getAsString()) {
            case "CLOUDY":
                t.forecast = WeatherForecastEnum.Cloudy;
                break;
            case "SUNNY":
                t.forecast = WeatherForecastEnum.Sunny;
                break;
            case "STORMY":
                t.forecast = WeatherForecastEnum.Stormy;
                break;
            case "SNOWY":
                t.forecast = WeatherForecastEnum.Snowy;
                break;
            case "RAINY":
                t.forecast = WeatherForecastEnum.Rainy;
                break;
        }
        return t;
    }
}
