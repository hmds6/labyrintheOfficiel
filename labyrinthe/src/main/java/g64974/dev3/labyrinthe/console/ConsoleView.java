package g64974.dev3.labyrinthe.console;

import g64974.dev3.labyrinthe.model.*;

import java.util.Scanner;
import java.util.Set;

/**
 * Console view for the Labyrinth game.
 * Allows playing the game in text mode.
 *
 * @author g64974
 */
public class ConsoleView {

    private final Facade facade;
    private final Scanner scanner;

    /**
     * Creates a new console view.
     */
    public ConsoleView() {
        this.facade = new Facade();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts and runs the game loop.
     */
    public void run() {
        displayWelcome();

        int nbPlayers = askNumberOfPlayers();
        facade.startGame(nbPlayers);

        System.out.println("\n✅ Game started with " + nbPlayers + " players!\n");

        while (!facade.isGameOver()) {
            playTurn();
        }

        displayWinner();
        scanner.close();
    }

    /**
     * Displays welcome message.
     */
    private void displayWelcome() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   LABYRINTH - Console Version     ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Asks the user for the number of players.
     *
     * @return number of players (2-4)
     */
    private int askNumberOfPlayers() {
        while (true) {
            System.out.print("Number of players (2-4): ");
            try {
                int nb = Integer.parseInt(scanner.nextLine().trim());
                if (nb >= 2 && nb <= 4) {
                    return nb;
                }
                System.out.println("❌ Please enter a number between 2 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Plays one complete turn for the current player.
     */
    private void playTurn() {
        Player current = facade.getCurrentPlayer();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎮 " + current.getName() + "'s turn");
        System.out.println("📍 Position: " + current.getPosition());
        System.out.println("🎯 Current objective: " + current.getCurrentObjective());
        System.out.println("📊 Progress: " + current.getProgress());
        System.out.println("=".repeat(50));

        displayBoard();

        // Action 1: Insert tile
        insertTileAction();

        System.out.println("\n📋 Board after insertion:");
        displayBoard();

        // Action 2: Move player
        movePlayerAction();
    }

    /**
     * Handles tile insertion action.
     */
    private void insertTileAction() {
        System.out.println("\n🔄 EXTRA TILE:");
        displayTile(facade.getExtraTile());

        // Ask if player wants to rotate
        while (true) {
            System.out.print("\nRotate extra tile? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("n") || choice.isEmpty()) {
                break;
            } else if (choice.equals("y")) {
                facade.rotateExtraTile();
                System.out.println("🔄 Tile rotated!");
                displayTile(facade.getExtraTile());
            } else {
                System.out.println("❌ Invalid choice. Enter 'y' to rotate or 'n' to continue.");
            }
        }

        // Ask where to insert
        Direction direction = null;
        int index = -1;

        while (direction == null || index == -1) {
            System.out.println("\n📥 Insert tile:");
            System.out.println("  Directions: NORTH (n), SOUTH (s), EAST (e), WEST (w)");
            System.out.println("  Valid indices: 1, 3, 5");

            System.out.print("Direction: ");
            String dirInput = scanner.nextLine().trim().toLowerCase();
            direction = parseDirection(dirInput);

            if (direction == null) {
                System.out.println("❌ Invalid direction. Use n, s, e, or w.");
                continue;
            }

            System.out.print("Index (1/3/5): ");
            try {
                index = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid index. Enter 1, 3, or 5.");
                continue;
            }

            if (!facade.canInsertTile(direction, index)) {
                System.out.println("❌ Cannot insert here (invalid index or reverse of last move). Try again.");
                direction = null;
                index = -1;
            }
        }

        facade.insertTile(direction, index);
        System.out.println("✅ Tile inserted at " + direction + " " + index);
    }

    /**
     * Handles player movement action.
     */
    private void movePlayerAction() {
        // FIX: Utiliser facade.getReachablePositions() au lieu de currentPlayer
        Set<Position> reachable = facade.getReachablePositions();

        System.out.println("\n🚶 MOVE PLAYER:");
        System.out.println("Reachable positions (" + reachable.size() + "):");

        // Display all reachable positions
        if (reachable.isEmpty()) {
            System.out.println("  ⚠️  No reachable positions! You're stuck!");
        } else {
            int count = 0;
            for (Position pos : reachable) {
                System.out.print("  " + pos);
                count++;
                if (count % 7 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
        }

        Position destination = null;

        while (destination == null) {
            System.out.print("\nEnter destination (row col) or 's' to stay: ");
            String input = scanner.nextLine().trim();

            // Allow staying in place
            if (input.equalsIgnoreCase("s")) {
                destination = facade.getCurrentPlayer().getPosition();
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("❌ Format: row col (e.g., 3 4) or 's' to stay");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                destination = new Position(row, col);

                if (!facade.canMoveTo(destination)) {
                    System.out.println("❌ Cannot move there. Choose from reachable positions above.");
                    destination = null;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid position. Use format: row col");
                destination = null;
            }
        }

        facade.movePlayer(destination);
        System.out.println("✅ Player moved to " + destination);

        // Check if objective collected
        Player current = facade.getCurrentPlayer();
        Tile tile = facade.getTileAt(destination);
        if (tile.hasObjective()) {
            Objective objHere = tile.getObjective();
            Objective objWanted = current.getCurrentObjective();

            if (objHere == objWanted) {
                System.out.println("\n🎉🎉🎉 OBJECTIVE COLLECTED: " + objHere.getEnglishName() + "! 🎉🎉🎉");
                System.out.println("New progress: " + current.getProgress());
            }
        }
    }

    /**
     * Displays the game board.
     */
    private void displayBoard() {
        System.out.println("\n    0   1   2   3   4   5   6");
        System.out.println("  ┌───┬───┬───┬───┬───┬───┬───┐");

        for (int row = 0; row < 7; row++) {
            System.out.print(row + " │");

            for (int col = 0; col < 7; col++) {
                Position pos = new Position(row, col);
                Tile tile = facade.getTileAt(pos);

                String symbol = getTileSymbol(tile);

                // Check if a player is here
                String playerMarker = "";
                for (int i = 0; i < facade.getPlayers().size(); i++) {
                    Player p = facade.getPlayers().get(i);
                    if (p.getPosition().equals(pos)) {
                        playerMarker = String.valueOf(i + 1);
                        break;
                    }
                }

                // Check if there's an objective here
                String objMarker = "";
                if (tile.hasObjective()) {
                    objMarker = "*";
                }

                if (!playerMarker.isEmpty()) {
                    System.out.print(" " + playerMarker + symbol + "│");
                } else if (!objMarker.isEmpty()) {
                    System.out.print(objMarker + symbol + " │");
                } else {
                    System.out.print(" " + symbol + " │");
                }
            }

            System.out.println();

            if (row < 6) {
                System.out.println("  ├───┼───┼───┼───┼───┼───┼───┤");
            }
        }

        System.out.println("  └───┴───┴───┴───┴───┴───┴───┘");
        System.out.println("Legend: 1,2,3,4=Players | *=Objective");
    }

    /**
     * Gets a symbol representing a tile.
     *
     * @param tile the tile
     * @return symbol character
     */
    private String getTileSymbol(Tile tile) {
        if (tile == null) {
            return "?";
        }

        Set<Direction> openings = tile.getOpenings();

        // T tiles (3 openings)
        if (openings.size() == 3) {
            if (!openings.contains(Direction.NORTH)) return "┬";
            if (!openings.contains(Direction.SOUTH)) return "┴";
            if (!openings.contains(Direction.EAST)) return "├";
            if (!openings.contains(Direction.WEST)) return "┤";
        }

        // L tiles (2 openings at right angle)
        if (openings.size() == 2) {
            if (openings.contains(Direction.NORTH) && openings.contains(Direction.EAST)) return "└";
            if (openings.contains(Direction.EAST) && openings.contains(Direction.SOUTH)) return "┌";
            if (openings.contains(Direction.SOUTH) && openings.contains(Direction.WEST)) return "┐";
            if (openings.contains(Direction.WEST) && openings.contains(Direction.NORTH)) return "┘";

            // I tiles (2 opposite openings)
            if (openings.contains(Direction.NORTH) && openings.contains(Direction.SOUTH)) return "│";
            if (openings.contains(Direction.EAST) && openings.contains(Direction.WEST)) return "─";
        }

        return "•";
    }

    /**
     * Displays a single tile info.
     *
     * @param tile the tile
     */
    private void displayTile(Tile tile) {
        System.out.println("  Type: " + tile.getType());
        System.out.println("  Rotation: " + tile.getRotation() + "°");
        System.out.println("  Openings: " + tile.getOpenings());
        if (tile.hasObjective()) {
            System.out.println("  Objective: " + tile.getObjective().getEnglishName());
        }
    }

    /**
     * Parses a direction from user input.
     *
     * @param input user input
     * @return Direction or null if invalid
     */
    private Direction parseDirection(String input) {
        switch (input) {
            case "n":
            case "north":
                return Direction.NORTH;
            case "s":
            case "south":
                return Direction.SOUTH;
            case "e":
            case "east":
                return Direction.EAST;
            case "w":
            case "west":
                return Direction.WEST;
            default:
                return null;
        }
    }

    /**
     * Displays the winner.
     */
    private void displayWinner() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🏆 GAME OVER!");

        Player winner = facade.getWinner();
        if (winner != null) {
            System.out.println("🎉 Winner: " + winner.getName() + "!");
            System.out.println("📊 Final progress: " + winner.getProgress());
        } else {
            System.out.println("Game abandoned.");
        }

        System.out.println("=".repeat(50));
    }

    /**
     * Main method to run the console game.
     */
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        view.run();
    }
}