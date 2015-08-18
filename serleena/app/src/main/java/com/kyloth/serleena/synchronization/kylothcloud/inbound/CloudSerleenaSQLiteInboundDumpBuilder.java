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
import com.kyloth.serleena.synchronization.kylothcloud.RasterDataEntity;
import com.kyloth.serleena.synchronization.kylothcloud.TelemetryEntity;
import com.kyloth.serleena.synchronization.kylothcloud.TrackEntity;
import com.kyloth.serleena.synchronization.kylothcloud.UserPointEntity;
import com.kyloth.serleena.synchronization.kylothcloud.WeatherDataEntity;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

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
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_RASTERS);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_TELEMETRIES);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_WEATHER_FORECASTS);
        res.add("DELETE FROM " + SerleenaDatabase.TABLE_CONTACTS);
        return res;
    }

    private SerleenaSQLiteInboundDump buildExperiences(Collection<ExperienceEntity> e) {
        SerleenaSQLiteInboundDump res =  new SerleenaSQLiteInboundDump();
        //HACK per SHANDROID-372
        int telemCounter = -1;
        for (ExperienceEntity exp : e) {
            res.add("INSERT INTO " + SerleenaDatabase.TABLE_EXPERIENCES +
                    "(`experience_uuid`," +
                    " `experience_name`)" +
                    " VALUES " +
                    "(\""+exp.uuid.toString()+"\"," +
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
                        "\"" + exp.uuid.toString() +"\"" +
                        ")");
            }

            for (RasterDataEntity raster : exp.rasterData) {
                res.add("INSERT INTO " + SerleenaDatabase.TABLE_RASTERS +"" +
                        "(`raster_experience`," +
                        "`raster_nw_corner_latitude`," +
                        "`raster_nw_corner_longitude`," +
                        "`raster_se_corner_latitude`," +
                        "`raster_se_corner_longitude`,"+
                        "`raster_base64`)" +
                        "VALUES" +
                        "("+ "\"" + exp.uuid.toString() +"\", " +
                        raster.boundingRect.getNorthWestPoint().latitude() +", " +
                         raster.boundingRect.getNorthWestPoint().longitude() +", " +
                         raster.boundingRect.getSouthEastPoint().latitude() +", " +
                         raster.boundingRect.getSouthEastPoint().longitude() +", " +
                         "\"" + raster.base64Raster + "\"" +
                        ") ");
            }
            for (TrackEntity track : exp.tracks) {
                res.add("INSERT INTO " + SerleenaDatabase.TABLE_TRACKS +"" +
                        "(`track_uuid`," +
                        "`track_name`, " +
                        "`track_experience`)" +
                        "VALUES" +
                        "("+ "\"" + track.uuid.toString() + "\", " +
                        "\""+ track.name + "\"," +
                        "\"" + exp.uuid.toString() +"\"" +
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
                            "\"" + track.uuid.toString() + "\"" +
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
                            "\"" + track.uuid.toString() + "\"" +
                            ")");

                    int eventCounter = 1;
                    for (Long ee : best.events) {
                        res.add("INSERT INTO " + SerleenaDatabase.TABLE_TELEM_EVENTS_CHECKP +
                                "(eventc_timestamp, " +
                                "eventc_value, " +
                                "eventc_telem) " +
                                "VALUES (" +
                                ee / 1000 + "," +
                                eventCounter + "," +
                                telemCounter + ")");
                        eventCounter++;
                    }
                }
            }
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
            c.setTimeZone(TimeZone.getTimeZone("GMT"));
            c.setTimeInMillis(weat.date);

            if(c.get(Calendar.HOUR_OF_DAY) != 0 || c.get(Calendar.MINUTE) != 0 || c.get(Calendar.SECOND) != 0) {
                // Ignora date non 00:00:00
            } else {
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
                        "(" + (weat.date / 1000) + "," +
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
