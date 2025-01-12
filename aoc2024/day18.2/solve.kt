import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.edsger

class GridGraph(private val grid: Grid<Char>) : GraphLike<Point> {
    override fun getNeighbors(node: Point): List<Point> =
        Direction.cardinal.map { node + it.vector }.filter { grid.isValid(it) && grid[it] != '#' }
}

fun isReachable(grid: Grid<Char>): Boolean {
    val start = Point(0, 0)
    val target = Point(grid.width - 1L, grid.height - 1L)

    val paths = edsger(start, GridGraph(grid))

    val path = paths[target]

    return path != null && path.size > 0 && path[0] == start && path[path.size - 1] == target
}

fun findUnreachable(originalGrid: Grid<Char>, points: List<Point>): String {
    // Binary search to find the spot where it starts to be unreachable.
    var min = 0
    var max = points.size - 1

    while (max - min > 1) {
        // Copy because the we are going out of order so we don't want to leave the later bytes on
        // the board when we get back to an earlier spot.
        val grid = originalGrid.copy()

        val midpoint = (min + max) / 2
        for (i in 0..midpoint) {
            grid[points[i]] = '#'
        }

        if (isReachable(grid)) {
            min = midpoint
        } else {
            max = midpoint
        }
    }

    val (x, y) = points[max]

    return "$x,$y"
}

fun main(args: Array<String>) {
    val size =
        if (args.size > 0) {
            args[0].toInt()
        } else {
            71
        }

    val bytes =
        if (args.size > 1) {
            args[1].toInt()
        } else {
            1024
        }

    val grid = Grid(size, size, '.')
    val points = mutableListOf<Point>()

    for (line in Scanners) {
        line.useDelimiter(",")
        val x = line.nextLong()
        val y = line.nextLong()
        points.add(Point(x, y))
    }

    val answer = findUnreachable(grid, points)

    println("Answer = $answer")
}
