package portsim.port;

import javafx.util.Pair;
import jdk.jshell.EvalException;
import portsim.cargo.*;
import portsim.evaluators.CargoDecompositionEvaluator;
import portsim.evaluators.QuayOccupancyEvaluator;
import portsim.evaluators.ShipFlagEvaluator;
import portsim.evaluators.StatisticsEvaluator;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;
import portsim.util.Tickable;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * A place where ships can come and dock with Quays to load / unload their
 * cargo.
 * <p>
 * Ships can enter a port through its queue. Cargo is stored within the port at warehouses.
 *
 * @ass1_partial
 */
public class Port implements Tickable, Encodable {

    /**
     * The name of this port used for identification
     */
    private String name;
    /**
     * number of minutes since simulation started
     */
    private long time;
    /**
     * ships waiting to enter the port
     */
    private ShipQueue shipQueue;

    /**
     * to store movements ordered by the time of the movement.
     */
    private PriorityQueue<Movement> priorityQueue;

    /**
     * The quays associated with this port
     */
    private List<Quay> quays;
    /**
     * The cargo currently stored at the port at warehouses. Cargo unloaded from trucks / ships
     */
    private List<Cargo> storedCargo;

    /**
     * list of statistics evaluator
     */
    private List<StatisticsEvaluator> statisticsEvaluatorList;



    /**
     * Creates a new port with the given name.
     * <p>
     * The time since the simulation was started should be initialised as 0.
     * <p>
     * The list of quays in the port, stored cargo (warehouses) and statistics evaluators should be
     * initialised as empty lists.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised
     * to store movements ordered by the time of the movement (see {@link Movement#getTime()}).
     *
     * @param name name of the port
     * @ass1_partial
     */
    public Port(String name) {
        final Comparator<Movement> movementComparator = new Comparator<Movement>() {
            /**
             * A custom comparator that compares two Movements by the time
             *
             * @param o1 Movement 1 to compares
             * @param o2 Movement 2 to compares
             * @return the time differ
             */
            @Override
            public int compare(Movement o1, Movement o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        };
        this.name = name;
        this.time = 0;
        this.shipQueue = new ShipQueue();
        this.priorityQueue = new PriorityQueue<>(movementComparator);
        this.quays = new ArrayList<Quay>();
        this.storedCargo = new ArrayList<Cargo>();
        this.statisticsEvaluatorList = new ArrayList<StatisticsEvaluator>();

    }

    /**
     * Creates a new port with the given name, time elapsed, ship queue, quays and stored cargo.
     * The list of statistics evaluators should be initialised as an empty list.
     *
     * @param name name of the port
     * @param time number of minutes since simulation started
     * @param shipQueue ships waiting to enter the port
     * @param quays the port's quays
     * @param storedCargo the cargo stored at the port
     * @throws IllegalArgumentException if time < 0
     */
    public Port(String name,
                 long time,
                 ShipQueue shipQueue,
                 List<Quay> quays,
                 List<Cargo> storedCargo)
            throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException();
        }
        final Comparator<Movement> movementComparator = new Comparator<>() {
            /**
             * A custom comparator that compares two Movements by the time
             *
             * @param o1 Movement 1 to compares
             * @param o2 Movement 2 to compares
             * @return the time differ
             */
            @Override
            public int compare(Movement o1, Movement o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        };
        this.name = name;
        this.time = time;
        this.shipQueue = shipQueue;
        this.priorityQueue = new PriorityQueue<>(movementComparator);
        this.quays = quays;
        this.storedCargo = storedCargo;
        this.statisticsEvaluatorList = new ArrayList<StatisticsEvaluator>();

    }

