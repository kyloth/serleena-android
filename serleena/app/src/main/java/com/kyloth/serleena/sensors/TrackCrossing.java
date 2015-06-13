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


package com.kyloth.serleena.sensors;

import com.android.internal.util.Predicate;
import com.kyloth.serleena.common.Checkpoint;
import com.kyloth.serleena.common.CheckpointReachedTelemetryEvent;
import com.kyloth.serleena.common.ImmutableList;
import com.kyloth.serleena.common.TelemetryEvent;
import com.kyloth.serleena.common.TelemetryEventType;
import com.kyloth.serleena.common.UnregisteredObserverException;
import com.kyloth.serleena.model.ITelemetry;
import com.kyloth.serleena.model.ITrack;
import com.kyloth.serleena.model.NoSuchTelemetryEventException;
import com.kyloth.serleena.model.NoSuchTelemetryException;

import java.util.ArrayList;

/**
 * Created by fsestini on 6/6/15.
 */
public final class TrackCrossing implements ITrackCrossing,
        ILocationReachedObserver {

    private final ILocationReachedManager locReachMan;
    private ITrack track;
    private int nextCheckpointIndex;
    private ArrayList<ITrackCrossingObserver> observers;
    private long trackStartTimestamp;
    private int lastPartial;

    public TrackCrossing(ILocationReachedManager locReachMan) {
        if (locReachMan == null)
            throw new IllegalArgumentException("Illegal location reached " +
                    "manager");

        this.locReachMan = locReachMan;
        observers = new ArrayList<ITrackCrossingObserver>();
    }

    @Override
    public void startTrack(ITrack track) {
        if (track == null)
            throw new IllegalArgumentException("Illegal null track");

        trackStartTimestamp = System.currentTimeMillis() / 1000L;
        lastPartial = 0;
        this.track = track;
        nextCheckpointIndex = -1;
        try {
            advanceCheckpoint();
        } catch (NoTrackCrossingException e) {}
    }

    @Override
    public int getLastCrossed() throws NoSuchCheckpointException {
        if (nextCheckpointIndex == 0)
            throw new NoSuchCheckpointException();
        return nextCheckpointIndex - 1;
    }

    @Override
    public int getNextCheckpoint()
            throws TrackEndedException, NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();
        if (nextCheckpointIndex == track.getCheckpoints().size())
            throw new TrackEndedException();
        return nextCheckpointIndex;
    }

    @Override
    public synchronized void attachObserver(ITrackCrossingObserver observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    @Override
    public synchronized void detachObserver(ITrackCrossingObserver observer) {
        if (observers.contains(observer))
            observers.remove(observer);
    }

    @Override
    public synchronized void advanceCheckpoint()
            throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        try {
            locReachMan.detachObserver(this);
        } catch (UnregisteredObserverException ex) { }

        notifyObservers();
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
    }

    @Override
    public int lastPartialTime() throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        return lastPartial;
    }

    @Override
    public ITrack getTrack() throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();
        return track;
    }

    @Override
    public synchronized void onLocationReached() {
        try {
            advanceCheckpoint();
        } catch (NoTrackCrossingException e) { }
    }

    @Override
    public void notifyObservers()
            throws NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();

        for (ITrackCrossingObserver o : observers)
            o.onCheckpointCrossed(nextCheckpointIndex - 1);
    }

    @Override
    public int comparePartialAgainstBest()
            throws NoSuchTelemetryEventException, NoSuchCheckpointException,
            NoSuchTelemetryException, NoTrackCrossingException {
        if (track == null)
            throw new NoTrackCrossingException();
        final int lastCrossed = this.getLastCrossed();
        ITelemetry best = track.getBestTelemetry();

        Predicate<TelemetryEvent> p = new Predicate<TelemetryEvent>() {
            @Override
            public boolean apply(TelemetryEvent telemetryEvent) {
                return telemetryEvent.getType() ==
                        TelemetryEventType.CheckpointReached &&
                        ((CheckpointReachedTelemetryEvent)telemetryEvent)
                        .checkpointNumber() == lastCrossed;
            }
        };

        Iterable<TelemetryEvent> events = best.getEvents(p);
        TelemetryEvent event = events.iterator().next();
        return lastPartial - event.timestamp();
    }

}
