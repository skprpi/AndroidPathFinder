package com.example.myapplication

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red


enum class CellState {
    EMPTY, WALL, START, FINISH, USED, PATH
}

enum class Mode {
    DRAWING, ERASE, FIND_PATH, SET_FINISH_POINTS, SET_START_POINTS
};

class Cell {
    public var cellState = CellState.EMPTY
    public var color1 = Color.WHITE
    public var alpha = 255
    public var preventPosition = arrayOf(-1, -1)
    public var size = cellSize


    private var iteration = 0
    private var iteration1 = 0
    private var iteration2 = 0

    private var colorAppear = Color.rgb(128, 4, 15)
    private var colorAppear2 = Color.rgb(3, 120, 245)
    private var deltaColor = Color.rgb(0, 0, 0)
    private var nowStepAppearColor = 0
    private var finishStepAppearColor = 0

    public fun setWall() {
        cellState = CellState.WALL
        color1 = Color.rgb(200, 20, 20)
        alpha = 255
        size = 0
    }

    public fun setEmpty() {
        cellState = CellState.EMPTY
        color1 = Color.WHITE
        alpha = 255
        size = cellSize
        finishStepAppearColor = 0
    }

    public fun setStart() {
        cellState = CellState.START
        color1 = Color.GREEN
        alpha = 255
        size = cellSize
        finishStepAppearColor = 0
    }

    public fun setFinish() {
        cellState = CellState.FINISH
        color1 = Color.BLUE
        alpha = 255
        size = cellSize
        finishStepAppearColor = 0
    }

    public fun setPath() {
        cellState = CellState.PATH
        color1 = Color.YELLOW
        alpha = 255
        size = cellSize
    }

    public fun setUsed() {
        cellState = CellState.USED
        color1 = Color.WHITE
        alpha = 0
        size = 0
    }

    public fun playAppearAnimation(incSize: Int) {
        iteration += 1
        if (alpha < 255) {
            if (iteration % 5 == 0) {
                alpha += incSize
            }
        }
        if (alpha > 255) {
            alpha = 255
        }
    }

    public fun playSizeAnimation(incSize: Int) {
        iteration1 += 1
        if (size < cellSize) {
            if (iteration1 % 5 == 0) {
                size += incSize
            }
        }
        if (size > cellSize) {
            size = cellSize
        }
    }

    private fun setDeltaColor(needColor: Int, step:Int) {
        deltaColor = Color.rgb(
            (needColor.red - color1.red) / step,
            (needColor.green - color1.green) / step,
            (needColor.blue - color1.blue) / step
        )
    }


    public fun playColorAnimation(colorA1: Int, colorA2: Int, step: Int=7) {
        if (finishStepAppearColor == 0) {
            colorAppear = colorA1
            colorAppear2 = colorA2
            setDeltaColor(colorAppear, step)
            finishStepAppearColor = step
            nowStepAppearColor = 0
            iteration2 = 0
        }


        if (iteration2 % 10 == 0 && nowStepAppearColor < finishStepAppearColor) {
            color1 += deltaColor
            nowStepAppearColor += 1
        } else if (iteration2 % 10 == 0 && nowStepAppearColor >= finishStepAppearColor &&  nowStepAppearColor <  finishStepAppearColor * 2) {
            if (nowStepAppearColor == finishStepAppearColor) {
                setDeltaColor(colorAppear2, step)
            }
            color1 += deltaColor
            nowStepAppearColor += 1
        }
        iteration2 += 1
    }
}
