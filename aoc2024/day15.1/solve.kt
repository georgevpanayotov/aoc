import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Lines
import net.panayotov.util.Point
import net.panayotov.util.times

const val ROBOT = '@'
const val BOX = 'O'
const val BLANK = '.'

// I didn't mean for the synonym here of instruction/directions.
fun parseInstructions(): List<Direction> {
    val instructions = mutableListOf<Direction>()

    for (line in Lines) {
        for (dir in line) {
            instructions.add(
                when (dir) {
                    '<' -> Direction.WEST
                    '>' -> Direction.EAST
                    'v' -> Direction.SOUTH
                    '^' -> Direction.NORTH
                    else -> error("Unrecognized direction $dir")
                }
            )
        }
    }

    return instructions.toList()
}

fun Grid<Char>.findRobot(): Point {
    for (x in 0L..<width) {
        for (y in 0L..<height) {
            val point = Point(x, y)
            if (this[point] == ROBOT) {
                return point
            }
        }
    }

    error("No robot found.")
}

fun boxesToMove(grid: Grid<Char>, robot: Point, direction: Direction): Long? {
    var boxes = 0L
    var box = robot + direction.vector

    while (grid.isValid(box) && grid[box] == BOX) {
        boxes++
        box = box + direction.vector
    }

    return if (grid.isValid(box) && grid[box] == '#') {
        // We hit a wall
        null
    } else {
        boxes
    }
}

fun followInstructions(grid: Grid<Char>, instructions: List<Direction>): Long {
    var robot = grid.findRobot()

    for (instruction in instructions) {
        val boxes = boxesToMove(grid, robot, instruction)
        if (boxes != null) {
            val vector = instruction.vector

            grid[robot] = BLANK
            grid[robot + vector] = ROBOT
            if (boxes != 0L) {
                grid[robot + (boxes + 1) * vector] = BOX
            }

            robot = robot + vector
        }
    }

    var score = 0L
    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            if (grid[x, y] == BOX) {
                score += x + (grid.height - y - 1) * 100
            }
        }
    }

    return score
}

fun main() {
    val grid = Grid.read(BLANK, "")
    val instructions = parseInstructions()

    val score = followInstructions(grid, instructions)

    println("Answer = $score")
}
