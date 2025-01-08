import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

const val STEPS = 10
val DIRECTIONS = listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

fun findElves(grid: Grid<Char>): List<Point> {
    val elves = mutableListOf<Point>()

    grid.forPoints { pt, value ->
        if (value == '#') {
            elves.add(pt)
        }
    }

    return elves.toList()
}

fun expandGrid(grid: Grid<Char>, delta: Int): Grid<Char> {
    val offset = Point(delta.toLong(), delta.toLong())

    val newGrid = Grid(grid.width + 2 * delta, grid.height + 2 * delta, '.')
    grid.forPoints { pt, value ->
        if (value == '#') {
            newGrid[pt + offset] = '#'
        }
    }

    return newGrid
}

fun neighboringDirections(direction: Direction): List<Direction> {
    val i = Direction.all.indexOf(direction)
    val size = Direction.all.size
    return listOf(
        Direction.all[((i - 1) % size + size) % size],
        direction,
        Direction.all[(i + 1) % size],
    )
}

fun empty(grid: Grid<Char>, point: Point, directions: List<Direction>): Boolean {
    val neighboring =
        directions.map { point + it.vector }.filter { grid.isValid(it) && grid[it] == '#' }
    return neighboring.size == 0
}

data class Proposal(val elf: Point, val newPoint: Point)

fun propose(grid: Grid<Char>, elf: Point, iDir: Int): Proposal? {
    if (empty(grid, elf, Direction.all)) {
        return null
    }

    for (i in 0..3) {
        val direction = DIRECTIONS[(iDir + i) % 4]
        val neighboring = neighboringDirections(direction)

        if (empty(grid, elf, neighboring)) {
            return Proposal(elf, elf + direction.vector)
        }
    }

    return null
}

fun doStep(grid: Grid<Char>, iDir: Int) {
    val elves = findElves(grid)
    val proposals = mutableMapOf<Point, MutableList<Proposal>>()

    for (elf in elves) {
        val proposal = propose(grid, elf, iDir)
        if (proposal != null) {
            proposals.getOrPut(proposal.newPoint) { mutableListOf() }.add(proposal)
        }
    }

    val accepted = proposals.entries.filter { it.value.size == 1 }.map { it.value[0] }

    for (proposal in accepted) {
        grid[proposal.elf] = '.'
        grid[proposal.newPoint] = '#'
    }
}

fun computeScore(grid: Grid<Char>): Int {
    val elves = findElves(grid)
    var minX: Long? = null
    var minY: Long? = null
    var maxX: Long? = null
    var maxY: Long? = null

    for (elf in elves) {
        if (minX == null || elf.x < minX) {
            minX = elf.x
        }
        if (minY == null || elf.y < minY) {
            minY = elf.y
        }
        if (maxX == null || elf.x > maxX) {
            maxX = elf.x
        }
        if (maxY == null || elf.y > maxY) {
            maxY = elf.y
        }
    }

    if (minX == null || maxX == null || minY == null || maxY == null) {
        error("No elves found.")
    }

    var score = 0
    for (x in minX..maxX) {
        for (y in minY..maxY) {
            if (grid[x, y] == '.') {
                score++
            }
        }
    }

    return score
}

fun main() {
    var grid = expandGrid(Grid.read('.'), STEPS)
    var iDir = 0

    for (i in 1..STEPS) {
        doStep(grid, iDir)
        iDir++
        iDir = iDir % 4
    }

    println("Answer = ${computeScore(grid)}")
}
