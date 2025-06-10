package org.game2048.engine

import org.game2048.model.Board
import org.game2048.model.GameState
import org.game2048.model.Move
import kotlin.test.*

class GameEngineEdgeCaseTest {
    private lateinit var engine: GameEngine

    @BeforeTest
    fun setup() {
        engine = GameEngineImpl()
    }

    private fun initializeBoard(cells: Array<Array<Int>>) {
        engine = GameEngineImpl()
        engine::class.java.getDeclaredField("_currentState").apply {
            isAccessible = true
            set(engine, GameState.Playing(Board(size = cells.size, cells = cells)))
        }
    }

    @Test
    fun `test merge only once per move`() {
        // This tests that tiles only merge once per move
        // For example, [4,4,4,0] should become [8,4,0,x] not [0,0,12,x]
        val cells = arrayOf(
            arrayOf(4, 4, 4, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        val state = engine.makeMove(Move.LEFT)
        val newCells = state.board.cells

        // First row: 4,4,4,0 -> 8,4,0,x
        assertEquals(8, newCells[0][0])
        assertEquals(4, newCells[0][1])
        
        // Check that we have the right number of non-zero tiles on the board
        val nonZeroCount = newCells.flatten().count { it != 0 }
        assertEquals(3, nonZeroCount) // 8 + 4 + new random tile
    }

    @Test
    fun `test merge chain reaction`() {
        // This tests that merges happen in the correct order
        // For example, [2,2,4,4] should become [4,8,0,x] not [4,0,8,x]
        val cells = arrayOf(
            arrayOf(2, 2, 4, 4),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        val state = engine.makeMove(Move.LEFT)
        val newCells = state.board.cells

        // First row: 2,2,4,4 -> 4,8,0,x
        assertEquals(4, newCells[0][0])
        assertEquals(8, newCells[0][1])
        assertEquals(0, newCells[0][2])
    }

    @Test
    fun `test move with full board`() {
        // Test a move with a completely full board
        val cells = arrayOf(
            arrayOf(2, 4, 2, 4),
            arrayOf(4, 2, 4, 2),
            arrayOf(2, 4, 2, 4),
            arrayOf(4, 2, 4, 2)
        )
        initializeBoard(cells)

        // This board has no valid moves
        assertFalse(engine.isValidMove(Move.LEFT))
        assertFalse(engine.isValidMove(Move.RIGHT))
        assertFalse(engine.isValidMove(Move.UP))
        assertFalse(engine.isValidMove(Move.DOWN))

        // Making a move should result in a Lost state
        val state = engine.makeMove(Move.LEFT)
        assertTrue(state is GameState.Lost)
    }

    @Test
    fun `test move with almost full board`() {
        // Test a move with an almost full board that has one valid move
        val cells = arrayOf(
            arrayOf(2, 4, 2, 4),
            arrayOf(4, 2, 4, 2),
            arrayOf(2, 4, 2, 4),
            arrayOf(4, 2, 4, 0)  // One empty cell
        )
        initializeBoard(cells)

        // RIGHT is valid (to move the 4 into the empty cell)
        assertTrue(engine.isValidMove(Move.RIGHT))
        
        // Making the valid move
        val state = engine.makeMove(Move.RIGHT)
        assertTrue(state is GameState.Playing)
        assertEquals(4, state.board.cells[3][3])  // The 4 moved to the empty cell
    }

    @Test
    fun `test move with only one tile`() {
        // Test a move with only one tile on the board
        val cells = arrayOf(
            arrayOf(2, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        // LEFT is not valid as the tile is already at the leftmost position
        assertFalse(engine.isValidMove(Move.LEFT))
        
        // RIGHT is valid
        assertTrue(engine.isValidMove(Move.RIGHT))
        val stateRight = engine.makeMove(Move.RIGHT)
        assertEquals(2, stateRight.board.cells[0][3])  // The 2 moved to the rightmost position
        
        // Reset and try UP/DOWN
        initializeBoard(cells)
        
        // UP is not valid as the tile is already at the topmost position
        assertFalse(engine.isValidMove(Move.UP))
        
        // DOWN is valid
        assertTrue(engine.isValidMove(Move.DOWN))
        val stateDown = engine.makeMove(Move.DOWN)
        assertEquals(2, stateDown.board.cells[3][0])  // The 2 moved to the bottommost position
    }
}