package g64974.dev3.labyrinthe.view;

import g64974.dev3.labyrinthe.controller.Controller;
import g64974.dev3.labyrinthe.model.Player;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.util.Observer;
import java.util.Observable;

/**
 * Main game view (MVC pattern with Observer).
 * Manages the overall layout: menu, board, and info panel.
 * Inherits from BorderPane for layout organization.
 * Implements Observer to receive model updates.
 *
 * @author g64974
 */
public class GameView extends BorderPane implements Observer {

    private final Controller controller;
    private BoardView boardView;
    private InfoPanel infoPanel;
    private MenuBar menuBar;

    /**
     * Creates the main game view.
     *
     * @param controller the game controller
     */
    public GameView(Controller controller) {
        this.controller = controller;

        initializeComponents();
        setupLayout();
        showWelcomeDialog();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        // Create menu bar
        menuBar = createMenuBar();

        // Info panel will be created after game starts
        infoPanel = null;

        // Board view will be created after game starts
        boardView = null;
    }

    /**
     * Sets up the layout.
     */
    private void setupLayout() {
        // Set menu at top
        setTop(menuBar);

        // Show welcome message in center
        Label welcomeLabel = new Label("Welcome to Labyrinth!\nUse File > New Game to start.");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-padding: 50px;");
        setCenter(welcomeLabel);
    }

    /**
     * Creates the menu bar.
     *
     * @return the menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");

        MenuItem newGameItem = new MenuItem("New Game");
        newGameItem.setOnAction(e -> showNewGameDialog());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newGameItem, new SeparatorMenuItem(), exitItem);

        // Edit menu
        Menu editMenu = new Menu("Edit");

        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e -> handleUndo());

        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e -> handleRedo());

        editMenu.getItems().addAll(undoItem, redoItem);

        // Help menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());

        helpMenu.getItems().add(aboutItem);

        bar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        return bar;
    }

    /**
     * Shows the welcome dialog at startup.
     */
    private void showWelcomeDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText("Welcome to Labyrinth!");
        alert.setContentText("Start a new game from the File menu.");
        alert.showAndWait();
    }

    /**
     * Shows dialog to start a new game.
     */
    private void showNewGameDialog() {
        Dialog<GameConfig> dialog = new Dialog<>();
        dialog.setTitle("New Game");
        dialog.setHeaderText("Configure New Game");

        // Create form
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label label = new Label("Number of AI players (1-3):");

        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(1, 2, 3);
        comboBox.setValue(1);

        Label difficultyLabel = new Label("AI Difficulty:");

        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("Random (Easy)", "Smart (Hard)");
        difficultyBox.setValue("Random (Easy)");

        content.getChildren().addAll(label, comboBox, difficultyLabel, difficultyBox);

        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType startButton = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startButton, ButtonType.CANCEL);

        // Convert result
        dialog.setResultConverter(button -> {
            if (button == startButton) {
                int aiCount = comboBox.getValue();
                boolean smartAI = difficultyBox.getValue().startsWith("Smart");
                return new GameConfig(aiCount, smartAI);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(config -> {
            int totalPlayers = config.aiCount + 1; // Human + AI
            startNewGame(totalPlayers, config.smartAI);
        });
    }

    /**
     * Starts a new game.
     *
     * @param numberOfPlayers total number of players
     * @param smartAI true for smart AI, false for random
     */
    private void startNewGame(int numberOfPlayers, boolean smartAI) {
        try {
            controller.startGame(numberOfPlayers);

            // Register as observer
            controller.getFacade().addObserver(this);

            // Create board view
            boardView = new BoardView(controller);

            // Create info panel
            infoPanel = new InfoPanel(controller);

            // Update layout
            setCenter(boardView);
            setRight(infoPanel);

            // Refresh display
            refresh();

        } catch (Exception e) {
            showError("Error starting game", e.getMessage());
        }
    }

    /**
     * Handles undo action.
     */
    private void handleUndo() {
        try {
            if (controller.canUndo()) {
                controller.undo();
                // refresh() will be called by update()
            }
        } catch (Exception e) {
            showError("Cannot undo", e.getMessage());
        }
    }

    /**
     * Handles redo action.
     */
    private void handleRedo() {
        try {
            if (controller.canRedo()) {
                controller.redo();
                // refresh() will be called by update()
            }
        } catch (Exception e) {
            showError("Cannot redo", e.getMessage());
        }
    }

    /**
     * Refreshes the display.
     */
    private void refresh() {
        if (boardView != null) {
            boardView.refresh();
        }
        if (infoPanel != null) {
            infoPanel.refresh();
        }

        // Check if game is over
        if (controller.getFacade().isGameOver()) {
            showGameOver();
        }
    }

    /**
     * Observer pattern: called when model changes.
     *
     * @param o the observable object
     * @param arg optional argument
     */
    @Override
    public void update(Observable o, Object arg) {
        // Update UI on JavaFX thread
        Platform.runLater(() -> {
            refresh();
        });
    }

    /**
     * Shows game over dialog.
     */
    private void showGameOver() {
        Player winner = controller.getFacade().getWinner();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished!");

        if (winner != null) {
            alert.setContentText("Winner: " + winner.getName() + "\n" +
                    "Progress: " + winner.getProgress());
        } else {
            alert.setContentText("Game was abandoned.");
        }

        alert.showAndWait();
    }

    /**
     * Shows about dialog.
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Labyrinth Game");
        alert.setContentText("Version 1.0\n" +
                "Project for 3dev3a course\n" +
                "Author: g64974");
        alert.showAndWait();
    }

    /**
     * Shows error dialog.
     *
     * @param title error title
     * @param message error message
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Inner class for game configuration.
     */
    private static class GameConfig {
        final int aiCount;
        final boolean smartAI;

        GameConfig(int aiCount, boolean smartAI) {
            this.aiCount = aiCount;
            this.smartAI = smartAI;
        }
    }
}