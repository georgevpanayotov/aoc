import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

fun getStartingPoint(grid: Grid<Char>): Point {
    var startingPoint: Point? = null

    grid.forPoints { pt, value ->
        if (value == 'S') {
            startingPoint = pt
        }
    }

    if (startingPoint == null) {
        error("No start found")
    }

    return startingPoint + Direction.SOUTH.vector
}

// Recursive solution with memoization to optimize.
fun computeTimelines(memo: MutableMap<Point, Long>, grid: Grid<Char>, beam: Point): Long {
    if (memo.contains(beam)) {
        return memo[beam]!!
    }
    var newBeam = beam

    while (grid.isValid(newBeam) && grid[newBeam] == '.') {
        newBeam = newBeam + Direction.SOUTH.vector
    }

    if (!grid.isValid(newBeam)) {
        return 1L
    } else if (grid[newBeam] == '^') {
        var left = newBeam + Direction.WEST.vector
        var right = newBeam + Direction.EAST.vector

        val timelinesLeft = computeTimelines(memo, grid, left)
        val timelinesRight = computeTimelines(memo, grid, right)

        memo[left] = timelinesLeft
        memo[right] = timelinesRight

        return timelinesLeft + timelinesRight
    } else {
        error("Unexpected beam stopping at $beam")
    }
}

fun main() {
    val grid = Grid.read('.')

    val score = computeTimelines(mutableMapOf(), grid, getStartingPoint(grid))

    println("Answer = $score")
}
