package portsim.movement;

import portsim.cargo.*;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchCargoException;


import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * The movement of cargo coming into or out of the port.
 *
 * @ass1_partial
 */
public class CargoMovement extends Movement {

    /**
     * The cargo that will be involved in the movement
     */
    private List<Cargo> cargo;

    /**
     * Creates a new cargo movement with the given action time and direction
     * to be undertaken with the given cargo.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param cargo     the cargo to be moved
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public CargoMovement(long time, MovementDirection direction,
                         List<Cargo> cargo) throws IllegalArgumentException {
        super(time, direction);
        this.cargo = cargo;
    }

    /**
     * Returns the cargo that will be moved.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all cargo in the movement
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    /**
     * Returns the human-readable string representation of this CargoMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION CargoMovement to occur at time involving num piece(s) of cargo </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code num} is the number of cargo pieces that are being moved</li>
     * </ul>
     * <p>
     * For example: <pre>
     * OUTBOUND CargoMovement to occur at 135 involving 5 piece(s) of cargo </pre>
     *
     * @return string representation of this CargoMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving %d piece(s) of cargo",
            super.toString(),
            this.cargo.size());
    }

    /**
     * Returns the machine-readable string representation of this movement.
     * The format of the string to return is
     * CargoMovement:time:direction:numCargo:ID1,ID2,...
     *
     * @return encoded string representation of this movement
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(super.encode());
        encode.add(Integer.toString(cargo.size()));
        StringJoiner cargoIds = new StringJoiner(",");
        for (Cargo cargo : cargo) {
            cargoIds.add(Integer.toString(cargo.getId()));
        }
        encode.add(cargoIds.toString());
        return encode.toString();
    }

    /**
     * Creates a cargo movement from a string encoding.
     * The format of the string should match the encoded representation of a cargo movement,
     *      as described in encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     *      rule1: The number of colons (:) detected was more/fewer than expected.
     *      rule2: The given string is not a CargoMovement encoding
     *      rule3: The time is not a long (i.e. cannot be parsed by Long.parseLong(String)).
     *      rule3: The time is less than zero (0).
     *      rule4: The movementDirection is not one of the valid directions (See MovementDirection).
     *      rule5 :The number of ids is not a int (i.e. cannot be parsed by
     *          Integer.parseInt(String)).
     *      rule5: The number of ids is less than one (1).
     *      rule6: An id is not a int (i.e. cannot be parsed by Integer.parseInt(String)).
     *      rule6: An id is less than zero (0).
     *      rule7: There is no cargo that exists with a specified id.
     *      rule8: The number of id's does not match the number specified.
     * @param string string containing the encoded CargoMovement
     * @return CargoMovement instance
     * @throws BadEncodingException  if the format of the given string is invalid
     *          according to the rules above
     */

    public static CargoMovement fromString(String string) throws BadEncodingException {
        long time;
        MovementDirection movementDirection;
        int idCounts;
        List<Integer> cargoIds = new ArrayList<>();
        List<Cargo> cargoList = new ArrayList<>();

        char temp;
        int colonsCount = 0;

        String[] cargoMovementInfo = string.split(":");
        for (int i = 0; i < string.length(); i++) {
            temp = string.charAt(i);
            if (temp == ':') {
                colonsCount++;
            }
        }
        if (colonsCount != 4) {
            throw new BadEncodingException("The number of colons (:)"
                    + " detected was more/fewer than expected.");
        }
        if (!cargoMovementInfo[0].equals("CargoMovement")) {
            throw new BadEncodingException("The given string is not a"
                    + " CargoMovement encoding");
        }
        try {
            time = Long.parseLong(cargoMovementInfo[1]);
            if (time < 0) {
                throw new BadEncodingException("The time is less than zero (0)");
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The time is not a long", e);
        }

        try {
            movementDirection = MovementDirection.valueOf(cargoMovementInfo[2]);
        } catch (IllegalArgumentException e) {
            throw new BadEncodingException("The movementDirection is not one"
                    + " of the valid directions", e);
        }

        try {
            idCounts = Integer.parseInt(cargoMovementInfo[3]);
            if (idCounts < 0) {
                throw new BadEncodingException("The number of ids is less than one (1)");
            } else if (idCounts > 0) {
                String[] ids = cargoMovementInfo[4].split(",");
                for (String idString : ids) {
                    int id;
                    try {
                        id = Integer.parseInt(idString);
                        if (id < 0) {
                            throw new BadEncodingException("An id is less than zero (0)");
                        }
                        cargoIds.add(id);
                    } catch (NumberFormatException e) {
                        throw new BadEncodingException("The cargo id is not a int", e);
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The number of ids is not a int", e);
        }
        
        for (int id : cargoIds) {
            try {
                cargoList.add(Cargo.getCargoById(id));
            } catch (NoSuchCargoException e) {
                throw new BadEncodingException("There is no cargo that exists with"
                        + " a specified id", e);
            }
        }
        if (cargoIds.size() != idCounts) {
            throw new BadEncodingException("The number of id's does not match"
                    + " the number specified.");
        }
        return new CargoMovement(time, movementDirection, cargoList);
    }

}
