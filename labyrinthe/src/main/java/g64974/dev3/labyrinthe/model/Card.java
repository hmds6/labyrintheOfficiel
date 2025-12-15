package g64974.dev3.labyrinthe.model;

/**
 * Represents an objective card that a player must collect.
 * Each card shows one objective that the player needs to reach.
 *
 * @author g64974
 */
public class Card {

    private final Objective objective;
    private boolean collected;

    /**
     * Creates a new card with the specified objective.
     *
     * @param objective the objective to collect
     */
    public Card(Objective objective) {
        if (objective == null) {
            throw new IllegalArgumentException("Objective cannot be null");
        }
        this.objective = objective;
        this.collected = false;
    }

    /**
     * Gets the objective on this card.
     *
     * @return the objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * Checks if this card has been collected.
     *
     * @return true if collected
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Marks this card as collected.
     */
    public void collect() {
        this.collected = true;
    }

    @Override
    public String toString() {
        String status = collected ? "[COLLECTED]" : "[NOT COLLECTED]";
    return "ss";
    }}