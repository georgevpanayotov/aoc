import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

const val STEPS = 10
val DIRECTIONS = listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

fun findElves(grid: Grid<Char>): MutableSet<Point> {
    val elves = mutableSetOf<Point>()

    grid.forPoints { pt, value ->
        if (value == '#') {
            elves.add(pt)
        }
    }

    return elves
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

fun empty(elves: Set<Point>, point: Point, directions: List<Direction>): Boolean {
    val neighboring = directions.map { point + it.vector }.filter { elves.contains(it) }
    return neighboring.size == 0
}

data class Proposal(val elf: Point, val newPoint: Point)

fun propose(elves: Set<Point>, elf: Point, iDir: Int): Proposal? {
    if (empty(elves, elf, Direction.all)) {
        return null
    }

    for (i in 0..3) {
        val direction = DIRECTIONS[(iDir + i) % 4]
        val neighboring = neighboringDirections(direction)

        if (empty(elves, elf, neighboring)) {
            return Proposal(elf, elf + direction.vector)
        }
    }

    return null
}

fun doStep(elves: MutableSet<Point>, iDir: Int): Boolean {
    val proposals = mutableMapOf<Point, MutableList<Proposal>>()

    for (elf in elves) {
        val proposal = propose(elves, elf, iDir)
        if (proposal != null) {
            proposals.getOrPut(proposal.newPoint) { mutableListOf() }.add(proposal)
        }
    }

    val accepted = proposals.entries.filter { it.value.size == 1 }.map { it.value[0] }

    for (proposal in accepted) {
        elves.remove(proposal.elf)
        elves.add(proposal.newPoint)
    }

    return accepted.size > 0
}

fun main() {
    var grid = findElves(Grid.read('.'))
    var iDir = 0
    var moving = true
    var step = 0

    while (moving) {
        moving = doStep(grid, iDir)

        iDir++
        iDir = iDir % 4
        step++
    }

    println("Answer = $step")
}
