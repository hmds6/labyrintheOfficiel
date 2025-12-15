package g64974.dev3.labyrinthe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Facade class.
 * Tests critical game operations and validation.
 *
 * @author g64974
 */
class FacadeTest {

    private Facade facade;

    @BeforeEach
    void setUp() {
        facade = new Facade();
        facade.startGame(2); // Start with 2 players for simplicity
    }

    /**
     * Test 1: Verify that tile insertion works correctly.
     * Critical for game functionality.
     */
    @Test
    void testInsertTile_ValidInsertion() {
        // Given: A started game
        Tile extraBefore = facade.getExtraTile();

        // When: Insert tile at valid position
        facade.insertTile(Direction.SOUTH, 1);

        // Then: Extra tile should have changed
        Tile extraAfter = facade.getExtraTile();
        assertNotEquals(extraBefore, extraAfter,
                "Extra tile should change after insertion");

        // And: Tile insertion flag should be set
        assertTrue(facade.isTileInsertedThisTurn(),
                "Tile inserted flag should be true after insertion");
    }

    /**
     * Test 2: Verify that opposite insertion is blocked.
     * Critical game rule implementation.
     */
    @Test
    void testCanInsertTile_OppositeInsertionBlocked() {
        // Given: First insertion from SOUTH at column 1
        facade.insertTile(Direction.SOUTH, 1);

        // When: Try to insert from opposite direction (NORTH, same column)
        boolean canInsertOpposite = facade.canInsertTile(Direction.NORTH, 1);

        // Then: Opposite insertion should be blocked
        assertFalse(canInsertOpposite,
                "Cannot insert at opposite position of last insertion");

        // But: Other insertions should be allowed
        assertTrue(facade.canInsertTile(Direction.SOUTH, 3),
                "Other insertions should still be allowed");
        assertTrue(facade.canInsertTile(Direction.EAST, 1),
                "Other insertions should still be allowed");
    }

    /**
     * Test 3: Verify that player movement validation works.
     * Critical for game rules enforcement.
     */
    @Test
    void testCanMoveTo_RequiresTileInsertion() {
        // Given: Game started but no tile inserted yet
        Position playerPos = facade.getCurrentPlayer().getPosition();

        // When: Try to move without inserting tile first
        boolean canMoveWithoutInsert = facade.canMoveTo(playerPos);

        // Then: Movement should not be allowed
        assertFalse(canMoveWithoutInsert,
                "Cannot move before inserting a tile");

        // When: Insert a tile
        facade.insertTile(Direction.SOUTH, 1);

        // Then: Movement should now be possible (at least to current position)
        assertTrue(facade.canMoveTo(playerPos),
                "Can move after tile insertion");
    }

    /**
     * Test 4: Verify that reachable positions are calculated correctly.
     * Critical for player movement validation.
     */
    @Test
    void testGetReachablePositions_AfterInsertion() {
        // Given: Tile has been inserted
        facade.insertTile(Direction.SOUTH, 1);

        // When: Get reachable positions
        var reachable = facade.getReachablePositions();

        // Then: Should include at least the current position
        Position currentPos = facade.getCurrentPlayer().getPosition();
        assertTrue(reachable.contains(currentPos),
                "Reachable positions should include current position");

        // And: Should not be empty
        assertFalse(reachable.isEmpty(),
                "Should have at least one reachable position");
    }

    /**
     * Test 5: Verify that game state transitions work correctly.
     * Critical for game flow management.
     */
    @Test
    void testGameStateTransitions() {
        // Given: Newly started game
        assertEquals(GameState.PLAYING, facade.getGameState(),
                "Game should be in PLAYING state after start");

        // When: Complete a turn (insert + move)
        facade.insertTile(Direction.SOUTH, 1);
        Position currentPos = facade.getCurrentPlayer().getPosition();
        facade.movePlayer(currentPos); // Move to same position (valid)

        // Then: Game should still be playing
        assertEquals(GameState.PLAYING, facade.getGameState(),
                "Game should remain in PLAYING state");

        // And: Next player's turn
        // (Player index changes after movePlayer)
    }

    /**
     * Test 6 (BONUS): Verify that tile rotation works.
     */
    @Test
    void testRotateExtraTile() {
        // Given: Extra tile with initial rotation
        Tile extra = facade.getExtraTile();
        int initialRotation = extra.getRotation();

        // When: Rotate extra tile (if it's rotatable)
        if (!extra.hasObjective() && !extra.isFixed()) {
            facade.rotateExtraTile();

            // Then: Rotation should have changed
            int newRotation = facade.getExtraTile().getRotation();
            assertEquals((initialRotation + 90) % 360, newRotation,
                    "Rotation should increase by 90 degrees");
        }
        // Note: If extra has objective, rotation won't change (expected)
    }

    /**
     * Test 7 (BONUS): Verify that invalid moves are rejected.
     */
    @Test
    void testMovePlayer_InvalidPosition() {
        // Given: Tile inserted
        facade.insertTile(Direction.SOUTH, 1);

        // When: Try to move to unreachable position
        Position unreachable = new Position(6, 6); // Likely unreachable from (0,0)

        // Then: If not reachable, should throw exception
        if (!facade.getReachablePositions().contains(unreachable)) {
            assertThrows(IllegalArgumentException.class, () -> {
                facade.movePlayer(unreachable);
            }, "Should throw exception for unreachable position");
        }
    }
}