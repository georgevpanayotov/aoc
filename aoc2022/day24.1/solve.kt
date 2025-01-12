import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger

data class Blizzard(val position: Point, val direction: Direction) {
    // Move once in the given direction.
    fun move(grid: Grid<Char>): Blizzard {
        var newPos = position + direction.vector
        if (!grid.isValid(newPos) || grid[newPos] == '#') {
            newPos = position

            // Move off the edge so go back to the other side.
            while (grid.isValid(newPos) && grid[newPos] != '#') {
                newPos -= direction.vector
            }

            newPos += direction.vector
        }

        return Blizzard(newPos, direction)
    }
}

fun possibleStep(point: Point, grid: Grid<Char>, unsafe: Set<Point>) =
    grid.isValid(point) && grid[point] != '#' && !unsafe.contains(point)

fun convertBlizzard(gridVal: Char): Direction? =
    when (gridVal) {
        '>' -> Direction.EAST
        '^' -> Direction.NORTH
        '<' -> Direction.WEST
        'v' -> Direction.SOUTH
        else -> null
    }

fun findBlizzards(grid: Grid<Char>): List<Blizzard> {
    val blizzards = mutableListOf<Blizzard>()

    grid.forPoints { pt, value -> convertBlizzard(value)?.let { blizzards.add(Blizzard(pt, it)) } }

    return blizzards.toList()
}

// The blizzards move in a cyclic pattern. The returned list, is the set of unsafe points (points
// with blizzards in them) for that step.
fun getCycles(grid: Grid<Char>): List<Set<Point>> {
    var blizzards = findBlizzards(grid)

    val cycles = mutableListOf<List<Blizzard>>()
    val seen = mutableSetOf<List<Blizzard>>()

    while (!seen.contains(blizzards)) {
        cycles.add(blizzards)
        seen.add(blizzards)

        blizzards = blizzards.map { it.move(grid) }
    }

    return cycles.map { it.map { it.position }.toSet() }
}

fun findPositions(grid: Grid<Char>): Pair<Point, Point> {
    var start: Point? = null
    var end: Point? = null

    for (x in 0L..<grid.width) {
        // Start in the gap in the top row.
        if (grid[x, grid.height - 1L] == '.') {
            start = Point(x, grid.height - 1L)
        }

        // End at the gap in the bottom row.
        if (grid[x, 0L] == '.') {
            end = Point(x, 0L)
        }
    }

    if (start == null || end == null) {
        error("Missing [$start, $end]")
    }

    return Pair(start, end)
}

data class Node(val position: Point, val step: Int) {
    fun proposeNext(cyclesSize: Int): List<Node> {
        // For steps beyond the step list, loop back to the start.
        val nextStep = (step + 1) % cyclesSize

        // Valid positions include staying in the same spot.
        val positions = Direction.cardinal.map { position + it.vector } + listOf(position)

        return positions.map { Node(it, nextStep) }
    }
}

class BlizzardGraph(val grid: Grid<Char>, val cycles: List<Set<Point>>) : GraphLike<Node> {
    override fun getNeighbors(node: Node) =
        node.proposeNext(cycles.size).filter { possibleStep(it.position, grid, cycles[it.step]) }
}

fun findShortest(grid: Grid<Char>, cycles: List<Set<Point>>, start: Point, end: Point): Int {
    val graph = BlizzardGraph(grid, cycles)
    val paths = edsger(Node(start, 0), graph) { it.position == end }

    val shortests = mutableListOf<Int>()

    for (key in paths.keys) {
        val (point, step) = key

        if (point == end) {
            shortests.add(paths[key]!!.size)
        }
    }

    // Subtract 1 because this is giving us the whole path but we just want the number of steps.
    return shortests.sorted()[0] - 1
}

fun main() {
    val grid = Grid.read('.')
    val (start, end) = findPositions(grid)
    val cycles = getCycles(grid)

    val score = findShortest(grid, cycles, start, end)

    println("Answer = $score")
}
