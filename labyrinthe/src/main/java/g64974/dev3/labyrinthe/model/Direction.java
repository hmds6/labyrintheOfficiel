package g64974.dev3.labyrinthe.model;


/**
 * Represents the four cardinal directions on the game board.
 * Each direction knows how to move from one position to another.
 *
 * @author g12345
 */
public enum Direction {

    /**
     * North direction - moves UP (decreases row).
     * Example: from (3,2) NORTH goes to (2,2)
     */
    NORTH(-1, 0),

    /**
     * South direction - moves DOWN (increases row).
     * Example: from (3,2) SOUTH goes to (4,2)
     */
    SOUTH(1, 0),

    /**
     * East direction - moves RIGHT (increases column).
     * Example: from (3,2) EAST goes to (3,3)
     */
    EAST(0, 1),

    /**
     * West direction - moves LEFT (decreases column).
     * Example: from (3,2) WEST goes to (3,1)
     */
    WEST(0, -1);

    // =====================================================
    // ATTRIBUTES - stored for each direction
    // =====================================================

    /**
     * How much the row changes when moving in this direction.
     * Negative = up, Positive = down, 0 = no change
     */
    private final int deltaRow;

    /**
     * How much the column changes when moving in this direction.
     * Negative = left, Positive = right, 0 = no change
     */
    private final int deltaColumn;

    // =====================================================
    // CONSTRUCTOR - called once for each enum constant
    // =====================================================

    /**
     * Creates a direction with its movement deltas.
     * This constructor is private and only called when the enum is loaded.
     *
     * @param deltaRow change in row (-1 for up, +1 for down)
     * @param deltaColumn change in column (-1 for left, +1 for right)
     */
    Direction(int deltaRow, int deltaColumn) {
        this.deltaRow = deltaRow;
        this.deltaColumn = deltaColumn;
    }

    // =====================================================
    // GETTER METHODS
    // =====================================================

    /**
     * Gets the row change for this direction.
     *
     * @return -1 for NORTH, +1 for SOUTH, 0 for EAST/WEST
     */
    public int getDeltaRow() {
        return deltaRow;
    }

    /**
     * Gets the column change for this direction.
     *
     * @return -1 for WEST, +1 for EAST, 0 for NORTH/SOUTH
     */
    public int getDeltaColumn() {
        return deltaColumn;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Returns the opposite direction.
     * NORTH ↔ SOUTH, EAST ↔ WEST

     * Useful when checking if a tile has a path: if you can exit NORTH,
     * you must be able to enter from SOUTH.
     *
     * @return the opposite direction
     */
    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    /**
     * Rotates this direction 90 degrees clockwise.
     * NORTH → EAST → SOUTH → WEST → NORTH

     * Useful when rotating tiles on the board.
     *
     * @return the direction after rotating 90° clockwise
     */
    public Direction rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    /**
     * Rotates this direction 90 degrees counter-clockwise.
     * NORTH → WEST → SOUTH → EAST → NORTH
     *
     * @return the direction after rotating 90° counter-clockwise
     */
    public Direction rotateCounterClockwise() {
        switch (this) {
            case NORTH: return WEST;
            case WEST: return SOUTH;
            case SOUTH: return EAST;
            case EAST: return NORTH;
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }

    /**
     * Checks if this direction is horizontal (EAST or WEST).
     *
     * @return true if EAST or WEST
     */
    public boolean isHorizontal() {
        return this == EAST || this == WEST;
    }

    /**
     * Checks if this direction is vertical (NORTH or SOUTH).
     *
     * @return true if NORTH or SOUTH
     */
    public boolean isVertical() {
        return this == NORTH || this == SOUTH;
    }
}