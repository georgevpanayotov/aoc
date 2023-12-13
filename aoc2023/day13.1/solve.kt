import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun readGrids(): List<List<String>> {
    val grids = mutableListOf<List<String>>()
    var grid = mutableListOf<String>()

    for (line in Lines) {
        if (line.length == 0) {
            grids.add(grid.toList())
            grid = mutableListOf<String>()
        } else {
            grid.add(line)
        }
    }
    if (grid.size > 0) {
        grids.add(grid.toList())
    }

    return grids.toList()
}

fun List<String>.rowsEqual(row1: Int, row2: Int): Boolean {
    for (j in 0..<this[row1].length) {
        if (this[row1][j] != this[row2][j]) {
            return false
        }
    }

    return true
}

fun List<String>.colsEqual(col1: Int, col2: Int): Boolean {
    for (i in 0..<this.size) {
        if (this[i][col1] != this[i][col2]) {
            return false
        }
    }

    return true
}

fun findGridReflection(grid: List<String>): Long {
    var score = 0L

    // Rows
    for (i in 0..<grid.size - 1) {
        if (grid.rowsEqual(i, i + 1)) {
            var isReflection = true
            var row1 = i
            var row2 = i + 1
            while (row1 >= 0 && row2 < grid.size) {
                if (!grid.rowsEqual(row1, row2)) {
                    isReflection = false
                    break
                }
                row1--
                row2++
            }

            if (isReflection) {
                score += 100L * (i.toLong() + 1L)
            }
        }
    }

    // Cols
    for (j in 0..<grid[0].length - 1) {
        if (grid.colsEqual(j, j + 1)) {
            var isReflection = true
            var col1 = j
            var col2 = j + 1
            while (col1 >= 0 && col2 < grid[0].length) {
                if (!grid.colsEqual(col1, col2)) {
                    isReflection = false
                    break
                }
                col1--
                col2++
            }

            if (isReflection) {
                score += j + 1
            }
        }
    }

    return score
}

fun main() {
    var score = 0L
    val grids = readGrids()

    for (grid in grids) {
        score += findGridReflection(grid)
    }

    println("Answer = $score")
}
