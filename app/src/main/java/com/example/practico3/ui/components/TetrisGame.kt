package com.example.practico3.ui.components

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import com.example.practico3.models.TetrisShape
import com.example.practico3.ui.activities.GameOverActivity
import java.util.Random

class TetrisGame(
    private val animationBoard: AnimationBoard,
    private val blockSize: Float,
    private val rows: Int,
    private val cols: Int,
    private val updateScoreCallback: (Int) -> Unit
) {
    private var currentShape: TetrisShape? = null
    private var currentCoordinates: Array<Pair<Int, Int>> = arrayOf()
    private var currentX: Float = 0f
    private var currentY: Float = 0f
    private val random = Random()
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var currentColor: Int = Color.WHITE
    private var gameBoard: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }
    private val possibleColors = listOf(
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
        Color.BLUE,
        Color.rgb(255, 165, 0)
    )
    private var currentScore: Int = 0
    private var gameSpeed: Long = 1000
    private var level: Int = 1

    fun getCurrentScore(): Int {
        return currentScore
    }

    fun handleLineClear(linesCleared: Int) {
        if (linesCleared > 0) {
            var scoreIncrement = 10 * linesCleared
            if (linesCleared >= 2) {
                scoreIncrement *= linesCleared
            }
            currentScore += scoreIncrement
            println("Líneas eliminadas: $linesCleared, Puntaje actual: $currentScore")
            animationBoard.post {
                Toast.makeText(animationBoard.context, "¡Puntaje +$scoreIncrement!", Toast.LENGTH_SHORT).show()
            }
            updateScoreCallback(currentScore)


            if (currentScore >= 30) {
                levelUp()
                animationBoard.post {
                    Toast.makeText(animationBoard.context, "¡Nivel $level!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun levelUp() {
        level++
        gameSpeed -= 500
        if (gameSpeed < 100) gameSpeed = 100
        println("¡Subiste al nivel $level! Nueva velocidad: $gameSpeed")
        resetBoard()
        animationBoard.updateGameSpeed(gameSpeed)
    }

    private fun resetBoard() {
        gameBoard = Array(rows) { IntArray(cols) { 0 } }
        generateNewShape()
        animationBoard.invalidate()
    }
    private fun checkAndClearLines(): Int {
        var linesCleared = 0
        val rowsToRemove = mutableListOf<Int>()

        for (i in rows - 1 downTo 0) {
            if (gameBoard[i].all { it != 0 }) {
                linesCleared++
                rowsToRemove.add(i)
            }
        }

        if (linesCleared > 0) {
            rowsToRemove.sortedDescending().forEach { rowToRemove ->
                for (i in rowToRemove downTo 1) {
                    gameBoard[i] = gameBoard[i - 1].copyOf()
                }
                gameBoard[0] = IntArray(cols) { 0 }
            }
            animationBoard.update()
        }

        return linesCleared
    }


    fun generateNewShape() {
        val nextShape = TetrisShape.values()[random.nextInt(TetrisShape.values().size)]
       currentShape = nextShape
        //currentShape = TetrisShape.I
        currentCoordinates = nextShape.coordinates.copyOf()
        val shapeWidthBlocks = nextShape.coordinates.maxOf { it.second } - nextShape.coordinates.minOf { it.second } + 1
        currentX = (cols / 2f - shapeWidthBlocks / 2f) * blockSize
        currentY = 0f
        currentColor = possibleColors[random.nextInt(possibleColors.size)]

        if (checkCollision(currentCoordinates, (currentX / blockSize).toInt(), (currentY / blockSize).toInt())) {
            println("¡Fin del juego!")
            gameOver()
            currentShape = null
        }
    }

    private fun gameOver() {
        animationBoard.stopGame()
        animationBoard.post {
            Toast.makeText(animationBoard.context, "¡Perdiste! Puntaje final: $currentScore", Toast.LENGTH_LONG).show()
            val context = animationBoard.context
            if (context is Activity) {
                val intent = Intent(context, GameOverActivity::class.java)
                intent.putExtra("final_score", currentScore)
                context.startActivity(intent)
                context.finish()
            }
        }
    }


    private fun checkCollision(coords: Array<Pair<Int, Int>>, xOffset: Int, yOffset: Int): Boolean {
        coords.forEach { (row, col) ->
            val boardRow = yOffset + row
            val boardCol = xOffset + col
            if (boardRow !in 0 until rows || boardCol !in 0 until cols || gameBoard[boardRow][boardCol] != 0) {
                return true
            }
        }
        return false
    }

    private fun fixShapeToBoard() {
        currentCoordinates.forEach { (row, col) ->
            val boardRow = (currentY / blockSize).toInt() + row
            val boardCol = (currentX / blockSize).toInt() + col
            if (boardRow in 0 until rows && boardCol in 0 until cols) {
                gameBoard[boardRow][boardCol] = currentColor
            }
        }
        val linesCleared = checkAndClearLines()
        handleLineClear(linesCleared)
        currentShape = null
        generateNewShape()
    }
    fun moveBottom() {
        Thread {
            while (true) {
                val nextYInt = (currentY / blockSize).toInt() + 1
                if (!checkCollision(currentCoordinates, (currentX / blockSize).toInt(), nextYInt)) {
                    currentY += blockSize
                    animationBoard.postInvalidate()
                    Thread.sleep(20)
                } else {
                    fixShapeToBoard()
                    break
                }
            }
        }.start()
    }

    fun moveDown() {
        val nextYInt = (currentY / blockSize).toInt() + 1
        if (!checkCollision(currentCoordinates, (currentX / blockSize).toInt(), nextYInt)) {
            currentY += blockSize
        } else {
            fixShapeToBoard()
        }
        animationBoard.update()
    }

    fun moveLeft() {
        val nextXInt = (currentX / blockSize).toInt() - 1
        if (!checkCollision(currentCoordinates, nextXInt, (currentY / blockSize).toInt())) {
            currentX -= blockSize
            animationBoard.update()
        }
    }

    fun moveRight() {
        val nextXInt = (currentX / blockSize).toInt() + 1
        if (!checkCollision(currentCoordinates, nextXInt, (currentY / blockSize).toInt())) {
            currentX += blockSize
            animationBoard.update()
        }
    }

    fun rotatePiece() {
        val shape = currentShape ?: return
        if (shape == TetrisShape.O) return
        val pivot = when (shape) {
            TetrisShape.I -> currentCoordinates[1]
            TetrisShape.L -> currentCoordinates[2]
            TetrisShape.J -> currentCoordinates[2]
            TetrisShape.T -> currentCoordinates[1]
            TetrisShape.S -> currentCoordinates[1]
            TetrisShape.Z -> currentCoordinates[1]
            TetrisShape.O -> return
        }

        val rotatedCoords = currentCoordinates.map { (row, col) ->
            val relativeRow = row - pivot.first
            val relativeCol = col - pivot.second
            -relativeCol + pivot.first to relativeRow + pivot.second
        }.toTypedArray()


        val boardX = (currentX / blockSize).toInt()
        val boardY = (currentY / blockSize).toInt()

        if (!checkCollision(rotatedCoords, boardX, boardY)) {
            currentCoordinates = rotatedCoords
            animationBoard.update()
        } else {

            val shiftedX = when {
                checkCollision(rotatedCoords, boardX - 1, boardY) -> {
                    if (!checkCollision(rotatedCoords, boardX + 1, boardY)) {

                        currentX += blockSize
                        1
                    } else {

                        return
                    }
                }

                else -> {

                    currentX -= blockSize
                    -1
                }
            }


            if (!checkCollision(rotatedCoords, boardX + shiftedX, boardY)) {
                currentCoordinates = rotatedCoords
                animationBoard.update()
            } else {
                return
            }
        }
    }

    fun draw(canvas: Canvas) {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val color = gameBoard[i][j]
                if (color != 0) {
                    paint.color = color
                    canvas.drawRect(
                        j * blockSize,
                        i * blockSize,
                        (j + 1) * blockSize,
                        (i + 1) * blockSize,
                        paint
                    )
                }
            }
        }

        currentCoordinates.forEach { (row, col) ->
            paint.color = currentColor
            val x = currentX + col * blockSize
            val y = currentY + row * blockSize
            canvas.drawRect(x, y, x + blockSize, y + blockSize, paint)
        }
    }
}