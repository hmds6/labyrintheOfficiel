package g64974.dev3.labyrinthe.view;

import g64974.dev3.labyrinthe.model.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.effect.ColorAdjust;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.List;

/**
 * View for a single tile using images.
 * Displays tile image, objectives, and player positions.
 *
 * UPDATED: Corner tiles now use generic L_tile.jpg with rotation
 * instead of specific corner images.
 *
 * @author g64974
 */
public class TileView extends StackPane {

    private static final String BASE_PATH = "/Tiles/";

    private Tile tile;
    private Position position;
    private final int size;

    public TileView(Tile tile, Position position, int size, Facade facade) {
        this.tile = tile;
        this.position = position;
        this.size = size;

        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);

        render(facade);
    }

    public void update(Tile tile, Position position, Facade facade) {
        this.tile = tile;
        this.position = position;
        render(facade);
    }

    private void render(Facade facade) {
        getChildren().clear();

        if (tile == null) {
            return;
        }

        // Load and display tile image
        ImageView tileImage = loadTileImage();

        // Apply green tint for reachable tiles
        if (facade != null && facade.isTileInsertedThisTurn()) {
            boolean isReachable = facade.getReachablePositions().contains(position);
            if (isReachable) {
                applyGreenTint(tileImage);
            }
        }

        getChildren().add(tileImage);

        // Show red star if this is the target objective
        if (tile.hasObjective() && facade != null) {
            drawTargetMarker(facade);
        }

        // Show players
        if (facade != null) {
            drawPlayers(facade);
        }

        setAlignment(Pos.CENTER);
    }

    // ======================
    // IMAGE LOADING
    // ======================

    private ImageView loadTileImage() {
        Image image = loadImage();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);

        // ⚠️ IMPORTANT: Apply rotation for ALL tiles
        // - Corner tiles are now generic L tiles with rotation
        // - Tiles with objectives have rotation = baseRotation (0)
        // - Generic tiles can have any rotation (0, 90, 180, 270)
        imageView.setRotate(tile.getRotation());

        return imageView;
    }

    private Image loadImage() {
        String imagePath = getImagePath();

        try {
            var stream = getClass().getResourceAsStream(imagePath);
            if (stream == null) {
                System.err.println("⚠️ Image not found: " + imagePath);
                return createFallbackImage();
            }
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("❌ Error loading image: " + imagePath);
            e.printStackTrace();
            return createFallbackImage();
        }
    }

    /**
     * Gets the path to the image for this tile.
     *
     * UPDATED: Corner tiles now use L_tile.jpg instead of specific corner images.
     */
    private String getImagePath() {
        // Tiles with objectives use the objective's image
        if (tile.hasObjective()) {
            return tile.getObjective().getImagePath();
        }

        // All other tiles (including corners) use generic images based on type
        if (tile.getType() == TileType.I) {
            return BASE_PATH + "I_Shape.jpg";
        } else if (tile.getType() == TileType.L) {
            // ⚠️ CHANGE: Corner tiles now also use L_tile.jpg
            return BASE_PATH + "L_tile.jpg";
        } else if (tile.getType() == TileType.T) {
            // Should not happen (all T have objectives), but for safety
            return BASE_PATH + "L_tile.jpg";
        }

        // Fallback
        return BASE_PATH + "L_tile.jpg";
    }

    private Image createFallbackImage() {
        return null;
    }

    // ======================
    // VISUAL EFFECTS
    // ======================

    private void applyGreenTint(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.3);
        colorAdjust.setSaturation(0.5);
        colorAdjust.setBrightness(0.2);
        imageView.setEffect(colorAdjust);
    }

    private void drawTargetMarker(Facade facade) {
        Player currentPlayer = facade.getCurrentPlayer();
        if (currentPlayer == null) {
            return;
        }

        Objective playerTarget = currentPlayer.getCurrentObjective();
        Objective tileObjective = tile.getObjective();

        if (playerTarget != null && playerTarget.equals(tileObjective)) {
            Label star = new Label("★");
            star.setStyle(
                    "-fx-font-size: 36px; " +
                            "-fx-text-fill: #FF1744; " +
                            "-fx-font-weight: bold; " +
                            "-fx-effect: dropshadow(gaussian, red, 20, 0.8, 0, 0);"
            );

            FadeTransition fade = new FadeTransition(Duration.seconds(0.8), star);
            fade.setFromValue(1.0);
            fade.setToValue(0.3);
            fade.setCycleCount(FadeTransition.INDEFINITE);
            fade.setAutoReverse(true);
            fade.play();

            getChildren().add(star);
        }
    }

    // ======================
    // PLAYER RENDERING
    // ======================

    private void drawPlayers(Facade facade) {
        List<Player> players = facade.getPlayers();
        HBox playerBox = new HBox(4);
        playerBox.setAlignment(Pos.BOTTOM_CENTER);

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getPosition().equals(position)) {
                Color color = getColorForStartPosition(player.getStartPosition());

                Label playerLabel = new Label(String.valueOf(i + 1));
                playerLabel.setStyle(
                        "-fx-background-color: " + toHex(color) + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5;" +
                                "-fx-font-size: 12px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-radius: 50%;" +
                                "-fx-background-radius: 50%;" +
                                "-fx-min-width: 20px;" +
                                "-fx-min-height: 20px;" +
                                "-fx-alignment: center;"
                );

                playerBox.getChildren().add(playerLabel);
            }
        }

        if (!playerBox.getChildren().isEmpty()) {
            getChildren().add(playerBox);
        }
    }

    private Color getColorForStartPosition(Position startPos) {
        int row = startPos.getRow();
        int col = startPos.getColumn();

        if (row == 0 && col == 0) return Color.RED;
        if (row == 0 && col == 6) return Color.BLUE;
        if (row == 6 && col == 6) return Color.GREEN;
        if (row == 6 && col == 0) return Color.YELLOW;

        return Color.GRAY;
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int)(c.getRed() * 255),
                (int)(c.getGreen() * 255),
                (int)(c.getBlue() * 255));
    }
}