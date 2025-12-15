package g64974.dev3.labyrinthe.model;



import java.util.Objects;

/**
 * Represents a position on the game board.
 * The board is a 7x7 grid, so valid positions are from (0,0) to (6,6).
 *
 * This class is immutable - once created, a Position cannot be modified.
 *
 * @author g12345
 */
public class Position {

    private final int row;
    private final int column;

    /**
     * Creates a new position with the given coordinates.
     *
     * @param row the row index (0 to 6)
     * @param column the column index (0 to 6)
     * @throws IllegalArgumentException if row or column is out of bounds
     */
    public Position(int row, int column) {
        if (!isValid(row, column)) {
            throw new IllegalArgumentException(
                    "Invalid position: (" + row + ", " + column + "). " +
                            "Valid range is (0,0) to (6,6)."
            );
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the row index of this position.
     *
     * @return the row (0 to 6)
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column index of this position.
     *
     * @return the column (0 to 6)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Checks if the given coordinates are valid for a 7x7 board.
     *
     * @param row the row to check
     * @param column the column to check
     * @return true if both are between 0 and 6 (inclusive)
     */
    public static boolean isValid(int row, int column) {
        return row >= 0 && row < 7 && column >= 0 && column < 7;
    }

    /**
     * Creates a new Position moved in the given direction.
     *
     * @param direction the direction to move
     * @return a new Position, or null if the move goes out of bounds
     */
    public Position move(Direction direction) {
        int newRow = row + direction.getDeltaRow();
        int newCol = column + direction.getDeltaColumn();

        if (isValid(newRow, newCol)) {
            return new Position(newRow, newCol);
        }
        return null;
    }

    /**
     * Checks if this position is a corner of the board.
     * Corners are: (0,0), (0,6), (6,0), (6,6) - the starting positions.
     *
     * @return true if this is a corner position
     */
    public boolean isCorner() {
        return (row == 0 || row == 6) && (column == 0 || column == 6);
    }

    /**
     * Calculates the Manhattan distance to another position.
     * This is the minimum number of moves needed to reach the other position
     * if you could only move horizontally and vertically.
     *

     * @return the Manhattan distance
     */


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}