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

fun main() {
    val grid = Grid.read('.')

    var guard = findGuard(grid)

    // Start at NORTH (first index in Direction.cardinal).
    var directionI = 0

    while (grid.isValid(guard)) {
        grid[guard] = 'X'

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
    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            if (grid[x, y] == 'X') {
                score++
            }
        }
    }
    println("Answer = $score")
}
