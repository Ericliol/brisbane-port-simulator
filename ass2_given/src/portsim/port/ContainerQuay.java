package portsim.port;

import java.util.StringJoiner;

/**
 * A Container Quay is a type of quay specifically designed for the unloading of
 * Container Ship vessels.
 *
 * @ass1_partial
 */
public class ContainerQuay extends Quay {
    /**
     * The maximum number of containers that the quay can handle
     */
    private int maxContainers;

    /**
     * Creates a new Container Quay with the given ID and maximum number of containers.
     *
     * @param id            quay ID
     * @param maxContainers maximum number of containers the quay can handle
     * @throws IllegalArgumentException if ID or maxContainers &lt; 0
     * @ass1
     */
    public ContainerQuay(int id, int maxContainers) throws IllegalArgumentException {
        super(id);
        if (maxContainers < 0) {
            throw new IllegalArgumentException("maxContainers must be greater than"
                + " or equal to 0: " + maxContainers);
        }
        this.maxContainers = maxContainers;
    }

    /**
     * Returns the maximum number of containers of this quay can process at once.
     *
     * @return maxContainers
     * @ass1
     */
    public int getMaxContainers() {
        return maxContainers;
    }

    /**
     * Returns true if and only if this ContainerQuay is equal to the other given ContainerQuay.
     * For two ContainerQuays to be equal, they must have the same ID, ship docked status
     * (must either both be empty or both be occupied) and same container capacity.
     *
     * @param o other object to check equality
     * @return true if equal, false otherwise
     */
    public boolean equals(Object o) {
        try {
            ContainerQuay containerQuay = (ContainerQuay) o;
            return super.equals(o) && getMaxContainers() == containerQuay.getMaxContainers();
        } catch (ClassCastException e) {
            return false;
        }

    }

    /**
     * Returns the hash code of this ContainerQuay.
     * Two ContainerQuays that are equal according to equals(Object) should have the same hash code.
     *
     * @return hash code of this quay.
     */
    public int hashCode() {
        return super.hashCode() + getMaxContainers();
    }

    /**
     * Returns the human-readable string representation of this ContainerQuay.
     * <p>
     * The format of the string to return is
     * <pre>ContainerQuay id [Ship: imoNumber] - maxContainers</pre>
     * Where:
     * <ul>
     * <li>{@code id} is the ID of this quay</li>
     * <li>{@code imoNumber} is the IMO number of the ship docked at this
     * quay, or {@code None} if the quay is unoccupied.</li>
     * <li>{@code maxContainers} is the number of containers this quay can
     * take.</li>
     * </ul>
     * <p>
     * For example: <pre>ContainerQuay 3 [Ship: 2125622] - 32</pre>
     *
     * @return string representation of this ContainerQuay
     * @ass1
     */
    @Override
    public String toString() {
        return super.toString() + " - " + this.maxContainers;
    }

    /**
     * Returns the machine-readable string representation of this ContainerQuay.
     * The format of the string to return is
     *
     * ContainerQuay:id:imoNumber:maxContainers
     *
     * @return encoded string representation of this quay
     */
    public String encode() {
        StringJoiner encode = new StringJoiner(":");
        encode.add(super.encode());
        encode.add(Integer.toString(getMaxContainers()));
        return encode.toString();
    }

}
