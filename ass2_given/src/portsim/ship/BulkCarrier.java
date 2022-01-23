package portsim.ship;

import portsim.cargo.*;
import portsim.port.BulkQuay;
import portsim.port.Quay;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchCargoException;
import portsim.util.NoSuchShipException;

import java.io.File;
import java.util.StringJoiner;

/**
 * Represents a ship capable of carrying bulk cargo.
 *
 * @ass1_partial
 */
public class BulkCarrier extends Ship {
    /**
     * Maximum amount of cargo in tonnes that the ship can carry
     */
    private int tonnageCapacity;

    /**
     * The cargo loaded on the ship
     */
    private BulkCargo cargo;

    /**
     * Creates a new bulk carrier with the given IMO number, name, origin
     * port, nautical flag and cargo capacity.
     *
     * @param imoNumber  unique identifier
     * @param name       name of the ship
     * @param originFlag port of origin
     * @param flag       the nautical flag this ship is flying
     * @param capacity   the tonnage capacity of this ship
     * @throws IllegalArgumentException if a ship already exists with the given
     *                                  imoNumber, imoNumber &lt; 0, imoNumber is not 7 digits long
     *                                  or if the tonnage capacity is &lt; than 0
     * @ass1
     */
    public BulkCarrier(long imoNumber, String name, String originFlag,
                       NauticalFlag flag, int capacity) throws IllegalArgumentException {
        super(imoNumber, name, originFlag, flag);
        if (capacity < 0) {
            throw new IllegalArgumentException("The tonnage capacity of the "
                + "ship must be positive: " + capacity);
        }
        this.tonnageCapacity = capacity;
        this.cargo = null;
    }

    /**
     * Check if this ship can dock with the specified quay.
     * <p>
     * The conditions for a compatible quay are:
     * <ol>
     *     <li>Quay must be a BulkQuay.</li>
     *     <li>The quay's maximum cargo weight must be &ge; the weight of the cargo on board the
     *     ship in tonnes.</li>
     * </ol>
     *
     * @param quay quay to be checked
     * @return true if the Quay satisfies the conditions else false
     * @ass1
     */
    @Override
    public boolean canDock(Quay quay) {
        if (quay instanceof BulkQuay) {
            BulkQuay bulkQuay = (BulkQuay) quay;
            return this.cargo == null || bulkQuay.getMaxTonnage() >= this.cargo.getTonnage();
        }
        return false;
    }

    /**
     * Checks whether the specified cargo can be loaded onto the ship.
     * <p>
     * The given cargo can only be loaded if all the following conditions are true:
     * <ol>
     *     <li>The ship does not have any cargo on board</li>
     *     <li>The cargo given is a BulkCargo</li>
     *     <li>The cargo tonnage is less than or equal to the ship's tonnage
     *     capacity</li>
     *     <li>The cargo's destination is the same as the ships origin
     *     country</li>
     * </ol>
     *
     * @param cargo cargo to be loaded
     * @return true if the Cargo satisfies the conditions else false
     * @ass1
     */
    public boolean canLoad(Cargo cargo) {
        if (this.cargo != null) {
            return false;
        }
        if (!(cargo instanceof BulkCargo)) {
            return false;
        }
        if (((BulkCargo) cargo).getTonnage() > tonnageCapacity) {
            return false;
        }
        return cargo.getDestination().equals(this.getOriginFlag());
    }

    /**
     * Loads the specified cargo onto the ship.
     *
     * @param cargo cargo to be loaded
     * @require Cargo given is able to be loaded onto this ship according to
     * {@link BulkCarrier#canLoad(Cargo)}
     * @ass1
     */
    public void loadCargo(Cargo cargo) {
        this.cargo = (BulkCargo) cargo;
    }

    /**
     * Unloads the cargo from the ship.
     * <p>
     * The ship's current cargo should be set to {@code null} at the end of the operation.
     *
     * @return the ships cargo
     * @throws NoSuchCargoException if the ship has already been unloaded
     * @ass1
     */
    public BulkCargo unloadCargo() throws NoSuchCargoException {
        if (cargo == null) {
            throw new NoSuchCargoException("Cargo has already been unloaded");
        }
        BulkCargo unload = cargo;
        cargo = null;
        return unload;
    }

    /**
     * Returns the current cargo onboard this vessel.
     *
     * @return bulk cargo on the vessel
     * @ass1
     */
    public BulkCargo getCargo() {
        return cargo;
    }

    /**
     * Returns true if and only if this BulkCarrier is equal to the other given BulkCarrier.
     * For two BulkCarriers to be equal, they must have the same name, flag,
     * origin flag, IMO number, and tonnage capacity.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        try {
            BulkCarrier bulkCarrier = (BulkCarrier) o;
            return super.equals(o) && this.tonnageCapacity == bulkCarrier.tonnageCapacity;
        } catch (ClassCastException e) {
            return false;
        }

    }

    /**
     * Returns the hash code of this BulkCarrier.
     *
     * @return hash code of this BulkCarrier.
     */
    public int hashCode() {
        return super.hashCode() + this.tonnageCapacity;
    }


    /**
     * Returns the human-readable string representation of this BulkCarrier.
     * <p>
     * The format of the string to return is
     * <pre>BulkCarrier name from origin [flag] carrying cargoType</pre>
     * Where:
     * <ul>
     *   <li>{@code name} is the name of this ship </li>
     *   <li>{@code origin} is the country of origin of this ship </li>
     *   <li>{@code flag} is the nautical flag of this ship </li>
     *   <li>{@code cargoType} is the type of cargo on board or the
     *   literal String {@code nothing} if there is no cargo currently on
     *   board</li>
     * </ul>
     * For example:
     * <pre>
     * BulkCarrier Evergreen from Australia [BRAVO] carrying OIL</pre>
     *
     * @return string representation of this BulkCarrier
     * @ass1
     */
    @Override
    public String toString() {
        String base = super.toString() + " carrying ";
        return this.cargo != null ? base + this.cargo.getType() : base
            + "nothing";
    }

    /**
     * Returns the machine-readable string representation of this BulkCarrier.
     * The format of the string to return is
     * <p>
     * BulkCarrier:imoNumber:name:origin:flag:capacity:id
     *
     * @return encoded string representation of this Ship
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(super.encode());
        encode.add(Integer.toString(this.tonnageCapacity));
        if (cargo != null) {
            encode.add(Integer.toString(cargo.getId()));
        }
        return encode.toString();
    }
}
