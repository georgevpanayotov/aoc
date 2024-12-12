import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

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

fun regionPerimeter(grid: Grid<Char>, region: List<Point>): Int {
    var perimeter = 0

    for (point in region) {
        for (direction in Direction.cardinal) {
            val newPoint = point + direction.vector
            if (!grid.isValid(newPoint) || grid[point] != grid[newPoint]) {
                perimeter++
            }
        }
    }

    return perimeter
}

fun main() {
    val grid = Grid.read('.')

    val regions = parseRegions(grid)

    val score =
        regions.map { regionPerimeter(grid, it) * it.size }.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
