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
 * Name: SerleenaDatabase.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 * Date: 2015-05-05
 *
 * History:
 * Version  Programmer          Date        Changes
 * 1.0.0    Filippo Sestini     2015-05-05  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 * 1.0.1    Tobia Tesan         2015-05-06  Aggiunti ON DELETE
 * 1.0.2    Tobia Tesan         2015-05-06  Aggiunto onConfigure.
 * 1.0.3    Filippo Sestini     2015-05-11  Aggiunta tabella 'checkpoints' al
 *                                          database.
 * 1.0.4    Tobia Tesan         2015-05-12  Rimossa TABLE_RASTER_MAPS
 */

package com.kyloth.serleena.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/**
 * Supporta la creazione e l'apertura del database SQLite utilizzato
 * dall'applicazione serleena, secondo quando prescritto dal framework Android.
 *
 * @use Viene utilizzato dall'activity e da SerleenaSQLiteDataSource. Istanziato dall'activity, che lo utilizza per creare il DAO di tipo SerleenaSQLiteDataSource. Quest'ultimo accede al database dell'applicazione attraverso l'oggetto SerleenaDatabase con il quale è stato creato.
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 * @since 2015-05-05
 */
public class SerleenaDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "serleena.kyloth.db";
    public static final String TABLE_EXPERIENCES = "experiences";
    public static final String TABLE_TRACKS = "tracks";
    public static final String TABLE_TELEMETRIES = "telemetries";
    public static final String TABLE_TELEM_EVENTS_HEART_CHECKP =
        "telemetry_events_heart_checkp";
    public static final String TABLE_TELEM_EVENTS_LOCATION =
        "telemetry_events_location";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_WEATHER_FORECASTS = "weather_forecasts";
    public static final String TABLE_USER_POINTS = "user_points";
    public static final String TABLE_CHECKPOINTS = "checkpoints";
    private static final int DATABASE_VERSION = 1;

    public static final String EVENT_TYPE_HEARTRATE = "event_heartrate";
    public static final String EVENT_TYPE_CHECKPOINT = "event_checkpoint";

    private static final String CREATE_TABLE_EXPERIENCES =
        "CREATE TABLE " + TABLE_EXPERIENCES + "(" +
        "experience_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "experience_name TEXT NOT NULL)";

    private static final String CREATE_TABLE_TRACKS =
        "CREATE TABLE " + TABLE_TRACKS + "(" +
        "track_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "track_name TEXT NOT NULL, " +
        "track_experience INTEGER NOT NULL, " +
        "FOREIGN KEY(track_experience) REFERENCES " + TABLE_EXPERIENCES + " (experience_id) ON DELETE CASCADE)";

    private static final String CREATE_TABLE_TELEMETRIES =
        "CREATE TABLE " + TABLE_TELEMETRIES + "(" +
        "telem_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "telem_track INTEGER NOT NULL, " +
        "FOREIGN KEY(telem_track) REFERENCES " + TABLE_TRACKS + "(track_id) ON DELETE CASCADE)";

    private static final String CREATE_TABLE_TELEM_EVENTS_LOCATION =
        "CREATE TABLE " + TABLE_TELEM_EVENTS_LOCATION + "(" +
        "eventl_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "eventl_timestamp INTEGER NOT NULL, " +
        "eventl_latitude REAL NOT NULL, " +
        "eventl_longitude REAL NOT NULL, " +
        "eventl_telem INTEGER NOT NULL, " +
        "FOREIGN KEY(eventl_telem) REFERENCES " + TABLE_TELEMETRIES + "(telem_id) ON DELETE CASCADE)";

    private static final String CREATE_TABLE_TELEM_EVENTS_HEART_CHECKP =
        "CREATE TABLE " + TABLE_TELEM_EVENTS_HEART_CHECKP + "(" +
        "eventhc_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "eventhc_timestamp INTEGER NOT NULL, " +
        "eventhc_value INTEGER NOT NULL, " +
        "eventhc_type TEXT NOT NULL, " +
        "eventhc_telem INTEGER NOT NULL, " +
        "FOREIGN KEY(eventhc_telem) REFERENCES " + TABLE_TELEMETRIES + "(telem_id) ON DELETE CASCADE)";

    private static final String CREATE_TABLE_CONTACTS =
        "CREATE TABLE " + TABLE_CONTACTS + "(" +
        "contact_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "contact_name TEXT NOT NULL, " +
        "contact_value TEXT NOT NULL, " +
        "contact_ne_corner_latitude REAL NOT NULL, " +
        "contact_ne_corner_longitude REAL NOT NULL, " +
        "contact_sw_corner_latitude REAL NOT NULL, " +
        "contact_sw_corner_longitude REAL NOT NULL)";

    private static final String CREATE_TABLE_WEATHER_FORECASTS =
        "CREATE TABLE " + TABLE_WEATHER_FORECASTS + "(" +
        "weather_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "weather_start INTEGER NOT NULL, " +
        "weather_end INTEGER NOT NULL, " +
        "weather_condition INTEGER NOT NULL, " +
        "weather_temperature INTEGER NOT NULL, " +
        "weather_ne_corner_latitude REAL NOT NULL, " +
        "weather_ne_corner_longitude REAL NOT NULL, " +
        "weather_sw_corner_latitude REAL NOT NULL, " +
        "weather_sw_corner_longitude REAL NOT NULL)";

    private static final String CREATE_TABLE_USER_POINTS =
        "CREATE TABLE " + TABLE_USER_POINTS + "(" +
        "userpoint_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        "userpoint_x REAL NOT NULL, " +
        "userpoint_y REAL NOT NULL, " +
        "userpoint_experience INTEGER NOT NULL, " +
        "FOREIGN KEY(userpoint_experience) REFERENCES " + TABLE_EXPERIENCES +
        "(experience_id) ON DELETE CASCADE)";

    private static final String CREATE_TABLE_CHECKPOINTS =
        "CREATE TABLE " + TABLE_CHECKPOINTS + "(" +
        "checkpoint_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "checkpoint_num INTEGER NOT NULL, " +
        "checkpoint_latitude REAL NOT NULL, " +
        "checkpoint_longitude REAL NOT NULL, " +
        "checkpoint_track INTEGER NOT NULL, " +
        "FOREIGN KEY(checkpoint_track) REFERENCES " + TABLE_TRACKS + "(track_id) ON DELETE CASCADE)";

    /**
     * Crea un oggetto SerleenaDatabase associato al database predefinito dalla
     * costante DATABASE_NAME.
     *
     * @param context Contesto usato per creare o aprire il database.
     * @param version Versione del database.
     */
    public SerleenaDatabase(Context context, int version) {
        this(context, DATABASE_NAME, null, version);
    }

    /**
     * Crea un oggetto SerleenaDatabase.
     *
     * @param context   Oggetto android.content.Context usato per creare o
     *                  aprire il database.
     * @param name      Nome del database.
     * @param factory   Usato per creare cursori.
     * @param version   Versione del database.
     */
    public SerleenaDatabase(Context context, String name, CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }

    /**
     * Crea il database su disco.
     *
     * Il metodo è chiamato quando il database non è presente su disco, ed è
     * necessario crearne uno.
     *
     * @param db Il database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXPERIENCES);
        db.execSQL(CREATE_TABLE_TRACKS);
        db.execSQL(CREATE_TABLE_TELEMETRIES);
        db.execSQL(CREATE_TABLE_TELEM_EVENTS_HEART_CHECKP);
        db.execSQL(CREATE_TABLE_TELEM_EVENTS_LOCATION);
        db.execSQL(CREATE_TABLE_CONTACTS);
        db.execSQL(CREATE_TABLE_WEATHER_FORECASTS);
        db.execSQL(CREATE_TABLE_USER_POINTS);
        db.execSQL(CREATE_TABLE_CHECKPOINTS);
    }

    /**
     * Configura la connessione al database.
     *
     * E' chiamato prima di onCreate, onUpgrade, etc.
     *
     * @author Tobia Tesan <tobia.tesan@gmail.com>
     * @param db Il database.
     * @since 1.0.2
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Aggiorna la versione del database su disco.
     *
     * Metodo chiamato quando la versione del database corrente non corrisponde
     * alla versione presente su disco, ed è necessario aggiornarla.
     *
     * @param db Il database.
     * @param oldVersion Vecchia versione.
     * @param newVersion Nuova versione.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPERIENCES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELEMETRIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELEM_EVENTS_HEART_CHECKP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELEM_EVENTS_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER_FORECASTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_POINTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKPOINTS);

        onCreate(db);
    }
}
