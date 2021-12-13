package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.floating_action_button).setOnClickListener {
            if (startPos[0] == -1 || finishPos[0] == -1){
                Toast.makeText(view.context, "Вы не выбрали start или finish", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            currentMode = Mode.FIND_PATH

            lifecycleScope.launch {
                if (spinnerAlgPos == 0){
                    a_star_classic(view, mat, startPos, finishPos)
                } else {
                    bfs(view, mat, startPos, finishPos)
                }

                if (mat[finishPos[0]][finishPos[1]].preventPosition.contentEquals(arrayOf(-1, -1))) {
                    Toast.makeText(view.context, "Путь не существует!", Toast.LENGTH_LONG).show()
                } else {
                    showPath(view, finishPos)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    suspend fun bfs(view: View, mat: Array<Array<Cell>>, start: Array<Int>, finish: Array<Int>) {

        var q: MutableList<Array<Int>> = mutableListOf(start)
        val steps =
            arrayOf(arrayListOf(1, 0), arrayListOf(-1, 0), arrayListOf(0, 1), arrayListOf(0, -1))
        var found = false
        while (q.size > 0) {
            val element = q[0]
            q.removeAt(0)



            if (element.contentEquals(finish)) {
                found = true
                break
            }
            for (i in steps.indices) {
                val newX = steps[i][0] + element[0]
                val newY = steps[i][1] + element[1]
                if (!(newX in mat.indices && newY in mat[0].indices)) {
                    continue
                }
                if (mat[newX][newY].cellState == CellState.EMPTY || mat[newX][newY].cellState == CellState.FINISH) {
                    q.add(arrayOf(newX, newY))
                    if (mat[newX][newY].cellState == CellState.EMPTY) {
                        mat[newX][newY].setUsed()
                        lastDrawUsed = arrayOf(newX, newY)
                    }
                    mat[newX][newY].preventPosition = arrayOf(element[0], element[1])
                }
            }
            delay(1L)
            view.findViewById<display>(R.id.durationview).invalidate()
        }
        if (!found) {
            mat[finish[0]][finish[1]].preventPosition = arrayOf(-1, -1)
        }

    }


    fun get_heuristic_path_len(now_point: Array<Int>): Int {
        return (Math.abs(now_point[0] - finishPos[0]) + Math.abs(now_point[1] - finishPos[1]))
    }

    suspend fun a_star_classic(view: View, mat: Array<Array<Cell>>, start: Array<Int>, finish: Array<Int>) {


        val q: MutableList<MutableList<Int>> = mutableListOf(mutableListOf(0, 0, start[0], start[1]))
        // q.add(arrayOf( get_heuristic_path_len(start), 0,start[0], start[1]))
        val steps =
            arrayOf(arrayListOf(1, 0), arrayListOf(-1, 0), arrayListOf(0, 1), arrayListOf(0, -1))
        var found = false
        while (q.size > 0) {
            q.sortBy {
                it[0] + it[1]
            }
            Log.e("123", "$q")
            val element = q[0]
            q.removeAt(0)

            if (arrayOf(element[2], element[3]).contentEquals(finish)) {
                found = true
                break
            }
            for (i in steps.indices) {
                val newX = steps[i][0] + element[2]
                val newY = steps[i][1] + element[3]
                if (!(newX in mat.indices && newY in mat[0].indices)) {
                    continue
                }
                if (mat[newX][newY].cellState == CellState.EMPTY || mat[newX][newY].cellState == CellState.FINISH) {

                    val path =  get_heuristic_path_len(arrayOf(newX, newY))
                    Log.e("555", "path - $path")
                    q.add(mutableListOf<Int>(path.toInt(), element[1] + 1,newX, newY))
                    if (mat[newX][newY].cellState == CellState.EMPTY) {
                        mat[newX][newY].setUsed()
                        lastDrawUsed = arrayOf(newX, newY)
                    }
                    mat[newX][newY].preventPosition = arrayOf(element[2], element[3])
                }

            }
            delay(1L)
            view.findViewById<display>(R.id.durationview).invalidate()
        }

        while (com.example.myapplication.mat[lastDrawUsed[0]][lastDrawUsed[1]].size < cellSize) {
            delay(20L)
            view.findViewById<display>(R.id.durationview).invalidate()
        }

        if (!found) {
            mat[finish[0]][finish[1]].preventPosition = arrayOf(-1, -1)
        }
    }

    suspend fun showPath(view: View, finish: Array<Int>) {
        var nowX = finish[0]
        var nowY = finish[1]
        while (mat[nowX][nowY].cellState ==CellState.FINISH || mat[nowX][nowY].cellState ==CellState.USED) {
            if (mat[nowX][nowY].cellState ==CellState.USED) {
                mat[nowX][nowY].setPath()
            }
            val oldX = nowX
            val oldY = nowY
            nowX = mat[oldX][oldY].preventPosition[0]
            nowY = mat[oldX][oldY].preventPosition[1]

            delay(20L)
            view.findViewById<display>(R.id.durationview).invalidate()
        }

        while (mat[lastDrawUsed[0]][lastDrawUsed[1]].size < cellSize) {
            delay(20L)
            view.findViewById<display>(R.id.durationview).invalidate()
        }
    }
}
