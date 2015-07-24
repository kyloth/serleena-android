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
 * Name: TrackCrossing.java
 * Package: com.kyloth.serleena.sensors
 * Author: Filippo Sestini
 *
 * History:
 * Version  Programmer        Changes
 * 1.0.0    Filippo Sestini   Creazione file e scrittura
 *                                         codice e documentazione Javadoc
 */

package com.kyloth.serleena.sensors;

import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.NoTrackCrossingException;
import com.kyloth.serleena.model.ITrack;

import java.util.ArrayList;

/**
 * Implementa ITrackCrossing
 *
 * @author Filippo Sestini <sestini.filippo@gmail.com>
 * @version 1.0.0
 */
public final class TrackCrossing implements ITrackCrossing,
        ILocationReachedObserver {

    private final ILocationReachedManager locReachMan;
    private ITrack track;
    private int nextCheckpointIndex;
    private ArrayList<ITrackCrossingObserver> observers;
    private long trackStartTimestamp;
    private int lastPartial;

    /**
     * Crea un oggetto TrackCrossing.
     *
     * Se il parametro ILocationReachedManager è null, viene sollevata
     * un'eccezione IllegalArgumentException.
     */
    public TrackCrossing(ILocationReachedManager locReachMan) {
        if (locReachMan == null)
            throw new IllegalArgumentException("Illegal location reached " +
                    "manager");

        this.locReachMan = locReachMan;
        observers = new ArrayList<ITrackCrossingObserver>();
    }

    /**
     * Implementa ITrackCrossing.startTrack().
     */
    @Override
    public void startTrack(ITrack track) {
        if (track == null)
            throw new IllegalArgumentException("Illegal null track");

        trackStartTimestamp = System.currentTimeMillis() / 1000L;
        lastPartial = 0;
        this.track = track;
        nextCheckpointIndex = -1;
        try {
            myAdvanceCheckpoint();
        } catch (NoTrackCrossingException e) {}
    }

    /**
     * Implementa ITrackCrossing.getLastCrossed().
     */
    @Override
    public int getLastCrossed() throws NoSuchCheckpointException {
        if (nextCheckpointIndex == 0)
            throw new NoSuchCheckpointException();
        return nextCheckpointIndex - 1;
    }

    /**
     * Implementa ITrackCrossing.getNextCheckpoint().
     */
    @Override
    public int getNextCheckpoint()
            throws NoTrackCrossingException {
        if (track == null ||
                nextCheckpointIndex == track.getCheckpoints().size())
            throw new NoTrackCrossingException();
        return nextCheckpointIndex;
    }

    /**
     * Implementa ITrackCrossing.attachObserver().
     */
    @Override
    public synchronized void attachObserver(ITrackCrossingObserver observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    /**
     * Implementa ITrackCrossing.detachObserver().
     */
    @Override
    public synchronized void detachObserver(ITrackCrossingObserver observer) {
        if (observers.contains(observer))
            observers.remove(observer);
    }

    /**
     * Implementa ITrackCrossing.advanceCheckpoint().
     */
    @Override
    public synchronized void advanceCheckpoint()
            throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();
        locReachMan.detachObserver(
                this,
                track.getCheckpoints().get(nextCheckpointIndex));
        myAdvanceCheckpoint();
    }

    private void myAdvanceCheckpoint() throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        long now = System.currentTimeMillis() / 1000L;
        lastPartial = (int) (now - trackStartTimestamp);

        if ((nextCheckpointIndex + 1) < track.getCheckpoints().size()) {
            nextCheckpointIndex++;
            Checkpoint nextCheckpoint =
                    track.getCheckpoints().get(nextCheckpointIndex);
            locReachMan.attachObserver(this, nextCheckpoint);
        } else {
            nextCheckpointIndex = track.getCheckpoints().size();
        }

        notifyObservers();
    }

    /**
     * Implementa ITrackCrossing.lastPartialTime().
     */
    @Override
    public int lastPartialTime() throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        return lastPartial;
    }

    /**
     * Implementa ITrackCrossing.getTrack().
     */
    @Override
    public ITrack getTrack() throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();
        return track;
    }

    /**
     * Implementa ITrackCrossing.isTrackCrossing().
     */
    @Override
    public boolean isTrackCrossing() {
        return !(track == null ||
                nextCheckpointIndex == track.getCheckpoints().size());
    }

    /**
     * Implementa ITrackCrossing.abort().
     */
    @Override
    public void abort() {
        track = null;
        nextCheckpointIndex = 0;
        locReachMan.detachObserver(this);
    }

    /**
     * Implementa ITrackCrossing.onLocationReached().
     */
    @Override
    public synchronized void onLocationReached() {
        try {
            myAdvanceCheckpoint();
        } catch (NoTrackCrossingException e) { }
    }

    /**
     * Implementa ITrackCrossing.notifyObservers().
     *
     * @throws NoTrackCrossingException
     */
    @Override
    public void notifyObservers()
            throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        for (ITrackCrossingObserver o : observers)
            o.onCheckpointCrossed(nextCheckpointIndex - 1);
    }

}
