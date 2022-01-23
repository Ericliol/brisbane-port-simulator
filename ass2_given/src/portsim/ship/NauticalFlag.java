package portsim.ship;

/**
 * Stores information about particular nautical flags flown by ships.
 * <p>
 * Maritime flags are used internationally to communicate what is happening
 * onboard a ship. Only a selection have been used below.
 * <p>
 * For more information:
 * <a target="_blank" href="http://www.marinewaypoints.com/learn/flags/flags.shtml">
 * http://www.marinewaypoints.com/learn/flags/flags.shtml</a>
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
public enum NauticalFlag {
    /**
     * The ship is carrying dangerous cargo.
     */
    BRAVO,

    /**
     * The ship is ready to be docked.
     */
    HOTEL,

    /**
     * Ship requires medical assistance.
     */
    WHISKEY,

    /**
     * Default flag where ship has no specific status.
     */
    NOVEMBER
}
