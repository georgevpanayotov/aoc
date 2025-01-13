import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.findMinMaxPoint

data class Instruction(val direction: Direction, val length: Int, val color: String)

fun parseDirection(dirStr: String) =
    when (dirStr) {
        "U" -> Direction.NORTH
        "R" -> Direction.EAST
        "D" -> Direction.SOUTH
        "L" -> Direction.WEST
        else -> error("Unknown direction $dirStr")
    }

fun enumerateBoundary(instructions: List<Instruction>): List<Point> {
    var point = Point(0L, 0L)
    val points = mutableListOf<Point>()

    for (instruction in instructions) {
        points.add(point)
        for (i in 1..instruction.length) {
            point = point + instruction.direction.vector
            points.add(point)
        }
    }

    return points.toList()
}

fun makeGrid(points: Set<Point>, max: Point): Grid<Char> {
    // Expand the grid by 1 on each side.
    // NOTE: +1 for the fact that points go for 0.. width -
    // 1/0.. height - 1 and +1 for the extra space on the top and right. adjustPoints already gave
    // us space on the bottom and left.
    val width = (max.x + 2).toInt()
    val height = (max.y + 2).toInt()

    val grid = Grid(width, height, '.')

    for (point in points) {
        grid[point] = '#'
    }

    return grid
}

fun adjustPoints(points: Set<Point>, min: Point) = points.map { it - min + Point(1, 1) }

fun computeArea(grid: Grid<Char>, boundary: Set<Point>): Int {
    val seen = mutableSetOf<Point>()
    val queue = ArrayDeque<Point>()

    // Since we expanded the grid by 1 on each side we know the 0,0 is outside of the boundary.
    queue.add(Point(0, 0))

    while (!queue.isEmpty()) {
        val point = queue.removeFirst()
        if (!seen.add(point)) {
            continue
        }

        queue.addAll(
            Direction.cardinal
                .map { point + it.vector }
                .filter { grid.isValid(it) && !boundary.contains(it) }
        )
    }

    // Seen is all of the points out side of the boundary.
    return grid.width * grid.height - seen.size
}

fun main() {
    val instructions = mutableListOf<Instruction>()

    for (line in Scanners) {
        val direction = parseDirection(line.next())
        val length = line.next().toInt()
        val colorLine = line.next()
        val color = colorLine.substring(2, colorLine.length - 1)

        instructions.add(Instruction(direction, length, color))
    }

    var boundary = enumerateBoundary(instructions).toSet()
    val (min, max) = findMinMaxPoint(boundary)!!

    boundary = adjustPoints(boundary, min).toSet()
    val (adjMin, adjMax) = findMinMaxPoint(boundary)!!

    val grid = makeGrid(boundary, adjMax)

    val score = computeArea(grid, boundary)

    println("Answer = $score")
}
