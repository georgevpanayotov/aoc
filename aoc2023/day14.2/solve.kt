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

operator fun Pair<Int, Int>.unaryMinus() = Pair(-first, -second)

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) =
    Pair(first + other.first, second + other.second)

fun Pair<Int, Int>.outerVector() =
    if (first == 0) {
        Pair(1, 0)
    } else {
        Pair(0, 1)
    }

fun List<StringBuilder>.inBounds(point: Pair<Int, Int>): Boolean =
    point.first >= 0 && point.first < this.size &&
    point.second >= 0 && point.second < this[point.first].length

fun move(inGrid: List<String>, vector: Pair<Int, Int>): List<String> {
    val outerIter = vector.outerVector()
    val innerIter = - vector

    val grid = inGrid.map(::StringBuilder)

    var outerPoint = when(vector) {
        Pair(-1, 0) -> Pair(0, 0)
        Pair(0, -1) -> Pair(0, 0)
        Pair(1, 0) -> Pair(inGrid.size - 1, 0)
        Pair(0, 1) -> Pair(0, inGrid[0].length - 1)
        else -> error ("Invalid vector")
    }

    while (grid.inBounds(outerPoint)) {
        var point = outerPoint

        while (grid.inBounds(point)) {
            if (grid[point.first][point.second] == 'O') {
                var curr = point + vector
                var lastBlank: Pair<Int, Int>? = null
                while (grid.inBounds(curr)) {
                    if (grid[curr.first][curr.second] == '.') {
                        lastBlank = curr
                    } else {
                        break
                    }

                    curr = curr + vector
                }

                if (lastBlank != null) {
                    grid[point.first].setCharAt(point.second, '.')
                    grid[lastBlank.first].setCharAt(lastBlank.second, 'O')
                }
            }

            point = point + innerIter
        }

        outerPoint = outerPoint + outerIter
    }

    return grid.map(StringBuilder::toString)
}

fun doCycle(inGrid: List<String>): List<String> {
    var grid = inGrid

    grid = move(grid, Pair(-1, 0))
    grid = move(grid, Pair(0, -1))
    grid = move(grid, Pair(1, 0))
    grid = move(grid, Pair(0, 1))

    return grid
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
    val cycleFinder = mutableMapOf<List<String>, Int>()
    val gridFinder = mutableMapOf<Int, List<String>>()

    var i = 0
    var done = false
    cycleFinder[grid] = 0

    while (!done) {
        i++
        grid = doCycle(grid)

        gridFinder[i] = grid

        if (!cycleFinder.contains(grid)) {
            cycleFinder[grid] = i
        } else {
            val length = i - cycleFinder[grid]!!
            val prefix = cycleFinder[grid]!!
            val index = (1000000000 - prefix) % length + prefix

            val foundGrid = gridFinder[index]

            if (foundGrid == null) {
                error("No grid for index $index.")
            }

            println("Answer = ${getScore(foundGrid)}")
            return
        }
    }
}
