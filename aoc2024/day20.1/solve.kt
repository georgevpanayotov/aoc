import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger

class GridGraph(private val grid: Grid<Char>) : GraphLike<Point> {
    private val nodes: List<Point>

    init {
        val mutableNodes = mutableListOf<Point>()

        grid.forPoints { point, value ->
            if (value != '#') {
                mutableNodes.add(point)
            }
        }

        nodes = mutableNodes.toList()
    }

    override fun getNeighbors(node: Point): List<Point> =
        Direction.cardinal.map { node + it.vector }.filter { grid.isValid(it) && grid[it] != '#' }

    override fun getNodes(): List<Point> = nodes
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

fun findAllCheats(grid: Grid<Char>, start: Point, end: Point, threshold: Int): Int {
    val graph = GridGraph(grid)
    val paths = edsger(start, graph)

    // Problem statement guarantees that this path is unique.
    val noCheatPath = paths[end]!!
    var score = 0

    // Cartesian product of cardinal x cardinal.
    val directions =
        Direction.cardinal.flatMap { outer -> Direction.cardinal.map { Pair(outer, it) } }

    for (point in noCheatPath) {
        for ((dir1, dir2) in directions) {
            val point1 = point + dir1.vector
            val point2 = point1 + dir2.vector

            // Does this pair of directions result in us cutting through a wall and coming out on a
            // point in the path?
            if (
                grid.isValid(point1) &&
                    grid[point1] == '#' &&
                    grid.isValid(point2) &&
                    grid[point2] != '#'
            ) {
                // This is guaranteed because `point` comes from traversing another path from the
                // `paths` (an edsger) response so we know this point is reachable so it should
                // appear in the map.
                val currBest = paths[point]!!.size

                // Problem statement guarantees that we hit all passable points in the track.
                val newBest = paths[point2]!!.size

                // If we reach a point that otherwise would have taken us enough extra steps
                // (threshold from the problem + 2 for the cheat) then it counts.
                if (newBest >= currBest + threshold + 2) {
                    score++
                }
            }
        }
    }

    return score
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