    /**
     * Adds a movement to the PriorityQueue of movements.
     *
     * @param movement movement to add
     * @throws IllegalArgumentException  movement's action time is less than the
     *          current number of minutes elapsed
     */
    public void addMovement(Movement movement) throws IllegalArgumentException {
        if (movement.getTime() >= this.time) {
            this.priorityQueue.add(movement);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Processes a movement.
     * The action taken depends on the type of movement to be processed.
     *
     * If the movement is a ShipMovement:
     *      If the movement direction is INBOUND then the ship should be added to the ship queue.
     *      If the movement direction is OUTBOUND then any cargo stored in the port whose
     *      destination
     *          is the ship's origin port should be added to the ship according to
     *          Ship.canLoad(Cargo).
     *          Next, the ship should be removed from the quay it is currently docked in (if any).
     *
     * If the movement is a CargoMovement:
     *      If the movement direction is INBOUND then all of the cargo that is being moved
     *      should be added to the port's stored cargo.
     *      If the movement direction is OUTBOUND then all cargo with the given IDs should
     *      be removed from the port's stored cargo.
     *
     * @param movement movement to execute
     */
    public void processMovement(Movement movement) {
        if (movement.getTime() < this.time) {
            return;
        }
        if (movement instanceof ShipMovement) {
            ShipMovement shipMovement = (ShipMovement) movement;
            if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                this.shipQueue.add(shipMovement.getShip());
            } else if (movement.getDirection().equals(MovementDirection.OUTBOUND)) {
                Ship ship = shipMovement.getShip();
                for (Cargo cargo : this.storedCargo) {
                    if (ship.canLoad(cargo)) {
                        ship.loadCargo(cargo);
                    }
                }
                for (Quay quay : this.quays) {
                    if (quay.getShip().equals(ship)) {
                        quay.shipDeparts();
                        break;
                    }
                }
            }
        } else if (movement instanceof CargoMovement) {
            CargoMovement cargoMovement = (CargoMovement) movement;
            if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                this.storedCargo.addAll(cargoMovement.getCargo());
            } else {
                this.storedCargo.removeIf(cargo ->
                        cargoMovement.encode().contains(Integer.toString(cargo.getId())));
            }
        }
        for (StatisticsEvaluator eval : statisticsEvaluatorList) {
            eval.onProcessMovement(movement);
        }
    }

    /**
     * Adds the given statistics evaluator to the port's list of evaluators.
     * If the port already has an evaluator of that type, no action should be taken.
     *
     * @param eval statistics evaluator to add to the port
     */
    public void addStatisticsEvaluator(StatisticsEvaluator eval) {
        if (!this.statisticsEvaluatorList.stream().anyMatch(
                e -> e.getClass() == eval.getClass())) {
            this.statisticsEvaluatorList.add(eval);
        }
    }

    /**
     * Returns the name of this port.
     *
     * @return port's name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the time since simulation started.
     *
     * @return time in minutes
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns a list of all quays associated with this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     * <p>
     * The order in which quays appear in this list should be the same as
     * the order in which they were added by calling {@link #addQuay(Quay)}.
     *
     * @return all quays
     * @ass1
     */
    public List<Quay> getQuays() {
        return new ArrayList<>(this.quays);
    }

    /**
     * Returns the cargo stored in warehouses at this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return port cargo
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(this.storedCargo);
    }

    /**
     * Returns the queue of ships waiting to be docked at this port.
     *
     * @return port's queue of ships
     */
    public ShipQueue getShipQueue() {
        return this.shipQueue;
    }

    /**
     * Returns the queue of movements waiting to be processed.
     *
     * @return movements queue
     */
    public PriorityQueue<Movement> getMovements() {
        return this.priorityQueue;
    }

    /**
     * Returns the list of evaluators at the port.
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return the ports evaluators
     */
    public List<StatisticsEvaluator> getEvaluators() {
        return new ArrayList<StatisticsEvaluator>(statisticsEvaluatorList);
    }

    /**
     * Adds a quay to the ports control.
     *
     * @param quay the quay to add
     * @ass1
     */
    public void addQuay(Quay quay) {
        this.quays.add(quay);
    }

