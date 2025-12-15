package g64974.dev3.labyrinthe.view;

import g64974.dev3.labyrinthe.controller.Controller;
import g64974.dev3.labyrinthe.model.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Information panel showing game state and controls.
 * Inherits from VBox for vertical layout.
 *
 * @author g64974
 */
public class InfoPanel extends VBox {

    private final Controller controller;
    private Label currentPlayerLabel;
    private Label objectiveLabel;
    private Label progressLabel;
    private Button rotateButton;
    private Label extraTileLabel;
    private StackPane extraTilePreview;
    private HBox playerIconBox;
    private Label objectiveIconLabel;

    /**
     * Creates the info panel.
     *
     * @param controller the game controller
     */
    public InfoPanel(Controller controller) {
        this.controller = controller;

        setupStyle();
        createComponents();
    }

    /**
     * Sets up the visual style.
     */
    private void setupStyle() {
        setPadding(new Insets(20));
        setSpacing(15);
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(280);
        setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #34495e; -fx-border-width: 2px;");
    }

    /**
     * Creates UI components.
     */
    private void createComponents() {
        // Title
        Label title = new Label("Game Info");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Current player info with icon
        playerIconBox = new HBox(10);
        playerIconBox.setAlignment(Pos.CENTER_LEFT);

        currentPlayerLabel = new Label();
        currentPlayerLabel.setWrapText(true);
        currentPlayerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        playerIconBox.getChildren().add(currentPlayerLabel);

        // Objective to find with visual indicator
        Label objectiveTitle = new Label("Current Objective:");
        objectiveTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        objectiveLabel = new Label();
        objectiveLabel.setWrapText(true);
        objectiveLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");

        objectiveIconLabel = new Label("★");
        objectiveIconLabel.setStyle("-fx-font-size: 40px;");

        VBox objectiveBox = new VBox(5);
        objectiveBox.setAlignment(Pos.CENTER);
        objectiveBox.getChildren().addAll(objectiveTitle, objectiveIconLabel, objectiveLabel);
        objectiveBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 10; -fx-border-color: #ffc107; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        progressLabel = new Label();
        progressLabel.setStyle("-fx-font-size: 12px;");

        Separator sep1 = new Separator();

        // Extra tile section
        Label extraTileTitle = new Label("Extra Tile");
        extraTileTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        extraTileLabel = new Label();
        extraTileLabel.setWrapText(true);
        extraTileLabel.setStyle("-fx-font-size: 11px;");

        // Preview of extra tile
        extraTilePreview = new StackPane();
        extraTilePreview.setPrefSize(60, 60);
        extraTilePreview.setStyle("-fx-border-color: black; -fx-border-width: 2;");

        rotateButton = new Button("Rotate Extra Tile ↻");
        rotateButton.setMaxWidth(Double.MAX_VALUE);
        rotateButton.setOnAction(e -> handleRotate());

        Separator sep2 = new Separator();

        // Instructions
        Label instructions = new Label(
                "📋 How to Play:\n" +
                        "1. Rotate extra tile if needed\n" +
                        "2. Click arrow to insert tile\n" +
                        "3. Click green tile to move\n\n" +
                        "🎯 Find the objective shown above!\n" +
                        "★ = Objective on board\n" +
                        "🟢 Green = Can move there\n" +
                        "🟤 Beige = Can't reach"
        );
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d; -fx-background-color: #f8f9fa; -fx-padding: 8; -fx-border-radius: 3; -fx-background-radius: 3;");

        // Add all components
        getChildren().addAll(
                title,
                playerIconBox,
                objectiveBox,
                progressLabel,
                sep1,
                extraTileTitle,
                extraTilePreview,
                extraTileLabel,
                rotateButton,
                sep2,
                instructions
        );

        refresh();
    }


    /**
     * Handles rotate button click.
     */
    private void handleRotate() {
        try {
            if (!controller.getFacade().isTileInsertedThisTurn()) {
                controller.rotateExtraTile();
                refresh();
            } else {
                showError("Cannot rotate after tile insertion!");
            }
        } catch (Exception e) {
            showError("Cannot rotate: " + e.getMessage());
        }
    }

    /**
     * Refreshes the panel display.
     */
    public void refresh() {
        Facade facade = controller.getFacade();
        Player current = facade.getCurrentPlayer();

        if (current != null) {
            // Update player info with colored icon
            playerIconBox.getChildren().clear();

            Circle playerIcon = createPlayerIcon(facade.getPlayers().indexOf(current));
            currentPlayerLabel.setText("Current: " + current.getName());

            playerIconBox.getChildren().addAll(playerIcon, currentPlayerLabel);

            // Update objective with clear visual
            Objective obj = current.getCurrentObjective();
            if (obj != null) {
                objectiveLabel.setText("🔍 " + obj.getFrenchName());
                objectiveIconLabel.setText("★ " + obj.getFrenchName());
                objectiveIconLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");
            } else {
                objectiveLabel.setText("🏠 Return to START!");
                objectiveIconLabel.setText("🏠");
                objectiveIconLabel.setStyle("-fx-font-size: 40px;");
            }

            progressLabel.setText("📊 Progress: " + current.getProgress());
        }

        // Update extra tile info
        Tile extraTile = facade.getExtraTile();
        if (extraTile != null) {
            extraTileLabel.setText(
                    "Type: " + extraTile.getType() + "\n" +
                            "Rotation: " + extraTile.getRotation() + "°"
            );

            // Update preview
            updateExtraTilePreview(extraTile);

            // ⚠️ CRITICAL: Disable rotation for tiles with objectives
            rotateButton.setDisable(facade.isTileInsertedThisTurn() || extraTile.hasObjective());
        }
    }

    /**
     * Creates a colored player icon.
     *
     * @param playerIndex the player index (0-3)
     * @return the player icon circle
     */
    private Circle createPlayerIcon(int playerIndex) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        Circle icon = new Circle(12);
        icon.setFill(colors[playerIndex % colors.length]);
        icon.setStroke(Color.BLACK);
        icon.setStrokeWidth(2);
        return icon;
    }

    /**
     * Updates the extra tile preview.
     *
     * @param tile the extra tile
     */
    private void updateExtraTilePreview(Tile tile) {
        extraTilePreview.getChildren().clear();

        // Create a mini TileView
        // Note: We pass null for facade since this is just a preview
        TileView miniTile = new TileView(tile, new Position(0, 0), 60, null);
        extraTilePreview.getChildren().add(miniTile);
    }

    /**
     * Shows error message.
     *
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}