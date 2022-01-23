package portsim.evaluators;


import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;

import java.util.HashMap;
import java.util.Map;

/**
 * Gathers data on how many ships each country has sent to this port.
 *
 * Stores a mapping of country-of-origin flags to the number of times
 *      that flag has been seen in inbound movements.
 */
public class ShipFlagEvaluator extends StatisticsEvaluator {

    /**
     * flag distribution with flag type as key and number of times the given
     * flag has been seen at the port as value.
     */
    private Map<String, Integer> flagDistribution;

    /**
     * Return the flag distribution seen at this port.
     */
    public ShipFlagEvaluator() {
        this.flagDistribution = new HashMap<>();
    }

    /**
     * Return the flag distribution seen at this port.
     *
     * @return flag distribution
     */
    public Map<String, Integer> getFlagDistribution() {
        return this.flagDistribution;
    }

    /**
     * Return the number of times the given flag has been seen at the port.
     *
     * @param flag country flag to find in the mapping
     * @return number of times flag seen or 0 if not seen
     */
    public int getFlagStatistics(String flag) {
        try {
            return flagDistribution.get(flag);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Updates the internal mapping of ship country flags using the given movement.
     * If the movement is not an OUTBOUND movement, this method returns immediately
     * without taking any action.
     *
     * If the movement is not a ShipMovement, this method returns immediately
     *      without taking any action.
     *
     * If the movement is an INBOUND ShipMovement, do the following:
     *
     * 1. If the flag has been seen before (exists as a key in the map) increment that number
     * 2. If the flag has not been seen before add as a key in the map with a
     *      corresponding value of 1
     *
     * @param movement movement to read
     */
    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection().equals(MovementDirection.INBOUND)
                && (movement instanceof ShipMovement)) {
            ShipMovement shipMovement = (ShipMovement) movement;
            String originFlag = shipMovement.getShip().getOriginFlag();
            if (this.flagDistribution.containsKey(originFlag)) {
                int oldValue = this.flagDistribution.get(originFlag);
                this.flagDistribution.put(originFlag, oldValue + 1);
            } else {
                this.flagDistribution.put(originFlag, 1);
            }
        }
    }

}
