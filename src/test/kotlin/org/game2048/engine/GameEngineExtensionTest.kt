package org.game2048.engine

import org.game2048.model.Board
import org.game2048.model.GameState
import org.game2048.model.Move
import kotlin.test.*

class GameEngineExtensionTest {
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
    fun `test right move`() {
        val cells = arrayOf(
            arrayOf(2, 0, 0, 0),
            arrayOf(2, 2, 0, 0),
            arrayOf(0, 0, 2, 2),
            arrayOf(2, 0, 2, 0)
        )
        initializeBoard(cells)

        val state = engine.makeMove(Move.RIGHT)
        val newCells = state.board.cells

        // First row: 2,0,0,0 -> 0,0,0,2
        assertEquals(2, newCells[0][3])
        
        // Second row: 2,2,0,0 -> 0,0,0,4
        assertEquals(4, newCells[1][3])
        
        // Third row: 0,0,2,2 -> 0,0,0,4
        assertEquals(4, newCells[2][3])
        
        // Fourth row: 2,0,2,0 -> 0,0,0,4
        assertEquals(4, newCells[3][3])
    }

    @Test
    fun `test down move`() {
        val cells = arrayOf(
            arrayOf(2, 2, 0, 2),
            arrayOf(0, 2, 0, 0),
            arrayOf(0, 0, 2, 2),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        val state = engine.makeMove(Move.DOWN)
        val newCells = state.board.cells

        // First column: 2,0,0,0 -> 0,0,0,2
        assertEquals(2, newCells[3][0])
        
        // Second column: 2,2,0,0 -> 0,0,0,4
        assertEquals(4, newCells[3][1])
        
        // Third column: 0,0,2,0 -> 0,0,0,2
        assertEquals(2, newCells[3][2])
        
        // Fourth column: 2,0,2,0 -> 0,0,0,4
        assertEquals(4, newCells[3][3])
    }

    @Test
    fun `test multiple merges in one move`() {
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
        
        // Check score is correct (4 + 8 = 12)
        assertEquals(12, state.board.score)
    }

    @Test
    fun `test no merge for different values`() {
        val cells = arrayOf(
            arrayOf(2, 4, 8, 16),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        val state = engine.makeMove(Move.LEFT)
        val newCells = state.board.cells

        // First row: 2,4,8,16 -> 2,4,8,16 (no merges, just one new tile)
        assertEquals(2, newCells[0][0])
        assertEquals(4, newCells[0][1])
        assertEquals(8, newCells[0][2])
        assertEquals(16, newCells[0][3])
        
        // Score should remain 0 as no merges occurred
        assertEquals(0, state.board.score)
    }

    @Test
    fun `test invalid move does not change state`() {
        val cells = arrayOf(
            arrayOf(2, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0),
            arrayOf(0, 0, 0, 0)
        )
        initializeBoard(cells)

        // LEFT is invalid as tiles are already at leftmost position
        assertFalse(engine.isValidMove(Move.LEFT))
        val initialState = engine.currentState
        val state = engine.makeMove(Move.LEFT)
        
        // State should not change
        assertEquals(initialState.board, state.board)
    }
}