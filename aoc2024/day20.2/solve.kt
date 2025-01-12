import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger

class GridGraph(private val grid: Grid<Char>) : GraphLike<Point> {
    override fun getNeighbors(node: Point): List<Point> =
        Direction.cardinal.map { node + it.vector }.filter { grid.isValid(it) && grid[it] != '#' }
}

fun findPoints(grid: Grid<Char>): Pair<Point, Point> {
    var start: Point? = null
    var end: Point? = null
    grid.forPoints { point, value ->
        if (value == 'S') {
            start = point
        } else if (value == 'E') {
            end = point
        }
    }
    if (start == null || end == null) {
        error("Start/end not found $start/$end")
    }

    return Pair(start, end)
}

data class Frame(val point: Point, val moves: Int) {
    constructor(point: Point) : this(point, 0)
}

fun findCheats(
    grid: Grid<Char>,
    paths: Map<Point, List<Point>>,
    cheatStart: Point,
    threshold: Int,
): Set<Pair<Point, Point>> {
    val cheats = mutableSetOf<Pair<Point, Point>>()
    val queue = ArrayDeque<Frame>()
    val seen = mutableSetOf<Point>()

    queue.add(Frame(cheatStart))

    while (!queue.isEmpty()) {
        val (point, moves) = queue.removeFirst()

        if (seen.contains(point)) {
            continue
        }
        seen.add(point)

        if (grid.isValid(point) && grid[point] != '#') {
            // This is guaranteed because `cheatStart` comes from traversing another path from the
            // `paths` (an edsger) response so we know this point is reachable so it should
            // appear in the map.
            val currBest = paths[cheatStart]!!.size

            // Problem statement guarantees that we hit all passable points in the track.
            val newBest = paths[point]!!.size

            // If we reach a point that otherwise would have taken us enough extra steps
            // (threshold from the problem + moves for the cheat) then it counts.
            if (newBest >= currBest + threshold + moves) {
                cheats.add(Pair(cheatStart, point))
            }
        }

        if (moves >= 20) {
            continue
        }

        queue.addAll(Direction.cardinal.map { Frame(point + it.vector, moves + 1) })
    }

    return cheats.toSet()
}

fun findAllCheats(grid: Grid<Char>, start: Point, end: Point, threshold: Int): Int {
    val graph = GridGraph(grid)
    val paths = edsger(start, graph)

    // Problem statement guarantees that this path is unique.
    val noCheatPath = paths[end]!!
    val cheats = mutableSetOf<Pair<Point, Point>>()

    // Cartesian product of cardinal x cardinal.
    val directions =
        Direction.cardinal.flatMap { outer -> Direction.cardinal.map { Pair(outer, it) } }

    for (point in noCheatPath) {
        // Unlike part 1 (where we hardcode 2 steps) delegate this to a traversal that finds a cheat
        // taking at most 20 steps.
        cheats.addAll(findCheats(grid, paths, point, threshold))
    }

    return cheats.size
}

fun main(args: Array<String>) {
    val threshold =
        if (args.size == 0) {
            100
        } else {
            args[0].toInt()
        }

    val grid = Grid.read('.')
    val (start, end) = findPoints(grid)

    val score = findAllCheats(grid, start, end, threshold)

    println("Answer = $score")
}
