package com.example.practico3.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.practico3.observer.Observer

class AnimationBoard(context: Context?, attrs: AttributeSet?) :
    View(context, attrs), Observer {

    private lateinit var tetrisGame: TetrisGame
    private val blockSize = 100f
    private val boardRows = 20
    private val boardCols = 10
    private var isGameRunning = false
    private var gameSpeed: Long = 800
    private var onScoreChangeListener: OnScoreChangeListener? = null



    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = android.graphics.Color.GRAY
        strokeWidth = 2f
    }
    interface OnScoreChangeListener {
        fun onScoreChanged(newScore: Int)
    }

    fun setOnScoreChangeListener(listener: OnScoreChangeListener) {
        onScoreChangeListener = listener
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        tetrisGame = TetrisGame(this, blockSize, boardRows, boardCols,this::updateScore)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        tetrisGame.generateNewShape()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        tetrisGame.draw(canvas)

    }

    fun moveLeft() {
        tetrisGame.moveLeft()
    }

    fun moveRight() {
        tetrisGame.moveRight()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val screenWidth = width
            val touchX = event.x

            if (touchX < screenWidth / 2) {
                moveLeft()
            } else {
                moveRight()
            }

            return true
        }
        return super.onTouchEvent(event)
    }

    fun rotate(){
        tetrisGame.rotatePiece()
    }
    fun moveBottom() {
        tetrisGame.moveBottom()
    }

    override fun update() {
        invalidate()
    }

    private fun gameLoop() {
        Thread {
            while (isGameRunning) {
                try {
                    Thread.sleep(gameSpeed)
                    post {
                        tetrisGame.moveDown()
                    }
                } catch (e: InterruptedException) {

                }
            }
        }.start()
    }
    fun updateGameSpeed(newSpeed: Long) {
        gameSpeed = newSpeed
    }
    private fun updateScore(newScore: Int) {
        onScoreChangeListener?.onScoreChanged(newScore)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isGameRunning = true
        gameLoop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isGameRunning = false
    }

    fun stopGame() {
        isGameRunning = false

    }
}
