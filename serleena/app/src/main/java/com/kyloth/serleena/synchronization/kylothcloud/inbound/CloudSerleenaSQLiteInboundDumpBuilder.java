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
 * Name: SerleenaSQLiteInboundDumpBuilder.java
 * Package: com.kyloth.serleena.synchronization
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer        Changes
 * 0.0.1    Tobia Tesan       Creazione file
 */
package com.kyloth.serleena.synchronization.kylothcloud.inbound;

import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.synchronization.InboundDumpBuilder;
import com.kyloth.serleena.synchronization.kylothcloud.CheckpointEntity;
import com.kyloth.serleena.synchronization.kylothcloud.EmergencyDataEntity;
import com.kyloth.serleena.synchronization.kylothcloud.ExperienceEntity;
import com.kyloth.serleena.synchronization.kylothcloud.InboundRootEntity;
import com.kyloth.serleena.synchronization.kylothcloud.TelemetryEntity;
import com.kyloth.serleena.synchronization.kylothcloud.TrackEntity;
import com.kyloth.serleena.synchronization.kylothcloud.UserPointEntity;
import com.kyloth.serleena.synchronization.kylothcloud.WeatherDataEntity;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static java.lang.Math.round;

/**
 * Concretizza InboundDumpBuilder in modo da restituire un InboundDump
 * compatibile con il formato del database interno dell'orologio.
 *
 * @use Viene usato da KylothCloudSynchronizer per trasformare una collezione di IDataEntity provenienti da un InboundStreamParser in un dump idoneo a essere caricato nel database dell'orologio.
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 0.1
 */
public class CloudSerleenaSQLiteInboundDumpBuilder implements InboundDumpBuilder {
    // TODO: Effettivamente se prende solo la root potrebbe non meritare il nome builder. D'altra parte e' inutile fare diversamente.
    InboundRootEntity root;

    public CloudSerleenaSQLiteInboundDumpBuilder(InboundRootEntity root) {
        this.root = root;
    }

