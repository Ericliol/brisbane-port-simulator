package portsim.cargo;

import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Denotes a cargo whose function is to be transported via a Ship or land
 * transport.
 * <p>
 * Cargo is kept track of via its ID.
 *
 * @ass1_partial
 */
public abstract class Cargo implements Encodable {
    /**
     * The ID of the cargo instance
     */
    private int id;

    /**
     * Destination for this cargo
     */
    private String destination;

    /**
     * Database of all cargo currently active in the simulation
     */
    private static Map<Integer, Cargo> cargoRegistry = new HashMap<>();

    /**
     * Creates a new Cargo with the given ID and destination port.
     * <p>
     * When a new piece of cargo is created, it should be added to the cargo registry.
     * @param id          cargo ID
     * @param destination destination port
     * @throws IllegalArgumentException if a cargo already exists with the
     *                                  given ID or ID &lt; 0
     * @ass1_partial
     */
    public Cargo(int id, String destination) throws IllegalArgumentException {
        if (id < 0 || getCargoRegistry().containsKey(id)) {
            throw new IllegalArgumentException("Cargo ID must be greater than"
                + " or equal to 0: " + id);
        }
        this.id = id;
        this.destination = destination;
        getCargoRegistry().put(id, this);
    }

    /**
     * Retrieve the ID of this piece of cargo.
     *
     * @return the cargo's ID
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieve the destination of this piece of cargo.
     *
     * @return the cargo's destination
     * @ass1
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns the global registry of all pieces of cargo, as a mapping from cargo
     * IDs to Cargo instances.
     * Adding or removing elements from the returned map should not affect the original map.
     *
     * @return cargo registry
     */
    public static Map<Integer, Cargo> getCargoRegistry() {
        return cargoRegistry;
    }

    /**
     * Checks if a cargo exists in the simulation using its ID.
     *
     * @param id  unique key to identify cargo
     * @return true if there is a cargo stored in the registry with key id; false otherwise
     */
    public static boolean cargoExists(int id) {
        return cargoRegistry.containsKey(id);
    }

    /**
     * Returns the cargo specified by the given ID.
     *
     * @param id unique key to identify cargo
     * @return cargo specified by the id
     * @throws NoSuchCargoException if the cargo does not exist in the registry
     */
    public static Cargo getCargoById(int id) throws NoSuchCargoException {
        if (cargoExists(id)) {
            return cargoRegistry.get(id);
        } else {
            throw new NoSuchCargoException();
        }
    }


    /**
     * Returns true if and only if this cargo is equal to the other given cargo.
     * For two cargo to be equal, they must have the same ID and destination.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        Cargo cargo = (Cargo) o;
        return getId() == cargo.getId() && getDestination().equals(cargo.getDestination());
    }

    /**
     * Returns the hash code of this cargo.
     *
     * @return hash code of this cargo.
     */
    public int hashCode() {
        return (int) getDestination().hashCode() + getId();
    }

    /**
     * Returns the human-readable string representation of this cargo.
     * <p>
     * The format of the string to return is
     * <pre>CargoClass id to destination</pre>
     * Where:
     * <ul>
     *   <li>{@code CargoClass} is the cargo class name</li>
     *   <li>{@code id} is the id of this cargo </li>
     *   <li>{@code destination} is the destination of the cargo </li>
     * </ul>
     * <p>
     * For example: <pre>Container 55 to New Zealand</pre>
     *
     * @return string representation of this Cargo
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d to %s",
            this.getClass().getSimpleName(),
            this.id,
            this.destination);
    }

    /**
     * Returns the machine-readable string representation of this BulkCargo.
     * The format of the string to return is
     *
     * CargoClass:id:destination
     *
     * @return encoded string representation of this Cargo
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(getClass().getSimpleName());
        encode.add(Integer.toString(getId()));
        encode.add(getDestination());
        return encode.toString();
    }

    /**
     * Reads a piece of cargo from its encoded representation in the given string.
     * The format of the given string should match the encoded representation of a Cargo,
     * as described in encode() (and subclasses).
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * rule1 - The number of colons (:) detected was more/fewer than expected.
     * rule2 - The cargo id is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * rule3 - The cargo id is less than one (1).
     * rule4 - A piece of cargo with the specified ID already exists
     * rule5 - The cargo type specified is not one of BulkCargoType or ContainerType
     * rule6 - If the cargo type is a BulkCargo:
     *      1. The cargo weight in tonnes is not an integer (i.e. cannot be parsed by
     *      Integer.parseInt(String)).
     *      2. The cargo weight in tonnes is less than one (1).
     *
     * @param string string containing the encoded cargo
     * @return decoded cargo instance
     * @throws BadEncodingException  if the format of the given string is invalid according
     * to the rules above
     */
    public static Cargo fromString(String string) throws BadEncodingException {
        String[] cargoString = string.split(":");
        String cargoClass = cargoString[0];

        if (cargoClass.equals("BulkCargo")) {
            int count = cargoString.length;
            if (count != 5) {
                throw new BadEncodingException("The number of colons (:) detected"
                        + " was more/fewer than expected.");
            }
            try {
                int tonnes = Integer.parseInt(cargoString[4]);
                if (tonnes < 1) {
                    throw new BadEncodingException("The cargo weight in tonnes is less than one");
                }
            } catch (NumberFormatException e) {
                throw new BadEncodingException(e);
            }
            try {
                BulkCargoType.valueOf(cargoString[3]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }

        } else if (cargoClass.equals("Container")) {
            int count = cargoString.length;
            if (count != 4) {
                throw new BadEncodingException("The number of colons (:) detected"
                        + " was more/fewer than expected.");
            }
            try {
                ContainerType.valueOf(cargoString[3]);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException(e);
            }
        } else {
            throw new BadEncodingException("The cargo type specified is not"
                    + " one of BulkCargoType or ContainerType");
        }

        try {
            int id = Integer.parseInt(cargoString[1]);
            if (id < 1) {
                throw new BadEncodingException("The cargo id is less than one");
            }
            if (getCargoRegistry().containsKey(id)) {
                throw new BadEncodingException("A piece of cargo with the specified"
                        + " ID already exists");
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException(e);
        }
        if (cargoClass.equals("BulkCargo")) {
            return new BulkCargo(Integer.parseInt(cargoString[1]), cargoString[2],
                    Integer.parseInt(cargoString[4]), BulkCargoType.valueOf(cargoString[3]));
        } else {
            return new Container(Integer.parseInt(cargoString[1]), cargoString[2],
                    ContainerType.valueOf(cargoString[3]));
        }

    }

    /**
     * Resets the global cargo registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetCargoRegistry() {
        Cargo.cargoRegistry = new HashMap<>();
    }


}
