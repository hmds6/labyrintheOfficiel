package g64974.dev3.labyrinthe.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a corridor tile on the game board.
 * Each tile has a type (T, L, or I), a rotation, and optionally an objective.
 *
 * ROTATION LOGIC:
 * - Tiles WITHOUT objectives: can be rotated freely, rotation affects openings
 * - Tiles WITH objectives: rotation is LOCKED to baseRotation from Objective
 *
 * @author g64974
 */
public class Tile {

    private final TileType type;
    private final boolean isFixed;
    private final Objective objective;
    private int rotation;

    /**
     * Creates a new tile.
     *
     * @param type the tile type (T, L, or I)
     * @param isFixed true if this tile cannot be moved
     * @param objective the objective on this tile, or null if none
     */
    public Tile(TileType type, boolean isFixed, Objective objective) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.type = type;
        this.isFixed = isFixed;
        this.objective = objective;

        // Set base rotation from objective if it has one
        if (objective != null) {
            this.rotation = objective.getBaseRotation();
        } else {
            this.rotation = 0;
        }
    }

    /**
     * Creates a tile without an objective.
     *
     * @param type the tile type
     * @param isFixed true if this tile is fixed
     */
    public Tile(TileType type, boolean isFixed) {
        this(type, isFixed, null);
    }

    public TileType getType() {
        return type;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public Objective getObjective() {
        return objective;
    }

    public boolean hasObjective() {
        return objective != null;
    }

    public int getRotation() {
        return rotation;
    }

    /**
     * Rotates this tile 90 degrees clockwise.
     * Only mobile tiles WITHOUT objectives can be rotated.
     *
     * ⚠️ IMPORTANT: Tiles with objectives CANNOT be rotated because
     * the image already shows the correct orientation (baseRotation).
     */
    public void rotate() {
        if (isFixed) {
            throw new IllegalStateException("Cannot rotate a fixed tile");
        }

        // CRITICAL: Tiles with objectives cannot be rotated
        // Their rotation is locked to baseRotation
        if (hasObjective()) {
            // Silent ignore - this is expected behavior
            // The rotation button should be disabled for tiles with objectives
            return;
        }

        rotation = (rotation + 90) % 360;
    }

    /**
     * Sets the rotation to a specific angle.
     * Used during board initialization.
     *
     * ⚠️ For tiles WITH objectives: only baseRotation is allowed
     * ⚠️ For tiles WITHOUT objectives: any valid rotation (0, 90, 180, 270)
     *
     * @param angle the angle (0, 90, 180, or 270)
     */
    public void setRotation(int angle) {
        if (angle < 0 || angle >= 360 || angle % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270");
        }

        // For tiles with objectives, enforce baseRotation
        if (hasObjective()) {
            int expectedRotation = objective.getBaseRotation();
            if (angle != expectedRotation) {
                System.err.println("⚠️ WARNING: Attempting to set rotation " + angle +
                        " on tile with objective (expected " + expectedRotation + ") - forcing correct rotation");
                this.rotation = expectedRotation;
                return;
            }
        }

        this.rotation = angle;
    }

    /**
     * Gets all directions where this tile has an opening (after rotation).
     * This is the CORE method for pathfinding!
     *
     * ALGORITHM:
     * 1. Get base directions from TileType (at 0°)
     * 2. Apply rotation by rotating each direction
     * 3. Return the rotated set
     *
     * EXAMPLE:
     * - L tile at 0°: {NORTH, EAST}
     * - L tile at 90°: {EAST, SOUTH}  (each direction rotated 90° clockwise)
     * - L tile at 180°: {SOUTH, WEST}
     * - L tile at 270°: {WEST, NORTH}
     *
     * @return set of directions with openings
     */
    public Set<Direction> getOpenings() {
        // Get base directions from TileType (before rotation)
        Set<Direction> base = type.getBaseDirections();
        Set<Direction> rotated = new HashSet<>();

        // Calculate how many 90° rotations to apply
        int rotations = rotation / 90;

        // Rotate each base direction
        for (Direction dir : base) {
            Direction rotatedDir = dir;

            // Apply rotation 'rotations' times
            for (int i = 0; i < rotations; i++) {
                rotatedDir = rotatedDir.rotateClockwise();
            }

            rotated.add(rotatedDir);
        }

        return rotated;
    }

    /**
     * Checks if you can move from one direction to another through this tile.
     * Both directions must be in the openings set.
     *
     * @param from the entry direction
     * @param to the exit direction
     * @return true if path exists
     */
    public boolean hasPath(Direction from, Direction to) {
        Set<Direction> openings = getOpenings();
        return openings.contains(from) && openings.contains(to);
    }

    /**
     * Checks if you can enter this tile from a given direction.
     * The direction must be in the openings set.
     *
     * @param direction the direction of entry
     * @return true if there is an opening
     */
    public boolean canEnter(Direction direction) {
        return getOpenings().contains(direction);
    }

    /**
     * Checks if you can exit this tile in a given direction.
     * Same as canEnter (openings work both ways).
     *
     * @param direction the direction of exit
     * @return true if there is an opening
     */
    public boolean canExit(Direction direction) {
        return getOpenings().contains(direction);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" ");
        sb.append(rotation).append("° ");
        if (hasObjective()) {
            sb.append("[").append(objective.getEnglishName()).append("] ");
        }
        sb.append(isFixed ? "(fixed)" : "(mobile)");
        sb.append(" Openings: ").append(getOpenings());
        return sb.toString();
    }
}