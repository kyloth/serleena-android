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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.Region;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by tobia on 13/08/15.
 */
public class RasterDataEntityDeserializer implements JsonDeserializer<RasterDataEntity> {
    @Override
    public RasterDataEntity deserialize(JsonElement json, Type typeOfR, JsonDeserializationContext context) throws JsonParseException {
        RasterDataEntity res = new RasterDataEntity();
        JsonObject boundingRect = json.getAsJsonObject().get("boundingRect").getAsJsonObject();
        final float nwLatitude = boundingRect.getAsJsonObject().get("topLeft").getAsJsonObject().get("latitude").getAsFloat();
        final float nwLongitude = boundingRect.getAsJsonObject().get("topLeft").getAsJsonObject().get("longitude").getAsFloat();
        final float seLatitude = boundingRect.getAsJsonObject().get("bottomRight").getAsJsonObject().get("latitude").getAsFloat();
        final float seLongitude = boundingRect.getAsJsonObject().get("bottomRight").getAsJsonObject().get("longitude").getAsFloat();
        res.boundingRect = new Region(new GeoPoint(nwLatitude, nwLongitude), new GeoPoint(seLatitude, seLongitude));
        res.base64Raster = json.getAsJsonObject().get("image").getAsString();
        return res;
    }
}