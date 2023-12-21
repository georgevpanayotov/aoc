import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner
import kotlin.collections.ArrayDeque

val vectors = listOf(
    Pair(-1, 0),
    Pair(0, -1),
    Pair(0, 1),
    Pair(1, 0),
)

fun readGrid(): List<String> {
    val grid = mutableListOf<String>()
    for (line in Lines) {
        grid.add(line)
    }

    return grid.toList()
}

fun List<String>.isValid(position: Pair<Int, Int>) = 
    position.first >= 0 && position.first < this.size &&
    position.second >= 0 && position.second < this[position.first].length

fun findStart(grid: List<String>): Pair<Int, Int> {
    for (i in 0..<grid.size) {
        for (j in 0..<grid[i].length) {
            if (grid[i][j] == 'S') {
                return Pair(i, j)
            }
        }
    }

    error("No start found")
}

fun computeSteps(grid: List<String>, steps: Int, start: Pair<Int, Int>): Int {
    data class Frame(val position: Pair<Int, Int>, val step: Int)

    val queue = ArrayDeque<Frame>()
    queue.addLast(Frame(start, 0))

    val plots = mutableSetOf<Pair<Int, Int>>()
    val seen = mutableSetOf<Frame>()

    while (queue.size > 0) {
        val frame = queue.removeFirst()
        if (seen.contains(frame)) {
            continue
        }
        seen.add(frame)

        if (frame.step == steps) {
            plots.add(frame.position)
        } else {
            for (vector in vectors) {
                val newPosition = Pair(frame.position.first + vector.first,
                                       frame.position.second + vector.second)
                if (grid.isValid(newPosition) &&
                    (grid[newPosition.first][newPosition.second] == '.' ||
                     grid[newPosition.first][newPosition.second] == 'S' )) {
                    queue.addLast(Frame(newPosition, frame.step + 1))
                }
            }
        }
    }

    return plots.size
}

fun main(args: Array<String>) {
    val steps = if (args.size == 1) {
        args[0].toInt()
    } else {
        64
    }

    val grid = readGrid()
    val start = findStart(grid)

    val score = computeSteps(grid, steps, start)
    println("Answer = $score")
}
