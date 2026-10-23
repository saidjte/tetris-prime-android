package com.tetrisprime.kotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Custom View that renders the Tetris board and current piece.
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val engine = GameEngine()
    private var cellSize = 0f
    private var boardOffsetX = 0f
    private var boardOffsetY = 0f

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(30, 255, 255, 255)
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val blockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ghostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val bgPaint = Paint().apply {
        color = Color.argb(180, 11, 8, 30)
    }

    val gameEngine: GameEngine get() = engine

    /** Piece colors. */
    private val pieceColors = mapOf(
        'I' to Color.parseColor("#22D3EE"),
        'O' to Color.parseColor("#FBBF24"),
        'T' to Color.parseColor("#A78BFA"),
        'S' to Color.parseColor("#34D399"),
        'Z' to Color.parseColor("#FB7185"),
        'J' to Color.parseColor("#60A5FA"),
        'L' to Color.parseColor("#FB923C")
    )

    init {
        engine.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val boardWidth = engine.board.width
        val boardHeight = engine.board.height
        cellSize = Math.min(w.toFloat() / boardWidth, h.toFloat() / boardHeight)
        boardOffsetX = (w - cellSize * boardWidth) / 2
        boardOffsetY = (h - cellSize * boardHeight) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#0B081E"))

        // Board background
        canvas.drawRect(
            boardOffsetX, boardOffsetY,
            boardOffsetX + cellSize * engine.board.width,
            boardOffsetY + cellSize * engine.board.height,
            bgPaint
        )

        // Grid lines
        for (x in 0..engine.board.width) {
            canvas.drawLine(
                boardOffsetX + x * cellSize, boardOffsetY,
                boardOffsetX + x * cellSize, boardOffsetY + cellSize * engine.board.height,
                gridPaint
            )
        }
        for (y in 0..engine.board.height) {
            canvas.drawLine(
                boardOffsetX, boardOffsetY + y * cellSize,
                boardOffsetX + cellSize * engine.board.width, boardOffsetY + y * cellSize,
                gridPaint
            )
        }

        // Locked blocks
        for (y in 0 until engine.board.height) {
            for (x in 0 until engine.board.width) {
                engine.board[x, y]?.let { type ->
                    val color = pieceColors[type] ?: Color.WHITE
                    drawBlock(canvas, x, y, color)
                }
            }
        }

        // Ghost piece
        engine.getGhostPiece()?.let { ghost ->
            val color = pieceColors[ghost.type.char] ?: Color.WHITE
            ghostPaint.color = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color))
            ghost.cells().forEach { (x, y) ->
                if (y >= 0) {
                    canvas.drawRect(
                        boardOffsetX + x * cellSize + 2,
                        boardOffsetY + y * cellSize + 2,
                        boardOffsetX + (x + 1) * cellSize - 2,
                        boardOffsetY + (y + 1) * cellSize - 2,
                        ghostPaint
                    )
                }
            }
        }

        // Current piece
        engine.currentPiece?.let { piece ->
            val color = pieceColors[piece.type.char] ?: Color.WHITE
            piece.cells().forEach { (x, y) ->
                if (y >= 0) {
                    drawBlock(canvas, x, y, color)
                }
            }
        }
    }

    private fun drawBlock(canvas: Canvas, x: Int, y: Int, color: Int) {
        blockPaint.color = color
        val left = boardOffsetX + x * cellSize + 1
        val top = boardOffsetY + y * cellSize + 1
        val right = boardOffsetX + (x + 1) * cellSize - 1
        val bottom = boardOffsetY + (y + 1) * cellSize - 1
        canvas.drawRect(left, top, right, bottom, blockPaint)

        // Inner highlight
        blockPaint.color = Color.argb(60, 255, 255, 255)
        canvas.drawRect(left, top, right, top + (bottom - top) * 0.2f, blockPaint)
    }

    fun update(deltaMs: Long) {
        engine.update(deltaMs)
        invalidate()
    }
}
