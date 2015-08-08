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
 * Name: SerleenaDatabaseTestUtils.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version  Programmer   Changes
 * 1.0      Tobia Tesan  Creazione file
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Classe di utilita' per i test di SerleenaDatabase e SerleenaSQLiteDataSource
 */
class SerleenaDatabaseTestUtils {

    /**
     * Metodo di utilita' per creare un'esperienza.
     */
    public static long makeExperience(SQLiteDatabase db) {
        ContentValues values;
        values = new ContentValues();
        values.put("experience_name", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_NAME);
        return db.insertOrThrow(SerleenaDatabase.TABLE_EXPERIENCES, null, values);
    }

    /**
     * Metodo di utilita' per creare un percorso in un DB vuoto.
     * Crea automaticamente l'esperienza richiesta.
     */
    public static long makeTrack(SQLiteDatabase db) {
        ContentValues values;
        values = new ContentValues();
        values.put("track_name", TestFixtures.EXPERIENCES_FIXTURE_EXPERIENCE_1_TRACK_1_NAME);
        values.put("track_experience", makeExperience(db));
        return db.insertOrThrow(SerleenaDatabase.TABLE_TRACKS, null, values);
    }

    /**
     * Metodo di utilita' per creare un tracciamento in un DB vuoto.
     * Crea automaticamente il percorso e l'esperienza richiesta.
     */
    public static long makeTelemetry(SQLiteDatabase db) {
        ContentValues values;
        values = new ContentValues();
        values.put("telem_track", makeTrack(db));
        return db.insertOrThrow(SerleenaDatabase.TABLE_TELEMETRIES, null, values);
    }
}
