package g64974.dev3.labyrinthe.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Tile types for Labyrinth.
 * Each type defines its base openings at rotation 0°.
 *
 * VERIFIED AGAINST IMAGE ORIENTATIONS:
 * - I_Shape.jpg: Vertical corridor (NORTH-SOUTH)
 * - L_tile.jpg: Corner with NORTH and EAST openings
 * - All T objectives: NORTH, EAST, and WEST openings (wall on SOUTH)
 *
 * @author g64974
 */
public enum TileType {

    /**
     * Straight corridor: NORTH <-> SOUTH at 0°
     *
     * Visual representation at 0°:
     *     |
     *     |
     *     |
     *
     * Opens to NORTH (top) and SOUTH (bottom).
     */
    I(EnumSet.of(Direction.NORTH, Direction.SOUTH)),

    /**
     * Corner corridor: NORTH + EAST at 0°
     *
     * Visual representation at 0°:
     *     |
     *     └──
     *
     * Opens to NORTH (top) and EAST (right).
     */
    L(EnumSet.of(Direction.NORTH, Direction.EAST)),

    /**
     * T-junction corridor: NORTH + EAST + WEST at 0°
     * Wall on SOUTH.
     *
     * Visual representation at 0°:
     *     |
     *   ──┼──
     *
     * Opens to NORTH (top), EAST (right), and WEST (left).
     * Wall on SOUTH (bottom).
     */
    T(EnumSet.of(Direction.NORTH, Direction.EAST, Direction.WEST));

    private final Set<Direction> baseDirections;

    /**
     * Creates a tile type with its base directions.
     *
     * @param baseDirections the openings at rotation 0°
     */
    TileType(Set<Direction> baseDirections) {
        this.baseDirections = EnumSet.copyOf(baseDirections);
    }

    /**
     * Gets the base openings for rotation 0°.
     * These define the corridors when the tile is not rotated.
     *
     * @return unmodifiable set of directions with openings at 0°
     */
    public Set<Direction> getBaseDirections() {
        return Collections.unmodifiableSet(baseDirections);
    }

    /**
     * Checks if this tile type has an opening in the given direction at 0°.
     *
     * @param direction the direction to check
     * @return true if there is an opening in that direction at 0°
     */
    public boolean hasOpening(Direction direction) {
        return baseDirections.contains(direction);
    }

    /**
     * Gets the number of openings for this tile type.
     *
     * @return 2 for I and L, 3 for T
     */
    public int getOpeningCount() {
        return baseDirections.size();
    }

    @Override
    public String toString() {
        return name() + " (" + baseDirections + ")";
    }
}