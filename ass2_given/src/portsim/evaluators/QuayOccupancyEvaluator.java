package portsim.evaluators;

import portsim.movement.Movement;
import portsim.port.Port;
import portsim.port.Quay;

/**
 * Evaluator to monitor how many quays are currently occupied at the port.
 */
public class QuayOccupancyEvaluator extends StatisticsEvaluator {
    /**
     * port to be evaluate Quay Occupancy of
     */
    private Port port;

    /**
     * Constructs a new QuayOccupancyEvaluator.
     *
     * @param port port to monitor quays
     */
    public QuayOccupancyEvaluator(Port port) {
        this.port = port;
    }

    /**
     * Return the number of quays that are currently occupied.
     *
     * @return number of quays
     */
    public int getQuaysOccupied() {
        int quaysOccupied = 0;

        for (Quay quay : this.port.getQuays()) {
            if (!quay.isEmpty()) {
                ++quaysOccupied;
            }
        }
        return quaysOccupied;
    }

    /**
     * this method can be left empty
     *
     * @param movement movement to read
     */
    @Override
    public void onProcessMovement(Movement movement) {
    }
}
