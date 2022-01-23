package portsim.port;

import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Queue of ships waiting to enter a Quay at the port.
 * Ships are chosen based on their priority.
 */
public class ShipQueue implements Encodable {
    /**
     * queue of ships.
     */
    private ArrayList<Ship> shipQueue;

    /**
     * Constructs a new ShipQueue with an initially empty queue of ships.
     */
    public ShipQueue() {
        shipQueue = new ArrayList<>();
    }

    /**
     * Gets the next ship to enter the port and removes it from the queue.
     * The same rules as described in peek() should be used for determining which ship
     * to remove and return.
     *
     * @return next ship to dock
     */
    public Ship poll() {
        Ship shipToDock = peek();
        shipQueue.remove(shipToDock);
        return shipToDock;
    }

    /**
     * Returns the next ship waiting to enter the port. The queue should not change.
     * The rules for determining which ship in the queue should be returned next are as follows:
     *
     * 1. If a ship is carrying dangerous cargo, it should be returned. If more than one ship
     * is carrying dangerous cargo return the one added to the queue first. BRAVO
     * 2. If a ship requires medical assistance, it should be returned. If more than one ship
     * requires medical assistance, return the one added to the queue first. WHISKEY
     * 3. If a ship is ready to be docked, it should be returned. If more than one ship is ready
     * to be docked, return the one added to the queue first. HOTEL
     * 4. If there is a container ship in the queue, return the one added to the queue first.
     * 5. If this point is reached and no ship has been returned, return the ship that was added
     * to the queue first.
     * 6. If there are no ships in the queue, return null.
     *
     * @return next ship in queue
     */
    public Ship peek() {
        Ship shipToEnter = null;
        for (Ship ship : shipQueue) {
            if (ship.getFlag() == NauticalFlag.BRAVO) {
                shipToEnter = ship;
                break;
            }
        }
        if (shipToEnter == null) {
            for (Ship ship : shipQueue) {
                if (ship.getFlag() == NauticalFlag.WHISKEY) {
                    shipToEnter = ship;
                    break;
                }
            }
        }
        if (shipToEnter == null) {
            for (Ship ship : shipQueue) {
                if (ship.getFlag() == NauticalFlag.HOTEL) {
                    shipToEnter = ship;
                    break;
                }
            }
        }
        if (shipToEnter == null) {
            for (Ship ship : shipQueue) {
                if (ship instanceof ContainerShip) {
                    shipToEnter = ship;
                    break;
                }
            }
        }
        if (shipToEnter == null) {
            try {
                shipToEnter = shipQueue.get(0);
            } catch (IndexOutOfBoundsException ignored) {
                return null;
            }
        }
        return shipToEnter;
    }

    /**
     * Adds the specified ship to the queue.
     *
     * @param ship to be added to queue
     */
    public void add(Ship ship) {
        shipQueue.add(ship);
    }

    /**
     * Returns a list containing all the ships currently stored in this ShipQueue.
     * The order of the ships in the returned list should be the order in which the ships were
     * added to the queue.
     *
     * @return ships in queue
     */
    public List<Ship> getShipQueue() {
        return shipQueue;
    }

    /**
     * Returns true if and only if this ship queue is equal to the other given ship queue.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        try {
            ShipQueue queue = (ShipQueue) o;
            return queue.getShipQueue().equals(getShipQueue());
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns the hash code of this ship queue.
     * Two ship queue's that are equal according to equals(Object) method should have the
     * same hash code.
     *
     * @return hash code of this ship queue.
     */
    public int hashCode() {
        int hashCode = 0;
        for (Ship ship : getShipQueue()) {
            hashCode += ship.hashCode();
        }
        return hashCode;
    }

    /**
     * Returns the machine-readable string representation of this ShipQueue.
     * The format of the string to return is
     *
     * ShipQueue:numShipsInQueue:shipID,shipID,...
     *
     * @return string representation of this ShipQueue
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        StringJoiner ships = new StringJoiner(",");
        int numShipsInQueue = getShipQueue().size();
        encode.add(getClass().getSimpleName());
        encode.add(Integer.toString(numShipsInQueue));
        if (numShipsInQueue > 0) {
            for (Ship ship : getShipQueue()) {
                ships.add(Long.toString(ship.getImoNumber()));
            }
            encode.add(ships.toString());
        }
        return encode.toString();
    }

    /**
     * Creates a ship queue from a string encoding.
     * The format of the string should match the encoded representation of a ship queue,
     * as described in encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * 1. The number of colons (:) detected was more/fewer than expected.
     * 2. The string does not start with the literal string "ShipQueue"
     * 3. The number of ships in the shipQueue is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)).
     * 4. The imoNumber of the ships in the shipQueue are not valid longs. (i.e. cannot
     * be parsed by Long.parseLong(String)).
     * 5. Any imoNumber read does not correspond to a valid ship in the simulation
     *
     * @param string string containing the encoded ShipQueue
     * @return decoded ship queue instance
     * @throws BadEncodingException if the format of the given string is invalid according
     * to the rules above
     */
    public static ShipQueue fromString(String string) throws BadEncodingException {

        int numShipsInQueue;
        String[] queueInfo = string.split(":");
        int colonsCount = 0;
        char temp;
        ShipQueue shipQueue = new ShipQueue();
        for (int i = 0; i < string.length(); i++) {
            temp = string.charAt(i);
            if (temp == ':') {
                colonsCount++;
            }
        }
        if (colonsCount != 2) {
            throw new BadEncodingException("The number of colons (:)"
                    + " detected was more/fewer than expected.");
        }
        if (!queueInfo[0].equals("ShipQueue")) {
            throw new BadEncodingException("The string does not start with the"
                    + " literal string \"ShipQueue\"");
        }
        try {
            numShipsInQueue = Integer.parseInt(queueInfo[1]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of ships in "
                    + "the shipQueue is not an integer" + e);
        }
        if (numShipsInQueue > 0) {
            String[] shipIds = queueInfo[2].split(",");
            if (shipIds.length != numShipsInQueue) {
                throw new BadEncodingException();
            }
            for (String id : shipIds) {
                try {
                    long imoNumber = Long.parseLong(id);
                    try {
                        Ship ship = Ship.getShipByImoNumber(imoNumber);
                        shipQueue.add(ship);
                    } catch (NoSuchShipException e) {
                        throw new BadEncodingException(e);
                    }
                } catch (NumberFormatException e) {
                    throw new BadEncodingException("The imoNumber of the ships"
                            + " in the shipQueue are not valid longs.", e);
                }
            }
        }
        return shipQueue;
    }
}
