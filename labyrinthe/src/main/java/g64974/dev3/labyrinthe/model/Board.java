package g64974.dev3.labyrinthe.model;

import java.util.*;

/**
 * Represents the game board for the Labyrinth game.
 * Manages the 7x7 grid of tiles and the extra tile.
 * Implements Observer to receive notifications from Game.
 *
 * @author g64974
 */
public class Board implements Observer {

    public static final int SIZE = 7;

    private final Tile[][] grid;
    private Tile extraTile;
    private Position lastInsertPosition;
    private Direction lastInsertDirection;

    /**
     * Creates a new board with randomized tiles.
     */
    public Board() {
        this.grid = new Tile[SIZE][SIZE];
        initializeBoard();
    }

    /**
     * Initializes the board with fixed and mobile tiles.
     * According to game rules:
     * - 16 fixed tiles (4 corners + 12 with objectives)
     * - 34 mobile tiles (6 T with objectives, 12 I plain, 16 L with 6 objectives)
     */
    private void initializeBoard() {
        List<Tile> mobileTiles = new ArrayList<>();

        // ==========================================
        // FIXED TILES (16 total)
        // ==========================================

        // 4 corners: L tiles WITHOUT objectives
        grid[0][0] = new Tile(TileType.L, true);
        grid[0][0].setRotation(0);

        grid[0][6] = new Tile(TileType.L, true);
        grid[0][6].setRotation(90);

        grid[6][6] = new Tile(TileType.L, true);
        grid[6][6].setRotation(180);

        grid[6][0] = new Tile(TileType.L, true);
        grid[6][0].setRotation(270);

        // Get objectives using fromFolder()
        List<Objective> fixedTObjectives = Objective.fromFolder(Objective.ObjectiveFolder.FIXED_TILES);
        Collections.shuffle(fixedTObjectives);

        // 12 fixed T tiles with objectives
        Position[] fixedPositions = {
                new Position(0, 2), new Position(0, 4),
                new Position(2, 0), new Position(2, 2), new Position(2, 4), new Position(2, 6),
                new Position(4, 0), new Position(4, 2), new Position(4, 4), new Position(4, 6),
                new Position(6, 2), new Position(6, 4)
        };

        for (int i = 0; i < fixedPositions.length; i++) {
            Objective obj = fixedTObjectives.get(i);
            Tile tile = new Tile(TileType.T, true, obj);
            // Rotation set by constructor to obj.getBaseRotation()
            grid[fixedPositions[i].getRow()][fixedPositions[i].getColumn()] = tile;
        }

        // ==========================================
        // MOBILE TILES (34 total)
        // ==========================================

        // Get objectives for mobile tiles
        List<Objective> goalTObjectives = Objective.fromFolder(Objective.ObjectiveFolder.GOAL);
        List<Objective> lObjectives = Objective.fromFolder(Objective.ObjectiveFolder.L_OBJECTIF);

        Collections.shuffle(goalTObjectives);
        Collections.shuffle(lObjectives);

        // 6 T tiles with objectives (Goal folder)
        for (int i = 0; i < 6 && i < goalTObjectives.size(); i++) {
            Objective obj = goalTObjectives.get(i);
            Tile tile = new Tile(TileType.T, false, obj);
            mobileTiles.add(tile);
        }

        // 6 L tiles with objectives (L.objectif folder)
        for (int i = 0; i < 6 && i < lObjectives.size(); i++) {
            Objective obj = lObjectives.get(i);
            Tile tile = new Tile(TileType.L, false, obj);
            mobileTiles.add(tile);
        }

        // 10 L tiles WITHOUT objectives
        for (int i = 0; i < 10; i++) {
            mobileTiles.add(new Tile(TileType.L, false));
        }

        // 12 I tiles WITHOUT objectives
        for (int i = 0; i < 12; i++) {
            mobileTiles.add(new Tile(TileType.I, false));
        }

        // Shuffle all mobile tiles
        Collections.shuffle(mobileTiles);

        // Randomize rotation for tiles WITHOUT objectives
        Random random = new Random();
        for (Tile tile : mobileTiles) {
            if (!tile.hasObjective()) {
                tile.setRotation(random.nextInt(4) * 90);
            }
        }

        // Place mobile tiles on empty positions
        int tileIndex = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == null) {
                    grid[row][col] = mobileTiles.get(tileIndex++);
                }
            }
        }

        // The last tile becomes the extra tile
        extraTile = mobileTiles.get(tileIndex);
        lastInsertPosition = null;
        lastInsertDirection = null;
    }

    public Tile getTile(Position position) {
        if (!isValid(position)) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return grid[position.getRow()][position.getColumn()];
    }

    public void setTile(Position position, Tile tile) {
        if (!isValid(position)) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        grid[position.getRow()][position.getColumn()] = tile;
    }

    public Tile getExtraTile() {
        return extraTile;
    }

    public void setExtraTile(Tile tile) {
        this.extraTile = tile;
    }

    public boolean isValid(Position position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean canInsert(Direction direction, int index) {
        if (index != 1 && index != 3 && index != 5) {
            return false;
        }

        if (lastInsertDirection == null) {
            return true;
        }

        Position proposedPosition = getInsertPosition(direction, index);
        Direction oppositeDir = lastInsertDirection.opposite();

        return !(proposedPosition.equals(lastInsertPosition) &&
                direction == oppositeDir);
    }

    private Position getInsertPosition(Direction direction, int index) {
        switch (direction) {
            case NORTH: return new Position(SIZE - 1, index);
            case SOUTH: return new Position(0, index);
            case EAST:  return new Position(index, 0);
            case WEST:  return new Position(index, SIZE - 1);
            default: throw new IllegalArgumentException("Invalid direction");
        }
    }

    public Tile insertTile(Direction direction, int index) {
        if (!canInsert(direction, index)) {
            throw new IllegalStateException(
                    "Cannot insert at " + direction + " " + index);
        }

        Tile pushedOut;

        switch (direction) {
            case NORTH:
                pushedOut = pushColumn(index, true);
                break;
            case SOUTH:
                pushedOut = pushColumn(index, false);
                break;
            case EAST:
                // FIXED: EAST pushes from left to right
                pushedOut = pushRow(index, false);
                break;
            case WEST:
                // FIXED: WEST pushes from right to left
                pushedOut = pushRow(index, true);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction");
        }

        lastInsertPosition = getInsertPosition(direction, index);
        lastInsertDirection = direction;

        return pushedOut;
    }

    private Tile pushColumn(int col, boolean upward) {
        Tile pushedOut;

        if (upward) {
            pushedOut = grid[0][col];
            for (int row = 0; row < SIZE - 1; row++) {
                grid[row][col] = grid[row + 1][col];
            }
            grid[SIZE - 1][col] = extraTile;
        } else {
            pushedOut = grid[SIZE - 1][col];
            for (int row = SIZE - 1; row > 0; row--) {
                grid[row][col] = grid[row - 1][col];
            }
            grid[0][col] = extraTile;
        }

        extraTile = pushedOut;
        return pushedOut;
    }

    private Tile pushRow(int row, boolean leftward) {
        Tile pushedOut;

        if (leftward) {
            pushedOut = grid[row][0];
            for (int col = 0; col < SIZE - 1; col++) {
                grid[row][col] = grid[row][col + 1];
            }
            grid[row][SIZE - 1] = extraTile;
        } else {
            pushedOut = grid[row][SIZE - 1];
            for (int col = SIZE - 1; col > 0; col--) {
                grid[row][col] = grid[row][col - 1];
            }
            grid[row][0] = extraTile;
        }

        extraTile = pushedOut;
        return pushedOut;
    }

    public Set<Position> getReachablePositions(Position from) {
        Set<Position> reachable = new HashSet<>();
        Queue<Position> toVisit = new LinkedList<>();

        reachable.add(from);
        toVisit.add(from);

        while (!toVisit.isEmpty()) {
            Position current = toVisit.poll();
            Tile currentTile = getTile(current);

            for (Direction dir : currentTile.getOpenings()) {
                Position neighbor = current.move(dir);

                if (neighbor != null && isValid(neighbor) && !reachable.contains(neighbor)) {
                    Tile neighborTile = getTile(neighbor);

                    if (neighborTile.getOpenings().contains(dir.opposite())) {
                        reachable.add(neighbor);
                        toVisit.add(neighbor);
                    }
                }
            }
        }

        return reachable;
    }

    public boolean canMove(Position from, Position to) {
        if (!isValid(from) || !isValid(to)) {
            return false;
        }
        return getReachablePositions(from).contains(to);
    }

    public void updatePlayerPositions(List<Player> players, Direction direction, int index) {
        for (Player player : players) {
            Position pos = player.getPosition();
            Position newPos = calculateNewPosition(pos, direction, index);
            if (newPos != null) {
                player.setPosition(newPos);
            }
        }
    }

    private Position calculateNewPosition(Position pos, Direction direction, int index) {
        int row = pos.getRow();
        int col = pos.getColumn();

        switch (direction) {
            case NORTH:
                if (col == index) {
                    row = (row == 0) ? SIZE - 1 : row - 1;
                    return new Position(row, col);
                }
                break;
            case SOUTH:
                if (col == index) {
                    row = (row == SIZE - 1) ? 0 : row + 1;
                    return new Position(row, col);
                }
                break;
            case EAST:
                if (row == index) {
                    col = (col == SIZE - 1) ? 0 : col + 1;
                    return new Position(row, col);
                }
                break;
            case WEST:
                if (row == index) {
                    col = (col == 0) ? SIZE - 1 : col - 1;
                    return new Position(row, col);
                }
                break;
        }

        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        // Board receives notifications from Game
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board 7x7\n");
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = grid[row][col];
                sb.append(tile.getType().name()).append(" ");
            }
            sb.append("\n");
        }
        sb.append("Extra: ").append(extraTile.getType().name()).append("\n");
        return sb.toString();
    }
}