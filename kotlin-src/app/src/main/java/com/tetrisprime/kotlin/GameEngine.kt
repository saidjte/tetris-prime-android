package com.tetrisprime.kotlin

/**
 * Core game state and logic.
 * Manages board, current piece, next pieces, hold, score, level.
 */
class GameEngine {
    val board = Board()
    private val bag = BagRandomizer()

    var currentPiece: Piece? = null
        private set
    var nextPieces: MutableList<PieceType> = mutableListOf()
        private set
    var holdPiece: PieceType? = null
        private set
    var holdUsed = false
        private set

    var score = 0L
        private set
    var lines = 0
        private set
    var level = 1
        private set
    var combo = -1
        private set
    var isGameOver = false
        private set
    var isPaused = false

    /** Drop speed in milliseconds per cell. */
    val dropIntervalMs: Long
        get() = Math.max(50L, (800 - (level - 1) * 60).toLong())

    private var dropAccumulator = 0L

    /** Initialize / restart the game. */
    fun start() {
        board.let { /* reset board */ }
        repeat(board.width * board.height) { idx ->
            // Clear board
        }
        score = 0
        lines = 0
        level = 1
        combo = -1
        isGameOver = false
        isPaused = false
        holdPiece = null
        holdUsed = false
        dropAccumulator = 0
        nextPieces.clear()
        repeat(5) { nextPieces.add(bag.next()) }
        spawnPiece()
    }

    /** Spawn the next piece from the queue. */
    private fun spawnPiece() {
        val type = nextPieces.removeAt(0)
        nextPieces.add(bag.next())
        val piece = Piece(type, x = 4, y = 0)
        if (board.isGameOver(piece)) {
            isGameOver = true
        } else {
            currentPiece = piece
            holdUsed = false
        }
    }

    /** Move piece left/right. Returns true if moved. */
    fun move(dx: Int): Boolean {
        val p = currentPiece ?: return false
        val moved = p.moved(dx, 0)
        return if (board.isValid(moved)) {
            currentPiece = moved
            true
        } else false
    }

    /** Rotate piece with SRS wall kicks. Returns true if rotated. */
    fun rotate(dir: Int): Boolean {
        val p = currentPiece ?: return false
        val oldRot = p.rotation
        val newRot = (oldRot + dir + 4) % 4
        val kicks = Piece.WALL_KICKS[oldRot to newRot] ?: listOf(0 to 0)
        for ((kx, ky) in kicks) {
            val test = Piece(p.type, p.x + kx, p.y - ky, newRot)
            if (board.isValid(test)) {
                currentPiece = test
                return true
            }
        }
        return false
    }

    /** Soft drop (move down one cell). Returns true if moved, false if locked. */
    fun softDrop(): Boolean {
        val p = currentPiece ?: return false
        val moved = p.moved(0, 1)
        return if (board.isValid(moved)) {
            currentPiece = moved
            score += 1L
            true
        } else {
            lockPiece()
            false
        }
    }

    /** Hard drop — instantly drop and lock. */
    fun hardDrop() {
        val p = currentPiece ?: return
        val ghost = board.ghostDrop(p)
        val dropDistance = ghost.y - p.y
        score += dropDistance.toLong() * 2L
        currentPiece = ghost
        lockPiece()
    }

    /** Hold the current piece (swap with held piece). */
    fun hold() {
        if (holdUsed) return
        val p = currentPiece ?: return
        val currentType = p.type
        if (holdPiece == null) {
            holdPiece = currentType
            spawnPiece()
        } else {
            val heldType = holdPiece!!
            holdPiece = currentType
            currentPiece = Piece(heldType, x = 4, y = 0)
        }
        holdUsed = true
    }

    /** Lock the current piece and clear lines. */
    private fun lockPiece() {
        val p = currentPiece ?: return
        board.lock(p)

        val cleared = board.clearLines()
        if (cleared > 0) {
            // Scoring: 100, 300, 500, 800 for 1-4 lines
            val baseScore = when (cleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                else -> 800
            }
            score += baseScore.toLong() * level
            lines += cleared
            combo++

            // Level up every 10 lines
            level = lines / 10 + 1

            // Combo bonus
            if (combo > 0) {
                score += 50L * combo * level
            }
        } else {
            combo = -1
        }

        currentPiece = null
        spawnPiece()
    }

    /** Update game state (called on each frame). deltaMs = milliseconds since last update. */
    fun update(deltaMs: Long) {
        if (isGameOver || isPaused || currentPiece == null) return
        dropAccumulator += deltaMs
        if (dropAccumulator >= dropIntervalMs) {
            dropAccumulator -= dropIntervalMs
            softDrop()
        }
    }

    /** Pause/unpause the game. */
    fun togglePause() {
        if (!isGameOver) isPaused = !isPaused
    }

    /** Get the ghost piece (shadow showing where piece will land). */
    fun getGhostPiece(): Piece? {
        val p = currentPiece ?: return null
        return board.ghostDrop(p)
    }
}
