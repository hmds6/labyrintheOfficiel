package g64974.dev3.labyrinthe.controller;

import g64974.dev3.labyrinthe.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Command to insert a tile into the board.
 * Saves the complete state needed for undo.
 *
 * @author g64974
 */
public class InsertTileCommand implements Command {

    private final Facade facade;
    private final Direction direction;
    private final int index;

    // State saved for undo
    private Tile[][] savedBoard;
    private Tile savedExtraTile;
    private Map<String, Position> savedPlayerPositions;

    /**
     * Creates a new insert tile command.
     *
     * @param facade the game facade
     * @param direction the insertion direction
     * @param index the row/column index (1, 3, or 5)
     */
    public InsertTileCommand(Facade facade, Direction direction, int index) {
        if (facade == null) {
            throw new IllegalArgumentException("Facade cannot be null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction cannot be null");
        }

        this.facade = facade;
        this.direction = direction;
        this.index = index;
    }

    @Override
    public boolean canExecute() {
        return facade.canInsertTile(direction, index);
    }

    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException(
                    "Cannot execute InsertTileCommand: insertion not allowed");
        }

        // Save state BEFORE executing
        saveState();

        // Execute the action
        facade.insertTile(direction, index);
    }

    @Override
    public void undo() {
        if (savedBoard == null) {
            throw new IllegalStateException(
                    "Cannot undo: command was not executed or state not saved");
        }

        // Use public facade method to restore board
        Board board = facade.getBoard();

        // Restore all tiles
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                board.setTile(pos, savedBoard[row][col]);
            }
        }

        // Restore extra tile
        board.setExtraTile(savedExtraTile);

        // Restore player positions using player names as keys
        for (Player player : facade.getPlayers()) {
            Position savedPos = savedPlayerPositions.get(player.getName());
            if (savedPos != null) {
                player.setPosition(savedPos);
            }
        }

        // Reset tile inserted flag using Game's public method
        Game game = facade.getGame();
        game.setTileInsertedThisTurn(false);
    }

    /**
     * Saves the current state for undo.
     */
    private void saveState() {
        Board board = facade.getBoard();

        // Save complete board state
        savedBoard = new Tile[7][7];
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                savedBoard[row][col] = board.getTile(pos);
            }
        }

        // Save extra tile
        savedExtraTile = board.getExtraTile();

        // Save all player positions using player names as keys
        savedPlayerPositions = new HashMap<>();
        for (Player player : facade.getPlayers()) {
            savedPlayerPositions.put(player.getName(), player.getPosition());
        }
    }

    @Override
    public String toString() {
        return "InsertTile(" + direction + ", " + index + ")";
    }
}