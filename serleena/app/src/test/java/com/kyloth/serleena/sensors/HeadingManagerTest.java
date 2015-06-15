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
 * Name: HeadingManagerTest.java
 * Package: com.kyloth.serleena.sensors;
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Filippo Sestini  Creazione file scrittura
 *                           codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Contiene i test di unità per la classe HeadingManager.
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
public class HeadingManagerTest {

    private HeadingManager getManager()
            throws SensorNotAvailableException {
        SensorManager sm = mock(SensorManager.class);
        when(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)).thenReturn(mock
                (Sensor.class));
        when(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(mock
                (Sensor.class));
        return new HeadingManager(sm);
    }

    /**
     * Verifica che la costruzione di un oggetto HeadingManager sollevi
     * un'eccezione quando non è disponibile l'accelerometro del dispositivo.
     */
    @Test(expected = SensorNotAvailableException.class)
    public void ctorShouldThrowWhenSensorNotAvailable1()
            throws SensorNotAvailableException {
        SensorManager sm = mock(SensorManager.class);
        when(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(null);
        new HeadingManager(sm);
    }

    /**
     * Verifica che la costruzione di un oggetto HeadingManager sollevi
     * un'eccezione quando non è disponibile il sensore di campo magnetico del
     * dispositivo.
     */
    @Test(expected = SensorNotAvailableException.class)
    public void ctorShouldThrowWhenSensorNotAvailable2()
            throws SensorNotAvailableException {
        SensorManager sm = mock(SensorManager.class);
        when(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)).thenReturn(null);
        new HeadingManager(sm);
    }

    /**
     * Verifica che gli observer vengano notificati correttamente in base al
     * loro stato di registrazione o non registrazione.
     */
    @Test
    public void testThatObserversGetNotifiedCorrectly()
            throws SensorNotAvailableException {
        SensorManager sm = mock(SensorManager.class);
        when(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)).thenReturn(mock
                (Sensor.class));
        when(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(mock
                (Sensor.class));

        HeadingManager hm = new HeadingManager(sm);
        IHeadingObserver o1 = mock(IHeadingObserver.class);
        IHeadingObserver o2 = mock(IHeadingObserver.class);
        IHeadingObserver o3 = mock(IHeadingObserver.class);

        hm.attachObserver(o1);
        hm.attachObserver(o2);
        hm.notifyObservers();
        verify(o1).onHeadingUpdate(any(Integer.class));
        verify(o2).onHeadingUpdate(any(Integer.class));

        hm.detachObserver(o2);
        hm.attachObserver(o3);
        hm.notifyObservers();
        verify(o1, times(2)).onHeadingUpdate(any(Integer.class));
        verify(o3).onHeadingUpdate(any(Integer.class));
        verifyNoMoreInteractions(o2);
    }

    /**
     * Verifica che l'oggetto HeadingManager calcoli il valore di orientamento
     * corretto attravero il confronto con dati noti.
     */
    @Test
    public void testCorrectHeadingCalculationAndNotification()
            throws SensorNotAvailableException, ExecutionException,
            InterruptedException {
        final float[] accelerometerValues = new float[] { 11, 22, 33 };
        final float[] magneticFieldValues = new float[] { 32, 21, 10 };
        final HeadingManager hm = getManager();

        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        float expected = (float) Math.toDegrees(values[0]);

        IHeadingObserver o = mock(IHeadingObserver.class);
        hm.attachObserver(o);

        hm.onRawDataReceived(accelerometerValues, magneticFieldValues);
        verify(o).onHeadingUpdate(expected);
    }

    /**
     * Verifica la corretta registrazione e deregistrazione ai sensori del
     * dispositivo, in base al numero di observer registrati all'oggetto
     * HeadingManager.
     */
    @Test
    public void testSensorManagerRegistrationUnregistration()
            throws SensorNotAvailableException {
        SensorManager sm = mock(SensorManager.class);
        Sensor accelerometer = mock(Sensor.class);
        Sensor magnetometer = mock(Sensor.class);
        when(sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
                .thenReturn(magnetometer);
        when(sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                .thenReturn(accelerometer);
        HeadingManager hm = new HeadingManager(sm);

        IHeadingObserver o1 = mock(IHeadingObserver.class);
        IHeadingObserver o2 = mock(IHeadingObserver.class);
        IHeadingObserver o3 = mock(IHeadingObserver.class);

        hm.attachObserver(o1);
        verify(sm).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);

        hm.attachObserver(o2);
        verify(sm, times(1)).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm, times(1)).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);

        hm.detachObserver(o2);
        verify(sm, times(1)).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm, times(1)).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);

        hm.detachObserver(o1);
        verify(sm).unregisterListener(hm);
        verify(sm, times(1)).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm, times(1)).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);

        hm.attachObserver(o1);
        verify(sm, times(2)).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm, times(2)).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);

        hm.detachObserver(o1);
        verify(sm, times(2)).unregisterListener(hm);
        verify(sm, times(2)).registerListener(hm, accelerometer, SensorManager
                .SENSOR_DELAY_UI);
        verify(sm, times(2)).registerListener(hm, magnetometer, SensorManager
                .SENSOR_DELAY_UI);
    }

    /**
     * Verifica che il metodo onRawDataReceived() sollevi un eccezione in caso
     * di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatPassingNullToOnRawDataReceiverThrows1()
            throws SensorNotAvailableException {
        float[] values = new float[] { 1, 2, 3 };
        HeadingManager hm = getManager();
        hm.onRawDataReceived(null, values);
    }

    /**
     * Verifica che il metodo onRawDataReceived() sollevi un eccezione in caso
     * di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatPassingNullToOnRawDataReceiverThrows2()
            throws SensorNotAvailableException {
        float[] values = new float[] { 1, 2, 3 };
        HeadingManager hm = getManager();
        hm.onRawDataReceived(values, null);
    }

    /**
     * Verifica che il metodo onRawDataReceived() sollevi un eccezione in caso
     * di parametri null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testThatPassingNullToOnRawDataReceiverThrows3()
            throws SensorNotAvailableException {
        HeadingManager hm = getManager();
        hm.onRawDataReceived(null, null);
    }

}
