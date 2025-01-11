import java.util.PriorityQueue
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

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

data class State(val score: Int, val position: Point, val step: Int) {
    fun proposeNext(cyclesSize: Int): List<State> {
        // For steps beyond the step list, loop back to the start.
        val nextStep = (step + 1) % cyclesSize

        // Valid positions include staying in the same spot.
        val positions = Direction.cardinal.map { position + it.vector } + listOf(position)

        return positions.map { State(score + 1, it, nextStep) }
    }
}

fun findShortest(
    grid: Grid<Char>,
    cycles: List<Set<Point>>,
    start: Point,
    startStep: Int,
    end: Point,
): Int {
    val queue = PriorityQueue<State>(compareBy { it.score })
    val seen = mutableSetOf<Pair<Point, Int>>()

    queue.add(State(0, start, startStep))

    while (!queue.isEmpty()) {
        val state = queue.poll()!!
        val (score, position, step) = state

        if (position == end) {
            return score
        }

        if (!seen.add(Pair(position, step))) {
            continue
        }

        val next = state.proposeNext(cycles.size)

        queue.addAll(next.filter { possibleStep(it.position, grid, cycles[it.step]) })
    }

    error("Failed to reach end.")
}

fun main() {
    val grid = Grid.read('.')
    val (start, end) = findPositions(grid)
    val cycles = getCycles(grid)

    val there = findShortest(grid, cycles, start, 0, end)
    val back = findShortest(grid, cycles, end, there, start)
    val aHobbitsTale = findShortest(grid, cycles, start, there + back, end) + there + back

    println("Answer = $aHobbitsTale")
}
