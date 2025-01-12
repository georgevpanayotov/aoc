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

    var count = 0
    for (line in Scanners) {
        line.useDelimiter(",")
        val x = line.nextLong()
        val y = line.nextLong()
        grid[x, y] = '#'
        count++
        if (count == bytes) {
            break
        }
    }

    val start = Point(0, 0)
    val target = Point(size - 1L, size - 1L)

    val paths = edsger(start, GridGraph(grid))

    // Subtract 1 because we don't count the starting spot in the number of steps.
    val score = paths[target]?.size?.let { it - 1 }

    println("Answer = $score")
}
