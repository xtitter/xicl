package ru.icl.dicewars.core;

import java.awt.Color;
import java.util.*;

import ru.icl.dicewars.client.Flag;

public class RealFullWorldGeneratorImpl implements FullWorldGenerator {

    /*** CONST ***/

    private final static Integer WORLD_Y_SIZE = 55;
    private final static Integer WORLD_X_SIZE = 68;
    private final static Integer MIN_LAND_SIZE = 55;
    private final static Integer MAX_LAND_SIZE = 68;
    private final static Integer MIN_LAND_COUNT = 28;
    private final static Integer MAX_LAND_COUNT = 32;
    private final static Integer DICE_PER_LAND = 3;
    private final static Integer MIN_DICE_PER_LAND = 1;
    private final static Integer MAX_DICE_PER_LAND = 8;

    /** CLASSES **/

    //private static Hex[][] weightenedPoints;

    /*static {
        weightenedPoints = new Hex[WORLD_X_SIZE][WORLD_Y_SIZE];
        for (int i=0; i<WORLD_X_SIZE; i++) { weightenedPoints[i] = new Hex[WORLD_Y_SIZE]; }
    }*/

   /* public Hex newWeightenedPoint(Point point) {
        if (null == weightenedPoints[point.getX()][point.getY()]) {
            weightenedPoints[point.getX()][point.getY()] = new Hex(point);
        }
        return weightenedPoints[point.getX()][point.getY()];
    }*/

    private class Hex {

        private Integer x;
        private Integer y;

        public Hex(Point point) {
            this.x = point.getX();
            this.y = point.getY();
        }

        public Hex(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }

        public Integer getX() {
            return x;
        }

        public Integer getY() {
            return y;
        }

