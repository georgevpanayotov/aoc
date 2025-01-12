import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger

class GridGraph(private val grid: Grid<Char>) : GraphLike<Pair<Point, Direction>> {
    override fun getNeighbors(node: Pair<Point, Direction>): List<Pair<Point, Direction>> {
        val (point, direction) = node

        return Direction.cardinal
            .map {
                if (it == direction) {
                    Pair(point + direction.vector, direction)
                } else {
                    Pair(point, it)
                }
            }
            .filter {
                val (newPoint, newDirection) = it
                grid.isValid(newPoint) &&
                    grid[newPoint] != '#' &&
                    (newDirection.vector + direction.vector) != Point(0, 0)
            }
    }

    override fun getEdgeWeight(from: Pair<Point, Direction>, to: Pair<Point, Direction>): Int {
        val (fromPoint, fromDir) = from
        val (toPoint, toDir) = to

        return if (fromPoint == toPoint) {
            // Turning
            1000
        } else {
            // Moving forward
            1
        }
    }
}

fun findPoints(grid: Grid<Char>): Pair<Point, Point> {
    var start: Point? = null
    var end: Point? = null

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val point = Point(x, y)
            if (grid[point] == 'S') {
                start = point
            } else if (grid[point] == 'E') {
                end = point
            }
        }
    }
    if (start == null) {
        error("Start not found.")
    }
    if (end == null) {
        error("End not found.")
    }

    return Pair(start, end)
}

fun computeScore(path: List<Pair<Point, Direction>>): Int {
    var score = 0
    var lastPoint: Point? = null
    var lastDirection: Direction? = null

    for ((point, direction) in path) {
        if (lastPoint != null && lastDirection != null) {
            score +=
                if (point == lastPoint) {
                    1000
                } else {
                    1
                }
        }

        lastPoint = point
        lastDirection = direction
    }

    return score
}

fun main() {
    val grid = Grid.read('.')

    val (start, end) = findPoints(grid)

    val paths = edsger(Pair(start, Direction.EAST), GridGraph(grid))

    val endPaths = Direction.cardinal.map { paths[Pair(end, it)] }.filterNotNull()

    var minPath: List<Pair<Point, Direction>>? = null
    for (path in endPaths) {
        if (minPath == null || path.size < minPath.size) {
            minPath = path
        }
    }

    val score = minPath?.let(::computeScore)

    println("Answer = $score")
}
