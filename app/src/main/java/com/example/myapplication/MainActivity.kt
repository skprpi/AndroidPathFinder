package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

var drawingBrushSize = 0

var eraseBrushSize = 0

var spinnerPos = 0

var spinnerAlgPos = 0

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }


    }

    override fun onStart() {
        super.onStart()
        binding.toolbarText.text = getString(R.string.mode_tittle).plus(resources.getStringArray(R.array.mode_names)[spinnerPos])
    }
}
