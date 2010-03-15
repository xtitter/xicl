package ru.icl.dicewars.core;

import java.util.*;

import ru.icl.dicewars.client.Flag;

public class RealFullWorldGeneratorImpl implements FullWorldGenerator {

    // do not change these values!
    private final static int COLOR_SELECTION_LIMIT = 100;
    private final static int HEXES_TRY_CREATION_LIMIT = 5;
    private final static int LAND_TRY_CREATION_LIMIT = 5;

    /*** CONST ***/

    private final static int WORLD_Y_SIZE = 53;
    private final static int WORLD_X_SIZE = 68;
    private final static int MIN_LAND_SIZE = 55;
    private final static int MAX_LAND_SIZE = 68;
    private final static int MIN_LAND_COUNT = 40;
    private final static int MAX_LAND_COUNT = 50;
    private final static int DICE_PER_LAND = 2;
    private final static int MIN_DICE_PER_LAND = 1;
    private final static int MAX_DICE_PER_LAND = 8;
    private final static int HOLES_COUNT = 5;
    private final static int HOLE_SIZE = 100;
    private final static int TENTACLE_WIDTH = 4;

    private static class Hex {
    	private static final Hex[][] cache = new Hex[WORLD_X_SIZE+1][WORLD_Y_SIZE+1]; 
    	
        private int x;
        private int y;
        private volatile int hashCode = 0;

