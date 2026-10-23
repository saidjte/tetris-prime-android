package com.tetrisprime.kotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var scoreText: TextView
    private lateinit var levelText: TextView
    private lateinit var linesText: TextView

    private var lastUpdateTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.gameView)
        scoreText = findViewById(R.id.scoreText)
        levelText = findViewById(R.id.levelText)
        linesText = findViewById(R.id.linesText)

        setupControls()
    }

    private fun setupControls() {
        findViewById<Button>(R.id.btnLeft).setOnClickListener { gameView.gameEngine.move(-1) }
        findViewById<Button>(R.id.btnRight).setOnClickListener { gameView.gameEngine.move(1) }
        findViewById<Button>(R.id.btnRotate).setOnClickListener { gameView.gameEngine.rotate(1) }
        findViewById<Button>(R.id.btnSoftDrop).setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                gameView.gameEngine.softDrop()
            }
            false
        }
        findViewById<Button>(R.id.btnHardDrop).setOnClickListener { gameView.gameEngine.hardDrop() }
        findViewById<Button>(R.id.btnHold).setOnClickListener { gameView.gameEngine.hold() }
        findViewById<Button>(R.id.btnPause).setOnClickListener { gameView.gameEngine.togglePause() }
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            gameView.gameEngine.start()
        }
    }

    override fun onResume() {
        super.onResume()
        lastUpdateTime = System.currentTimeMillis()
        gameLoop()
    }

    private fun gameLoop() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                val delta = now - lastUpdateTime
                lastUpdateTime = now

                gameView.update(delta)

                val engine = gameView.gameEngine
                scoreText.text = "Score: ${engine.score}"
                levelText.text = "Level: ${engine.level}"
                linesText.text = "Lines: ${engine.lines}"

                handler.postDelayed(this, 16) // ~60 FPS
            }
        }
        handler.post(runnable)
    }
}
