package com.tetrisprime.kotlin

/**
 * The game board (10 columns x 20 rows).
 * Stores which cells are filled and with which piece type.
 */
class Board(val width: Int = 10, val height: Int = 20) {
    private val grid: Array<CharArray?> = arrayOfNulls(width * height)

    operator fun get(x: Int, y: Int): Char? {
        if (x < 0 || x >= width || y < 0 || y >= height) return null
        return grid[y * width + x]
    }

    operator fun set(x: Int, y: Int, value: Char?) {
        if (x in 0 until width && y in 0 until height) {
            grid[y * width + x] = value
        }
    }

    /** Check if a piece position is valid (no collision, in bounds). */
    fun isValid(piece: Piece): Boolean {
        return piece.cells().all { (x, y) ->
            x in 0 until width && y < height && (y < 0 || this[x, y] == null)
        }
    }

    /** Lock a piece into the board. */
    fun lock(piece: Piece) {
        val typeChar = piece.type.char
        piece.cells().forEach { (x, y) ->
            if (y in 0 until height && x in 0 until width) {
                this[x, y] = typeChar
            }
        }
    }

    /** Clear completed lines. Returns the number of lines cleared. */
    fun clearLines(): Int {
        var cleared = 0
        for (y in height - 1 downTo 0) {
            var full = true
            for (x in 0 until width) {
                if (this[x, y] == null) {
                    full = false
                    break
                }
            }
            if (full) {
                // Shift everything above down
                for (ny in y downTo 1) {
                    for (x in 0 until width) {
                        this[x, ny] = this[x, ny - 1]
                    }
                }
                for (x in 0 until width) {
                    this[x, 0] = null
                }
                cleared++
                // Re-check same row (now has new content)
            }
        }
        return cleared
    }

    /** Check if the board is in game-over state (piece spawned colliding). */
    fun isGameOver(piece: Piece): Boolean = !isValid(piece)

    /** Get the ghost piece position (where piece would land). */
    fun ghostDrop(piece: Piece): Piece {
        var p = piece
        while (isValid(p.moved(0, 1))) {
            p = p.moved(0, 1)
        }
        return p
    }
}
