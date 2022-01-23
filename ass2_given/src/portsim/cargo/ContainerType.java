package portsim.cargo;

/**
 * Represents the possible types of Containers a ship can carry.
 * <p>
 * <span style="font-size: 1.2em;">
 * <b>NOTE:</b> You do <b>not</b> need to implement the {@code values()} or
 * {@code valueOf(String)} methods as part of the assignment.
 * Their implementations are generated automatically by the compiler.
 * Also, you do <b>not</b> need to implement the {@code Serializable} or {@code Comparable}
 * interfaces, or extend {@code Enum}.
 * </span>
 *
 * @ass1
 */
public enum ContainerType {
    /**
     * A large standardized shipping container, designed and built for
     * intermodal freight transport.
     *
     * @ass1
     */
    STANDARD,
    /**
     * Open top shipping containers have the goods are loaded in through the
     * top by crane or other top loading machinery.
     *
     * @ass1
     */
    OPEN_TOP,
    /**
     * Reefer containers are big fridges that are used to transport
     * temperature controlled cargoes such as fruits, meat, fish, seafood.
     *
     * @ass1
     */
    REEFER,
    /**
     * Tank containers can be used for food grade liquid storage and transport.
     *
     * @ass1
     */
    TANKER,
    /**
     * Another form of shipping container that does not fit into other
     * categories.
     *
     * @ass1
     */
    OTHER
}
