package g64974.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Random strategy (Level 0) for AI players.
 * Makes completely random valid moves WITHOUT considering objectives.
 * This is the REQUIRED basic AI for the project.
 *
 * @author g64974
 */
public class RandomStrategy implements PlayerStrategy {

    private final Random random;

    /**
     * Creates a random strategy with a random seed.
     */
    public RandomStrategy() {
        this.random = new Random();
    }

    /**
     * Creates a random strategy with a specific seed (for testing).
     *
     * @param seed the random seed
     */
    public RandomStrategy(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public InsertMove decideInsertion(Facade facade) {
        // Get all valid insertion positions
        List<InsertMove> validMoves = getValidInsertions(facade);

        if (validMoves.isEmpty()) {
            throw new IllegalStateException("No valid insertions available");
        }

        // Pick one at random (does NOT consider objectives)
        return validMoves.get(random.nextInt(validMoves.size()));
    }

    @Override
    public Position decideMovement(Facade facade) {
        // Get all reachable positions
        Set<Position> reachable = facade.getReachablePositions();

        if (reachable.isEmpty()) {
            // Should not happen, but stay in place if it does
            return facade.getCurrentPlayer().getPosition();
        }

        // Convert to list for random access
        List<Position> positions = new ArrayList<>(reachable);

        // Pick one at random (does NOT try to reach objectives)
        return positions.get(random.nextInt(positions.size()));
    }

    @Override
    public int decideRotation(Facade facade) {
        // Random number of 90° rotations (0, 1, 2, or 3)
        // Only if extra tile can be rotated
        Tile extra = facade.getExtraTile();

        if (extra.hasObjective() || extra.isFixed()) {
            return 0; // Cannot rotate tiles with objectives
        }

        return random.nextInt(4);
    }

    @Override
    public String getName() {
        return "Random";
    }

    @Override
    public int getLevel() {
        return 0;
    }

    /**
     * Gets all valid insertion moves.
     *
     * @param facade the game facade
     * @return list of valid moves
     */
    private List<InsertMove> getValidInsertions(Facade facade) {
        List<InsertMove> moves = new ArrayList<>();

        // Valid insertion indices
        int[] validIndices = {1, 3, 5};

        // Try all 4 directions
        for (Direction dir : Direction.values()) {
            for (int index : validIndices) {
                if (facade.canInsertTile(dir, index)) {
                    moves.add(new InsertMove(dir, index));
                }
            }
        }

        return moves;
    }

    @Override
    public String toString() {
        return "RandomStrategy (Level " + getLevel() + ")";
    }
}