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

fun computeBeams(grid: Grid<Char>): Int {
    var startingPoint = getStartingPoint(grid)
    var splits = 0

    var beams = listOf<Point>(startingPoint)

    while (!beams.isEmpty()) {
        var newBeams = mutableSetOf<Point>()
        for (beam in beams) {
            var newBeam = beam + Direction.SOUTH.vector
            if (!grid.isValid(newBeam)) {
                continue
            }

            if (grid[newBeam] == '^') {
                var left = newBeam + Direction.WEST.vector
                var right = newBeam + Direction.EAST.vector
                if (grid.isValid(left)) {
                    newBeams.add(left)
                }

                if (grid.isValid(right)) {
                    newBeams.add(right)
                }
                splits++
            } else {
                newBeams.add(newBeam)
            }
        }

        beams = newBeams.toList()
    }

    return splits
}

fun main() {
    val grid = Grid.read('.')

    val score = computeBeams(grid)

    println("Answer = $score")
}
