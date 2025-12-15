package g64974.dev3.labyrinthe.model;

import java.util.*;

/**
 * Facade for the Labyrinth game model.
 * This is the ONLY entry point for controllers and views.
 * Prevents cheating and ensures model consistency.
 * Extends Observable to propagate game state changes.
 *
 * @author g64974
 */
public class Facade extends Observable {

    private Game game;

    /**
     * Starts a new game with the specified number of players.
     *
     * @param numberOfPlayers between 2 and 4
     */
    public void startGame(int numberOfPlayers) {
        game = new Game(numberOfPlayers);

        // Subscribe to game changes
        game.addObserver((o, arg) -> {
            setChanged();
            notifyObservers(arg);
        });

        initializeBoard();
        game.start();
    }

    /**
     * Initializes the board with all tiles using the correct image structure.
     * Sets up the 16 fixed tiles and 34 mobile tiles.
     *
     * CRITICAL: This method ensures tiles with objectives have correct baseRotation
     */
    private void initializeBoard() {
        Board board = game.getBoard();

        // ===============================
        // Separate objectives by folder/type
        // ===============================

        List<Objective> fixedTObjectives = Objective.fromFolder(Objective.ObjectiveFolder.FIXED_TILES);
        List<Objective> goalTObjectives = Objective.fromFolder(Objective.ObjectiveFolder.GOAL);
        List<Objective> lObjectives = Objective.fromFolder(Objective.ObjectiveFolder.L_OBJECTIF);

        // Shuffle for randomness
        Collections.shuffle(fixedTObjectives);
        Collections.shuffle(goalTObjectives);
        Collections.shuffle(lObjectives);

        // ===============================
        // FIXED TILES (16 total)
        // ===============================

        // 4 corners: L tiles without objectives
        board.setTile(new Position(0, 0), new Tile(TileType.L, true));
        board.getTile(new Position(0, 0)).setRotation(0);

        board.setTile(new Position(0, 6), new Tile(TileType.L, true));
        board.getTile(new Position(0, 6)).setRotation(90);

        board.setTile(new Position(6, 6), new Tile(TileType.L, true));
        board.getTile(new Position(6, 6)).setRotation(180);

        board.setTile(new Position(6, 0), new Tile(TileType.L, true));
        board.getTile(new Position(6, 0)).setRotation(270);

        // 12 fixed T tiles with objectives (from Fixed_tiles folder)
        Position[] fixedTPositions = {
                new Position(0, 2), new Position(0, 4),
                new Position(2, 0), new Position(2, 2), new Position(2, 4), new Position(2, 6),
                new Position(4, 0), new Position(4, 2), new Position(4, 4), new Position(4, 6),
                new Position(6, 2), new Position(6, 4)
        };

        for (int i = 0; i < fixedTPositions.length; i++) {
            Objective obj = fixedTObjectives.get(i);
            Tile tile = new Tile(TileType.T, true, obj);
            // ⚠️ CRITICAL: Tile constructor sets rotation to obj.getBaseRotation()
            // This ensures the image and the openings are aligned
            board.setTile(fixedTPositions[i], tile);
        }

        // ===============================
        // MOBILE TILES (34 total)
        // ===============================

        List<Tile> mobileTiles = new ArrayList<>();

        // 6 T tiles with creatures (from Goal folder)
        for (int i = 0; i < 6 && i < goalTObjectives.size(); i++) {
            Objective obj = goalTObjectives.get(i);
            Tile tile = new Tile(TileType.T, false, obj);
            // Rotation is set to baseRotation by constructor
            mobileTiles.add(tile);
        }

        // 6 L tiles with objectives (from L.objectif folder)
        for (int i = 0; i < 6 && i < lObjectives.size(); i++) {
            Objective obj = lObjectives.get(i);
            Tile tile = new Tile(TileType.L, false, obj);
            // Rotation is set to baseRotation by constructor
            mobileTiles.add(tile);
        }

        // 10 L tiles WITHOUT objectives
        for (int i = 0; i < 10; i++) {
            mobileTiles.add(new Tile(TileType.L, false));
        }

        // 12 I tiles (no objectives)
        for (int i = 0; i < 12; i++) {
            mobileTiles.add(new Tile(TileType.I, false));
        }

        // Shuffle all mobile tiles
        Collections.shuffle(mobileTiles);

        // Place 33 mobile tiles on empty board positions
        int tileIndex = 0;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                if (board.getTile(pos) == null) {
                    Tile tile = mobileTiles.get(tileIndex);

                    // ⚠️ CRITICAL: Only rotate tiles WITHOUT objectives
                    if (!tile.hasObjective()) {
                        tile.setRotation((int)(Math.random() * 4) * 90);
                    }
                    // Tiles WITH objectives keep their baseRotation (already set)

                    board.setTile(pos, tile);
                    tileIndex++;
                }
            }
        }

        // The 34th tile is the extra tile
        Tile extra = mobileTiles.get(tileIndex);

        // Only rotate if no objective
        if (!extra.hasObjective()) {
            extra.setRotation((int)(Math.random() * 4) * 90);
        }
        // Extra tile with objective keeps its baseRotation

        board.setExtraTile(extra);
    }

    // =====================================================
    // PUBLIC API
    // =====================================================

    public boolean canInsertTile(Direction direction, int index) {
        if (game == null || game.getState() != Game.GameState.RUNNING) {
            return false;
        }
        if (game.isTileInsertedThisTurn()) {
            return false;
        }
        return game.getBoard().canInsert(direction, index);
    }

    public void insertTile(Direction direction, int index) {
        if (!canInsertTile(direction, index)) {
            throw new IllegalStateException("Cannot insert tile");
        }
        game.insertTile(direction, index);
    }

    public void rotateExtraTile() {
        if (game == null) {
            throw new IllegalStateException("No game started");
        }
        if (game.isTileInsertedThisTurn()) {
            throw new IllegalStateException("Cannot rotate after insertion");
        }

        Tile extra = game.getBoard().getExtraTile();

        // Only rotate if tile has no objective
        if (!extra.hasObjective()) {
            extra.rotate();
        } else {
            // Silent ignore - tiles with objectives cannot be rotated
            System.out.println("INFO: Cannot rotate tile with objective");
        }

        // Notify observers of the change
        setChanged();
        notifyObservers();
    }

    public Set<Position> getReachablePositions() {
        if (game == null || game.getState() != Game.GameState.RUNNING) {
            return Collections.emptySet();
        }
        if (!game.isTileInsertedThisTurn()) {
            return Collections.emptySet();
        }
        return game.getReachablePositions();
    }

    public boolean canMoveTo(Position destination) {
        if (game == null || game.getState() != Game.GameState.RUNNING) {
            return false;
        }
        if (!game.isTileInsertedThisTurn()) {
            return false;
        }
        return game.canMoveTo(destination);
    }

    public void movePlayer(Position destination) {
        if (!canMoveTo(destination)) {
            throw new IllegalStateException("Cannot move to " + destination);
        }
        game.movePlayer(destination);
    }

    public Player getCurrentPlayer() {
        if (game == null) {
            return null;
        }
        return game.getCurrentPlayer();
    }

    public Tile getTileAt(Position position) {
        if (game == null) {
            return null;
        }
        return game.getBoard().getTile(position);
    }

    public Tile getExtraTile() {
        if (game == null) {
            return null;
        }
        return game.getBoard().getExtraTile();
    }

    public List<Player> getPlayers() {
        if (game == null) {
            return Collections.emptyList();
        }
        return game.getPlayers();
    }

    public boolean isGameOver() {
        return game != null && game.isGameOver();
    }

    public Player getWinner() {
        if (game == null) {
            return null;
        }
        return game.getWinner();
    }

    public void abandonGame() {
        if (game != null) {
            game.abandon();
        }
    }

    public int getBoardSize() {
        return 7;
    }

    public boolean isTileInsertedThisTurn() {
        return game != null && game.isTileInsertedThisTurn();
    }

    public Game getGame() {
        return game;
    }

    public Board getBoard() {
        return game != null ? game.getBoard() : null;
    }
    public void startGame(int numberOfPlayers, int numberOfHumans) {
        this.game = new Game(numberOfPlayers, numberOfHumans);
        game.start();
    }
}