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

fun List<String>.rowSmudges(row1: Int, row2: Int): Int? {
    var smudges = 0

    for (j in 0..<this[row1].length) {
        if (this[row1][j] != this[row2][j]) {
            smudges++
            if (smudges > 1) {
                return null
            }
        }
    }

    return smudges
}

fun List<String>.colSmudges(col1: Int, col2: Int): Int? {
    var smudges = 0

    for (i in 0..<this.size) {
        if (this[i][col1] != this[i][col2]) {
            smudges++
            if (smudges > 1) {
                return null
            }
        }
    }

    return smudges
}

fun findGridReflection(grid: List<String>): Long {
    var score = 0L

    // Rows
    for (i in 0..<grid.size - 1) {
        var smudges = grid.rowSmudges(i, i + 1)
        if (smudges != null) {
            var isReflection = true
            var row1 = i - 1
            var row2 = i + 2
            while (row1 >= 0 && row2 < grid.size && smudges <= 1) {
                val newSmudges = grid.rowSmudges(row1, row2)
                if (newSmudges == null || smudges + newSmudges > 1) {
                    isReflection = false
                    break
                }
                smudges += newSmudges
                row1--
                row2++
            }

            if (isReflection && smudges == 1) {
                score += 100L * (i.toLong() + 1L)
            }
        }
    }

    // Cols
    for (j in 0..<grid[0].length - 1) {
        var smudges = grid.colSmudges(j, j + 1)
        if (smudges != null) {
            var isReflection = true
            var col1 = j - 1
            var col2 = j + 2
            while (col1 >= 0 && col2 < grid[0].length && smudges <= 1) {
                val newSmudges = grid.colSmudges(col1, col2)
                if (newSmudges == null || smudges + newSmudges > 1) {
                    isReflection = false
                    break
                }
                smudges += newSmudges
                col1--
                col2++
            }

            if (isReflection && smudges == 1) {
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
