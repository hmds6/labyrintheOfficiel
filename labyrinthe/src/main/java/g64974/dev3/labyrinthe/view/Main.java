package g64974.dev3.labyrinthe.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import g64974.dev3.labyrinthe.model.Facade;
import g64974.dev3.labyrinthe.controller.Controller;

/**
 * Main entry point for the JavaFX application.
 * Launches the Labyrinth game with graphical interface.
 *
 * @author g64974
 */
public class Main extends Application {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 650;

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        // Create Model-View-Controller
        Facade facade = new Facade();
        Controller controller = new Controller(facade);

        // Create the main view
        GameView gameView = new GameView(controller);

        // Create the scene
        Scene scene = new Scene(gameView, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Optional: Add CSS stylesheet
        // scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Configure the stage
        primaryStage.setTitle("Labyrinth Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}