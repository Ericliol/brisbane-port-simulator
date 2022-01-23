package portsim.port;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchShipException;

import java.util.StringJoiner;


/**
 * Quay is a platform lying alongside or projecting into the water where
 * ships are moored for loading or unloading.
 *
 * @ass1_partial
 */
public abstract class Quay {
    /**
     * The ID of the quay
     */
    private int id;

    /**
     * The ship currently in the Quay
     */
    private Ship ship;

    /**
     * Creates a new Quay with the given ID, with no ship docked at the quay.
     *
     * @param id quay ID
     * @throws IllegalArgumentException if ID &lt; 0
     * @ass1
     */
    public Quay(int id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Quay ID must be greater than"
                + " or equal to 0: " + id);
        }
        this.id = id;
        this.ship = null;
    }

    /**
     * Get the id of this quay
     *
     * @return quay id
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Docks the given ship at the Quay so that the quay becomes occupied.
     *
     * @param ship ship to dock to the quay
     * @ass1
     */
    public void shipArrives(Ship ship) {
        this.ship = ship;
    }

    /**
     * Removes the current ship docked at the quay.
     * The current ship should be set to {@code null}.
     *
     * @return the current ship or null if quay is empty.
     * @ass1
     */
    public Ship shipDeparts() {
        Ship current = this.ship;
        this.ship = null;
        return current;
    }

    /**
     * Returns whether a ship is currently docked at this quay.
     *
     * @return true if there is no ship docked else false
     * @ass1
     */
    public boolean isEmpty() {
        return this.ship == null;
    }

    /**
     * Returns the ship currently docked at the quay.
     *
     * @return ship at quay or null if no ship is docked
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns true if and only if this Quay is equal to the other given Quay.
     * For two Quays to be equal, they must have the same ID and ship
     * docked status (must either both be empty or both be occupied).
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        Quay quay = (Quay) o;
        return getId() == quay.getId() && isEmpty() == quay.isEmpty();
    }

    /**
     * Returns the hash code of this quay.
     *
     * @return hash code of this quay.
     */
    public int hashCode() {
        return getId() + Boolean.hashCode(isEmpty());
    }

    /**
     * Returns the human-readable string representation of this quay.
     * <p>
     * The format of the string to return is
     * <pre>QuayClass id [Ship: imoNumber]</pre>
     * Where:
     * <ul>
     * <li>{@code id} is the ID of this quay</li>
     * <li>{@code imoNumber} is the IMO number of the ship docked at this
     * quay, or {@code None} if the quay is unoccupied.</li>
     * </ul>
     * <p>
     * For example: <pre>BulkQuay 1 [Ship: 2313212]</pre> or
     * <pre>ContainerQuay 3 [Ship: None]</pre>
     *
     * @return string representation of this quay
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d [Ship: %s]",
            this.getClass().getSimpleName(),
            this.id,
            (this.ship != null ? this.ship.getImoNumber() : "None"));
    }

    /**
     * Returns the machine-readable string representation of this Quay.
     * The format of the string to return is
     * QuayClass:id:imoNumber
     *
     * @return encoded string representation of this quay
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(getClass().getSimpleName());
        encode.add(Integer.toString(getId()));
        if (getShip() == null) {
            encode.add("None");
        } else {
            encode.add(Long.toString(getShip().getImoNumber()));
        }
        return encode.toString();
    }

    /**
     * Reads a Quay from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a Quay, as
     *  described in encode() (and subclasses).
     * The encoded string is invalid if any of the following conditions are true:
     *
     * 1. The number of colons (:) detected was more/fewer than expected.
     * 2. The quay id is not a long (i.e. cannot be parsed by Long.parseLong(String)).
     * 3. The quay id is less than one (1).
     * 4. The quay type specified is not one of BulkQuay or ContainerQuay
     * 5. If the encoded ship is not None then the ship must exist and the imoNumber specified must
     *      be an integer (i.e. can be parsed by Integer.parseInt(String)).
     * 6. The quay capacity is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     *
     * @param string string containing the encoded Quay
     * @return decoded Quay instance
     * @throws BadEncodingException if the format of the given string is invalid
     *              according to the rules above
     */
    public static Quay fromString(String string) throws BadEncodingException {

        boolean rule4 = false;
        boolean rule5 = false;
        boolean rule6 = false;

        String[] quayInfo = string.split(":");
        final String quayType = quayInfo[0];
        String shipString = quayInfo[2];
        int quayId;
        int quayCapacity;
        int colonsCount = 0;
        char temp;
        Quay quay = null;
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

        try {
            quayId = Integer.parseInt(quayInfo[1]);
            if (quayId < 1) {
                throw new BadEncodingException("The quay id is less than one (1).");
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The quay id is not a long", e);
        }

        try {
            quayCapacity =  Integer.parseInt(quayInfo[3]);
        } catch (NumberFormatException e) {
            throw new BadEncodingException("The quay capacity is not an integer", e);
        }

        if (quayType.equals("BulkQuay")) {
            quay = new BulkQuay(quayId, quayCapacity);
            if (!shipString.equals("None")) {
                try {
                    long imoNumber = Long.parseLong(shipString);
                    Ship ship = Ship.getShipByImoNumber(imoNumber);
                    quay.shipArrives(ship);
                } catch (NumberFormatException | NoSuchShipException e) {
                    throw new BadEncodingException(e);
                }
            }
        } else if (quayType.equals("ContainerQuay")) {
            quay = new ContainerQuay(quayId, quayCapacity);
            if (!shipString.equals("None")) {
                try {
                    long imoNumber = Long.parseLong(shipString);
                    Ship ship = Ship.getShipByImoNumber(imoNumber);
                    quay.shipArrives(ship);
                } catch (NumberFormatException | NoSuchShipException e) {
                    throw new BadEncodingException(e);
                }
            }
        } else {
            throw new BadEncodingException("The quay type specified is"
                    + " not one of BulkQuay or ContainerQuay");
        }
        return quay;
    }


}
