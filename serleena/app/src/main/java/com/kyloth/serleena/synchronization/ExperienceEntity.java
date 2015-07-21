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
 * Name: ExperienceEntity.java
 * Package: com.hitchikers.serleena.synchronization
 * Author: Tobia Tesan <tobia.tesan@gmail.com>
 *
 * History:
 * Version    Programmer   Changes
 * 1.0        Tobia Tesan  Creazione del file
 */

package com.kyloth.serleena.synchronization;

import com.kyloth.serleena.common.IQuadrant;
import com.kyloth.serleena.common.UserPoint;

import java.util.Collection;

/**
 * Struct rappresentante un'esperienza
 */
public class ExperienceEntity implements IDataEntity {
	Collection<TrackEntity> tracks;
	String name;
	RasterDataEntity rasterData;
	IQuadrant region;
	Collection<UserPoint> userPoints;
	// TODO: Il nostro UserPoint non ha un ID, il loro si'. Chi vince?
	// TODO: Loro ci passano PointsOfInterest. Continuiamo a ignorarli? Cfr. SHANDROID-288
}
