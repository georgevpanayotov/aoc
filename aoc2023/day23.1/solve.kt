import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

data class State(val point: Point, val score: Int, val seen: Set<Point>) {
    constructor(point: Point) : this(point, 0, setOf()) {}

    fun neighbor(newPoint: Point) = State(newPoint, score + 1, seen + setOf(point))
}

fun findPoints(grid: Grid<Char>): Pair<Point, Point> {
    var start: Point? = null
    var end: Point? = null

    for (x in 0L..<grid.width) {
        if (grid[x, grid.height - 1L] == '.') {
            start = Point(x, grid.height - 1L)
        }

        if (grid[x, 0] == '.') {
            end = Point(x, 0)
        }
    }

    if (start == null || end == null) {
        error("$start / $end not found in $grid")
    }

    return Pair(start, end)
}

fun getDirection(char: Char) =
    when (char) {
        '^' -> Direction.NORTH
        '>' -> Direction.EAST
        'v' -> Direction.SOUTH
        '<' -> Direction.WEST
        else -> null
    }

fun main() {
    val grid = Grid.read('.')
    val (start, end) = findPoints(grid)

    val bestMap = mutableMapOf<Point, Int>()
    val queue = ArrayDeque<State>()
    queue.add(State(start))

    while (!queue.isEmpty()) {
        val state = queue.poll()
        val (point, score, seen) = state

        val best = bestMap[point]
        if (seen.contains(point) || (best != null && score < best)) {
            continue
        }

        bestMap[point] = score

        val directions = getDirection(grid[point])?.let { listOf(it) } ?: Direction.cardinal

        val neighbors =
            directions
                .map { point + it.vector }
                .filter { newPoint -> grid.isValid(newPoint) && grid[newPoint] != '#' }
                .map { newPoint -> state.neighbor(newPoint) }

        queue.addAll(neighbors)
    }

    val score = bestMap[end]

    println("Answer = $score")
}
