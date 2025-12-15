package g64974.dev3.labyrinthe.controller;

import g64974.dev3.labyrinthe.model.Facade;
import g64974.dev3.labyrinthe.model.Game;
import g64974.dev3.labyrinthe.model.Player;
import g64974.dev3.labyrinthe.model.Position;

/**
 * Command to move the current player to a destination.
 * Saves the complete state needed for undo.
 *
 * @author g64974
 */
public class MovePlayerCommand implements Command {

    private final Facade facade;
    private final Position destination;

    // State saved for undo
    private String playerName;
    private Position previousPosition;
    private int previousCurrentCardIndex;
    private int previousPlayerIndex;

    /**
     * Creates a new move player command.
     *
     * @param facade the game facade
     * @param destination the target position
     */
    public MovePlayerCommand(Facade facade, Position destination) {
        if (facade == null) {
            throw new IllegalArgumentException("Facade cannot be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        this.facade = facade;
        this.destination = destination;
    }

    @Override
    public boolean canExecute() {
        return facade.canMoveTo(destination);
    }

    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException(
                    "Cannot execute MovePlayerCommand: move not allowed");
        }

        // Save state BEFORE executing
        saveState();

        // Execute the action
        facade.movePlayer(destination);
    }

    @Override
    public void undo() {
        if (playerName == null || previousPosition == null) {
            throw new IllegalStateException(
                    "Cannot undo: command was not executed or state not saved");
        }

        // Find the player by name
        Player player = findPlayerByName(playerName);
        if (player == null) {
            throw new IllegalStateException("Player not found: " + playerName);
        }

        // Restore player position
        player.setPosition(previousPosition);

        // Restore card index if needed
        if (player.getCurrentCardIndex() != previousCurrentCardIndex) {
            player.setCurrentCardIndex(previousCurrentCardIndex);
        }

        // Restore previous player turn
        Game game = facade.getGame();
        game.setCurrentPlayerIndex(previousPlayerIndex);

        // Reset tile inserted flag for this turn
        game.setTileInsertedThisTurn(false);
    }

    /**
     * Saves the current state for undo.
     */
    private void saveState() {
        Game game = facade.getGame();
        Player currentPlayer = facade.getCurrentPlayer();

        playerName = currentPlayer.getName();
        previousPosition = currentPlayer.getPosition();
        previousCurrentCardIndex = currentPlayer.getCurrentCardIndex();
        previousPlayerIndex = game.getCurrentPlayerIndex();
    }

    /**
     * Finds a player by name.
     *
     * @param name the player name
     * @return the player, or null if not found
     */
    private Player findPlayerByName(String name) {
        for (Player p : facade.getPlayers()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MovePlayer(" + destination + ")";
    }
}