    private SerleenaSQLiteInboundDump flush() {
        SerleenaSQLiteInboundDump res =  new SerleenaSQLiteInboundDump();
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_EXPERIENCES);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_USER_POINTS);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_TRACKS);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_CHECKPOINTS);
        return res;
    }

    private SerleenaSQLiteInboundDump buildExperiences(Collection<ExperienceEntity> e) {
        SerleenaSQLiteInboundDump res =  new SerleenaSQLiteInboundDump();
        int expCounter = 0;
        int trackCounter = 0;
        int telemCounter = 0;
        for (ExperienceEntity exp : e) {
            res.add("INSERT INTO " + SerleenaDatabase.TABLE_EXPERIENCES +
                    "(`experience_id`," +
                    " `experience_name`)" +
                    " VALUES " +
                    "("+expCounter+"," +
                    "  \"" + exp.name + "\")");
            // TODO: Manca la region? SHANDROID-291
            for (UserPointEntity up : exp.userPoints) {
                res.add("INSERT INTO " + SerleenaDatabase.TABLE_USER_POINTS +
                        "(`userpoint_x`, " +
                        " `userpoint_y`, " +
                        " `userpoint_experience`)" +
                        " VALUES " +
                        "(" + up.point.latitude() +"," +
                        " " + up.point.longitude() + "," +
                        " " + expCounter +
                        ")");
            }
            for (TrackEntity track : exp.tracks) {
                res.add("INSERT INTO " + SerleenaDatabase.TABLE_TRACKS +"" +
                        "(`track_id`," +
                        "`track_name`, " +
                        "`track_experience`)" +
                        "VALUES" +
                        "("+ trackCounter+", "+
                        "\""+track.name+"\"," +
                        expCounter  +
                        ") ");
                for (CheckpointEntity cp : track.checkpoints) {
                    res.add("INSERT INTO " + SerleenaDatabase.TABLE_CHECKPOINTS + "" +
                            "(`checkpoint_num`," +
                            "`checkpoint_latitude`," +
                            "`checkpoint_longitude`," +
                            "`checkpoint_track`)" +
                            "VALUES" +
                            "(" +
                            cp.id + ", " +
                            cp.point.latitude() + ", " +
                            cp.point.longitude() + ", " +
                            trackCounter +
                            ")");
                }

                if(track.telemetries.size() != 0) {
                    if (track.telemetries.size() != 1) {
                        throw new IllegalArgumentException();
                        // TODO: Ce n'e' una migliore?
                    }

                    Iterator<TelemetryEntity> it = track.telemetries.iterator();
                    TelemetryEntity best = it.next();

                    res.add("INSERT INTO " + SerleenaDatabase.TABLE_TELEMETRIES +
                            "(telem_id," +
                            "telem_track) " +
                            "VALUES (" +
                            telemCounter + "," +
                            trackCounter + ")");

                    int eventCounter = 0;
                    for (Long ee : best.events) {
                        res.add("INSERT INTO " + SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP +
                                "(eventc_timestamp, " +
                                "eventc_value, " +
                                "eventc_telem) " +
                                "VALUES (" +
                                ee + "," +
                                eventCounter + "," +
                                telemCounter + ")");
                        eventCounter++;
                    }
                    telemCounter++;
                }
                trackCounter++;
            }
            expCounter++;
        }
        return res;
    }

    private SerleenaSQLiteInboundDump buildContacts(Collection<EmergencyDataEntity> c) {
        SerleenaSQLiteInboundDump res =  new SerleenaSQLiteInboundDump();
        for (EmergencyDataEntity cont : c) {
            res.add("INSERT INTO " + SerleenaDatabase.TABLE_CONTACTS + "" +
                    "(`contact_name`," +
                    "`contact_value`," +
                    "`contact_nw_corner_latitude`," +
                    "`contact_nw_corner_longitude`," +
                    "`contact_se_corner_latitude`," +
                    "`contact_se_corner_longitude`)" +
                    "VALUES " +
                    "(" +
                    "\"" + cont.name + "\", " +
                    "\"" + cont.number + "\", " +
                    cont.rect.getNorthWestPoint().latitude() + ", " +
                    cont.rect.getNorthWestPoint().longitude() + ", " +
                    cont.rect.getSouthEastPoint().latitude() + ", " +
                    cont.rect.getSouthEastPoint().longitude() + ")");
        }
        return res;
    }

    private SerleenaSQLiteInboundDump buildWeatherData(Collection<WeatherDataEntity> w) {
        // TODO: Controllare la data
        SerleenaSQLiteInboundDump res =  new SerleenaSQLiteInboundDump();
        for (WeatherDataEntity weat : w) {

            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(weat.date);

            if(c.get(Calendar.HOUR_OF_DAY) != 0 || c.get(Calendar.MINUTE) != 0 || c.get(Calendar.SECOND) != 0) {
                throw new IllegalArgumentException("Date "+c.toString()+" is not 00:00:00");
            }

            res.add("INSERT INTO " + SerleenaDatabase.TABLE_WEATHER_FORECASTS +
            "(weather_date," +
            "weather_condition_morning," +
            "weather_temperature_morning," +
            "weather_condition_afternoon," +
            "weather_temperature_afternoon," +
            "weather_condition_night," +
            "weather_temperature_night," +
		    "weather_nw_corner_latitude," +
		    "weather_nw_corner_longitude," +
		    "weather_se_corner_latitude," +
		    "weather_se_corner_longitude)" +
		    " VALUES " +
		    "(" + (weat.date/1000) + "," +
		    weat.morning.forecast.ordinal() + "," +
            round(weat.morning.temperature) + "," + // In DB la temperature e' int
            weat.afternoon.forecast.ordinal() + "," +
            round(weat.afternoon.temperature) + "," +
            weat.night.forecast.ordinal() + "," +
            round(weat.night.temperature) + "," + // In DB la temperature e' int
            weat.boundingRect.getNorthWestPoint().latitude() + "," +
            weat.boundingRect.getNorthWestPoint().longitude() + "," +
            weat.boundingRect.getSouthEastPoint().latitude() + "," +
            weat.boundingRect.getSouthEastPoint().longitude() + ")");
        }
        return res;
    }
    @Override
    public SerleenaSQLiteInboundDump build() {
        SerleenaSQLiteInboundDump dump = new SerleenaSQLiteInboundDump();
        dump.add("BEGIN TRANSACTION");
        dump.addAll(flush());
        dump.addAll(buildExperiences(root.experiences));
        dump.addAll(buildContacts(root.emergencyData));
        dump.addAll(buildWeatherData(root.weatherData));
        dump.add("COMMIT");
        return dump;
    }
}