package g64974.dev3.labyrinthe.controller;

import g64974.dev3.labyrinthe.model.Direction;
import g64974.dev3.labyrinthe.model.Facade;
import g64974.dev3.labyrinthe.model.Position;

/**
 * Controller for the Labyrinth game (MVC pattern).
 * Manages commands and provides undo/redo functionality.
 * Acts as intermediary between view and model.
 *
 * @author g64974
 */
public class Controller {

    private final Facade facade;
    private final CommandHistory history;

    /**
     * Creates a new controller.
     *
     * @param facade the game facade
     */
    public Controller(Facade facade) {
        if (facade == null) {
            throw new IllegalArgumentException("Facade cannot be null");
        }

        this.facade = facade;
        this.history = new CommandHistory();
    }

    /**
     * Starts a new game.
     * Clears command history.
     *
     * @param numberOfPlayers number of players (2-4)
     */
    public void startGame(int numberOfPlayers) {
        facade.startGame(numberOfPlayers);
        history.clear();
    }

    /**
     * Rotates the extra tile 90 degrees clockwise.
     * This action is not recorded in history (cannot be undone).
     */
    public void rotateExtraTile() {
        facade.rotateExtraTile();
    }

    /**
     * Inserts a tile using the Command pattern.
     * The action can be undone.
     *
     * @param direction the insertion direction
     * @param index the row/column index (1, 3, or 5)
     */
    public void insertTile(Direction direction, int index) {
        Command command = new InsertTileCommand(facade, direction, index);
        history.executeCommand(command);
    }

    /**
     * Moves the current player to a destination using the Command pattern.
     * The action can be undone.
     *
     * @param destination the target position
     */
    public void movePlayer(Position destination) {
        Command command = new MovePlayerCommand(facade, destination);
        history.executeCommand(command);
    }

    /**
     * Undoes the last command.
     *
     * @throws IllegalStateException if nothing to undo
     */
    public void undo() {
        history.undo();
    }

    /**
     * Redoes the last undone command.
     *
     * @throws IllegalStateException if nothing to redo
     */
    public void redo() {
        history.redo();
    }

    /**
     * Checks if undo is possible.
     *
     * @return true if there are commands to undo
     */
    public boolean canUndo() {
        return history.canUndo();
    }

    /**
     * Checks if redo is possible.
     *
     * @return true if there are commands to redo
     */
    public boolean canRedo() {
        return history.canRedo();
    }

    /**
     * Gets the facade (for read-only access from view).
     *
     * @return the facade
     */
    public Facade getFacade() {
        return facade;
    }

    /**
     * Gets the command history (for debugging/display).
     *
     * @return the command history
     */
    public CommandHistory getHistory() {
        return history;
    }

    /**
     * Abandons the current game.
     */
    public void abandonGame() {
        facade.abandonGame();
        history.clear();
    }

    /**
     * Gets information about the controller state.
     *
     * @return state description
     */
    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Controller State:\n");
        info.append("  Can Undo: ").append(canUndo())
                .append(" (").append(history.getUndoSize()).append(" commands)\n");
        info.append("  Can Redo: ").append(canRedo())
                .append(" (").append(history.getRedoSize()).append(" commands)\n");
        return info.toString();
    }
}