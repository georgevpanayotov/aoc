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

fun findAntiNodes(grid: Grid<Char>, antennaMap: Map<Char, List<Point>>): Set<Point> {
    val uniqueAntiNodes = mutableSetOf<Point>()

    for (entries in antennaMap.entries) {
        val antennae = entries.value

        for (i in 0..<antennae.size) {
            for (j in i + 1..<antennae.size) {
                val first = antennae[i]
                val second = antennae[j]

                val diff = first - second

                val antiNodes = listOf(first + diff, second - diff)

                uniqueAntiNodes.addAll(antiNodes.filter { grid.isValid(it) })
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
