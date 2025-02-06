import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

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

class Graph(
    val grid: Grid<Char>,
    val points: List<Point>,
    val weights: Map<Pair<Point, Point>, Int>,
) {
    val neighbors: Map<Point, Set<Point>>

    init {
        val neighborsBuilder = mutableMapOf<Point, MutableSet<Point>>()
        for ((left, right) in weights.keys) {
            // This was the source of a bug that stumped me forever. Apparently everyone on the
            // subreddit was doing the same technique as me but they're code would run in a
            // reasonable amount of time.

            // Problem was: I'm (correctly) adding bidirectional edges here. However, the value of
            // the map was originally a list of points not a set. This basically doubled every step
            // of the iteration in main. Doubling the branch factor is a huge exponential expansion.
            neighborsBuilder.getOrPut(left) { mutableSetOf() }.add(right)
            neighborsBuilder.getOrPut(right) { mutableSetOf() }.add(left)
        }

        neighbors = neighborsBuilder.entries.associate { Pair(it.key, it.value.toSet()) }
    }
}

// Finds every point where we have a choice of which direction to go. Most points have 2 directions:
// the way you entered the point and the way you left.
fun findJunctions(grid: Grid<Char>, start: Point, end: Point): Set<Point> {
    val junctions = mutableSetOf<Point>()
    val seen = mutableSetOf<Point>()
    // Use a deque as a stack so that we do DFS. This is more memory efficient because we trace down
    // path after path instead of trying to simultaneously store all paths in the stack. Not a big
    // deal here but in the lower iteration it will be important for memory usage.
    val stack = ArrayDeque<Point>()

    stack.addLast(start)
    junctions.add(start)

    while (!stack.isEmpty()) {
        val point = stack.removeLast()

        if (!seen.add(point)) {
            continue
        }

        val neighbors =
            Direction.cardinal
                .map { point + it.vector }
                .filter { grid.isValid(it) && grid[it] != '#' }

        if (neighbors.size > 2 || point == end) {
            junctions.add(point)
        }

        stack.addAll(neighbors)
    }

    return junctions.toSet()
}

// Using the junctions from the above method, we create a weight graph.
fun findJunctionGraph(grid: Grid<Char>, junctions: Set<Point>): Graph {
    data class JunctionState(val point: Point, val path: Set<Point>) {
        constructor(point: Point) : this(point, setOf(point))

        fun neighbor(newPoint: Point) = JunctionState(newPoint, path + setOf(newPoint))
    }

    val weights = mutableMapOf<Pair<Point, Point>, Int>()

    for (start in junctions) {
        // Use a deque as a stack so that we do DFS again. Again, this is not a big deal here but in
        // the lower iteration it will be important for memory usage.
        val stack = ArrayDeque<JunctionState>()
        stack.addLast(JunctionState(start))

        while (!stack.isEmpty()) {
            val state = stack.removeLast()
            val (point, path) = state

            if (point != start && junctions.contains(point)) {
                val prevWeight1 = weights[Pair(start, point)]
                val prevWeight2 = weights[Pair(point, start)]

                val weight = listOf(path.size - 1, prevWeight1, prevWeight2).filterNotNull().max()

                weights[Pair(start, point)] = weight
                weights[Pair(point, start)] = weight
            } else {
                val neighbors =
                    Direction.cardinal
                        .map { point + it.vector }
                        .filter { grid.isValid(it) && grid[it] != '#' && !path.contains(it) }

                stack.addAll(neighbors.map { state.neighbor(it) })
            }
        }
    }

    return Graph(grid, junctions.toList(), weights.toMap())
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
    data class State(val point: Point, val score: Int, val seen: List<Point>) {
        constructor(point: Point) : this(point, 0, listOf()) {}

        fun neighbor(newPoint: Point, weight: Int) =
            State(newPoint, score + weight, seen + listOf(point))
    }

    val grid = Grid.read('.')
    val (start, end) = findPoints(grid)

    val junctions = findJunctions(grid, start, end)
    val graph = findJunctionGraph(grid, junctions)

    var best: Int? = null
    // Use a deque as a stack so that we do DFS. This is more memory efficient because we trace down
    // path after path instead of trying to simultaneously store all paths in the stack. Here it is
    // important for memory usage.
    val stack = ArrayDeque<State>()
    stack.addLast(State(start))

    while (!stack.isEmpty()) {
        val state = stack.removeLast()
        val (point, score, seen) = state

        if (seen.contains(point)) {
            continue
        }

        if (point == end && (best == null || score > best)) {
            best = score
        } else {
            graph.neighbors[point]?.let { neighbors ->
                val neighborsToAdd =
                    neighbors.map { newPoint ->
                        val weight1 = graph.weights[Pair(point, newPoint)]
                        val weight2 = graph.weights[Pair(newPoint, point)]
                        val weight = listOf(weight1, weight2).filterNotNull().maxOrNull()
                        if (weight == null) {
                            error("No weight for $point, $newPoint")
                        }
                        state.neighbor(newPoint, weight)
                    }

                stack.addAll(neighborsToAdd)
            }
        }
    }

    println("Answer = $best")
}
