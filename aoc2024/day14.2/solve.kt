import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.times

const val WIDTH = 101L
const val HEIGHT = 103L

data class Robot(val point: Point, val vector: Point)

fun superMod(x: Long, mod: Long): Long = (x % mod + mod) % mod

fun moveRobot(robot: Robot, width: Long, height: Long): Robot {
    var newPoint = robot.point + robot.vector
    newPoint = Point(superMod(newPoint.x, width), superMod(newPoint.y, height))

    return Robot(newPoint, robot.vector)
}

fun toGrid(robots: List<Robot>, width: Long, height: Long, hideCenters: Boolean): Grid<Char> {
    val grid = Grid<Int>(width.toInt(), height.toInt(), 0)

    for (robot in robots) {
        grid[robot.point] = (grid[robot.point] ?: 0) + 1
    }

    val chGrid = grid.map { if (it == 0) '.' else it.toString()[0] }
    if (hideCenters) {
        for (x in 0L..<grid.width) {
            chGrid[x, height / 2] = ' '
        }
        for (y in 0L..<grid.height) {
            chGrid[width / 2, y] = ' '
        }
    }
    return chGrid
}

fun checkTree(robots: List<Robot>, width: Long, height: Long): Boolean {
    var count = 0
    val grid = toGrid(robots, width, height, false)

    for (y in 0L..<grid.height) {
        for (x in 0L..<grid.width) {
            val spot = grid[x, y]
            if (spot == '1') {
                count++
                if (count > 8) {
                    return true
                }
            } else {
                count = 0
            }
        }
    }

    return false
}

fun main(args: Array<String>) {
    var width: Long = WIDTH
    var height: Long = HEIGHT

    if (args.size > 1) {
        width = args[0].toLong()
        height = args[1].toLong()
    }

    val robots = mutableListOf<Robot>()

    for (line in Scanners) {
        line.useDelimiter("( |=|,)")
        line.next() // p
        val x = line.nextLong()
        val y = line.nextLong()
        val point = Point(x, y)

        line.next() // v
        val vx = line.nextLong()
        val vy = line.nextLong()
        val vector = Point(vx, vy)

        robots.add(Robot(point, vector))
    }

    // Some big number ... pulled from thin air
    for (j in 1..1338100) {
        var i = 0
        while (i < robots.size) {
            val robot = moveRobot(robots[i], width, height)
            robots[i] = robot

            i++
        }

        if (checkTree(robots, width, height)) {
            println(toGrid(robots, width, height, true))
            println("Answer = $j")
            break
        }
    }
}
