package com.example.practico3.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practico3.R
import com.example.practico3.database.AppDatabase
import com.example.practico3.models.Score
import com.example.practico3.models.dao.ScoreDao
import kotlinx.coroutines.launch

class GameOverActivity : AppCompatActivity() {
    private var finalScore: Int = 0
    private lateinit var scoreDao: ScoreDao
    private lateinit var finalScoreTextView: TextView
    private lateinit var highScoresTitleTextView: TextView
    private lateinit var highScoresListTextView: TextView
    private lateinit var restartButton: Button
    private lateinit var saveScoreButton: Button
    private lateinit var playerNameInputEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        scoreDao = AppDatabase.getDatabase(applicationContext).scoreDao()

        finalScoreTextView = findViewById(R.id.final_score_text)
        highScoresTitleTextView = findViewById(R.id.high_scores_title)
        highScoresListTextView = findViewById(R.id.high_scores_list)
        restartButton = findViewById(R.id.btnRestart)
        saveScoreButton = findViewById(R.id.save_score_button)
        playerNameInputEditText = findViewById(R.id.player_name_input)

        finalScore = intent.getIntExtra("final_score", 0)
        finalScoreTextView.text = "Puntaje: $finalScore"

        loadHighScores()

        saveScoreButton.setOnClickListener {
            val playerName = playerNameInputEditText.text.toString().trim()
            if (playerName.isNotEmpty()) {
                saveScore(playerName, finalScore)
            } else {
                Toast.makeText(this@GameOverActivity, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show()
            }
        }

        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveScore(name: String, score: Int) {
        lifecycleScope.launch {
            val newScore = Score(playerName = name, scoreValue = score)
            scoreDao.insert(newScore)
            Toast.makeText(this@GameOverActivity, "Puntaje guardado", Toast.LENGTH_SHORT).show()
            loadHighScores()
        }
    }

    private fun loadHighScores() {
        lifecycleScope.launch {
            val scores = scoreDao.getAllScores()
            displayHighScores(scores)
        }
    }

    private fun displayHighScores(scores: List<Score>) {
        val stringBuilder = StringBuilder()
        if (scores.isNotEmpty()) {
            for (score in scores) {
                stringBuilder.append("${score.playerName}: ${score.scoreValue}\n")
            }
            highScoresListTextView.text = stringBuilder.toString()
        } else {
            highScoresListTextView.text = "No hay puntajes guardados."
        }
    }
}