package portsim.evaluators;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import portsim.cargo.Cargo;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.port.Port;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;

import static org.junit.Assert.*;

public class ShipThroughputEvaluatorTest {

    Ship ship1;
    Ship ship2;
    Ship ship3;

    ShipMovement movement1;
    ShipMovement movement2;
    ShipMovement movement3;
    ShipMovement invaildMov;

    Port port;
    ShipThroughputEvaluator e;
    @Before
    public void setUp() throws Exception {
        e = new ShipThroughputEvaluator();
        Ship.resetShipRegistry();
        ship1 = new BulkCarrier(3456789, "Glorious", "ABC",
                NauticalFlag.HOTEL, 120);
        ship2 = new BulkCarrier(3456788, "Glorious", "ABC",
                NauticalFlag.HOTEL, 120);
        ship3 = new ContainerShip(1234567, "Polly", "DEF",
                NauticalFlag.WHISKEY, 4);

        movement1 = new ShipMovement(0,
                MovementDirection.OUTBOUND,ship1);
        movement2 = new ShipMovement(0,
                MovementDirection.INBOUND,ship2);
        movement3 = new ShipMovement(0,
                MovementDirection.OUTBOUND,ship3);

    }

    @After
    public void tearDown() throws Exception {
        Cargo.resetCargoRegistry();
        Ship.resetShipRegistry();
    }

    @Test
    public void testGetThroughputPerHour() {
        assertEquals(0,e.getThroughputPerHour());
        e.onProcessMovement(movement1);
        assertEquals(1,e.getThroughputPerHour());
    }

    @Test
    public void testOnProcessMovement() {
        assertEquals(0,e.getThroughputPerHour());
        e.onProcessMovement(movement1);
        assertEquals(1,e.getThroughputPerHour());
        e.onProcessMovement(movement2);
        assertEquals(1,e.getThroughputPerHour());
        for (int i = 0; i<70 ;i++) {
            e.elapseOneMinute();
        }
        assertEquals(0,e.getThroughputPerHour());
    }

    @Test
    public void testElapseOneMinute() {
        assertEquals(0,e.getTime());
        e.elapseOneMinute();
        assertEquals(1,e.getTime());
    }

}