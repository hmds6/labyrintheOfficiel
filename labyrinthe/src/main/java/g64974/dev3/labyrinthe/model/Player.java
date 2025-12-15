package g64974.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Labyrinth game.
 * Tracks position, objectives, and progress.
 *
 * @author g64974
 */
public class Player {

    private final String name;
    private final Position startPosition;
    private Position position;
    private final List<Objective> objectives;
    private int currentObjectiveIndex;

    /**
     * Creates a new player.
     *
     * @param name the player's name
     * @param startPosition the starting position
     */
    public Player(String name, Position startPosition) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (startPosition == null) {
            throw new IllegalArgumentException("Start position cannot be null");
        }

        this.name = name;
        this.startPosition = startPosition;
        this.position = startPosition;
        this.objectives = new ArrayList<>();
        this.currentObjectiveIndex = 0;
    }

    public String getName() {
        return name;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * Sets the player's position.
     *
     * @param position the new position
     */
    public void setPosition(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position = position;
    }

    /**
     * Adds an objective to this player's list.
     *
     * @param objective the objective to add
     */
    public void addObjective(Objective objective) {
        if (objective == null) {
            throw new IllegalArgumentException("Objective cannot be null");
        }
        objectives.add(objective);
    }

    /**
     * Gets the current objective the player is trying to reach.
     *
     * @return the current objective, or null if all objectives collected
     */
    public Objective getCurrentObjective() {
        if (currentObjectiveIndex < objectives.size()) {
            return objectives.get(currentObjectiveIndex);
        }
        return null; // All objectives collected
    }

    /**
     * Gets the index of the current objective.
     *
     * @return the current objective index
     */
    public int getCurrentObjectiveIndex() {
        return currentObjectiveIndex;
    }

    /**
     * Gets the index of the current objective card.
     *
     * <p>This is an alias for {@link #getCurrentObjectiveIndex()} to preserve
     * compatibility with command logic that saves and restores the current
     * card for undo/redo.</p>
     *
     * @return the current objective index
     */
    public int getCurrentCardIndex() {
        return getCurrentObjectiveIndex();
    }

    /**
     * Sets the current objective index (for undo).
     *
     * @param index the objective index
     */
    public void setCurrentObjectiveIndex(int index) {
        if (index < 0 || index > objectives.size()) {
            throw new IllegalArgumentException("Invalid objective index");
        }
        this.currentObjectiveIndex = index;
    }

    /**
     * Sets the index of the current objective card.
     *
     * <p>This is an alias for {@link #setCurrentObjectiveIndex(int)} to
     * preserve compatibility with command logic that saves and restores the
     * current card for undo/redo.</p>
     *
     * @param index the objective index to restore
     */
    public void setCurrentCardIndex(int index) {
        setCurrentObjectiveIndex(index);
    }

    /**
     * Moves to the next objective.
     * Called when current objective is reached.
     */
    public void nextObjective() {
        if (currentObjectiveIndex < objectives.size()) {
            currentObjectiveIndex++;
        }
    }

    /**
     * Gets all objectives for this player.
     *
     * @return list of objectives
     */
    public List<Objective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * Gets the player's progress as a string.
     * Format: "X/Y" where X is objectives collected, Y is total objectives.
     *
     * @return progress string (e.g., "3/6")
     */
    public String getProgress() {
        return currentObjectiveIndex + "/" + objectives.size();
    }

    /**
     * Gets the number of objectives collected.
     *
     * @return number of objectives collected
     */
    public int getObjectivesCollected() {
        return currentObjectiveIndex;
    }

    /**
     * Gets the total number of objectives.
     *
     * @return total number of objectives
     */
    public int getTotalObjectives() {
        return objectives.size();
    }

    /**
     * Gets the progress as a percentage (0.0 to 1.0).
     *
     * @return progress percentage
     */
    public double getProgressPercentage() {
        if (objectives.isEmpty()) {
            return 0.0;
        }
        return (double) currentObjectiveIndex / objectives.size();
    }

    /**
     * Checks if all objectives have been collected.
     *
     * @return true if all objectives collected
     */
    public boolean hasCollectedAllObjectives() {
        return currentObjectiveIndex >= objectives.size();
    }

    /**
     * Checks if the player has won.
     * Win condition: all objectives collected AND returned to start position.
     *
     * @return true if player has won
     */
    public boolean hasWon() {
        return hasCollectedAllObjectives() && position.equals(startPosition);
    }

    /**
     * Checks if this is an AI player.
     * Override in subclass AIPlayer to return true.
     *
     * @return false for human players, true for AI
     */
    public boolean isAI() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" at ").append(position);
        sb.append(" [");

        if (hasCollectedAllObjectives()) {
            sb.append("All objectives collected");
        } else {
            sb.append("Objective ").append(currentObjectiveIndex + 1);
            sb.append("/").append(objectives.size());
            sb.append(": ").append(getCurrentObjective());
        }

        sb.append("]");

        if (hasWon()) {
            sb.append(" WINNER!");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}