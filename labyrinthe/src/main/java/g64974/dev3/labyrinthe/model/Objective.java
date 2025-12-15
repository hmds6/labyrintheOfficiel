package g64974.dev3.labyrinthe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents all objectives in the Labyrinth game.
 * Each objective has an image, tile type, and BASE ROTATION.
 *
 * IMPORTANT: All images have been manually oriented to match TileType base directions:
 * - L images: NORTH + EAST openings (top + right)
 * - T images: NORTH + EAST + WEST openings (wall on SOUTH = bottom)
 *
 * Therefore, ALL baseRotation values are 0.
 *
 * @author g64974
 */
public enum Objective {

    // ==================================================
    // L OBJECTIF (6) -> dossier L.objectif
    // All images oriented to NORTH + EAST (baseRotation = 0)
    // ==================================================
    BUTTERFLY("Papillon", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_butterfly.jpg", 0),
    OWL("Hibou", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_hibou.jpg", 0),
    BEETLE("Insecte", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_insecte.jpg", 0),
    LIZARD("Lézard", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_lezard.jpg", 0),
    SPIDER("Araignée", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_spider.jpg", 0),
    RAT("Souris", ObjectiveTileType.L, ObjectiveFolder.L_OBJECTIF, "goal_mouse.jpg", 0),

    // ==================================================
    // T MOBILE CREATURES (6) -> dossier Goal
    // All images oriented to NORTH + EAST + WEST (baseRotation = 0)
    // ==================================================
    BAT("Chauve-souris", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_bat.jpg", 0),
    DRAGON("Dragon", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_dragon.jpg", 0),
    GHOST("Fantôme", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_ghost.jpg", 0),
    GHOST2("Fantôme 2", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_ghost2.jpg", 0),
    PIG("Cochon", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_pig.jpg", 0),
    WITCH("Cupidon", ObjectiveTileType.T, ObjectiveFolder.GOAL, "goal_witch.jpg", 0),

    // ==================================================
    // T FIXES (12) -> dossier Fixed_tiles
    // All images oriented to NORTH + EAST + WEST (baseRotation = 0)
    // ==================================================
    BOOK("Grimoire", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_book.jpg", 0),
    MONEY("Bourse", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_money.jpg", 0),
    MAP("Carte", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_map.jpg", 0),
    CROWN("Couronne", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_crown.jpg", 0),
    KEYS("Clefs", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_keys.jpg", 0),
    SKULL("Os", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_skull.jpg", 0),
    RING("Bague", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_ring.jpg", 0),
    CHEST("Coffre", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_coffre.jpg", 0),
    EMERALD("Saphir", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_saphir.jpg", 0),
    SWORD("Épée", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_sword.jpg", 0),
    CANDELABRA("Chandelier", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_candleholder.jpg", 0),
    HELMET("Heaume", ObjectiveTileType.T, ObjectiveFolder.FIXED_TILES, "goal_helmet.jpg", 0);

    // ==================================================
    // ATTRIBUTES
    // ==================================================
    private final String frenchName;
    private final ObjectiveTileType tileType;
    private final ObjectiveFolder folder;
    private final String imageFile;
    private final int baseRotation;

    /**
     * Creates an objective with specified properties.
     *
     * @param frenchName the French name of the objective
     * @param tileType the type of tile (L or T)
     * @param folder the folder containing the image
     * @param imageFile the image filename
     * @param baseRotation the rotation (always 0 since images are pre-oriented)
     */
    Objective(String frenchName,
              ObjectiveTileType tileType,
              ObjectiveFolder folder,
              String imageFile,
              int baseRotation) {
        this.frenchName = frenchName;
        this.tileType = tileType;
        this.folder = folder;
        this.imageFile = imageFile;
        this.baseRotation = baseRotation % 360;
    }

    // ==================================================
    // GETTERS
    // ==================================================

    /**
     * Gets the English name (derived from enum constant name).
     *
     * @return the English name
     */
    public String getEnglishName() {
        String name = name().replace('_', ' ');
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Gets the French name of this objective.
     *
     * @return the French name
     */
    public String getFrenchName() {
        return frenchName;
    }

    /**
     * Gets the tile type for this objective.
     *
     * @return L or T
     */
    public ObjectiveTileType getTileType() {
        return tileType;
    }

    /**
     * Gets the base rotation for this objective.
     * Always 0 since all images have been manually oriented correctly.
     *
     * @return rotation in degrees (always 0)
     */
    public int getBaseRotation() {
        return baseRotation;
    }

    /**
     * Gets the full image path for this objective.
     * Path format: /Tiles/{folder}/{imageFile}
     *
     * @return the resource path to the image
     */
    public String getImagePath() {
        return switch (folder) {
            case L_OBJECTIF -> "/Tiles/L.objectif/" + imageFile;
            case GOAL -> "/Tiles/Goal/" + imageFile;
            case FIXED_TILES -> "/Tiles/Fixed_tiles/" + imageFile;
        };
    }

    // ==================================================
    // FILTERS
    // ==================================================

    /**
     * Gets all L-type objectives.
     *
     * @return list of objectives that go on L tiles
     */
    public static List<Objective> lObjectives() {
        return filter(ObjectiveTileType.L);
    }

    /**
     * Gets all T-type objectives.
     *
     * @return list of objectives that go on T tiles
     */
    public static List<Objective> tObjectives() {
        return filter(ObjectiveTileType.T);
    }

    /**
     * Filters objectives by tile type.
     *
     * @param type the tile type to filter by
     * @return list of matching objectives
     */
    private static List<Objective> filter(ObjectiveTileType type) {
        List<Objective> result = new ArrayList<>();
        for (Objective obj : values()) {
            if (obj.tileType == type) {
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Gets all objectives from a specific folder.
     *
     * @param folder the folder to filter by
     * @return list of objectives in that folder
     */
    public static List<Objective> fromFolder(ObjectiveFolder folder) {
        List<Objective> result = new ArrayList<>();
        for (Objective obj : values()) {
            if (obj.folder == folder) {
                result.add(obj);
            }
        }
        return result;
    }

    // ==================================================
    // INNER ENUMS
    // ==================================================

    /**
     * The type of tile this objective appears on.
     */
    public enum ObjectiveTileType {
        /**
         * L-shaped tile (2 openings at right angle).
         */
        L,

        /**
         * T-shaped tile (3 openings, 1 wall).
         */
        T
    }

    /**
     * The folder where the objective's image is located.
     */
    public enum ObjectiveFolder {
        /**
         * L tiles with objectives (6 objectives).
         */
        L_OBJECTIF,

        /**
         * Mobile T tiles with creatures (6 objectives).
         */
        GOAL,

        /**
         * Fixed T tiles with objects (12 objectives).
         */
        FIXED_TILES
    }

    @Override
    public String toString() {
        return frenchName + " (" + tileType + ", " + baseRotation + "°)";
    }
}