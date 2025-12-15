package g64974.dev3.labyrinthe.view;

import g64974.dev3.labyrinthe.controller.Controller;
import g64974.dev3.labyrinthe.model.Direction;
import g64974.dev3.labyrinthe.model.Facade;
import g64974.dev3.labyrinthe.model.Position;
import g64974.dev3.labyrinthe.model.Tile;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

/**
 * View for the game board (7x7 grid with insertion arrows).
 * Inherits from BorderPane to include arrows around the grid.
 *
 * @author g64974
 */
public class BoardView extends BorderPane {

    private static final int TILE_SIZE = 70;
    private static final int ARROW_SIZE = 30;
    private final Controller controller;
    private TileView[][] tileViews;
    private GridPane gridPane;

    /**
     * Creates the board view.
     *
     * @param controller the game controller
     */
    public BoardView(Controller controller) {
        this.controller = controller;
        this.tileViews = new TileView[7][7];

        setupStyle();
        initializeBoard();
        addInsertionArrows();
    }

    /**
     * Sets up the visual style.
     */
    private void setupStyle() {
        setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
    }

    /**
     * Initializes the board grid.
     */
    private void initializeBoard() {
        gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(2);
        gridPane.setAlignment(Pos.CENTER);

        Facade facade = controller.getFacade();

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                Tile tile = facade.getTileAt(pos);

                TileView tileView = new TileView(tile, pos, TILE_SIZE, facade);
                tileView.setOnMouseClicked(e -> handleTileClick(pos));

                tileViews[row][col] = tileView;
                gridPane.add(tileView, col, row);
            }
        }

        setCenter(gridPane);
    }

    /**
     * Adds insertion arrows around the board.
     * Arrows at positions 1, 3, 5 for each side.
     *
     * IMPORTANT: Arrow position vs push direction
     * - Top arrows push DOWN (SOUTH)
     * - Bottom arrows push UP (NORTH)
     * - Left arrows push RIGHT (EAST) - arrow is at left, pushes toward right
     * - Right arrows push LEFT (WEST) - arrow is at right, pushes toward left
     */
    private void addInsertionArrows() {
        // Top arrows (SOUTH direction - push down)
        GridPane topArrows = createArrowRow(Direction.SOUTH, true);
        setTop(topArrows);

        // Bottom arrows (NORTH direction - push up)
        GridPane bottomArrows = createArrowRow(Direction.NORTH, true);
        setBottom(bottomArrows);

        // ⚠️ FIXED: Arrows were inverted!
        // Left arrows push RIGHT (tiles move from left to right)
        GridPane leftArrows = createArrowColumn(Direction.EAST, false);
        setLeft(leftArrows);

        // Right arrows push LEFT (tiles move from right to left)
        GridPane rightArrows = createArrowColumn(Direction.WEST, false);
        setRight(rightArrows);
    }

    /**
     * Creates a row of arrows (for top/bottom).
     *
     * @param direction the push direction
     * @param horizontal true for horizontal layout
     * @return grid pane with arrows
     */
    private GridPane createArrowRow(Direction direction, boolean horizontal) {
        GridPane pane = new GridPane();
        pane.setHgap(2);
        pane.setAlignment(Pos.CENTER);

        int[] positions = {1, 3, 5};

        for (int col = 0; col < 7; col++) {
            if (contains(positions, col)) {
                Button arrow = createArrowButton(direction, col);
                pane.add(arrow, col, 0);
            } else {
                // Empty space
                StackPane empty = new StackPane();
                empty.setPrefSize(TILE_SIZE, ARROW_SIZE);
                pane.add(empty, col, 0);
            }
        }

        return pane;
    }

    /**
     * Creates a column of arrows (for left/right).
     *
     * @param direction the push direction
     * @param horizontal true for horizontal layout
     * @return grid pane with arrows
     */
    private GridPane createArrowColumn(Direction direction, boolean horizontal) {
        GridPane pane = new GridPane();
        pane.setVgap(2);
        pane.setAlignment(Pos.CENTER);

        int[] positions = {1, 3, 5};

        for (int row = 0; row < 7; row++) {
            if (contains(positions, row)) {
                Button arrow = createArrowButton(direction, row);
                pane.add(arrow, 0, row);
            } else {
                // Empty space
                StackPane empty = new StackPane();
                empty.setPrefSize(ARROW_SIZE, TILE_SIZE);
                pane.add(empty, 0, row);
            }
        }

        return pane;
    }

    /**
     * Creates an arrow button for tile insertion.
     *
     * @param direction the push direction
     * @param index the row/column index
     * @return the arrow button
     */
    private Button createArrowButton(Direction direction, int index) {
        Button button = new Button();

        // Create arrow shape
        Polygon arrow = createArrowShape(direction);
        button.setGraphic(arrow);

        // Set size based on direction
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            button.setPrefSize(TILE_SIZE, ARROW_SIZE);
        } else {
            button.setPrefSize(ARROW_SIZE, TILE_SIZE);
        }

        button.setStyle("-fx-background-color: #3498db; -fx-border-color: #2980b9;");

        // Handle click
        button.setOnAction(e -> handleArrowClick(direction, index));

        // ⚠️ CRITICAL FIX: Hover effect shows if insertion is allowed
        button.setOnMouseEntered(e -> {
            boolean canInsert = controller.getFacade().canInsertTile(direction, index);
            if (canInsert) {
                button.setStyle("-fx-background-color: #2ecc71; -fx-border-color: #27ae60;");
            } else {
                button.setStyle("-fx-background-color: #e74c3c; -fx-border-color: #c0392b;");
            }
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #3498db; -fx-border-color: #2980b9;");
        });

        return button;
    }

    /**
     * Creates an arrow polygon pointing in the specified direction.
     *
     * @param direction the direction
     * @return polygon representing arrow
     */
    private Polygon createArrowShape(Direction direction) {
        Polygon arrow = new Polygon();
        arrow.setFill(Color.WHITE);

        switch (direction) {
            case NORTH:
                arrow.getPoints().addAll(
                        10.0, 20.0,  // Bottom left
                        15.0, 10.0,  // Top center
                        20.0, 20.0   // Bottom right
                );
                break;
            case SOUTH:
                arrow.getPoints().addAll(
                        10.0, 10.0,  // Top left
                        15.0, 20.0,  // Bottom center
                        20.0, 10.0   // Top right
                );
                break;
            case EAST:
                arrow.getPoints().addAll(
                        10.0, 10.0,  // Top left
                        20.0, 15.0,  // Center right
                        10.0, 20.0   // Bottom left
                );
                break;
            case WEST:
                arrow.getPoints().addAll(
                        20.0, 10.0,  // Top right
                        10.0, 15.0,  // Center left
                        20.0, 20.0   // Bottom right
                );
                break;
        }

        return arrow;
    }

    /**
     * Handles arrow click for tile insertion.
     * ⚠️ CRITICAL: Checks canInsertTile before allowing insertion
     *
     * @param direction the insertion direction
     * @param index the row/column index
     */
    private void handleArrowClick(Direction direction, int index) {
        try {
            // ⚠️ CRITICAL FIX: Check if insertion is allowed (respects opposite rule)
            if (controller.getFacade().canInsertTile(direction, index)) {
                controller.insertTile(direction, index);
                refresh();
            } else {
                showMessage("Cannot insert here! (opposite of last insertion)");
            }
        } catch (Exception e) {
            showMessage("Error inserting tile: " + e.getMessage());
        }
    }

    /**
     * Handles click on a tile.
     *
     * @param position the clicked position
     */
    private void handleTileClick(Position position) {
        Facade facade = controller.getFacade();

        // Check if tile insertion phase
        if (!facade.isTileInsertedThisTurn()) {
            showMessage("Please insert a tile first!");
            return;
        }

        // Try to move player
        if (facade.canMoveTo(position)) {
            try {
                controller.movePlayer(position);
                refresh();
            } catch (Exception e) {
                showMessage("Error moving player: " + e.getMessage());
            }
        } else {
            showMessage("Cannot move there!");
        }
    }

    /**
     * Refreshes the board display.
     */
    public void refresh() {
        Facade facade = controller.getFacade();

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                Tile tile = facade.getTileAt(pos);

                tileViews[row][col].update(tile, pos, facade);
            }
        }
    }

    /**
     * Shows a temporary message (simplified version).
     *
     * @param message the message to show
     */
    private void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Helper method to check if array contains value.
     *
     * @param array the array
     * @param value the value to find
     * @return true if found
     */
    private boolean contains(int[] array, int value) {
        for (int v : array) {
            if (v == value) return true;
        }
        return false;
    }
}