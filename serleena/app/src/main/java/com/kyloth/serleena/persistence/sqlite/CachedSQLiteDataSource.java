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
 * Name: CachedSQLiteDataSource.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer          Changes
 * 1.0      Filippo Sestini     Scrittura codice e javadoc
 */

package com.kyloth.serleena.persistence.sqlite;

import com.kyloth.serleena.common.DirectAccessList;
import com.kyloth.serleena.common.EmergencyContact;
import com.kyloth.serleena.common.GeoPoint;
import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.NoSuchWeatherForecastException;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.UserPoint;
import com.kyloth.serleena.persistence.IExperienceStorage;
import com.kyloth.serleena.persistence.IWeatherStorage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Realizza il pattern Decorator offrendo un meccanismo di caching per
 * oggetti di interfaccia ISerleenaSQLiteDataSource.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
final class CachedSQLiteDataSource implements ISerleenaSQLiteDataSource {

    private final ISerleenaSQLiteDataSource dataSource;
    private IQuadrant quadrant;
    private GeoPoint lastEmergencyLocation;
    private GeoPoint lastWeatherLocation;
    private Date lastWeatherDate;
    private DirectAccessList<EmergencyContact> contacts;
    private IWeatherStorage forecast;
    private Iterable<IExperienceStorage> experiences;
    private Map<IExperienceStorage, Iterable<UserPoint>> upCache;

    /**
     * Crea un nuovo oggetto CachedSQLiteDataSource che fa da decorator ad un
     * oggetto ISerleenaSQLiteDataSource.
     *
     * @param dataSource Oggetto su cui applicare il Decorator.
     */
    public CachedSQLiteDataSource(ISerleenaSQLiteDataSource dataSource) {
        this.dataSource = dataSource;
        upCache = new HashMap<IExperienceStorage, Iterable<UserPoint>>();
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getTracks().
     *
     * Inoltra la chiamata all'oggetto ISerleenaSQLiteDataSource sottostante.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Percorsi.
     * @return Dati restituiti dalla chiamata all'oggetto
     * ISerleenaSQLiteDataSource sottostante.
     */
    @Override
    public Iterable<SQLiteDAOTrack> getTracks(SQLiteDAOExperience experience) {
        return dataSource.getTracks(experience);
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getTelemetries().
     *
     * Inoltra la chiamata all'oggetto ISerleenaSQLiteDataSource sottostante.
     *
     * @param track Percorso di cui si vogliono ottenere i Tracciamenti.
     * @return Dati restituiti dalla chiamata all'oggetto
     * ISerleenaSQLiteDataSource sottostante.
     */
    @Override
    public Iterable<SQLiteDAOTelemetry> getTelemetries(SQLiteDAOTrack track) {
        return dataSource.getTelemetries(track);
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getUserPoints().
     *
     * I dati forniti dall'oggetto ISerleenaSQLiteDataSource sottostante vengono
     * cachati e restituiti nelle successive chiamate al metodo.
     *
     * @param experience Esperienza di cui si vogliono ottenere i Punti Utente.
     * @return Dati cachati relativi all'esperienza specificata.
     */
    @Override
    public Iterable<UserPoint> getUserPoints(SQLiteDAOExperience
                                                        experience) {
        if (!upCache.containsKey(experience))
            upCache.put(experience, dataSource.getUserPoints(experience));
        return upCache.get(experience);
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.addUserPoint().
     *
     * Inoltra la chiamata all'oggetto ISerleenaSQLiteDataSource sottostante, e
     * annulla i dati cachati relativi ai punti utente dell'esperienza
     * specificata.
     *
     * @param experience Esperienza a cui aggiungere il punto utente.
     * @param point Punto utente da aggiungere.
     */
    @Override
    public void addUserPoint(SQLiteDAOExperience experience, UserPoint point) {
        dataSource.addUserPoint(experience, point);
        upCache.remove(experience);
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.createTelemetry().
     *
     * Inoltra la chiamata all'oggetto ISerleenaSQLiteDataSource sottostante.
     *
     * @param events Eventi di tracciamento da cui costruire il Tracciamento.
     * @param track Percorso a cui associare il Tracciamento.
     */
    @Override
    public void createTelemetry(Iterable<TelemetryEvent> events,
                                SQLiteDAOTrack track) {
        dataSource.createTelemetry(events, track);
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getExperiences().
     *
     * Inoltra la chiama all'oggetto ISerleenaSQLiteDataSource sottostante,
     * memorizzando i dati ottenuti e restituendoli alle successive chiamate
     * del metodo.
     *
     * @return Dati cachati dall'oggetto ISerleenaSQLiteDataSource sottostante.
     */
    @Override
    public Iterable<IExperienceStorage> getExperiences() {
        if (experiences == null)
            experiences = dataSource.getExperiences();
        return experiences;
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getWeatherInfo().
     *
     * Inoltra la chiamata all'oggetto ISerleenaSQLiteDataSource sottostante, e
     * memorizza i dati ottenuti, restituendoli per chiamate successive del
     * metodo con stessi parametri di posizione geografica e data.
     *
     * Nel caso di chiamata con parametri diversi, la chiamata viene
     * inoltrata all'oggetto ISerleenaSQLiteDataSource sottostante.
     *
     * @param location Posizione geografica di cui si vogliono ottenere le
     *                 previsioni.
     * @param date Data di cui si vogliono ottenere le previsioni.
     * @return Dati cachati o ottenuti dall'oggetto ISerleenaSQLiteDataSource
     * sottostante.
     * @throws NoSuchWeatherForecastException
     */
    @Override
    public IWeatherStorage getWeatherInfo(GeoPoint location, Date date)
            throws NoSuchWeatherForecastException {
        if (lastWeatherDate == null || lastWeatherLocation == null ||
                forecast == null || !lastWeatherLocation.equals(location) ||
                !lastWeatherDate.equals(date)) {
            forecast = dataSource.getWeatherInfo(location, date);
            lastWeatherLocation = location;
            lastWeatherDate = date;
        }
        return forecast;
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getQuadrant().
     *
     * Inoltra la chiama all'oggetto ISerleenaSQLiteDataSource sottostante,
     * memorizzando i dati ottenuti e restituendoli alle successive chiamate
     * del metodo nel caso la posizione geografica specificata per parametro
     * è relativa al quadrante presente in cache.
     *
     * @return Dati cachati dall'oggetto ISerleenaSQLiteDataSource sottostante.
     */
    @Override
    public IQuadrant getQuadrant(GeoPoint location) {
        if (quadrant == null || !quadrant.contains(location))
            quadrant = dataSource.getQuadrant(location);
        return quadrant;
    }

    /**
     * Implementa ISerleenaSQLiteDataSource.getContacts().
     *
     * Inoltra la chiama all'oggetto ISerleenaSQLiteDataSource sottostante,
     * memorizzando i dati ottenuti e restituendoli alle successive chiamate
     * del metodo per stesso parametro di posizione geografica.
     *
     * @return Dati cachati dall'oggetto ISerleenaSQLiteDataSource sottostante.
     */
    @Override
    public DirectAccessList<EmergencyContact> getContacts(GeoPoint location) {
        if (contacts == null || lastEmergencyLocation == null ||
                !location.equals(lastEmergencyLocation)) {
            contacts = dataSource.getContacts(location);
            lastEmergencyLocation = location;
        }
        return contacts;
    }
}

