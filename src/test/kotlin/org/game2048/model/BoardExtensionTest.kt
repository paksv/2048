package org.game2048.model

import kotlin.test.*

class BoardExtensionTest {
    @Test
    fun `test board has empty cells`() {
        // Empty board
        val emptyBoard = Board()
        assertTrue(emptyBoard.cells.flatten().all { it == 0 })
        
        // Board with some filled cells
        val partiallyFilledCells = Array(4) { Array(4) { 0 } }
        partiallyFilledCells[0][0] = 2
        partiallyFilledCells[1][1] = 4
        val partiallyFilledBoard = Board(cells = partiallyFilledCells)
        assertTrue(partiallyFilledBoard.cells.flatten().any { it == 0 })
        
        // Completely filled board
        val filledCells = Array(4) { r -> Array(4) { c -> 2 * (r + c + 1) } }
        val filledBoard = Board(cells = filledCells)
        assertFalse(filledBoard.cells.flatten().any { it == 0 })
    }
    
    @Test
    fun `test board contains winning value`() {
        // Board without winning value
        val regularCells = Array(4) { Array(4) { 0 } }
        regularCells[0][0] = 1024
        regularCells[1][1] = 512
        val regularBoard = Board(cells = regularCells)
        assertFalse(regularBoard.cells.flatten().any { it >= Board.WINNING_VALUE })
        
        // Board with winning value
        val winningCells = Array(4) { Array(4) { 0 } }
        winningCells[2][2] = Board.WINNING_VALUE
        val winningBoard = Board(cells = winningCells)
        assertTrue(winningBoard.cells.flatten().any { it >= Board.WINNING_VALUE })
        
        // Board with value greater than winning value
        val superWinningCells = Array(4) { Array(4) { 0 } }
        superWinningCells[3][3] = Board.WINNING_VALUE * 2
        val superWinningBoard = Board(cells = superWinningCells)
        assertTrue(superWinningBoard.cells.flatten().any { it >= Board.WINNING_VALUE })
    }
    
    @Test
    fun `test board deep copy`() {
        // Create a board with some values
        val originalCells = Array(4) { Array(4) { 0 } }
        originalCells[0][0] = 2
        originalCells[1][1] = 4
        originalCells[2][2] = 8
        val originalBoard = Board(cells = originalCells, score = 100)
        
        // Create a deep copy by creating a new Board with the same properties
        val copiedCells = originalCells.map { it.clone() }.toTypedArray()
        val copiedBoard = Board(cells = copiedCells, score = originalBoard.score)
        
        // Verify the copy is equal to the original
        assertEquals(originalBoard, copiedBoard)
        
        // Modify the copy and verify it doesn't affect the original
        copiedCells[0][0] = 16
        val modifiedBoard = Board(cells = copiedCells, score = copiedBoard.score + 50)
        
        assertNotEquals(originalBoard, modifiedBoard)
        assertEquals(2, originalBoard.cells[0][0])
        assertEquals(16, modifiedBoard.cells[0][0])
        assertEquals(100, originalBoard.score)
        assertEquals(150, modifiedBoard.score)
    }
}