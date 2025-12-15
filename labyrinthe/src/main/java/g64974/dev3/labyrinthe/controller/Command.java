package g64974.dev3.labyrinthe.controller;

/**
 * Interface for the Command design pattern.
 * Each command encapsulates an action that can be executed and undone.
 *
 * @author g64974
 */
public interface Command {

    /**
     * Executes the command.
     * Performs the action and saves the necessary state for undo.
     */
    void execute();

    /**
     * Undoes the command.
     * Reverts the action to the previous state.
     */
    void undo();

    /**
     * Checks if this command can be executed.
     *
     * @return true if the command is valid in the current state
     */
    boolean canExecute();
}