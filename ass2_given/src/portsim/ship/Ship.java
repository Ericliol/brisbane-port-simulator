package portsim.ship;

import portsim.cargo.Cargo;
import portsim.port.Quay;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;
import portsim.util.NoSuchShipException;


import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Represents a ship whose movement is managed by the system.
 * <p>
 * Ships store various types of cargo which can be loaded and unloaded at a port.
 *
 * @ass1_partial
 */
public abstract class Ship implements Encodable {
    /**
     * Name of the ship
     */
    private String name;

    /**
     * Unique 7 digit identifier to identify this ship (no leading zero's [0])
     */
    private long imoNumber;

    /**
     * Port of origin of ship
     */
    private String originFlag;

    /**
     * Maritime flag designated for use on this ship
     */
    private NauticalFlag flag;

    /**
     * Database of all ships currently active in the simulation
     */
    private static Map<Long, Ship> shipRegistry = new HashMap<>();

    /**
     * Creates a new ship with the given
     * <a href="https://en.wikipedia.org/wiki/IMO_number">IMO number</a>,
     * name, origin port flag and nautical flag.
     * <p>
     * Finally, the ship should be added to the ship registry with the
     * IMO number as the key.
     *
     * @param imoNumber  unique identifier
     * @param name       name of the ship
     * @param originFlag port of origin
     * @param flag       the nautical flag this ship is flying
     * @throws IllegalArgumentException if a ship already exists with the given
     *                                  imoNumber, imoNumber &lt; 0 or imoNumber is not 7 digits
     *                                  long (no leading zero's [0])
     * @ass1_partial
     */
    public Ship(long imoNumber, String name, String originFlag,
                NauticalFlag flag) throws IllegalArgumentException {
        if (imoNumber < 0) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must be positive: " + imoNumber);
        }
        if (String.valueOf(imoNumber).length() != 7 || String.valueOf(imoNumber).startsWith("0")) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must have 7 digits (no leading zero's [0]): " + imoNumber);
        }
        this.imoNumber = imoNumber;
        this.name = name;
        this.originFlag = originFlag;
        this.flag = flag;
        getShipRegistry().put(imoNumber, this);
    }

    /**
     * Checks if a ship exists in the simulation using its IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return true if there is a ship with key imoNumber else false
     */
    public static boolean shipExists(long imoNumber) {
        return shipRegistry.containsKey(imoNumber);
    }

    /**
     * Returns the ship specified by the IMO number.
     *
     * @param imoNumber unique key to identify ship
     * @return Ship specified by the given IMO number
     * @throws NoSuchShipException if the ship does not exist
     */
    public static Ship getShipByImoNumber(long imoNumber) throws NoSuchShipException {
        if (shipExists(imoNumber)) {
            try {
                return shipRegistry.get(imoNumber);
            } catch (NullPointerException e) {
                throw new NoSuchShipException(e);
            }
        }
        throw new NoSuchShipException("the ship does not exist");
    }


    /**
     * Check if this ship can dock with the specified quay according
     * to the conditions determined by the ships type.
     *
     * @param quay quay to be checked
     * @return true if the Quay satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canDock(Quay quay);

    /**
     * Checks if the specified cargo can be loaded onto the ship according
     * to the conditions determined by the ships type and contents.
     *
     * @param cargo cargo to be loaded
     * @return true if the Cargo satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canLoad(Cargo cargo);

    /**
     * Loads the specified cargo onto the ship.
     *
     * @param cargo cargo to be loaded
     * @require Cargo given is able to be loaded onto this ship according to
     * the implementation of {@link Ship#canLoad(Cargo)}
     * @ass1
     */
    public abstract void loadCargo(Cargo cargo);

    /**
     * Returns this ship's name.
     *
     * @return name
     * @ass1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this ship's IMO number.
     *
     * @return imoNumber
     * @ass1
     */
    public long getImoNumber() {
        return this.imoNumber;
    }

    /**
     * Returns this ship's flag denoting its origin.
     *
     * @return originFlag
     * @ass1
     */
    public String getOriginFlag() {
        return this.originFlag;
    }

    /**
     * Returns the nautical flag the ship is flying.
     *
     * @return flag
     * @ass1
     */
    public NauticalFlag getFlag() {
        return this.flag;
    }

    /**
     * Returns the database of ships currently active in the simulation as a mapping from
     * the ship's IMO number to its Ship instance.
     * Adding or removing elements from the returned map should not affect the original map.
     *
     * @return ship registry database
     */
    public static Map<Long, Ship> getShipRegistry() {
        return shipRegistry;
    }

    /**
     * Returns true if and only if this ship is equal to the other given ship.
     * For two ships to be equal, they must have the same name, flag, origin port,
     * and IMO number.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        try {
            Ship ship = (Ship) o;
            return ship.getName().equals(name) && ship.getFlag().equals(flag)
                    && ship.getOriginFlag().equals(originFlag) && ship.getImoNumber() == imoNumber;
        } catch (ClassCastException e) {
            return false;
        }

    }

    /**
     * Returns the hash code of this ship.
     *
     * @return hash code of this ship.
     */
    public int hashCode() {
        return (int) (name.hashCode() + flag.hashCode() + originFlag.hashCode() + imoNumber);
    }

    /**
     * Returns the human-readable string representation of this Ship.
     * <p>
     * The format of the string to return is
     * <pre>ShipClass name from origin [flag]</pre>
     * Where:
     * <ul>
     *   <li>{@code ShipClass} is the Ship class</li>
     *   <li>{@code name} is the name of this ship</li>
     *   <li>{@code origin} is the country of origin of this ship</li>
     *   <li>{@code flag} is the nautical flag of this ship</li>
     * </ul>
     * For example: <pre>BulkCarrier Evergreen from Australia [BRAVO]</pre>
     *
     * @return string representation of this Ship
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %s from %s [%s]",
            this.getClass().getSimpleName(),
            this.name,
            this.originFlag,
            this.flag);
    }

    /**
     * Returns the machine-readable string representation of this Ship.
     * The format of the string to return is
     *
     * ShipClass:imoNumber:name:origin:flag
     *
     * @return encoded string representation of this Ship
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(getClass().getSimpleName());
        encode.add(Long.toString(getImoNumber()));
        encode.add(getName());
        encode.add(getOriginFlag());
        encode.add(getFlag().toString());
        return encode.toString();
    }

    /**
     * Reads a Ship from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a Ship, as described
     * in encode() (and subclasses).
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * 1. The number of colons (:) detected was more/fewer than expected
     * 2. The ship's IMO number is not an integer (i.e. cannot be parsed by
     *          Integer.parseInt(String))
     * 3. The ship's IMO number is less than one (1)
     * 4. The ship's type specified is not one of ContainerShip or BulkCarrier
     * 5. The encoded Nautical flag is not one of NauticalFlag.values()
     * 6. The encoded cargo to add does not exist in the simulation according to
     *          Cargo.cargoExists(int)
     * 7. The encoded cargo can not be added to the ship according to canLoad(Cargo)
     *        NOTE: Keep this in mind when making your own save files
     * 8. Any of the parsed values given to a subclass constructor causes
     *          an IllegalArgumentException.
     *
     * @param string string containing the encoded Ship
     * @return decoded ship instance
     * @throws BadEncodingException if the format of the given string is invalid
     *          according to the rules above
     */
    public static Ship fromString(String string) throws BadEncodingException {
        String[] shipInfo = string.split(":");

        Ship ship;
        long imoNumber;
        NauticalFlag flag;
        int capacity;
        if (shipInfo.length > 6) {
            try {
                imoNumber = Long.parseLong(shipInfo[1]);
                if (imoNumber < 1) {
                    throw new BadEncodingException("The ship's IMO number is less than one (1)");
                }
            } catch (NumberFormatException e) {
                throw new BadEncodingException(e);
            }

            try {
                flag = NauticalFlag.valueOf(shipInfo[4]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }

            try {
                capacity = Integer.parseInt(shipInfo[5]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }

        } else {
            throw new BadEncodingException(" The number of colons (:)"
                    + " detected was more/fewer than expected");
        }
        int colonsCount = 0;
        char temp;
        for (int i = 0; i < string.length(); i++) {
            temp = string.charAt(i);
            if (temp == ':') {
                colonsCount++;
            }
        }


        if (shipInfo[0].equals("ContainerShip")) {
            if (colonsCount != 7) {
                throw new BadEncodingException(" The number of colons (:)"
                        + " detected was more/fewer than expected");
            }
            try {
                int cargoNum = Integer.parseInt(shipInfo[6]);
                ship = new ContainerShip(imoNumber, shipInfo[2], shipInfo[3], flag, capacity);
                if (cargoNum > 0) {
                    String[] cargos = shipInfo[7].split(",");
                    for (String cargoIdStr : cargos) {
                        try {
                            int cargoId = Integer.parseInt(cargoIdStr);
                            if (Cargo.cargoExists(cargoId)) {
                                Cargo cargo = Cargo.getCargoById(cargoId);
                                if (ship.canLoad(cargo)) {
                                    ship.loadCargo(cargo);
                                } else {
                                    throw new BadEncodingException("The encoded cargo can"
                                            + " not be added to the ship according to"
                                            + " canLoad(Cargo)");
                                }
                            }
                        } catch (NumberFormatException | NoSuchCargoException e) {
                            throw new BadEncodingException(e);
                        }
                    }
                }

            } catch (IllegalArgumentException  e) {
                throw new BadEncodingException(e);
            }


        } else if (shipInfo[0].equals("BulkCarrier")) {

            if (colonsCount != 6) {
                throw new BadEncodingException(" The number of colons (:)"
                        + " detected was more/fewer than expected");
            }
            try {
                ship = new BulkCarrier(imoNumber, shipInfo[2], shipInfo[3], flag, capacity);
                int cargoId;
                try {
                    cargoId = Integer.parseInt(shipInfo[6]);
                    if (Cargo.cargoExists(cargoId)) {
                        Cargo cargo = Cargo.getCargoById(cargoId);
                        if (ship.canLoad(cargo)) {
                            ship.loadCargo(cargo);
                        } else {
                            throw new BadEncodingException("The encoded cargo can"
                                    + " not be added to the ship according to canLoad(Cargo)");
                        }
                    }

                } catch (NumberFormatException | NoSuchCargoException e) {
                    throw new BadEncodingException(e);
                }
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }
        } else {
            throw new BadEncodingException("The ship's type specified"
                    + " is not one of ContainerShip or BulkCarrier");
        }
        return ship;
    }

    /**
     * Resets the global ship registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetShipRegistry() {
        Ship.shipRegistry = new HashMap<>();
    }

}
