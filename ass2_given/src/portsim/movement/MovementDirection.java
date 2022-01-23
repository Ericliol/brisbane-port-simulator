package portsim.movement;

/**
 * Represents the possible directions a ship or cargo can be moving.
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
public enum MovementDirection {
    /**
     * Any cargo / ships that are coming to the port are considered inbound.
     */
    INBOUND,
    /**
     * Any cargo / ships that are moving away from the port are considered
     * outbound.
     */
    OUTBOUND
}
