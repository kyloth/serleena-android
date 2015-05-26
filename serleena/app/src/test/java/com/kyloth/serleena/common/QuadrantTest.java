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
 * Name: UserPoint.java
 * Package: com.kyloth.serleena.common
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version  Programmer   Changes
 * 1.0.0    Tobia Tesan  Creazione file e scrittura di codice
 *                                          e documentazione in Javadoc.
 */

package com.kyloth.serleena.common;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class QuadrantTest {
	/**
	 * Verifica che contains dia risultato positivo per un punto
	 * effettivamente contenuto nel quadrante.
	 */
	@Test
	public void testContains() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint p = new GeoPoint(1, 1);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(ne, sw, null);
		assertTrue(q.contains(p));
	}

	/**
	 * Verifica che contains dia risultato positivo per l'estremita'
	 * NE del quadrante.
	 */
	@Test
	public void testContainsNEEdge() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(ne, sw, null);
		assertTrue(q.contains(ne));
	}

	/**
	 * Verifica che contains dia risultato positivo per l'estremita'
	 * SW del quadrante.
	 */
	@Test
	public void testContainsSWEdge() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(ne, sw, null);
		assertTrue(q.contains(sw));
	}

	/**
	 * Verifica che contains dia risultato negativo per un
	 * punto non contenuto nel quadrante.
	 */
	@Test
	public void testDoesNotContain() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint p = new GeoPoint(3, 3);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(ne, sw, null);
		assertFalse(q.contains(p));
	}

	/**
	 * Verifica che contains sollevi eccezione IllegalArgumentException
	 * se gli viene passato null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNoNullContainsArg() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(ne, sw, null);
		assertTrue(q.contains(null));
	}

	/**
	 * Verifica che il costruttore di Quadrant sollevi
	 * IllegalArgumentException se riceve un'estremita' null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNoNullFirstArg() {
		GeoPoint p = new GeoPoint(1, 1);
		GeoPoint sw = new GeoPoint(2, 2);
		Quadrant q = new Quadrant(null, sw, null);
		assertTrue(q.contains(p));
	}

	/**
	 * Verifica che il costruttore di Quadrant sollevi
	 * IllegalArgumentException se riceve un'estremita' null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNoNullSecondArg() {
		GeoPoint ne = new GeoPoint(0, 0);
		GeoPoint p = new GeoPoint(1, 1);
		Quadrant q = new Quadrant(ne, null, null);
		assertTrue(q.contains(p));
	}

}
