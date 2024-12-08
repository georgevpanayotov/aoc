import net.panayotov.util.Grid
import net.panayotov.util.Point

fun findAntennae(grid: Grid<Char>): Map<Char, List<Point>> {
    val antennae = mutableMapOf<Char, MutableList<Point>>()

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val point = Point(x, y)
            val name = grid[point]
            if (name.isDigit() || name.isLetter()) {
                antennae.getOrPut(name) { mutableListOf() }.add(point)
            }
        }
    }

    return antennae.mapValues { it.value.toList() }
}

// Finds all valid points in the given grid from the starting point (inclusive) following a given
// vector.
fun findValidInLine(grid: Grid<Char>, starting: Point, vector: Point): List<Point> {
    val points = mutableListOf<Point>()
    var current = starting

    while (grid.isValid(current)) {
        points.add(current)
        current = current + vector
    }

    return points.toList()
}

fun findAntiNodes(grid: Grid<Char>, antennaMap: Map<Char, List<Point>>): Set<Point> {
    val uniqueAntiNodes = mutableSetOf<Point>()

    for (entries in antennaMap.entries) {
        val antennae = entries.value

        for (i in 0..<antennae.size) {
            for (j in i + 1..<antennae.size) {
                val first = antennae[i]
                val second = antennae[j]

                uniqueAntiNodes.addAll(findValidInLine(grid, first, first - second))
                uniqueAntiNodes.addAll(findValidInLine(grid, second, second - first))
            }
        }
    }

    return uniqueAntiNodes
}

fun main() {
    val grid = Grid.read('.')

    val antennaMap = findAntennae(grid)
    val antiNodes = findAntiNodes(grid, antennaMap)

    println("Answer = ${antiNodes.size}")
}
