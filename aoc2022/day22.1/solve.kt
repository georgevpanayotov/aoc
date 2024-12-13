import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.toDirection

fun Grid<Char>.isOnBoard(point: Point) = isValid(point) && this[point] != ' '

fun Grid<Char>.start(): Position {
    var curr = Position(Point(0, height - 1L), Direction.EAST)

    while (this[curr.point] != '.') {
        curr = curr.next()
    }

    return curr
}

// Assume that position is off the board. Return Position a point on the other end of the board
// (outside of the board).
fun Grid<Char>.oppositeEdge(position: Position): Position {
    var opposite = position.point - position.direction.vector
    while (isOnBoard(opposite)) {
        opposite = opposite - position.direction.vector
    }
    return Position(opposite, position.direction)
}

data class Position(val point: Point, val direction: Direction) {
    fun next() = Position(point + direction.vector, direction)
}

sealed interface Instruction {
    fun nextPosition(grid: Grid<Char>, position: Position): Position
}

data object TurnRight : Instruction {
    override fun nextPosition(grid: Grid<Char>, position: Position) =
        Position(position.point, position.direction.vector.rotateRight().toDirection()!!)
}

data object TurnLeft : Instruction {
    override fun nextPosition(grid: Grid<Char>, position: Position) =
        Position(position.point, position.direction.vector.rotateLeft().toDirection()!!)
}

data class Move(val count: Int) : Instruction {
    override fun nextPosition(grid: Grid<Char>, position: Position): Position {
        var hitWall = false
        var curr = position
        var moves = 0

        while (!hitWall && moves < count) {
            var next = curr.next()

            if (!grid.isOnBoard(next.point)) {
                val opposite = grid.oppositeEdge(next)

                next = opposite.next()
            }

            if (grid[next.point] == '.') {
                curr = next
                moves++
            } else if (grid[next.point] == '#') {
                hitWall = true
            }
        }

        return curr
    }
}

fun parseInstructions(line: String): List<Instruction> {
    var number = true
    var i = 0
    val instructions = mutableListOf<Instruction>()

    while (i < line.length) {
        if (line[i].isDigit()) {
            val numberBuilder = StringBuilder()
            while (i < line.length && line[i].isDigit()) {
                numberBuilder.append(line[i])
                i++
            }

            instructions.add(Move(numberBuilder.toString().toInt()))
        } else if (line[i] == 'R') {
            instructions.add(TurnRight)
            i++
        } else if (line[i] == 'L') {
            instructions.add(TurnLeft)
            i++
        }
    }

    return instructions.toList()
}

fun followInstructions(grid: Grid<Char>, instructions: List<Instruction>): Int {
    var curr = grid.start()
    val drawGrid = grid.copy()

    for (instruction in instructions) {
        curr = instruction.nextPosition(grid, curr)
        drawGrid[curr.point] = '*'
    }

    return (1000 * (grid.height - curr.point.y) +
            4 * (curr.point.x + 1) +
            Direction.cardinal.indexOf(curr.direction.vector.rotateLeft().toDirection()!!))
        .toInt()
}

fun main() {
    // NOTE: Works only if you pre-process the input into a perfect grid (i.e. pad it with spaces so
    // each line is the same length).with spaces so each line is the same length).with spaces so
    // each line is the same length).with spaces so each line is the same length).
    val grid = Grid.read(' ', "")
    val instructions = parseInstructions(readLine()!!)

    val score = followInstructions(grid, instructions)
    println("Answer = $score")
}
