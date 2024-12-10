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
    val trailEnds = mutableSetOf<Point>()
    val queue = ArrayDeque<Point>()

    queue.addLast(point)

    while (queue.size > 0) {
        val point = queue.removeFirst()
        val elevation = grid.elevation(point)

        val nextSteps =
            Direction.cardinal
                .map { point + it.vector }
                .filter { grid.isValid(it) && grid.elevation(it) == elevation + 1 }

        if (elevation == 8) {
            trailEnds.addAll(nextSteps)
        } else {
            queue.addAll(nextSteps)
        }
    }

    return trailEnds.size
}

fun main() {
    val grid = Grid.read('.')

    val potential = potentialTrailHeads(grid)

    val score = potential.map { numTrails(grid, it) }.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
