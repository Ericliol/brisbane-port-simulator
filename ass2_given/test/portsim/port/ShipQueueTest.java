package portsim.port;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import portsim.cargo.Cargo;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ShipQueueTest {

    private ShipQueue s;
    private ShipQueue s2;
    private Ship ship1;
    private Ship ship2;
    private Ship ship3;
    private Ship ship4;
    private Ship ship5;

    @Before
    public void setUp() throws Exception {
        s = new ShipQueue();
        ship1 = new BulkCarrier(1234567, "Victory", "United Kingdom",
                NauticalFlag.NOVEMBER, 400);
        ship2 = new BulkCarrier(3456789, "Glorious", "Switzerland",
                NauticalFlag.HOTEL, 120);
        ship3 = new ContainerShip(2545679, "Legion", "France",
                NauticalFlag.BRAVO, 800);
        ship4 = new ContainerShip(2540079, "Legion", "France",
                NauticalFlag.WHISKEY, 800);
        ship5 = new BulkCarrier(2525679,"Business", "USA",
                NauticalFlag.HOTEL,100);
    }

    @After
    public void tearDown() throws Exception {
        Cargo.resetCargoRegistry();
        Ship.resetShipRegistry();
    }

    @Test
    public void testPoll() {
        s.add(ship1);
        assertEquals(ship1, s.poll());

    }

    @Test
    public void testPeek() {
        s.add(ship2); // ready to be docked
        s.add(ship3); // carrying dangerous cargo, should be removed first

        assertEquals(ship3,s.peek());
        s.poll();
        s.add(ship4);
        assertEquals(ship4, s.peek());
        s.poll();
        s.add(ship5);
        assertEquals(ship2, s.peek());
    }

    @Test
    public void testAdd() {
        s.add(ship1);
        assertEquals(ship1, s.poll());
    }

    @Test
    public void testGetShipQueue() {

    }

    @Test
    public void testTestEquals() {
        assertFalse(s.equals(null));
    }

    @Test
    public void testFromString() throws BadEncodingException {

        s.add(ship1);
        s.add(ship4);
        assertEquals(s,ShipQueue.fromString("ShipQueue:2:1234567,2540079"));
        boolean thrown = false;
        boolean thrown2 = false;
        boolean thrown3 = false;
        boolean thrown4 = false;
        boolean thrown5 = false;
        try {
            ShipQueue.fromString("ShipQueue:3:1234567,2540079");
        } catch (BadEncodingException e) {
            thrown = true;
        }
        try {
            ShipQueue.fromString("ShipQueue:1:1000003");
        } catch (BadEncodingException e) {
            thrown2 = true;
        }

        try {
            ShipQueue.fromString("bad string ::");
        } catch (BadEncodingException e) {
            thrown3 = true;
        }
        try {
            ShipQueue.fromString("ShipQueue:AA:1234567");
        } catch (BadEncodingException e) {
            thrown4 = true;
        }
        try {
            ShipQueue.fromString("ShipQueue:AA:1234567");
        } catch (BadEncodingException e) {
            thrown4 = true;
        }
        try {
            ShipQueue.fromString("ShipQueue:1:1234567:");
        } catch (BadEncodingException e) {
            thrown5 = true;
        }

        assertTrue(thrown);
        assertTrue(thrown2);
        assertTrue(thrown3);
        assertTrue(thrown4);
        assertTrue(thrown5);
    }
}