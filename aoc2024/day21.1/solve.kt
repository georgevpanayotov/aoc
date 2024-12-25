import kotlin.math.abs
import kotlin.math.sign
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Lines
import net.panayotov.util.Point

val numPad = Grid.read(' ', listOf("789", "456", "123", " 0A"))
val dirPad = Grid.read(' ', listOf(" ^A", "<v>"))

fun find(grid: Grid<Char>, target: Char): Point? {
    var found: Point? = null
    grid.forPoints { point, value ->
        if (value == target) {
            found = point
        }
    }

    if (found == null) {
        error("Key $target not found on pad:\n$grid")
    }

    return found
}

// Turns a vector into dpad move instructions. Optimizes by grouping all vertical/horizontal moves
// together to reduce changing direction. Changing direction is expensive because the robot
// controlling this one must move to another dpad spot to change the direction. Instead of just
// hitting "A".
fun vectorToDpad(vector: Point): List<List<Char>> {
    val (x, y) = vector

    val xMoves = (1..abs(x)).map { if (x.sign > 0) '>' else '<' }
    val yMoves = (1..abs(y)).map { if (y.sign > 0) '^' else 'v' }

    return listOf(xMoves + yMoves + listOf('A'), yMoves + xMoves + listOf('A'))
}

fun dpadToVector(char: Char) =
    when (char) {
        '^' -> Direction.NORTH.vector
        '<' -> Direction.WEST.vector
        'v' -> Direction.SOUTH.vector
        '>' -> Direction.EAST.vector
        else -> null
    }

// Returns the shortest number of d-pad presses that a must be entered to move a robot from one
// position to another. The from/to points are in the given Grid.
fun shortest(grid: Grid<Char>, from: Point, to: Point): List<List<Char>> =
    vectorToDpad(to - from).filter {
        var curr = from
        for (dpad in it) {
            curr = dpadToVector(dpad)?.let { curr + it } ?: curr

            if (!grid.isValid(curr) || grid[curr] == ' ') {
                return@filter false
            }
        }
        true
    }

fun solve(code: String) =
    code.substring(0, code.length - 1).toLong() * shortestByPad(0, code.map { it })

// The shortest number of instructions that the human must enter on the dpad to enter the code on
// the given pad (identified by an index where 0 == the numeric pad).
fun shortestByPad(iPad: Int, code: List<Char>): Long {
    val pads = listOf(numPad, dirPad, dirPad)
    if (iPad >= pads.size) {
        return code.size.toLong()
    }

    val pad = pads[iPad]
    var score = 0L

    // Robots always start at A.
    var from = find(pad, 'A')!!
    for (key in code) {
        val to = find(pad, key)!!

        val newInstructions = shortest(pad, from, to)

        val scores = newInstructions.map { shortestByPad(iPad + 1, it) }
        score += scores.minOf { it }

        from = to
    }

    return score
}

fun main() {
    val codes = Lines.asSequence().toList()

    val score = codes.map(::solve).reduce { acc, value -> acc + value }
    println("Answer = $score")
}
