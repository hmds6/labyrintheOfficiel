package g64974.dev3.labyrinthe.model;

/**
 * Represents the state of the game.
 *
 * @author g64974
 */
public enum GameState {
    /**
     * Game created but not started yet.
     */
    NOT_STARTED,

    /**
     * Game is in progress.
     */
    RUNNING,

    /**
     * Game is finished (won or abandoned).
     */
    FINISHED
}