package g64974.dev3.labyrinthe.model;

/**
 * AI-controlled player that uses a strategy to make decisions.
 * Required for the project to implement random AI opponent.
 *
 * @author g64974
 */
public class AIPlayer extends Player {

    private final PlayerStrategy strategy;

    /**
     * Creates an AI player with a strategy.
     *
     * @param name the player name
     * @param startPosition the starting position
     * @param strategy the decision-making strategy
     */
    public AIPlayer(String name, Position startPosition, PlayerStrategy strategy) {
        super(name, startPosition);

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }

        this.strategy = strategy;
    }

    /**
     * Gets the strategy used by this AI player.
     *
     * @return the strategy
     */
    public PlayerStrategy getStrategy() {
        return strategy;
    }

    /**
     * Makes the AI play its turn using its strategy.
     *
     * @param facade the game facade
     */
    public void playTurn(Facade facade) {
        if (facade.getCurrentPlayer() != this) {
            throw new IllegalStateException(
                    "Cannot play: it's not this player's turn");
        }

        // 1. Decide rotation
        int rotations = strategy.decideRotation(facade);
        for (int i = 0; i < rotations; i++) {
            facade.rotateExtraTile();
        }

        // 2. Decide and execute insertion
        PlayerStrategy.InsertMove insertion = strategy.decideInsertion(facade);
        facade.insertTile(insertion.getDirection(), insertion.getIndex());

        // 3. Decide and execute movement
        Position destination = strategy.decideMovement(facade);
        facade.movePlayer(destination);
    }

    /**
     * Checks if this is an AI player.
     *
     * @return true (always, for AIPlayer)
     */
    @Override
    public boolean isAI() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " [AI: " + strategy.getName() + " Lv" + strategy.getLevel() + "]";
    }
}