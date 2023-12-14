import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun readGrid(): List<String> {
    val grid = mutableListOf<String>()
    for (line in Lines) {
        grid.add(line)
    }

    return grid.toList()
}

fun moveNorth(grid: List<String>): List<String> {
    val grid = grid.map(::StringBuilder)

    for (j in 0..<grid[0].length) {
        for (i in 0..<grid.size) {
            if (grid[i][j] == 'O') {
                var i_start = i - 1
                var lastBlank: Int? = null
                while (i_start >= 0) {
                    if (grid[i_start][j] == '.') {
                        lastBlank = i_start
                    }
                    if (grid[i_start][j] != '.') {
                        break
                    }

                    i_start--
                }

                if (lastBlank != null) {
                    grid[lastBlank].setCharAt(j, 'O')
                    grid[i].setCharAt(j, '.')
                }
            }
        }
    }

    return grid.map(StringBuilder::toString)
}

fun getScore(grid: List<String>): Long {
    var score = 0L

    for (i in 0..<grid.size) {
        for (j in 0..<grid[i].length) {
            if (grid[i][j] == 'O') {
                score += grid.size - i
            }
        }
    }

    return score
}

fun main() {
    var grid = readGrid()
    grid = moveNorth(grid)

    var score = 0

    println("Answer = ${getScore(grid)}")
}
