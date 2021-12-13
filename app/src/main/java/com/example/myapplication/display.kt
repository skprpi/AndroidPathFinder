package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.Log.INFO
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.jar.Attributes

var currentMode = Mode.DRAWING
val cellSize = 20;
var startPos = arrayOf(-1, -1)
var finishPos = arrayOf(-1, -1)
var width2 = 0//width.toInt();
var height2 = 0//height.toInt();
var mat: Array<Array<Cell>> = Array(
    (width2 / cellSize).toInt()
) { Array((height2 / cellSize)) { Cell() } }

var lastDrawWall = arrayOf(-1, -1)
var lastDrawUsed = arrayOf(-1, -1)

val deltaBetweenCell = 0

open class display @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes dAtr: Int = 0
) : View(context, attrs, dAtr) {
    private val appPreferences = AppPreferences(context)
    init {
        width2 = resources.displayMetrics.widthPixels
        height2 = resources.displayMetrics.heightPixels
        mat = Array(
            (width2 / cellSize).toInt()
        ) { Array((height2 / cellSize)) { Cell() } }
    }
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeWidth = 30f
    }


    val start = appPreferences.getStart("keyx", "keyY")
    private var path = Path()

    fun playAnimationForCell(cell: Cell) {
        paint.color = cell.color1
        paint.alpha = cell.alpha
        if (cell.cellState == CellState.USED) {
            cell.playAppearAnimation(12)
            cell.playSizeAnimation(1)

            cell.playColorAnimation(
                Color.rgb(128, 4, 15),
                Color.rgb(3, 120, 245)
            )
        }
        if (cell.cellState == CellState.WALL) {
            cell.playSizeAnimation(5)
        }
    }

    fun getCellDelta(cell: Cell): Float {
        var delta = (cellSize.toFloat() / 2) - (cell.size.toFloat() / 2)
        if (cell.size == cellSize) {
            delta = 0F
        }
        return delta
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            for (i in 0 until width2 / cellSize) {
                for (j in 0 until height2 / cellSize) {
                    if (mat[i][j].cellState != CellState.EMPTY) {
                        playAnimationForCell(mat[i][j])

                        val delta = getCellDelta(mat[i][j])
                        val x = i * cellSize
                        val y = j * cellSize
                        drawRect(
                            x.toFloat() + delta + deltaBetweenCell ,
                            y.toFloat() + delta + deltaBetweenCell,
                            x + delta + mat[i][j].size.toFloat() - deltaBetweenCell,
                            y  + delta + mat[i][j].size.toFloat() - deltaBetweenCell,
                            paint
                        )
                    }
                }
            }
        }
    }

    fun checkUniqueInstance(positionArray: Array<Int>, x: Int, y:Int) {
        if (positionArray[0] != -1) {
            mat[positionArray[0]][positionArray[1]].setEmpty()
        }
        positionArray[0] = x
        positionArray[1] = y
    }

    fun drawingStategy(x: Int, y: Int) {
        for (diffX in -drawingBrushSize..drawingBrushSize) {
            for (diffY in -drawingBrushSize..drawingBrushSize) {
                val newX = x + diffX
                val newY = y + diffY
                if (newX in mat.indices && newY in mat[0].indices &&
                    mat[newX][newY].cellState != CellState.WALL) {

                    mat[newX][newY].setWall()
                    lastDrawWall = arrayOf(newX, newY)
                }
            }
        }
    }

    fun eraseStategy(x: Int, y: Int) {
        for (diffX in -eraseBrushSize..eraseBrushSize) {
            for (diffY in -eraseBrushSize..eraseBrushSize) {
                val newX = x + diffX
                val newY = y + diffY
                if (newX in mat.indices && newY in mat[0].indices) {
                    mat[newX][newY].setEmpty()
                }
            }
        }
    }

    fun playStrategyByMode(x: Int, y: Int) {
        if (currentMode == Mode.DRAWING) {
            drawingStategy(x, y)
        } else if (currentMode == Mode.ERASE) {
            eraseStategy(x, y)
        } else if (currentMode == Mode.SET_START_POINTS) {
            checkUniqueInstance(startPos, x, y)
            mat[x][y].setStart()
        } else if (currentMode == Mode.SET_FINISH_POINTS) {
            checkUniqueInstance(finishPos, x, y)
            mat[x][y].setFinish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val x = event.x.toInt() / cellSize
                    val y = event.y.toInt() / cellSize

                    if ( x in 0 until (width2 / cellSize) && y in 0 until (height2 / cellSize) && currentMode != Mode.FIND_PATH) {
                        playStrategyByMode(x, y)
                    }
                    invalidate()
                }

                MotionEvent.ACTION_UP -> {
                    GlobalScope.launch {
                        while (lastDrawWall[0] != -1 && mat[lastDrawWall[0]][lastDrawWall[1]].size < cellSize) {
                            delay(20)
                            invalidate()
                        }
                    }
                }
            }
        }
        return true
    }
}
