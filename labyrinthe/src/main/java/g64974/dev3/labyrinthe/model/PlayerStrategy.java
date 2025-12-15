package g64974.dev3.labyrinthe.model;

/**
 * Strategy interface for AI player decision making.
 * Allows different AI implementations (random, smart, etc.)
 *
 * @author g64974
 */
public interface PlayerStrategy {

    /**
     * Decides where to insert the extra tile.
     *
     * @param facade the game facade (read-only access)
     * @return a Move containing direction and index
     */
    InsertMove decideInsertion(Facade facade);

    /**
     * Decides where to move the player.
     *
     * @param facade the game facade (read-only access)
     * @return the target position
     */
    Position decideMovement(Facade facade);

    /**
     * Decides whether to rotate the extra tile before inserting.
     *
     * @param facade the game facade
     * @return number of 90° rotations to apply (0-3)
     */
    int decideRotation(Facade facade);

    /**
     * Gets the name of this strategy.
     *
     * @return strategy name
     */
    default String getName() {
        return "AI";
    }

    /**
     * Gets the difficulty level of this strategy.
     *
     * @return difficulty level (0 = random)
     */
    default int getLevel() {
        return 0;
    }

    /**
     * Inner class representing an insertion move.
     */
    class InsertMove {
        private final Direction direction;
        private final int index;

        public InsertMove(Direction direction, int index) {
            this.direction = direction;
            this.index = index;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "InsertMove(" + direction + ", " + index + ")";
        }
    }
}