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
 * Name: ISerleenaSQLiteDataSink.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Tobia Tesan      Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kyloth.serleena.synchronization.InboundDump;
import com.kyloth.serleena.synchronization.kylothcloud.inbound.SerleenaSQLiteInboundDump;

public class SerleenaSQLiteDataSink implements ISerleenaSQLiteDataSink {
    private SerleenaDatabase dbHelper;
    private Context context;

    public SerleenaSQLiteDataSink(Context context, SerleenaDatabase dbHelper) {
        this.dbHelper = dbHelper;
        this.context = context;
    }

    /**
     * Carica un dump di dati proveniente dall'esterno.
     *
     * @param dump
     */
    @Override
    public void load(InboundDump dump) {
        if (dump instanceof SerleenaSQLiteInboundDump) {
            SQLiteDatabase a = dbHelper.getWritableDatabase();
            for (String instr : dump) {
                a.execSQL(instr);
            }
            // TODO: Esegui il dump riga per riga
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Svuota completamente i dati.
     */
    @Override
    public void flush() {
        // TODO: Svuota il database
    }
}
