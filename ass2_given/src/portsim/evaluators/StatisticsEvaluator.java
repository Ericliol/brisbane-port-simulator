package portsim.evaluators;

import portsim.movement.Movement;
import portsim.util.Tickable;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A base class representing an object that gathers and
 * reports data on various aspects of the port's operation.
 */
public abstract class StatisticsEvaluator implements Tickable {
    /**
     * time since creation in minutes
     */
    private long timeElapse;


    /**
     * Creates a statistics evaluator and initialises the time since the evaluator
     * was created to zero.
     */
    public StatisticsEvaluator()  {
        this.timeElapse = 0;
    }

    /**
     * Return the time since the evaluator was created.
     *
     * @return time
     */
    public long getTime() {
        return timeElapse;
    }

    /**
     * Read a movement to update the relevant evaluator data.
     *
     * @param movement movement to read
     */
    public abstract void onProcessMovement(Movement movement);

    /**
     * Simulate a minute passing. The time since the evaluator was created should
     * be incremented by one.
     */
    public void elapseOneMinute() {
        timeElapse += 1;
    }
}
