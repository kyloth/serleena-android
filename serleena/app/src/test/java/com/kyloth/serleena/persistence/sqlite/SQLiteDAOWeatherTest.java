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
 * Name: SQLiteDAOWeatherTest.java
 * Package: com.kyloth.serleena.persistence.sqlite
 * Author: Gabriele Pozzan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0      Gabriele Pozzan  Creazione file, codice e javadoc
 */

package com.kyloth.serleena.persistence.sqlite;


import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

import java.util.Date;

import com.kyloth.serleena.persistence.WeatherForecastEnum;
/**
 * Contiene test per la classe SQLiteDAOWeather.
 *
 * @author Gabriele Pozzan <gabriele.pozzan@studenti.unipd.it>
 * @version 1.0
 */

public class SQLiteDAOWeatherTest {

    /**
     * Verifica che il costruttore inizializzi correttamente i campi
     * dati della classe e che i metodi getter li restituiscano
     * di conseguenza.
     */

    @Test
    public void testConstructorAndGetters() {
        SQLiteDAOWeather daoWeath = new SQLiteDAOWeather(WeatherForecastEnum.Sunny,
                WeatherForecastEnum.Cloudy,
                WeatherForecastEnum.Rainy,
                10, 20, 30, new Date(140));
        assertTrue(daoWeath.getMorningForecast() == WeatherForecastEnum.Sunny);
        assertTrue(daoWeath.getAfternoonForecast() == WeatherForecastEnum.Cloudy);
        assertTrue(daoWeath.getNightForecast() == WeatherForecastEnum.Rainy);
        assertTrue(daoWeath.getMorningTemperature() == 10);
        assertTrue(daoWeath.getAfternoonTemperature() == 20);
        assertTrue(daoWeath.getNightTemperature() == 30);
        assertTrue(daoWeath.date().equals(new Date(140)));
    }
}
