import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

fun findGuard(grid: Grid<Char>): Point {
    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            if (grid[x, y] == '^') {
                return Point(x, y)
            }
        }
    }

    error("No Point!")
}

fun isLooping(grid: Grid<Char>, guardStart: Point): Boolean {
    var guard = guardStart

    // Each entry indicates which directions we enter that point from. This is a list because each
    // point might be entered from multiple directions.
    val dirGrid =
        Grid<MutableList<Direction>>(grid.width, grid.height) { mutableListOf<Direction>() }

    // Start at NORTH (first index in Direction.cardinal).
    var directionI = 0

    while (true) {
        val nextPosition = guard + Direction.cardinal[directionI].vector

        if (!grid.isValid(nextPosition)) {
            // We have exited the grid without finding a loop.
            return false
        } else if (grid[nextPosition] == '#') {
            directionI = (directionI + 1) % Direction.cardinal.size
        } else {
            guard = nextPosition
            if (dirGrid[guard].contains(Direction.cardinal[directionI])) {
                // We have entered this cell from the same direction before so we conclude we have
                // found a loop.
                return true
            }

            dirGrid[guard].add(Direction.cardinal[directionI])
        }
    }
}

fun main() {
    val grid = Grid.read('.')
    val guardStart = findGuard(grid)

    var guard = guardStart

    // Start at NORTH (first index in Direction.cardinal).
    var directionI = 0

    // First, save the points of path without any added obstructions.
    val path = mutableSetOf<Point>()
    while (grid.isValid(guard)) {
        path.add(guard)

        val nextPosition = guard + Direction.cardinal[directionI].vector

        if (!grid.isValid(nextPosition)) {
            guard = nextPosition
        } else if (grid[nextPosition] == '#') {
            directionI = (directionI + 1) % Direction.cardinal.size
        } else {
            guard = nextPosition
        }
    }

    var score = 0
    for (point in path) {
        // Each point  in the path (except the starting point) is a potential candidate for the
        // obstruction. Try it out on a copy of the grid.
        if (point == guardStart) {
            continue
        }

        val testGrid = grid.copy()
        testGrid[point] = '#'

        if (isLooping(testGrid, guardStart)) {
            score++
        }
    }

    println("Answer = $score")
}
