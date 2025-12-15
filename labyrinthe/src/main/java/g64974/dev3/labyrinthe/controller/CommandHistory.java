package g64974.dev3.labyrinthe.controller;

import java.util.Stack;

/**
 * Manages command history for undo/redo functionality.
 * Uses two stacks: one for undo, one for redo.
 *
 * @author g64974
 */
public class CommandHistory {

    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;

    /**
     * Creates a new command history.
     */
    public CommandHistory() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Executes a command and adds it to history.
     * Clears the redo stack.
     *
     * @param command the command to execute
     */
    public void executeCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        if (!command.canExecute()) {
            throw new IllegalStateException(
                    "Cannot execute command: " + command);
        }

        command.execute();
        undoStack.push(command);

        // Clear redo stack when new command is executed
        redoStack.clear();
    }

    /**
     * Undoes the last command.
     * Moves it to the redo stack.
     */
    public void undo() {
        if (!canUndo()) {
            throw new IllegalStateException("Nothing to undo");
        }

        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Redoes the last undone command.
     * Moves it back to the undo stack.
     */
    public void redo() {
        if (!canRedo()) {
            throw new IllegalStateException("Nothing to redo");
        }

        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }

    /**
     * Checks if undo is possible.
     *
     * @return true if there are commands to undo
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if redo is possible.
     *
     * @return true if there are commands to redo
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Gets the number of commands that can be undone.
     *
     * @return undo stack size
     */
    public int getUndoSize() {
        return undoStack.size();
    }

    /**
     * Gets the number of commands that can be redone.
     *
     * @return redo stack size
     */
    public int getRedoSize() {
        return redoStack.size();
    }

    /**
     * Clears all history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Gets a description of the command history.
     *
     * @return history description
     */
    public String getHistoryInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Command History:\n");
        info.append("  Undo stack (").append(undoStack.size()).append("):\n");

        for (int i = undoStack.size() - 1; i >= 0; i--) {
            info.append("    ").append(i + 1).append(". ")
                    .append(undoStack.get(i)).append("\n");
        }

        info.append("  Redo stack (").append(redoStack.size()).append("):\n");

        for (int i = redoStack.size() - 1; i >= 0; i--) {
            info.append("    ").append(i + 1).append(". ")
                    .append(redoStack.get(i)).append("\n");
        }

        return info.toString();
    }
}