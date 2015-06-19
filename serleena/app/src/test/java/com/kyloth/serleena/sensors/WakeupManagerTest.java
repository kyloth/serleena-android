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
 * Name: WakeupScheduleTest.java
 * Package: com.kyloth.serleena.presenters;
 * Author: Tobia Tesan
 *
 * History:
 * Version  Programmer       Changes
 * 1.0.0    Tobia Tesan      Creazione iniziale file
 */

package com.kyloth.serleena.sensors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.kyloth.serleena.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlarmManager;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.UUID;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * Testa le funzionalita' di base di WakeupManager
 *
 * @author Tobia Tesan <tobia.tesan@gmail.com>
 * @version 1.0.0
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 19)
public class WakeupManagerTest {
	AlarmManager am;
	ShadowAlarmManager sam;
	WakeupManager wm;
	IWakeupObserver obs;
	IWakeupObserver otherObs;

	class StubbyWakeupObserver implements IWakeupObserver {
		int wakeUps;
		String uuid;

		StubbyWakeupObserver() {
			wakeUps = 0;
			uuid = UUID.randomUUID().toString();
		}
		public int countWakeups() {
			return wakeUps;
		}

		@Override
		public void onWakeup() {
			wakeUps++;
		}

		@Override
		public String getUUID() {
			return uuid;
		}
	}

	/**
	 * Verifica che dopo essere stato accoppiato l'observer
	 * venga svegliato dopo ogni Intent con ALARM_UUID settato
	 * al suo UUID.
	 */
	@Test
	public void testObserverIsAwaken() {
		IWakeupObserver obs = new StubbyWakeupObserver();
		String uuid = obs.getUUID();
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);

		wm.attachObserver(obs, 1, false);
		Intent foo = new Intent();
		foo.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, foo);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);

		Intent bar = new Intent();
		bar.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, bar);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 2);
	}

	/**
	 * Verifica che dopo essere stato accoppiato solo l'observer
	 * con l'UUID uguale a ALARM_UUID venga svegliato, non altri.
	 */
	@Test
	public void testOnlyTargetIsAwaken() {
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);
		wm.attachObserver(obs, 1, false);
		Intent foo = new Intent();
		foo.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, foo);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);
		assertTrue(((StubbyWakeupObserver) otherObs).countWakeups() == 0);
	}

	/**
	 * Verifica che un secondo Intent con ALARM_UUID uguale all'UUID
	 * di un oggetto one time risulti in una NoSuchElementException
	 */
	@Test(expected = NoSuchElementException.class)
	public void testOneTimeObserverIsDetached() {
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);

		wm.attachObserver(obs, 1, true);
		Intent foo = new Intent();
		foo.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, foo);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);

		Intent bar = new Intent();
		bar.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, bar);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);
	}
	/**
	 * Verifica che un Intent con ALARM_UUID uguale all'UUID
	 * di un oggetto detached risulti in una NoSuchElementException
	 */
	@Test(expected = NoSuchElementException.class)
	public void testDetachDoesDetach() {
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);

		wm.attachObserver(obs, 1, false);
		Intent foo = new Intent();
		foo.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, foo);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);

		wm.detachObserver(obs);

		Intent bar = new Intent();
		bar.putExtra("ALARM_UUID", obs.getUUID());
		wm.onReceive(RuntimeEnvironment.application, bar);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 1);
	}

	/**
	 * Verifica che attach() risulti in uno ScheduledAlarm
	 * schedulato con il delay giusto.
	 */
	@Test
	public void testAttachObserverSchedules() {
		wm = new WakeupManager(RuntimeEnvironment.application, am);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);
		wm.attachObserver(obs, 1, false);
		ShadowAlarmManager.ScheduledAlarm alarm = sam.peekNextScheduledAlarm();
		assertTrue(alarm.triggerAtTime == 1000);
		assertTrue(alarm.triggerAtTime != 900);
		alarm = sam.getNextScheduledAlarm();
		assertTrue(alarm != null);
	}

	/**
	 * Verifica che attach() one time risulti in un
	 * singolo ScheduledAlarm
	 */
	@Test
	public void testAttachSchedulesJustOnce() {
		wm = new WakeupManager(RuntimeEnvironment.application, am);
		assertTrue(((StubbyWakeupObserver) obs).countWakeups() == 0);
		wm.attachObserver(obs, 1, true);
		ShadowAlarmManager.ScheduledAlarm alarm = sam.getNextScheduledAlarm();
		assertTrue(alarm.triggerAtTime == 1000);
		alarm = sam.getNextScheduledAlarm();
		assertTrue(alarm == null);
	}

	@Before
	public void setUp() {
		am = (AlarmManager)RuntimeEnvironment.application.getSystemService(Context.ALARM_SERVICE);
		sam = shadowOf(am);
		wm = new WakeupManager(RuntimeEnvironment.application, am);
		obs = new StubbyWakeupObserver();
		otherObs = new StubbyWakeupObserver();
	}
}
