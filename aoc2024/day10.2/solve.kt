import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

fun Grid<Char>.elevation(point: Point) = this[point].code - '0'.code

fun potentialTrailHeads(grid: Grid<Char>): List<Point> {
    val potential = mutableListOf<Point>()
    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val point = Point(x, y)
            if (grid[point] == '0') {
                potential.add(point)
            }
        }
    }

    return potential.toList()
}

fun numTrails(grid: Grid<Char>, point: Point): Int {
    val trails = mutableSetOf<List<Point>>()
    val queue = ArrayDeque<List<Point>>()

    queue.addLast(listOf(point))

    while (queue.size > 0) {
        val trail = queue.removeFirst()
        val curr = trail[trail.size - 1]
        val elevation = grid.elevation(curr)

        val nextSteps =
            Direction.cardinal
                .map { curr + it.vector }
                .filter { grid.isValid(it) && grid.elevation(it) == elevation + 1 }
                .map { trail + listOf(it) }

        if (elevation == 8) {
            trails.addAll(nextSteps)
        } else {
            queue.addAll(nextSteps)
        }
    }

    return trails.size
}

fun main() {
    val grid = Grid.read('.')

    val potential = potentialTrailHeads(grid)

    val score = potential.map { numTrails(grid, it) }.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
