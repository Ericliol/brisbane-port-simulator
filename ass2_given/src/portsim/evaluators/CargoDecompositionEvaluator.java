package portsim.evaluators;

import portsim.cargo.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.NoSuchShipException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects data on what types of cargo are passing through the port.
 * Gathers data on all derivatives of the cargo class.
 */
public class CargoDecompositionEvaluator extends StatisticsEvaluator {

    /**
     * cargo distribution map
     */
    private Map<String, Integer> cargoDistribution;
    /**
     * bulk cargo distribution map
     */
    private Map<BulkCargoType, Integer> bulkCargoTypeDistribution;
    /**
     * container distribution map
     */
    private Map<ContainerType, Integer> containerTypeDistribution;



    /**
     * Constructs a new CargoDecompositionEvaluator.
     */
    public CargoDecompositionEvaluator() {
        this.cargoDistribution = new HashMap<>();
        this.bulkCargoTypeDistribution = new HashMap<>();
        this.containerTypeDistribution = new HashMap<>();
    }

    /**
     * Returns the distribution of which cargo types that have entered the port.
     *
     * @return cargo distribution map
     */
    public Map<String, Integer> getCargoDistribution() {
        return this.cargoDistribution;
    }

    /**
     * Returns the distribution of bulk cargo types that have entered the port.
     *
     * @return bulk cargo distribution map
     */
    public Map<BulkCargoType, Integer> getBulkCargoDistribution() {
        return this.bulkCargoTypeDistribution;
    }

    /**
     * Returns the distribution of container cargo types that have entered the port.
     *
     * @return container distribution map
     */
    public Map<ContainerType, Integer> getContainerDistribution() {
        return this.containerTypeDistribution;
    }

    /**
     * Updates the internal distributions of cargo types using the given movement.
     * If the movement is not an INBOUND movement, this method returns immediately
     *      without taking any action.
     *
     * If the movement is an INBOUND movement, do the following:
     *
     * - If the movement is a ShipMovement, Retrieve the cargo from the ships and
     *      for each piece of cargo:
     * 1.   If the cargo class (Container / BulkCargo) has been seen before (simple name exists as
     *  a key in the cargo map) -> increment that number
     * 2.   If the cargo class has not been seen before then add its class simple name as a key in
     *  the map with a corresponding value of 1
     * 3.   If the cargo type (Value of ContainerType / BulkCargoType) for the given cargo class
     *  has been seen before (exists as a key in the map) increment that number
     * 4.   If the cargo type (Value of ContainerType / BulkCargoType) for the given cargo class has
     *  not been seen before add as a key in the map with a corresponding value of 1
     * - If the movement is a CargoMovement, Retrieve the cargo from the movement. For the cargo
     *  retrieved:
     * 1.   Complete steps 1-4 as given above for ShipMovement
     *
     * @param movement movement to read
     */
    @Override
    public void onProcessMovement(Movement movement) {

        if (movement.getDirection().equals(MovementDirection.OUTBOUND)) {
            if (movement instanceof ShipMovement) {
                ShipMovement shipMovement = (ShipMovement) movement;
                Ship ship = shipMovement.getShip();
                if (ship instanceof BulkCarrier) {
                    BulkCarrier bulkCarrier = (BulkCarrier) ship;
                    BulkCargoType bulkCargoType = bulkCarrier.getCargo().getType();
                    if (this.bulkCargoTypeDistribution.containsKey(bulkCargoType)) {
                        int oldValue = this.bulkCargoTypeDistribution.get(bulkCargoType);
                        this.bulkCargoTypeDistribution.put(bulkCargoType, oldValue + 1);
                    } else {
                        this.bulkCargoTypeDistribution.put(bulkCargoType, 1);
                    }

                    if (this.cargoDistribution.containsKey(bulkCargoType.toString())) {
                        int oldValue = this.cargoDistribution.get(bulkCargoType.toString());
                        this.cargoDistribution.put(bulkCargoType.toString(), oldValue + 1);
                    } else {
                        this.cargoDistribution.put(bulkCargoType.toString(), 1);
                    }
                } else if (ship instanceof ContainerShip) {
                    ContainerShip containerShip = (ContainerShip) ship;
                    for (Container container : containerShip.getCargo()) {
                        ContainerType containerType = container.getType();
                        if (this.containerTypeDistribution.containsKey(containerType)) {
                            int oldValue = this.containerTypeDistribution.get(containerType);
                            this.containerTypeDistribution.put(containerType, oldValue + 1);
                        } else {
                            this.containerTypeDistribution.put(containerType, 1);
                        }
                        if (this.cargoDistribution.containsKey(containerType.toString())) {
                            int oldValue = this.cargoDistribution.get(containerType.toString());
                            this.cargoDistribution.put(containerType.toString(), oldValue + 1);
                        } else {
                            this.cargoDistribution.put(containerType.toString(), 1);
                        }
                    }
                }
            } else if (movement instanceof CargoMovement) {
                CargoMovement cargoMovement = (CargoMovement) movement;
                List<Cargo> cargos = cargoMovement.getCargo();
                for (Cargo cargo : cargos) {
                    if (cargo instanceof BulkCargo) {
                        BulkCargo bulkCargo = (BulkCargo) cargo;
                        BulkCargoType bulkCargoType = bulkCargo.getType();
                        if (this.bulkCargoTypeDistribution.containsKey(bulkCargoType)) {
                            int oldValue = this.bulkCargoTypeDistribution.get(bulkCargoType);
                            this.bulkCargoTypeDistribution.put(bulkCargoType, oldValue + 1);
                        } else {
                            this.bulkCargoTypeDistribution.put(bulkCargoType, 1);
                        }

                        if (this.cargoDistribution.containsKey(bulkCargoType.toString())) {
                            int oldValue = this.cargoDistribution.get(bulkCargoType.toString());
                            this.cargoDistribution.put(bulkCargoType.toString(), oldValue + 1);
                        } else {
                            this.cargoDistribution.put(bulkCargoType.toString(), 1);
                        }

                    } else if (cargo instanceof Container) {
                        Container container = (Container) cargo;
                        ContainerType containerType = container.getType();
                        if (this.containerTypeDistribution.containsKey(containerType)) {
                            int oldValue = this.containerTypeDistribution.get(containerType);
                            this.containerTypeDistribution.put(containerType, oldValue + 1);
                        } else {
                            this.containerTypeDistribution.put(containerType, 1);
                        }
                        if (this.cargoDistribution.containsKey(containerType.toString())) {
                            int oldValue = this.cargoDistribution.get(containerType.toString());
                            this.cargoDistribution.put(containerType.toString(), oldValue + 1);
                        } else {
                            this.cargoDistribution.put(containerType.toString(), 1);
                        }
                    }
                }
            }
        }
    }




}