        private Hex(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        private static Hex getHex(int x, int y){
        	/*if (x < 0 || y < 0 || x > WORLD_X_SIZE || y > WORLD_Y_SIZE){
        		throw new IllegalArgumentException();
        	}*/
        	
        	if (cache[x][y] == null){
        		cache[x][y] = new Hex(x, y);
        	}
        	return cache[x][y];
        }

        private int getX() {
            return x;
        }

        private int getY() {
            return y;
        }

        public int hashCode() {
        	int result = hashCode;
            if (result == 0) {
                 result = 17;
                 result = 31 * result + x;
                 result = 31 * result + y;
                 hashCode = result;
            }
            return result;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Hex)) return false;
            Hex point = (Hex) obj;
            return x == point.x && y == point.y;
        }
    }

    private class Weightened<T> {

        private T element;
        private int weight;

        public Weightened(T element, int weight) {
            this.element = element;
            this.weight = weight;
        }

        public T getElement() {
            return element;
        }

        public int getWeight() {
            return weight;
        }

    }

    private class WeightenedComparator implements Comparator<Weightened<Hex>> {

        public int compare(Weightened<Hex> o1, Weightened<Hex> o2) {
            int w1 = o1.getWeight(); int w2 = o2.getWeight();
            int x1 = o1.getElement().x; int x2 = o2.getElement().x;
            int y1 = o1.getElement().y; int y2 = o2.getElement().y;
            if (x1 == x2 && y1 == y2) {
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

    private class CheeseMaker {

        private Boolean[][] cheese;

        public CheeseMaker() {
            // init cheese
            if (null == cheese) { cheese = new Boolean[WORLD_Y_SIZE][WORLD_X_SIZE]; }
            for (int row=0; row<WORLD_Y_SIZE; row++) {
                if (null == cheese[row]) { cheese[row] = new Boolean[WORLD_X_SIZE]; }
                for (int column=0; column<WORLD_X_SIZE; column++) {
                    cheese[row][column] = false;
                }
            }
        }

        public void make() {
            for (int i=0; i<HOLES_COUNT; i++) {
                Set<Hex> cheeseHole = wave(getRandomHex(true), HOLE_SIZE, true);
                for (Hex hex : cheeseHole) { cheese[hex.y][hex.x] = true; }
            }
        }

        public Boolean checkForCheese(Hex hex, Set<Hex> inside) {
            if (isWorldBorder(hex)) {
                return false;
            }
            Set<Hex> border = getBorder(hex);
            for (int i=0; i<TENTACLE_WIDTH-1; i++) {
                border = getBorder(border);
            }
            border.removeAll(inside);
            for (Hex candidate : border) {
                if (cheese[candidate.y][candidate.x]) {
                    return false;
                }
            }
            return true;
        }

        public Set<Hex> checkForCheese(Set<Hex> hexes, Set<Hex> inside) {
            Set<Hex> result = new HashSet<Hex>();
            for (Hex hex : hexes) {
                if (checkForCheese(hex, inside)) { result.add(hex); }
            }
            return result;
        }
    }

    /*** ATTRS ***/

    private int back = 0;

    private long cheesetime;

    private long landtime;

    private long eliminatetime;

	private int playersCount;

    private Boolean[][] empty;

    private int[][] ids;

    private Flag[][] colors;

    private List<Flag> playerStack;

    private CheeseMaker cheeseMaker = new CheeseMaker();

	public void setPlayersCount(int playersCount) {
		this.playersCount = playersCount;
	}

    private int countFrees() {
        int frees = 0;
        for (int i=0; i<WORLD_Y_SIZE; i++) {
            for (int j=0; j<WORLD_X_SIZE; j++) {
                frees += isEmpty(Hex.getHex(j, i)) ? 1 : 0;
            }
        }
        return frees;
    }

    private void createPlayerStack(Collection<Flag> playerFlags) {
        int maxLandCount = Math.min(Math.round(1.0F * countFrees() / MAX_LAND_SIZE), MAX_LAND_COUNT);
        int landCount = MIN_LAND_COUNT + random(maxLandCount - MIN_LAND_COUNT);
        playerStack = new ArrayList<Flag>();
        Set<Flag> remains = new HashSet<Flag>();
        for (int i=0; i<landCount; i++) {
            if (remains.isEmpty()) { remains.addAll(playerFlags); }
            Flag chosen = choose(remains);
            remains.remove(chosen);
            playerStack.add(chosen);
        }
    }

    private <T> T choose(Set<T> elements) {
        int index = random(elements.size()); int current = 0;
        for (T element : elements) {
            if (index == current++) { return element; }
        }
        return null;
    }

    private Hex chooseWeightened(Set<Weightened<Hex>> weights) {
        int sum = 0;
        for (Weightened<Hex> weight : weights) { sum += weight.getWeight(); }
        int index = random(sum); int current = 0;
        for (Weightened<Hex> weight : weights) {
            current += weight.getWeight();
            if (index < current) { return weight.getElement(); }
        }
        return null;
    }

    private int random(int max) {
        return Math.round((float) Math.floor(Math.random() * max));
    }

    private int countFrees(Hex hex) {
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
        Set<Hex> border = new HashSet<Hex>(hexes.size() * 6);
        for (Hex hex : hexes) {
            border.addAll(getBorder(hex));
        }
        return border;
    }

    private Boolean checkCoords(int row, int column) {
        return row >= 0 && row < WORLD_Y_SIZE && column >=0 && column < WORLD_X_SIZE;
    }

    private Set<Hex> getBorder(Hex hex) {
        Set<Hex> border = new HashSet<Hex>(6);
        if (checkCoords(hex.y, hex.x-1)) { border.add(Hex.getHex(hex.x-1, hex.y)); }
        if (checkCoords(hex.y, hex.x+1)) { border.add(Hex.getHex(hex.x+1, hex.y)); }
        if (0==hex.getY()%2) {
            if (checkCoords(hex.y-1, hex.x)) { border.add(Hex.getHex(hex.x, hex.y-1)); }
            if (checkCoords(hex.y-1, hex.x+1)) { border.add(Hex.getHex(hex.x+1, hex.y-1)); }
            if (checkCoords(hex.y+1, hex.x)) { border.add(Hex.getHex(hex.x, hex.y+1)); }
            if (checkCoords(hex.y+1, hex.x+1)) { border.add(Hex.getHex(hex.x+1, hex.y+1)); }
        } else {
            if (checkCoords(hex.y-1, hex.x)) { border.add(Hex.getHex(hex.x, hex.y-1)); }
            if (checkCoords(hex.y-1, hex.x-1)) { border.add(Hex.getHex(hex.x-1, hex.y-1)); }
            if (checkCoords(hex.y+1, hex.x)) { border.add(Hex.getHex(hex.x, hex.y+1)); }
            if (checkCoords(hex.y+1, hex.x-1)) { border.add(Hex.getHex(hex.x-1, hex.y+1)); }
        }
        return border;
    }

    private void busy(Hex hex) {
        empty[hex.y][hex.x] = false;
    }

    private void busy(Set<Hex> hexes) {
        for (Hex hex : hexes) {
            busy(hex);
        }
    }

    public void empty(Hex hex) {
        empty[hex.y][hex.x] = true;
    }

    public int id(Hex hex) {
        return ids[hex.y][hex.x];
    }

    private void empty(Set<Hex> hexes) {
        for (Hex hex : hexes) {
            empty(hex);
        }
    }

    private Boolean isEmpty(Hex hex) {
        return empty[hex.y][hex.x];
    }

    private Set<Hex> filterFree(Set<Hex> hexes) {
        Set<Hex> free = new HashSet<Hex>();
        for (Hex hex : hexes) {
            if (isEmpty(hex)) { free.add(hex); }
        }
        return free;
    }

    private Set<Hex> filterBusy(Set<Hex> hexes) {
        Set<Hex> result = new HashSet<Hex>();
        for (Hex hex : hexes) {
            if (!isEmpty(hex) && 0 != id(hex)) { result.add(hex); }
        }
        return result;
    }

    private void processBorder(Hex hex, Set<Hex> border, Set<Hex> inside, Boolean check) {
        border.remove(hex);
        Set<Hex> hexes = check
                ? cheeseMaker.checkForCheese(filterFree(getBorder(hex)), inside)
                : filterFree(getBorder(hex));
        border.addAll(hexes);
    }

    private int getLandSize() {
        // todo: more sophisticated method
        return MIN_LAND_SIZE + random(MAX_LAND_SIZE - MIN_LAND_SIZE);
    }

    private Boolean isWorldBorder(Hex hex) {
        return
                0 == hex.x
                || 0 == hex.y
                || WORLD_X_SIZE - 1 == hex.x
                || WORLD_Y_SIZE - 1 == hex.y;
    }

    private Set<Hex> wave(Hex initialHex, int size, Boolean check) {
        if (null == initialHex) { return null; }
        Set<Hex> inside = new HashSet<Hex>();
        Set<Hex> border = new HashSet<Hex>();
        border.add(initialHex);
        Hex newHex = getHex(border);
        for (int i=0; i<size && null != newHex; i++) {
            busy(newHex); inside.add(newHex);
            processBorder(newHex, border, inside, check);
            newHex = getHex(border);
        }
        return inside;
    }

    private Set<Hex> getLandHexes(Set<FullLand> lands) {
        Set<Hex> landHexes = null;
        int count = 0;
        while (null == landHexes && count++ < HEXES_TRY_CREATION_LIMIT) {
            landHexes = wave(
                    0 == lands.size() ? getRandomHex(false) : getHex(getWorldBorder(lands)),
                    getLandSize(),
                    false
            );
            if (landHexes.size() < MIN_LAND_SIZE) {
                empty(landHexes);
                landHexes = null;
            }
        }
        return landHexes;
    }

    private Set<Hex> getWorldBorder(Set<FullLand> lands) {
        Set<Hex> territory = new HashSet<Hex>();
        for (FullLand land : lands) { territory.addAll(hexes(land.getPoints())); }
        return filterFree(getBorder(territory));
    }

    private Hex hex(Point point) {
        return Hex.getHex(point.getX(), point.getY());
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
        //if (!isEmpty(hex)) { throw new IllegalArgumentException(); } 
        land.getPoints().add(transform(hex));
        colors[hex.y][hex.x] = land.getFlag();
        ids[hex.y][hex.x] = land.getLandId();
        busy(hex);
    }

    private void color(FullLand land, Set<Hex> hexes) {
        for (Hex hex : hexes) { color(land, hex); }
    }

    private void uncolor(Hex hex) {
        if (0 == id(hex)) { throw new IllegalArgumentException(); }
        colors[hex.y][hex.x] = null;
        ids[hex.y][hex.x] = 0;
        empty(hex);
    }

    private void uncolor(FullLand land) {
        Set<Hex> hexes = hexes(land.getPoints());
        for (Hex hex : hexes) { uncolor(hex); }
    }

    private Flag getColor(Set<Hex> landHexes) {
        Set<Hex> neighbourHexes = filterBusy(getBorder(landHexes));
        Set<Flag> neighbours = new HashSet<Flag>();
        for (Hex hex : neighbourHexes) {
            if (null != colors[hex.y][hex.x]) {
                neighbours.add(colors[hex.y][hex.x]);
            }
        }
        int count = 0;
        Flag result = playerStack.get(random(playerStack.size()));
        while (count++ < COLOR_SELECTION_LIMIT && neighbours.contains(result)) {
            result =  playerStack.get(random(playerStack.size()));
        }
        return result;
    }

    private FullLand addLand(Set<FullLand> lands, Set<Hex> landHexes) {
        FullLand land = new FullLandImpl(lands.size() + 1);
        land.setFlag(getColor(landHexes));
        playerStack.remove(land.getFlag());
        color(land, landHexes);
        lands.add(land);
        return land;
    }

    private void removeLand(FullLand land, Set<FullLand> lands) {
        playerStack.add(land.getFlag());
        uncolor(land); // and empty too
        lands.remove(land);
    }

    private Boolean createNewLand(Set<FullLand> lands) {
        if (0 == playerStack.size()) { return true; }
        if (countFrees() < playerStack.size()*MIN_LAND_SIZE) { return false; }

        int count = 0;
        do {
            Set<Hex> landHexes = getLandHexes(lands);
            if (null == landHexes) { return false; }
            FullLand land = addLand(lands, landHexes);
            if (createNewLand(lands)) { return true; }
            removeLand(land, lands); back++;
        } while(count++ < LAND_TRY_CREATION_LIMIT);

        return false;
    }

    private Hex getRandomHex(Boolean center) {
        if (!center && isEmpty(Hex.getHex(0, 0))) { return Hex.getHex(0, 0); }
        if (!center && isEmpty(Hex.getHex(WORLD_X_SIZE-1, 0))) { return Hex.getHex(WORLD_X_SIZE-1, 0); }
        if (!center && isEmpty(Hex.getHex(0, WORLD_Y_SIZE-1))) { return Hex.getHex(0, WORLD_Y_SIZE-1); }
        if (!center && isEmpty(Hex.getHex(WORLD_X_SIZE-1, WORLD_Y_SIZE-1))) { return Hex.getHex(WORLD_X_SIZE-1, WORLD_Y_SIZE-1); }

        int offsetX = center ? WORLD_X_SIZE/6 : 0;
        int offsetY = center ? WORLD_Y_SIZE/6 : 0;
        int rangeX = center ? 2*WORLD_X_SIZE/3 : WORLD_X_SIZE;
        int rangeY = center ? 2*WORLD_Y_SIZE/3 : WORLD_Y_SIZE;
        Hex hex = Hex.getHex(offsetX + random(rangeX), offsetY + random(rangeY));
        while (!isEmpty(hex) || !cheeseMaker.checkForCheese(hex, new HashSet<Hex>())) {
            hex = Hex.getHex(offsetX + random(rangeX), offsetY + random(rangeY));
        }
        return hex;
    }

    private void init() {
        // init empties
        if (null == empty) { empty = new Boolean[WORLD_Y_SIZE][WORLD_X_SIZE]; }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            if (null == empty[row]) { empty[row] = new Boolean[WORLD_X_SIZE]; }
            for (int column=0; column<WORLD_X_SIZE; column++) {
                empty[row][column] = true;
            }
        }
        // init colors
        if (null == colors) { colors = new Flag[WORLD_Y_SIZE][WORLD_X_SIZE]; }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            if (null == colors[row]) { colors[row] = new Flag[WORLD_X_SIZE]; }
            for (int column=0; column<WORLD_X_SIZE; column++) {
                colors[row][column] = null;
            }
        }
        // init ids
        if (null == ids) { ids = new int[WORLD_Y_SIZE][WORLD_X_SIZE]; }
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            if (null == ids[row]) { ids[row] = new int[WORLD_X_SIZE]; }
            for (int column=0; column<WORLD_X_SIZE; column++) {
                ids[row][column] = 0;
            }
        }
    }

    private void eliminateHoles(Set<FullLand> lands) {
        Set<Hex> worldBorder = getWorldBorder(lands);
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

    private Set<FullLand> createLands(Collection<Flag> playerFlags) {
        // init
        init();
        // set of all lands
        Set<FullLand> lands = new HashSet<FullLand>();
        // create big holes
        cheesetime = new GregorianCalendar().getTimeInMillis();
        cheeseMaker.make();
        cheesetime = new GregorianCalendar().getTimeInMillis() - cheesetime;
        //System.out.println("cheese   : " + cheesetime + " ms");
        //output(lands);
        // get all land colors are to place into the world
        createPlayerStack(playerFlags);
        // place lands into world one by one in recursive manner
        landtime = new GregorianCalendar().getTimeInMillis();
        createNewLand(lands);
        landtime = new GregorianCalendar().getTimeInMillis() - landtime;
        //System.out.println("lands    : " + landtime + " ms");
        //System.out.println("back     : " + back);
        // fill holes
        eliminatetime = new GregorianCalendar().getTimeInMillis();
        eliminateHoles(lands);
        eliminatetime = new GregorianCalendar().getTimeInMillis() - eliminatetime;
        //System.out.println("eliminate: " + eliminatetime + " ms");
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
            if (element.y == hex.y && element.x == hex.x) {
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

    private List<Flag> createPlayerFlags() {
        Flag[] flags = Flag.values();
        List<Flag> playerFlags = new ArrayList<Flag>();
        playerFlags.addAll(Arrays.asList(flags).subList(0, playersCount));
        return playerFlags;
    }

    //Mkamalov: Client interface should be clear to end user.
	private char getChar(Flag f) {
		switch (f) {
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
		case GRAY:
			return 'T';
		case YELLOW:
			return 'Y';
		}
		throw new IllegalStateException();
	}
    
    private void output(Set<FullLand> lands) {
        Set<Hex> border = getWorldBorder(lands);
        for (int row=0; row<WORLD_Y_SIZE; row++) {
            for (int column=0; column<WORLD_Y_SIZE; column++) {
                Hex current = Hex.getHex(column, row);
                FullLand owner = getOwnerLand(current, lands);	
                System.out.print(null != owner ? getChar(owner.getFlag()) : (contains(current, border) ? '.' : (!isEmpty(current) ? '#' : ' ')));
            }
            System.out.println();
        }
    }

    private int countLands(Flag player, Set<FullLand> lands) {
        int count = 0;
        for (FullLand land : lands) {
            count += land.getFlag() == player ? 1 : 0;
        }
        return count;
    }

    private void processDices(Set<FullLand> lands, Collection<Flag> players) {
        for (Flag player : players) {
            int left = countLands(player, lands);
            int dices = left * DICE_PER_LAND;
            for (FullLand land : lands) {
                if (land.getFlag().equals(player)) {
                    int max = Math.min(dices - MIN_DICE_PER_LAND * (left - 1), MAX_DICE_PER_LAND);
                    int min = Math.max(dices - MAX_DICE_PER_LAND * (left - 1), MIN_DICE_PER_LAND);
                    int amount = min + random(max - min);
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
        List<Flag> playerFlags = createPlayerFlags();
        // all lands
        Set<FullLand> lands = createLands(playerFlags);
        // all neighbours
        processNeighbours(lands);
        // all dices
        processDices(lands, playerFlags);
        // validation
        validate(lands);
        // output
        //output(lands);
        // and world!
        return new FullWorldImpl(0, lands, playerFlags, new HashMap<Flag, Integer>());
    }

}