    /**
     * Simulate a minute passing. The time since the port was created should
     * be incremented by one.
     * On each call to elapseOneMinute(), the following actions should be completed by
     *      the port in order:
     *
     * Advance the simulation time by 1
     * 1. If the time is a multiple of 10, attempt to bring a ship from the ship queue to any empty
     *      quay that matches the requirements from Ship.canDock(Quay). The ship should
     *      only be docked to one quay.
     * 2. If the time is a multiple of 5, all quays must unload the cargo from ships docked
     *      (if any) and add it to warehouses at the port (the Port's list of stored cargo)
     * 3. All movements stored in the queue whose action time is equal to the current time should
     *      be processed by processMovement(Movement)
     * 4. Call StatisticsEvaluator.elapseOneMinute() on all statistics evaluators
     */
    public void elapseOneMinute() {
        time += 1;
        if (getTime() % 10 == 0) {
            Ship ship = this.shipQueue.peek();
            for (Quay quay : this.quays) {
                if (ship.canDock(quay) && quay.isEmpty()) {
                    quay.shipArrives(ship);
                    this.shipQueue.poll();
                }
            }
        }
        if (getTime() % 5 == 0) {
            for (Quay quay : this.quays) {
                if (!quay.isEmpty()) {
                    Ship ship = quay.getShip();
                    if (ship instanceof ContainerShip) {
                        ContainerShip containerShip = (ContainerShip) ship;
                        try {
                            this.storedCargo.addAll(containerShip.unloadCargo());
                        } catch (NoSuchCargoException ignored) {
                            continue;
                        }
                    } else if (ship instanceof BulkCarrier) {
                        BulkCarrier bulkCarrier = (BulkCarrier) ship;
                        try {
                            this.storedCargo.add(bulkCarrier.unloadCargo());
                        } catch (NoSuchCargoException ignored) {
                            continue;
                        }
                    }
                }
            }
        }
        for (Movement movement : this.priorityQueue) {
            if (movement.getTime() == this.getTime()) {
                this.processMovement(movement);
            }
        }
        for (StatisticsEvaluator statisticsEvaluator : this.statisticsEvaluatorList) {
            statisticsEvaluator.elapseOneMinute();
        }
    }

    /**
     * Returns the machine-readable string representation of this Port.
     * The format of the string to return is
     *
     *  Name
     *  Time
     *  numCargo
     *  EncodedCargo
     *  EncodedCargo...
     *  numShips
     *  EncodedShip
     *  EncodedShip...
     *  numQuays
     *  EncodedQuay
     *  EncodedQuay...
     *  ShipQueue:numShipsInQueue:shipID,shipID,...
     *  StoredCargo:numCargo:cargoID,cargoID,...
     *  Movements:numMovements
     *  EncodedMovement
     *  EncodedMovement...
     *  Evaluators:numEvaluators:EvaluatorSimpleName,EvaluatorSimpleName,...
     *
     * @return encoded string representation of this port
     */
    public String encode() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add(getName());
        joiner.add(Long.toString(getTime()));
        joiner.add(Integer.toString(Cargo.getCargoRegistry().size()));
        for (Cargo cargo : Cargo.getCargoRegistry().values()) {
            joiner.add(cargo.encode());
        }
        joiner.add(Integer.toString(Ship.getShipRegistry().size()));
        for (Ship ship : Ship.getShipRegistry().values()) {
            joiner.add(ship.encode());
        }
        joiner.add(Integer.toString(getQuays().size()));
        for (Quay quay : getQuays()) {
            joiner.add(quay.encode());
        }
        joiner.add(getShipQueue().encode());

        StringJoiner storedCargoJoiner = new StringJoiner(":");
        storedCargoJoiner.add("StoredCargo");
        storedCargoJoiner.add(Integer.toString(getCargo().size()));
        StringJoiner cargoIds = new StringJoiner(",");
        for (Cargo cargo : getCargo()) {
            cargoIds.add(Integer.toString(cargo.getId()));
        }
        storedCargoJoiner.add(cargoIds.toString());
        joiner.add(storedCargoJoiner.toString());

