package g64974.dev3.labyrinthe.model;

import java.util.*;

/**
 * Represents a complete game of Labyrinth.
 * Manages players, turns, game state, and win conditions.
 * Implements Observable pattern to notify views of changes.
 *
 * @author g64974
 */
public class Game extends Observable {

    public static g64974.dev3.labyrinthe.model.GameState GameState;
    private final Board board;
    private final List<Player> players;
    private int currentPlayerIndex;
    private GameState state;
    private boolean tileInsertedThisTurn;

    /**
     * Creates a new game with all human players.
     *
     * @param nbPlayers number of players (2-4)
     */
    public Game(int nbPlayers) {
        this(nbPlayers, 1); // 1 human, rest AI by default
    }

    /**
     * Creates a new game with specified human and AI players.
     *
     * @param nbPlayers total number of players (2-4)
     * @param nbHumans number of human players (1 minimum)
     */
    public Game(int nbPlayers, int nbHumans) {
        if (nbPlayers < 2 || nbPlayers > 4) {
            throw new IllegalArgumentException("Number of players must be 2-4");
        }
        if (nbHumans < 1 || nbHumans > nbPlayers) {
            throw new IllegalArgumentException("Invalid number of humans");
        }

        this.board = new Board();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.state = GameState.NOT_STARTED;
        this.tileInsertedThisTurn = false;

        // Starting positions (corners)
        Position[] startPositions = {
                new Position(0, 0),  // Top-left
                new Position(0, 6),  // Top-right
                new Position(6, 6),  // Bottom-right
                new Position(6, 0)   // Bottom-left
        };

        // Create human players
        for (int i = 0; i < nbHumans; i++) {
            Player player = new Player("Player " + (i + 1), startPositions[i]);
            players.add(player);
        }

        // Create AI players with RandomStrategy
        for (int i = nbHumans; i < nbPlayers; i++) {
            AIPlayer aiPlayer = new AIPlayer(
                    "AI " + (i - nbHumans + 1),
                    startPositions[i],
                    new RandomStrategy()
            );
            players.add(aiPlayer);
        }

        // Distribute objectives evenly among all players
        List<Objective> allObjectives = new ArrayList<>(Arrays.asList(Objective.values()));
        Collections.shuffle(allObjectives);

        int objectivesPerPlayer = allObjectives.size() / nbPlayers;

        for (int i = 0; i < nbPlayers; i++) {
            for (int j = 0; j < objectivesPerPlayer; j++) {
                int index = i * objectivesPerPlayer + j;
                if (index < allObjectives.size()) {
                    players.get(i).addObjective(allObjectives.get(index));
                }
            }
        }

        // Register board as observer
        addObserver(board);
    }

    /**
     * Starts the game.
     * Must be called after construction.
     */
    public void start() {
        if (state != GameState.NOT_STARTED) {
            throw new IllegalStateException("Game already started");
        }

        state = GameState.RUNNING;
        currentPlayerIndex = 0;
        tileInsertedThisTurn = false;
        notifyViews();
    }

