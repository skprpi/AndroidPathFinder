package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivitySettingsBinding
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)

        activitySettingsBinding.seekbarWalls.progress = drawingBrushSize
        activitySettingsBinding.seekbarEraser.progress = eraseBrushSize
        activitySettingsBinding.spinner.setSelection(spinnerPos)
        activitySettingsBinding.spinnerAlg.setSelection(spinnerAlgPos)

        activitySettingsBinding.buttonBack.setOnClickListener {
            finish()
        }
        activitySettingsBinding.seekbarWalls.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                drawingBrushSize = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        activitySettingsBinding.seekbarEraser.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                eraseBrushSize = progress
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        activitySettingsBinding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position).toString()) {
                    "Drawing" -> {
                        currentMode = Mode.DRAWING
                        setDefaultState(mat)
                        spinnerPos = position
                    }
                    "Erase" -> {
                        currentMode = Mode.ERASE
                        setDefaultState(mat)
                        spinnerPos = position
                    }
                    "Start" -> {
                        currentMode = Mode.SET_START_POINTS
                        setDefaultState(mat)
                        spinnerPos = position
                    }
                    "Finish" -> {
                        currentMode = Mode.SET_FINISH_POINTS
                        setDefaultState(mat)
                        spinnerPos = position
                    }
                }
            }

            fun setDefaultState(mat: Array<Array<Cell>>) {
                for (i in mat.indices) {
                    for (j in mat[i].indices) {
                        if (mat[i][j].cellState == CellState.PATH || mat[i][j].cellState == CellState.USED) {
                            mat[i][j].setEmpty()
                        }
                    }
                }
            }

        }

        activitySettingsBinding.spinnerAlg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(parent?.getItemAtPosition(position).toString()){
                    "A-star" -> {
                        spinnerAlgPos = 0
                    }
                    "Bfs" -> {
                        spinnerAlgPos = 1
                    }
                }
            }
        }


    }
}