        StringJoiner movementJoiner = new StringJoiner(":");
        movementJoiner.add("Movements");
        movementJoiner.add(Integer.toString(getMovements().size()));
        joiner.add(movementJoiner.toString());
        for (Movement movement : getMovements()) {
            joiner.add(movement.encode());
        }
        StringJoiner evaluatorJoiner = new StringJoiner(":");
        evaluatorJoiner.add("Evaluators");
        evaluatorJoiner.add(Integer.toString(getEvaluators().size()));
        StringJoiner evaluatorNames = new StringJoiner(",");
        for (StatisticsEvaluator evaluator : getEvaluators()) {
            evaluatorNames.add(evaluator.getClass().getSimpleName());
        }
        evaluatorJoiner.add(evaluatorNames.toString());
        joiner.add(evaluatorJoiner.toString());
        return joiner.toString();
    }

    /**
     * The encoded string is invalid if any of the following conditions are true:
     *
     * The time is not a valid long (i.e. cannot be parsed by Long.parseLong(String)).
     * The number of cargo is not an integer (i.e. cannot
     *      be parsed by Integer.parseInt(String)).
     * The number of cargo to be read in does not match the number
     *      specified above. (ie. too many / few encoded cargo following the number)
     * An encoded cargo line throws a BadEncodingException
     * The number of ships is not an integer (i.e. cannot be parsed
     *      by Integer.parseInt(String)).
     * The number of ship to be read in does not match the number specified above.
     *      (ie. too many / few encoded ships following the number)
     * An encoded ship line throws a BadEncodingException
     * The number of quays is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of quays to be read in does not match the number specified above.
     *      (ie. too many / few encoded quays following the number)
     * An encoded quay line throws a BadEncodingException
     * The shipQueue does not follow the last encoded quay
     * The number of ships in the shipQueue is not an integer (i.e. cannot be parsed by
     *      Integer.parseInt(String)).
     * The imoNumber of the ships in the shipQueue are not valid longs. (i.e. cannot be
     *      parsed by Long.parseLong(String)).
     * Any imoNumber read does not correspond to a valid ship in the simulation
     * The storedCargo does not follow the encoded shipQueue
     * The number of cargo in the storedCargo is not an integer (i.e. cannot be parsed
     *      by Integer.parseInt(String)).
     * The id of the cargo in the storedCargo are not valid Integers. (i.e. cannot be
     *      parsed by Integer.parseInt(String)).
     * Any cargo id read does not correspond to a valid cargo in the simulation
     * The movements do not follow the encoded storedCargo
     * The number of movements is not an integer (i.e. cannot be parsed
     *      by Integer.parseInt(String)).
     * The number of movements to be read in does not match the number specified above.
     *      (ie. too many / few encoded movements following the number)
     * An encoded movement line throws a BadEncodingException
     * The evaluators do not follow the encoded movements
     * The number of evaluators is not an integer (i.e. cannot be parsed by
     *      Integer.parseInt(String)).
     * The number of evaluators to be read in does not match the number specified above.
     *      (ie. too many / few encoded evaluators following the number)
     * An encoded evaluator name does not match any of the possible evaluator classes
     * If any of the following lines are missing:
     *          Name
     *          Time
     *          Number of Cargo
     *          Number of Ships
     *          Number of Quays
     *          ShipQueue
     *          StoredCargo
     *          Movements
     *          Evaluators
     *
     * @param reader reader from which to load all info
     * @return port created by reading from given reader
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws BadEncodingException if the reader reads a line that does not adhere to the
     *              rules above indicating that the contents of the reader are invalid
     */
    public static Port initialisePort(Reader reader)
            throws IOException, BadEncodingException {
        BufferedReader portInfo = new BufferedReader(reader);
        List<Quay> quays = new ArrayList<>();
        List<Cargo> storedCargos = new ArrayList<>();
        List<Movement> movements = new ArrayList<>();
        List<StatisticsEvaluator> evaluatorList = new ArrayList<>();
        long portTime;
        final String portName = portInfo.readLine();
        try {
            portTime = Long.parseLong(portInfo.readLine());
        } catch (NumberFormatException e) {
            throw new BadEncodingException(e);
        }
        portListInitialise(portInfo, 1);
        portListInitialise(portInfo, 2);

        try {
            int numQuays = Integer.parseInt(portInfo.readLine());
            for (int i = 0; i < numQuays; i++) {
                quays.add(Quay.fromString(portInfo.readLine()));
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException(e);
        }
        final ShipQueue portShipQueue = ShipQueue.fromString(portInfo.readLine());
        String[] storedCargoInfo = portInfo.readLine().split(":");
        if (!storedCargoInfo[0].equals("StoredCargo")) {
            throw new BadEncodingException();
        }
        try {
            int numCargoStored = Integer.parseInt(storedCargoInfo[1]);
            if (numCargoStored > 0) {
                String[] idStings = storedCargoInfo[2].split(",");
                if (idStings.length != numCargoStored) {
                    throw new BadEncodingException();
                }
                for (String idSting : idStings) {
                    int id = Integer.parseInt(idSting);
                    storedCargos.add(Cargo.getCargoById(id));
                }
            }
        } catch (NumberFormatException | NoSuchCargoException e) {
            throw new BadEncodingException(e);
        }
        String[] movementInfo = portInfo.readLine().split(":");
        if (!movementInfo[0].equals("Movements")) {
            throw new BadEncodingException();
        }
        try {
            int numMovement = Integer.parseInt(movementInfo[1]);
            for (int i = 0; i < numMovement; i++) {
                String movementDetail = portInfo.readLine();
                if (movementDetail.split(":")[0].equals("ShipMovement")) {
                    movements.add(ShipMovement.fromString(movementDetail));
                } else if (movementDetail.split(":")[0].equals("CargoMovement")) {
                    movements.add(CargoMovement.fromString(movementDetail));
                } else {
                    throw new BadEncodingException();
                }
            }
        } catch (NumberFormatException e) {
            throw new BadEncodingException(e);
        }
        String[] evaluatorInfo = portInfo.readLine().split(":");
        if (!evaluatorInfo[0].equals("Evaluators")) {
            throw new BadEncodingException();
        }
        Port port = new Port(portName, portTime, portShipQueue, quays, storedCargos);
        try {
            int numEval = Integer.parseInt(evaluatorInfo[1]);
            if (numEval > 0) {
                String[] evalNameList = evaluatorInfo[2].split(",");
                if (evalNameList.length != numEval) {
                    throw new BadEncodingException();
                }
                for (String evalName : evalNameList) {
                    Class<?> clazz = Class.forName("portsim.evaluators." + evalName);
                    if (evalName.equals("QuayOccupancyEvaluator")) {
                        Constructor<?> constructor = clazz.getConstructor(Port.class);
                        evaluatorList.add((StatisticsEvaluator) constructor.newInstance(port));
                    } else {
                        evaluatorList.add((StatisticsEvaluator) clazz.newInstance());
                    }
                }
            }
        } catch (NumberFormatException | ClassNotFoundException | InstantiationException
                | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new BadEncodingException(e);
        }
        movements.forEach(port::addMovement);
        evaluatorList.forEach(port::addStatisticsEvaluator);
        return port;
    }

    /**
     * initialise ship and cargo for the port simulations
     *
     * @param portInfo BufferedReader contains port information
     * @param select selector to initialise ship or cargo
     * @throws BadEncodingException if the reader reads a line that does not adhere to the
     *              rules above indicating that the contents of the reader are invalid
     */
    private static void portListInitialise(
            BufferedReader portInfo, Integer select) throws BadEncodingException {
        if (select == 1) {
            try {
                int numCargo = Integer.parseInt(portInfo.readLine());
                for (int i = 0; i < numCargo; i++) {
                    Cargo.fromString(portInfo.readLine());

                }
            } catch (NumberFormatException | IOException e) {
                throw new BadEncodingException(e);
            }
        } else if (select == 2) {
            try {
                int numShips = Integer.parseInt(portInfo.readLine());
                for (int i = 0; i < numShips; i++) {
                    Ship.fromString(portInfo.readLine());

                }
            } catch (NumberFormatException | IOException e) {
                throw new BadEncodingException(e);
            }
        }
    }

}
