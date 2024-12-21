import java.util.PriorityQueue
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.times
import net.panayotov.util.toDirection

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

data class State(
    val point: Point,
    val direction: Direction,
    val score: Int,
    val paths: List<Point>,
)

fun findAllPaths(grid: Grid<Char>, start: Pair<Point, Direction>, end: Point): List<List<Point>> {
    val queue = PriorityQueue<State>(compareBy { it.score })
    val paths = mutableListOf<List<Point>>()
    val scores = mutableMapOf<Pair<Point, Direction>, Int>()
    var lowestScore: Int? = null

    queue.add(State(start.first, start.second, 0, listOf()))

    while (!queue.isEmpty()) {
        val state = queue.poll()
        val (point, direction, score, path) = state

        val existingScoreLower = scores[Pair(point, direction)]?.let { it < score } ?: false
        if (existingScoreLower) {
            continue
        }

        scores[Pair(point, direction)] = score

        if (point == end) {
            if (lowestScore == null || lowestScore == score) {
                lowestScore = score
                paths.add(path + listOf(end))
            }
        }

        val vector = direction.vector
        val options =
            listOf(
                Pair(vector, 1),
                Pair(vector.rotateRight(), 1000),
                Pair(vector.rotateLeft(), 1000),
            )

        for ((newDirection, scoreDelta) in options) {
            val newPoint = point + newDirection
            if (grid.isValid(newPoint) && grid[newPoint] != '#') {
                queue.add(
                    State(
                        newPoint,
                        newDirection.toDirection()!!,
                        score + scoreDelta,
                        path + listOf(point),
                    )
                )
            }
        }
    }
    return paths.toList()
}

fun main() {
    val grid = Grid.read('.')

    val (start, end) = findPoints(grid)

    val allPaths = findAllPaths(grid, Pair(start, Direction.EAST), end)

    val visited = mutableSetOf<Point>()

    for (path in allPaths) {
        for (point in path) {
            visited.add(point)
        }
    }

    println("Answer = ${visited.size}")
}
