import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Lines
import net.panayotov.util.Point
import net.panayotov.util.times

const val ROBOT = '@'
const val BOX = 'O'
const val BOX_LEFT = '['
const val BOX_RIGHT = ']'
const val BLANK = '.'
const val WALL = '#'

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

fun Char.isBox() = this == BOX_LEFT || this == BOX_RIGHT

fun Grid<Char>.boxPair(point: Point) =
    when (this[point]) {
        BOX_LEFT -> Pair(point, point + Direction.EAST.vector)
        BOX_RIGHT -> Pair(point + Direction.WEST.vector, point)
        else -> null
    }

fun boxesToMove(grid: Grid<Char>, robot: Point, direction: Direction): List<Pair<Point, Point>>? {
    val boxes = mutableListOf<Pair<Point, Point>>()

    val queue = ArrayDeque<Pair<Point, Point>>()

    var next = robot + direction.vector
    if (grid.isValid(next)) {
        if (grid[next] == BLANK) {
            return listOf()
        } else if (grid[next] == WALL) {
            return null
        }
    }

    grid.boxPair(next)?.also { queue.add(it) }

    while (queue.size > 0) {
        val pair = queue.removeFirst()
        val (left, right) = pair
        boxes.add(pair)

        val newLeft = left + direction.vector
        val newRight = right + direction.vector

        if (grid[newLeft] == WALL || grid[newRight] == WALL) {
            return null
        }

        val newBox =
            (setOf(newRight, newLeft) - setOf(left, right)).toList().filter { grid[it].isBox() }

        queue.addAll(newBox.map { grid.boxPair(it) })
    }

    return boxes.toList()
}

fun followInstructions(grid: Grid<Char>, instructions: List<Direction>): Long {
    var robot = grid.findRobot()

    for (instruction in instructions) {
        val boxes = boxesToMove(grid, robot, instruction)
        if (boxes != null) {
            for (i in boxes.size - 1 downTo 0) {
                val (newLeft, newRight) = boxes[i]
                grid[newLeft] = BLANK
                grid[newRight] = BLANK

                grid[newLeft + instruction.vector] = BOX_LEFT
                grid[newRight + instruction.vector] = BOX_RIGHT
            }
            grid[robot] = BLANK
            robot = robot + instruction.vector
            grid[robot] = ROBOT
        }
    }

    var score = 0L
    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            if (grid[x, y] == BOX_LEFT) {
                score += x + (grid.height - y - 1) * 100
            }
        }
    }

    return score
}

fun widen(grid: Grid<Char>): Grid<Char> {
    val wideGrid = Grid(2 * grid.width, grid.height, '.')

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val (first, second) =
                when (grid[x, y]) {
                    BLANK -> Pair(BLANK, BLANK)
                    BOX -> Pair(BOX_LEFT, BOX_RIGHT)
                    ROBOT -> Pair(ROBOT, BLANK)
                    WALL -> Pair(WALL, WALL)
                    else -> error("Unexpected ${grid[x, y]}")
                }
            wideGrid[2 * x, y] = first
            wideGrid[2 * x + 1, y] = second
        }
    }

    return wideGrid
}

fun main() {
    val grid = widen(Grid.read(BLANK, ""))
    val instructions = parseInstructions()

    val score = followInstructions(grid, instructions)

    println("Answer = $score")
}