        public int hashCode() {
            return y*WORLD_X_SIZE + x;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Hex)) { throw new ClassCastException("wrong argument for equals()"); }
            Hex point = (Hex) obj;
            return x.equals(point.x) && y.equals(point.y);
        }
    }

    private class Weightened<T> {

        private T element;
        private Integer weight;

        public Weightened(T element, Integer weight) {
            this.element = element;
            this.weight = weight;
        }

        public T getElement() {
            return element;
        }

        public Integer getWeight() {
            return weight;
        }

    }

    private class WeightenedComparator implements Comparator<Weightened<Hex>> {

        public int compare(Weightened<Hex> o1, Weightened<Hex> o2) {
            Integer w1 = o1.getWeight(); Integer w2 = o2.getWeight();
            Integer x1 = o1.getElement().getX(); Integer x2 = o2.getElement().getX();
            Integer y1 = o1.getElement().getY(); Integer y2 = o2.getElement().getY();
            if (x1.equals(x2) && y1.equals(y2)) {
                return 0;
            } else if (w1 < w2) {
                return -1;
            } else if (w1 > w2) {
                return 1;
            } else if (x1 < x2) {
                return -1;
            } else if (x1 > x2) {
                return 1;
            } else if (y1 < y2) {
                return -1;
            } else if (y1 > y2) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    /*** ATTRS ***/

    private Integer lastId;

	private int playersCount;

    private Boolean[][] empty;

    private Integer[][] ids;

	public void setPlayersCount(int playersCount) {
		this.playersCount = playersCount;
	}

    private Collection<Flag> createPlayerStack(Set<Flag> playerFlags) {
        Integer maxLandCount = Math.min(Math.round(1.0F * WORLD_X_SIZE * WORLD_Y_SIZE / MAX_LAND_SIZE), MAX_LAND_COUNT);
        Integer landCount = MIN_LAND_COUNT + random(maxLandCount - MIN_LAND_COUNT);
        Collection<Flag> result = new ArrayList<Flag>();
        Set<Flag> remains = new HashSet<Flag>();
        for (int i=0; i<landCount; i++) {
            if (remains.isEmpty()) { remains.addAll(playerFlags); }
            Flag chosen = choose(remains);
            remains.remove(chosen);
            result.add(chosen);
        }
        return result;
    }

    private <T> T choose(Set<T> elements) {
        int index = random(elements.size()); int current = 0;
        for (T element : elements) {
            if (index == current++) { return element; }
        }
        return null;
    }

    private Hex chooseWeightened(Set<Weightened<Hex>> weights) {
        Integer sum = 0;
        for (Weightened<Hex> weight : weights) { sum += weight.getWeight(); }
        int index = random(sum); int current = 0;
        for (Weightened<Hex> weight : weights) {
            current += weight.getWeight();
            if (index < current) { return weight.getElement(); }
        }
        return null;
    }

    private Integer random(Integer top) {
        return Math.round((float) Math.floor(Math.random() * top));
    }

    private Integer countFrees(Hex hex) {
        return filterFree(getBorder(hex)).size();
    }

    private Weightened<Hex> weight(Hex hex) {
        return new Weightened<Hex>(hex, 7 - countFrees(hex));
    }

    private Set<Weightened<Hex>> weight(Set<Hex> hexes) {
        Set<Weightened<Hex>> result = new HashSet<Weightened<Hex>>();
        for (Hex hex : hexes) { result.add(weight(hex)); }
        return result;
    }

    private Hex getHex(Set<Hex> border) {
        return chooseWeightened(weight(border));
    }

    private Set<Hex> getBorder(Set<Hex> hexes) {
        Set<Hex> border = new HashSet<Hex>();
        for (Hex hex : hexes) {
            border.addAll(getBorder(hex));
        }
        return border;
    }

    private Boolean checkCoords(Integer row, Integer column) {
        return row >= 0 && row < WORLD_Y_SIZE && column >=0 && column < WORLD_X_SIZE;
    }

    private Set<Hex> getBorder(Hex hex) {
        Set<Hex> border = new HashSet<Hex>();
        if (checkCoords(hex.getY(), hex.getX()-1)) { border.add(new Hex(hex.getX()-1, hex.getY())); }
        if (checkCoords(hex.getY(), hex.getX()+1)) { border.add(new Hex(hex.getX()+1, hex.getY())); }
        if (0==hex.getY()%2) {
            if (checkCoords(hex.getY()-1, hex.getX())) { border.add(new Hex(hex.getX(), hex.getY()-1)); }
            if (checkCoords(hex.getY()-1, hex.getX()+1)) { border.add(new Hex(hex.getX()+1, hex.getY()-1)); }
            if (checkCoords(hex.getY()+1, hex.getX())) { border.add(new Hex(hex.getX(), hex.getY()+1)); }
            if (checkCoords(hex.getY()+1, hex.getX()+1)) { border.add(new Hex(hex.getX()+1, hex.getY()+1)); }
        } else {
            if (checkCoords(hex.getY()-1, hex.getX())) { border.add(new Hex(hex.getX(), hex.getY()-1)); }
            if (checkCoords(hex.getY()-1, hex.getX()-1)) { border.add(new Hex(hex.getX()-1, hex.getY()-1)); }
            if (checkCoords(hex.getY()+1, hex.getX())) { border.add(new Hex(hex.getX(), hex.getY()+1)); }
            if (checkCoords(hex.getY()+1, hex.getX()-1)) { border.add(new Hex(hex.getX()-1, hex.getY()+1)); }
        }
        return border;
    }

    private void busy(Hex hex) {
        empty[hex.getY()][hex.getX()] = false;
    }

    private void busy(Set<Hex> hexes) {
        for (Hex hex : hexes) {
            busy(hex);
        }
    }

    public void empty(Hex hex) {
        empty[hex.getY()][hex.getX()] = true;
    }

    public Integer id(Hex hex) {
        return ids[hex.getY()][hex.getX()];
    }

    private void empty(Set<Hex> hexes) {
        for (Hex hex : hexes) {
            empty(hex);
        }
    }

    private Boolean isEmpty(Hex hex) {
        return empty[hex.getY()][hex.getX()];
    }

    // todo: check
    private Set<Hex> filterFree(Set<Hex> hexes) {
        Set<Hex> free = new HashSet<Hex>();
        for (Hex hex : hexes) {
            if (isEmpty(hex)) { free.add(hex); }
        }
        return free;
    }

    // todo: check
    private Set<Hex> filterBusy(Set<Hex> hexes) {
        Set<Hex> result = new HashSet<Hex>();
        for (Hex hex : hexes) {
            if (!isEmpty(hex)) { result.add(hex); }
        }
        return result;
    }

    private void processBorder(Hex hex, Set<Hex> border, Set<Hex> worldBorder) {
//        int bsize = border.size();
//        int wsize = worldBorder.size();
//        boolean bfound = contains(hex, border);
//        boolean wfound = contains(hex, worldBorder);
        border.remove(hex);
        worldBorder.remove(hex);
//        if (bfound && bsize == border.size()) { throw new IllegalStateException(); }
//        if (wfound && wsize == worldBorder.size()) { throw new IllegalStateException(); }
        Set<Hex> hexes = filterFree(getBorder(hex));
        border.addAll(hexes);
        worldBorder.addAll(hexes);
//        for (Hex h : hexes) {
//            if (!contains(h, border)) {
//                border.add(h);
//            }
//        }
//        for (Hex h : hexes) {
//            if (!contains(h, worldBorder)) {
//                worldBorder.add(h);
//            }
//        }
    }

    private Integer getLandSize() {
        // todo: more sophisticated method
        return MIN_LAND_SIZE + random(MAX_LAND_SIZE - MIN_LAND_SIZE);
    }

    private Set<Hex> _getLandHexes(Set<Hex> worldBorder) {
        Integer size = getLandSize();
        Set<Hex> landHexes = new HashSet<Hex>();
        Set<Hex> border = new HashSet<Hex>();
        Hex initialHex = getHex(worldBorder);
        if (null == initialHex) { return null; }
        border.add(initialHex);
        Hex newHex = getHex(border);
        for (int i=0; i<size && null != newHex; i++) {
            busy(newHex);
            processBorder(newHex, border, worldBorder);
            landHexes.add(newHex);
            newHex = getHex(border);
        }
        if (landHexes.size() < MIN_LAND_SIZE) {
            empty(landHexes);
            return null;
        }
        return landHexes;
    }

    private Set<Hex> getLandPoints(Set<Hex> worldBorder) {
        Set<Hex> landHexes = null;
        Set<Hex> _worldBorder = new HashSet<Hex>();
        while (null == landHexes) {
            _worldBorder.clear();
            _worldBorder.addAll(worldBorder);
            landHexes = _getLandHexes(_worldBorder);
        }
        worldBorder.clear();
        worldBorder.addAll(_worldBorder);
//        for (Hex h : landHexes) {
//            if (contains(h, worldBorder)) { throw  new IllegalStateException(); }
//        }
//        for (Hex h : worldBorder) {
//            if (0 != id(h)) { throw  new IllegalStateException(); }
//        }
        return landHexes;
    }

    private Hex hex(Point point) {
        return new Hex(point.getX(), point.getY());
    }

    private Set<Hex> hexes(Set<Point> points) {
        Set<Hex> result = new HashSet<Hex>();
        for (Point point : points) { result.add(hex(point)); }
        return result;
    }

    private Point transform(Hex hex) {
        return new Point(hex.getX(), hex.getY());
    }

    private Set<Point> transform(Set<Hex> hexes) {
        Set<Point> result = new HashSet<Point>();
        for (Hex hex : hexes) { result.add(transform(hex)); }
        return result;
    }

    private void color(FullLand land, Hex hex) {
        if (0 != id(hex)) { throw new IllegalArgumentException(); }
        land.getPoints().add(transform(hex));
        ids[hex.getY()][hex.getX()] = land.getLandId();
        busy(hex);
    }

    private void color(FullLand land, Set<Hex> hexes) {
        for (Hex hex : hexes) { color(land, hex); }
    }

    private FullLand createNewLand(Flag currentColor, Set<Hex> worldBorder) {
        FullLand land = new FullLandImpl(lastId + 1);
        land.setFlag(currentColor);
        color(land, getLandPoints(worldBorder));
        lastId++;
        return land;
    }

    private Hex getRandomHex() {
        return new Hex(random(WORLD_X_SIZE), random(WORLD_Y_SIZE));
    }

    private void init() {
        // lastId
        lastId = 0;
        // init empties
        if (null == empty) { empty = new Boolean[WORLD_Y_SIZE][WORLD_X_SIZE]; }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            if (null == empty[row]) { empty[row] = new Boolean[WORLD_X_SIZE]; }
            for (int column=0; column<WORLD_X_SIZE; column++) {
                empty[row][column] = true;
            }
        }
        // init ids
        if (null == ids) { ids = new Integer[WORLD_Y_SIZE][WORLD_X_SIZE]; }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            if (null == ids[row]) { ids[row] = new Integer[WORLD_X_SIZE]; }
            for (int column=0; column<WORLD_X_SIZE; column++) {
                ids[row][column] = 0;
            }
        }
    }

    private void eliminateHoles(Set<FullLand> lands, Set<Hex> worldBorder) {
        SortedSet<Weightened<Hex>> heap = new TreeSet<Weightened<Hex>>(new WeightenedComparator());
        for (Hex hex : worldBorder) { heap.add(weight(hex)); }
        while (!heap.isEmpty() && heap.last().getWeight() > 4) {
            Weightened<Hex> weightened = heap.last();
            heap.remove(weightened);
            if (0 != id(weightened.getElement())) { continue; }
            // select one of lands
            Set<Hex> neighbours = filterBusy(getBorder(weightened.getElement()));
            Hex hex = choose(neighbours);
            color(getOwnerLand(hex, lands), weightened.getElement());
            neighbours = filterFree(getBorder(weightened.getElement()));
            for (Hex neighbour : neighbours) {
                weightened = weight(neighbour);
                if (0 != id(weightened.getElement())) { throw new IllegalStateException(); }
                if (heap.contains(weightened)) {
                    heap.remove(weightened);
                    heap.add(weightened);
                } else {
                    heap.add(weightened);
                }
            }
        }
    }

    private Set<FullLand> createLands(Set<Flag> playerFlags) {
        // init
        init();
        // set of all lands
        Set<FullLand> lands = new HashSet<FullLand>();
        // get all land colors are to place into the world
        Collection<Flag> playerStack = createPlayerStack(playerFlags);
        // world border
        Set<Hex> worldBorder = new HashSet<Hex>();
        // initial position
        worldBorder.add(getRandomHex());
        // place lands into world one by one
        for (Flag current : playerStack) {
            lands.add(createNewLand(current, worldBorder));
        }
        // fill holes
        eliminateHoles(lands, worldBorder);
        // output
        output(lands);
        // return!
        return lands;
    }

    private FullLand getOwnerLand(Hex hex, Set<FullLand> lands) {
        for (FullLand land : lands) {
            if (land.getLandId() == id(hex)) {
                return land;
            }
        }
        return null;
    }

    private Boolean contains(Hex hex, Set<Hex> elements) {
        for (Hex element : elements) {
            if (element.getY().equals(hex.getY()) && element.getX().equals(hex.getX())) {
                return true;
            }
        }
        return false;
    }

    private Set<FullLand> getNeighboursFrom(FullLand land, Set<FullLand> lands) {
        Set<FullLand> neighbours = new HashSet<FullLand>();
        Set<Hex> landHexes = hexes(land.getPoints());
        Set<Hex> borderHexes = getBorder(landHexes);
        for (Hex borderHex : borderHexes) {
            FullLand neighbour = getOwnerLand(borderHex, lands);
            if (null != neighbour && neighbour.getLandId() != land.getLandId()) {
                neighbours.add(neighbour);
            }
        }
        return neighbours;
    }

    private void processNeighbours(Set<FullLand> lands) {
        for (FullLand land : lands) {
            land.setNeighbouringLands(getNeighboursFrom(land, lands));
        }
    }

    private Set<Flag> createPlayerFlags() {
        Flag[] flags = Flag.values();
        Set<Flag> playerFlags = new HashSet<Flag>();
        playerFlags.addAll(Arrays.asList(flags).subList(0, playersCount));
        return playerFlags;
    }

    //Mkamalov: Client interface should be clear to end user.
	private char getChar(Flag f) {
		switch (f) {
		case WHITE:
			return 'W';
		case BLUE:
			return 'B';
		case CYAN:
			return 'C';
		case GREEN:
			return 'G';
		case MAGENTA:
			return 'M';
		case ORANGE:
			return 'O';
		case RED:
			return 'R';
		case BROWN:
			return 'B';
		case CHARTREUSE:
			return 'T';
		case GRAY:
			return 'A';
		case YELLOW:
			return 'Y';
		}
		throw new IllegalStateException();
	}
    
    private void output(Set<FullLand> lands) {
        Set<Hex> border = new HashSet<Hex>();
        for (FullLand land : lands) { border.addAll(getBorder(hexes(land.getPoints()))); }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            for (int column=0; column<WORLD_Y_SIZE; column++) {
                Hex current = new Hex(column, row);
                FullLand owner = getOwnerLand(current, lands);
                System.out.print(null != owner ? getChar(owner.getFlag()) : (contains(current, border) ? '.' : ' '));
            }
            System.out.println();
        }
    }

    private Integer countLands(Flag player, Set<FullLand> lands) {
        Integer count = 0;
        for (FullLand land : lands) {
            count += land.getFlag() == player ? 1 : 0;
        }
        return count;
    }

    private void processDices(Set<FullLand> lands, Set<Flag> players) {
        for (Flag player : players) {
            Integer left = countLands(player, lands);
            Integer dices = left * DICE_PER_LAND;
            for (FullLand land : lands) {
                if (land.getFlag() == player) {
                    Integer amount = MIN_DICE_PER_LAND + Math.min(dices - MIN_DICE_PER_LAND * left, random(MAX_DICE_PER_LAND - MIN_DICE_PER_LAND));
                    land.setDiceCount(DiceStack.valueOf(amount));
                    dices -= amount;
                    left--;
                }
            }
        }
    }

    private void validate(Set<FullLand> lands) {
        for (FullLand land : lands) {
            Set<FullLand> neighbours = land.getNeighbouringFullLands();
            for (FullLand neighbour : neighbours) {
                if (!neighbour.getNeighbouringFullLands().contains(land)) {
                    throw new IllegalStateException("Not bidirectional link");
                }
            }
        }
    }

	@Override
    public FullWorld generate() {
        // all players
        Set<Flag> playerFlags = createPlayerFlags();
        // all lands
        Set<FullLand> lands = createLands(playerFlags);
        // all neighbours
        processNeighbours(lands);
        // all dices
        processDices(lands, playerFlags);
        // validation
        validate(lands);
        // and world!
        return new FullWorldImpl(0, lands, playerFlags, new HashMap<Flag, Integer>());
    }

}