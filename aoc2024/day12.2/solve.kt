import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

data class Side(val points: Set<Point>, val direction: Direction) {
    fun merge(other: Side): Side? {
        var merge = false
        for (point in other.points) {
            // If two points (from each side) are next to each other (in a direction orthogonal to
            // the side direction) we can merge the sides.
            if (
                ((points intersect neighbors(point, other.direction)).size > 0) &&
                    direction == other.direction
            ) {
                merge = true
                break
            }
        }

        return if (merge) {
            val newPoints = points.toMutableSet()
            newPoints.addAll(other.points)

            Side(newPoints.toSet(), direction)
        } else {
            null
        }
    }
}

fun iterateRegion(grid: Grid<Char>, seen: MutableSet<Point>, start: Point): List<Point> {
    val resource = grid[start]

    val queue = ArrayDeque<Point>()
    val region = mutableListOf<Point>()

    queue.add(start)

    while (queue.size > 0) {
        val point = queue.removeFirst()

        if (grid.isValid(point) && grid[point] == resource && !seen.contains(point)) {
            region.add(point)
            seen.add(point)

            queue.addAll(Direction.cardinal.map { point + it.vector })
        }
    }

    return region.toList()
}

fun parseRegions(grid: Grid<Char>): List<List<Point>> {
    val seen = mutableSetOf<Point>()
    val regions = mutableListOf<List<Point>>()

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val point = Point(x, y)
            if (seen.contains(point)) {
                continue
            }

            regions.add(iterateRegion(grid, seen, point))
        }
    }

    return regions.toList()
}

// Neighbors of a point orthogonal to a direction.
fun neighbors(point: Point, direction: Direction) =
    setOf(point + direction.vector.rotateRight(), point + direction.vector.rotateLeft())

fun mergeSides(sides: MutableList<Side>): Boolean {
    var i = 0
    var merges = 0
    while (i < sides.size) {
        var j = i + 1
        while (j < sides.size) {
            val mergedSide = sides[i].merge(sides[j])

            if (mergedSide != null) {
                sides[i] = mergedSide
                sides.removeAt(j)
                merges++
            } else {
                j++
            }
        }

        i++
    }

    return merges == 0
}

fun regionSides(grid: Grid<Char>, region: List<Point>): Int {
    val sides = mutableListOf<Side>()

    for (point in region) {
        for (direction in Direction.cardinal) {
            val newPoint = point + direction.vector
            if (!grid.isValid(newPoint) || grid[point] != grid[newPoint]) {
                sides.add(Side(setOf(point), direction))
            }
        }
    }

    var done = false
    while (!done) {
        done = mergeSides(sides)
    }

    return sides.size
}

fun main() {
    val grid = Grid.read('.')

    val regions = parseRegions(grid)

    val score = regions.map { regionSides(grid, it) * it.size }.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
