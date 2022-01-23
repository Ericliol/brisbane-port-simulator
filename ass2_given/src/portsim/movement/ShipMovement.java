package portsim.movement;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchShipException;

import java.util.StringJoiner;

/**
 * The movement of a ship coming into or out of the port.
 *
 * @ass1_partial
 */
public class ShipMovement extends Movement {

    /**
     * The ship entering of leaving the Port
     */
    private Ship ship;

    /**
     * Creates a new ship movement with the given action time and direction
     * to be undertaken with the given ship.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param ship      the ship which that is waiting to move
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public ShipMovement(long time, MovementDirection direction, Ship ship)
        throws IllegalArgumentException {
        super(time, direction);
        this.ship = ship;
    }

    /**
     * Returns the ship undertaking the movement.
     *
     * @return movements ship
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns the human-readable string representation of this ShipMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION ShipMovement to occur at time involving the ship name </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code name} is the name of the ship that is being moved</li>
     * </ul>
     * For example:
     * <pre>
     * OUTBOUND ShipMovement to occur at 135 involving the ship Voyager </pre>
     *
     * @return string representation of this ShipMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving the ship %s",
            super.toString(),
            this.ship.getName());
    }

    /**
     * Returns the machine-readable string representation of this ship movement.
     * The format of the string to return is
     *
     * ShipMovement:time:direction:imoNumber
     *
     * @return encoded string representation of this movement
     */
    public String encode() {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(super.encode());
        joiner.add(Long.toString(getShip().getImoNumber()));
        return joiner.toString();
    }

    /**
     *  Creates a ship movement from a string encoding.
     *  The format of the string should match the encoded representation of a ship
     *          movement, as described in encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     *     rule1: The number of colons (:) detected was more/fewer than expected.
     *     rule2: The given string is not a ShipMovement encoding
     *     rule3: The time is not a long (i.e. cannot be parsed by Long.parseLong(String)).
     *     rule3: The time is less than zero (0).
     *     rule4: The movementDirection is not one of the valid directions (See MovementDirection).
     *     rule5: The imoNumber is not a long (i.e. cannot be parsed by Long.parseLong(String)).
     *     rule5: There is no ship that exists with the specified imoNumber.
     *
     * @param string string containing the encoded ShipMovement
     * @return decoded ShipMovement instance
     * @throws BadEncodingException if the format of the given string is invalid
     *              according to the rules above
     */
    public static ShipMovement fromString(String string) throws BadEncodingException {
        long time = 0;
        MovementDirection movementDirection = null;
        Ship ship = null;
        String[] movementInfo = string.split(":");
        char temp;
        int colonsCount = 0;
        for (int i = 0; i < string.length(); i++) {
            temp = string.charAt(i);
            if (temp == ':') {
                colonsCount++;
            }
        }
        if (colonsCount != 3) {
            throw new BadEncodingException("The number of colons (:) detected"
                    + " was more/fewer than expected.");
        }
        if (!movementInfo[0].equals("ShipMovement")) {
            throw new BadEncodingException("The given string is not a ShipMovement encoding");
        }
        try {
            time = Long.parseLong(movementInfo[1]);
            if (time < 0) {
                throw new BadEncodingException("The time is less than zero (0)");
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The time is not a long ", e);
        }
        try {
            movementDirection = MovementDirection.valueOf(movementInfo[2]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException(" The movementDirection is not one "
                    + "of the valid directions");
        }
        try {
            long shipImo = Long.parseLong(movementInfo[3]);
            ship = Ship.getShipByImoNumber(shipImo);
        } catch (NumberFormatException | NoSuchShipException e) {
            throw new BadEncodingException("The imoNumber is not a long or \nThere is no ship that"
                    + " exists with the specified imoNumber.", e);
        }
        return new ShipMovement(time, movementDirection, ship);
    }
}
