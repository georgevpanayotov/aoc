import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun readGrid(): List<String> {
    val grid = mutableListOf<StringBuilder>()
    for (line in Lines) {
        grid.add(StringBuilder(line))
        if (line.toString().fold(true) { acc, value -> acc && value == '.' }) {
            grid.add(StringBuilder(line))
        }
    }

    val blanks = mutableListOf<Int>()

    for (j in 0..grid[0].length - 1) {
        var blank = true
        for (i in 0..grid.size - 1) {
            if (grid[i][j] != '.') {
                blank = false
                break
            }
        }

        if (blank) {
            blanks.add(j)
        }
    }

    blanks.reverse()
    for (blank in blanks) {
        for (i in 0..grid.size - 1) {
            grid[i].insert(blank, '.')
        }
    }

    return grid.map(StringBuilder::toString).toList()
}

fun findGalaxies(grid: List<String>): List<Pair<Int, Int>> {
    val galaxies = mutableListOf<Pair<Int, Int>>()

    for (i in 0..grid.size - 1) {
        for (j in 0..grid[i].length - 1) {
            if (grid[i][j] == '#') {
                galaxies.add(Pair(i, j))
            }
        }
    }

    return galaxies.toList()
}

fun abs(value: Int): Int = if (value > 0) value else -value

fun getDistance(left: Pair<Int, Int>, right: Pair<Int, Int>): Int =
    abs(left.first - right.first) + abs(left.second - right.second)


fun main() {
    val grid = readGrid()
    val galaxies = findGalaxies(grid)

    var score = 0

    for (i in 0..galaxies.size - 1) {
        for (j in i + 1..galaxies.size - 1) {
            val distance = getDistance(galaxies[i], galaxies[j])
            score += distance
        }
    }

    println("Answer = $score")
}
