package com.example.practico3.ui.activities

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practico3.R
import com.example.practico3.databinding.ActivityMainBinding
import com.example.practico3.ui.components.AnimationBoard

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var scoreTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        scoreTextView = binding.scoreTextView
        setupEventListeners()
    }

    private fun setupEventListeners() {

       // binding.btnUp.setOnClickListener { binding.animationBoard.moveUp() }
        //binding.btnDown.setOnClickListener { binding.animationBoard.moveDown() }
        binding.btnLeft.setOnClickListener { binding.animationBoard.moveLeft() }
        binding.btnRight.setOnClickListener { binding.animationBoard.moveRight() }
        binding.btnRotate.setOnClickListener {  binding.animationBoard.rotate() }
        binding.btnDown.setOnClickListener { binding.animationBoard.moveBottom() }

        binding.animationBoard.setOnScoreChangeListener(object : AnimationBoard.OnScoreChangeListener {
            override fun onScoreChanged(newScore: Int) {
                runOnUiThread {
                    scoreTextView.text = "Puntaje: $newScore"
                }
            }
        })
    }
}