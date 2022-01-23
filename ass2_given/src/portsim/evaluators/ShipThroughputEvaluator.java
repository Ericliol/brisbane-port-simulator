package portsim.evaluators;

import portsim.cargo.Container;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Gathers data on how many ships pass through the port over time.
 * This evaluator only counts ships that have passed through the port in the last hour (60 minutes)
 */
public class ShipThroughputEvaluator extends StatisticsEvaluator {
    /**
     * data of the time when ship leaves the ports
     */
    private HashMap<Long, Ship> shipThroughputInOneHour;

    /**
     * A base class representing an object that gathers and reports data on various
     * aspects of the port's operation.
     */
    public ShipThroughputEvaluator() {
        this.shipThroughputInOneHour = new HashMap<>();
    }

    /**
     * Return the number of ships that have passed through the port in the last 60 minutes.
     *
     * @return ships throughput
     */
    public int getThroughputPerHour() {
        elapseOneMinute();
        return this.shipThroughputInOneHour.size();
    }

    /**
     * Updates the internal count of ships that have passed through the port using the
     *      given movement.
     * If the movement is not an OUTBOUND ShipMovement, this method returns immediately without
     *      taking any action.
     *
     * @param movement movement to read
     */
    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection().equals(MovementDirection.OUTBOUND)
                && (movement instanceof ShipMovement)) {
            ShipMovement shipMovement = (ShipMovement) movement;
            Ship ship = shipMovement.getShip();
            this.shipThroughputInOneHour.put(this.getTime(), ship);
        }
    }

    /**
     * Simulate a minute passing. The time since the evaluator was created should be
     *      incremented by one.
     *
     * If a ship has been more than 60 minutes since a ship exited the port, it should no longer be
     * counted towards the count returned by getThroughputPerHour().
     */
    public void elapseOneMinute() {
        super.elapseOneMinute();
        for (long leaveTime : this.shipThroughputInOneHour.keySet()) {
            long  timeSinceCreate = this.getTime() - leaveTime;
            if (timeSinceCreate >= 60) {
                this.shipThroughputInOneHour.remove(leaveTime);
            }
        }
    }

}
