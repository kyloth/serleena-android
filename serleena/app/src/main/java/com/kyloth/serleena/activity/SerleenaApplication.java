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
 * Name: SerleenaApplication.java
 * Package: com.kyloth.serleena.presentation
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                            codice e documentazione Javadoc
 */

package com.kyloth.serleena.activity;

import android.app.Application;

import com.kyloth.serleena.model.ISerleenaDataSource;
import com.kyloth.serleena.model.SerleenaDataSource;
import com.kyloth.serleena.persistence.IPersistenceDataSink;
import com.kyloth.serleena.persistence.IPersistenceDataSource;
import com.kyloth.serleena.persistence.sqlite.CachedSQLiteDataSource;
import com.kyloth.serleena.persistence.sqlite.SerleenaDatabase;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSink;
import com.kyloth.serleena.persistence.sqlite.SerleenaSQLiteDataSource;
import com.kyloth.serleena.presenters.ISerleenaApplication;
import com.kyloth.serleena.sensors.ISensorManager;
import com.kyloth.serleena.sensors.SerleenaSensorManager;
import com.kyloth.serleena.synchronization.KylothCloudSynchronizer;
import com.kyloth.serleena.synchronization.net.INetProxy;
import com.kyloth.serleena.synchronization.net.SerleenaJSONNetProxy;
import com.kyloth.serleena.synchronization.kylothcloud.LocalEnvKylothIdSource;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementa ISerleenaApplication.
 *
 * Costituisce un punto centralizzato e controllato di creazione e acquisizione
 * delle risorse dell'applicazione, garantendo che queste vengano create una
 * sola volta durante il ciclo di vita dell'app.
 *
 * @use SerleenaActivity ottiene un riferimento all'applicazione di tipo SerleenaApplication dietro la sua interfaccia ISerleenaApplication.
 * @field dataSource : ISerleenaDataSource Datasource dell'applicazione.
 * @field sensorManager : ISensorManager Gestore della sensoristica dell'applicazione
 * @field dataSink : IPersistenceDataSink Datasink dell'applicazione
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
*/
public class SerleenaApplication extends Application implements ISerleenaApplication {

    private ISerleenaDataSource dataSource;
    private ISensorManager sensorManager;
    private IPersistenceDataSink dataSink;

    /**
     * Ridefinisce Application.onCreate().
     *
     * Inizializza gli elementi principali dell'applicazione, quali gestore dei
     * sensori, della sincronizzazione, datasource e datasink.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = SerleenaSensorManager.getInstance(this);

        SerleenaDatabase serleenaDatabase = new SerleenaDatabase(this, 1);
        IPersistenceDataSource persistenceDataSource =
                new CachedSQLiteDataSource(
                        new SerleenaSQLiteDataSource(new SerleenaDatabase(this, 1)));
        dataSource = new SerleenaDataSource(persistenceDataSource);
        dataSink = new SerleenaSQLiteDataSink(this, serleenaDatabase);

        try {
            INetProxy netProxy = new SerleenaJSONNetProxy(
                    new LocalEnvKylothIdSource(),
                    new URL("http://api.kyloth.info/"));
            KylothCloudSynchronizer.getInstance(
                    netProxy, dataSink, persistenceDataSource);
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Implementa ISerleenaApplication.getDataSource().
     */
    @Override
    public ISerleenaDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Implementa ISerleenaApplication.getSensorManager().
     */
    @Override
    public ISensorManager getSensorManager() {
        return sensorManager;
    }

    /**
     * Implementa ISerleenaApplication.getDataSink().
     */
    @Override
    public IPersistenceDataSink getDataSink() {
        return dataSink;
    }

}