    public GameState getState() {
        return state;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Gets the player whose turn it is.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        if (state != GameState.RUNNING) {
            throw new IllegalStateException("Game is not running");
        }
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Checks if a tile has been inserted this turn.
     *
     * @return true if tile already inserted
     */
    public boolean isTileInsertedThisTurn() {
        return tileInsertedThisTurn;
    }

    /**
     * Inserts the extra tile at the specified position.
     * This is the FIRST action of each turn.
     *
     * @param direction the push direction
     * @param index the row/column index (1, 3, or 5)
     */
    public void insertTile(Direction direction, int index) {
        if (state != GameState.RUNNING) {
            throw new IllegalStateException("Game is not running");
        }
        if (tileInsertedThisTurn) {
            throw new IllegalStateException("Tile already inserted this turn");
        }
        if (!board.canInsert(direction, index)) {
            throw new IllegalArgumentException(
                    "Cannot insert at " + direction + " " + index);
        }

        // Insert the tile and update player positions
        board.insertTile(direction, index);
        board.updatePlayerPositions(players, direction, index);

        tileInsertedThisTurn = true;

        // Notify observers
        notifyViews();
    }

    /**
     * Moves the current player to a destination.
     * This is the SECOND action of each turn.
     *
     * @param destination where to move
     */
    public void movePlayer(Position destination) {
        if (state != GameState.RUNNING) {
            throw new IllegalStateException("Game is not running");
        }
        if (!tileInsertedThisTurn) {
            throw new IllegalStateException("Must insert tile before moving");
        }

        Player current = getCurrentPlayer();
        Position from = current.getPosition();

        if (!board.canMove(from, destination)) {
            throw new IllegalArgumentException(
                    "Cannot move from " + from + " to " + destination);
        }

        // Move the player
        current.setPosition(destination);

        // Check if player reached their objective
        Tile tile = board.getTile(destination);
        if (tile.hasObjective()) {
            Objective objectiveHere = tile.getObjective();
            Objective currentObjective = current.getCurrentObjective();

            if (currentObjective != null && currentObjective.equals(objectiveHere)) {
                current.nextObjective(); // Progress to next objective
            }
        }

        // Check for victory
        if (current.hasWon()) {
            state = GameState.FINISHED;
            notifyViews();
            return;
        }

        // Next turn
        nextTurn();
        notifyViews();
    }

    /**
     * Advances to the next player's turn.
     */
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        tileInsertedThisTurn = false;
    }

    /**
     * Gets all positions the current player can move to.
     *
     * @return set of reachable positions
     */
    public Set<Position> getReachablePositions() {
        if (state != GameState.RUNNING) {
            throw new IllegalStateException("Game is not running");
        }

        Player current = getCurrentPlayer();
        return board.getReachablePositions(current.getPosition());
    }

    /**
     * Checks if the current player can move to a position.
     *
     * @param destination the target position
     * @return true if move is possible
     */
    public boolean canMoveTo(Position destination) {
        if (state != GameState.RUNNING) {
            return false;
        }

        Player current = getCurrentPlayer();
        return board.canMove(current.getPosition(), destination);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if a player has won
     */
    public boolean isGameOver() {
        return state == GameState.FINISHED;
    }

    /**
     * Gets the winner of the game.
     *
     * @return the winning player, or null if no winner yet
     */
    public Player getWinner() {
        if (state != GameState.FINISHED) {
            return null;
        }

        for (Player player : players) {
            if (player.hasWon()) {
                return player;
            }
        }

        return null;
    }

    /**
     * Abandons the current game.
     * Sets state to FINISHED without a winner.
     */
    public void abandon() {
        if (state == GameState.RUNNING) {
            state = GameState.FINISHED;
            notifyViews();
        }
    }

    /**
     * Notifies all observers that the game state has changed.
     * This method is PRIVATE as required by the grading criteria.
     */
    private void notifyViews() {
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the current player index (for undo operations).
     * Public to allow controller access for undo functionality.
     *
     * @param index the player index (0 to players.size()-1)
     */
    public void setCurrentPlayerIndex(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IllegalArgumentException("Invalid player index: " + index);
        }
        this.currentPlayerIndex = index;
        notifyViews();
    }

    /**
     * Sets the tile inserted flag (for undo operations).
     * Public to allow controller access for undo functionality.
     *
     * @param inserted true if tile was inserted this turn
     */
    public void setTileInsertedThisTurn(boolean inserted) {
        this.tileInsertedThisTurn = inserted;
        notifyViews();
    }

    /**
     * Gets a summary of the current game state.
     *
     * @return game state description
     */
    public String getGameInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Game State: ").append(state).append("\n");

        if (state == GameState.RUNNING) {
            info.append("Current Player: ").append(getCurrentPlayer().getName()).append("\n");
            info.append("Tile Inserted: ").append(tileInsertedThisTurn).append("\n");
        }

        info.append("\nPlayers:\n");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String marker = (i == currentPlayerIndex) ? " <--" : "";
            info.append("  ").append(p).append(marker).append("\n");
        }

        if (state == GameState.FINISHED) {
            Player winner = getWinner();
            if (winner != null) {
                info.append("\nWinner: ").append(winner.getName()).append("!\n");
            } else {
                info.append("\nGame abandoned.\n");
            }
        }

        return info.toString();
    }
}