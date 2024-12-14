import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.times

const val WIDTH = 101L
const val HEIGHT = 103L
const val SECONDS = 100L

data class Robot(val point: Point, val vector: Point)

fun superMod(x: Long, mod: Long): Long = (x % mod + mod) % mod

fun moveRobot(robot: Robot, width: Long, height: Long): Robot {
    var newPoint = robot.point + SECONDS * robot.vector
    newPoint = Point(superMod(newPoint.x, width), superMod(newPoint.y, height))

    return Robot(newPoint, robot.vector)
}

fun quadrant(robot: Robot, width: Long, height: Long): Int {
    val left = robot.point.x >= 0 && robot.point.x < width / 2
    val right = robot.point.x > width / 2 && robot.point.x < width

    val top = robot.point.y >= 0 && robot.point.y < height / 2
    val bottom = robot.point.y > height / 2 && robot.point.y < height

    return if (top && left) {
        1
    } else if (top && right) {
        2
    } else if (bottom && left) {
        3
    } else if (bottom && right) {
        4
    } else {
        0
    }
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

    var i = 0
    while (i < robots.size) {
        val robot = moveRobot(robots[i], width, height)
        robots[i] = robot

        i++
    }

    val quadrants = mutableMapOf<Int, Int>()

    for (robot in robots) {
        val quad = quadrant(robot, width, height)
        if (quad != 0) {
            quadrants[quad] = (quadrants[quad] ?: 0) + 1
        }
    }

    val score = quadrants.values.reduce { acc, value -> acc * value }

    println("Answer = $score")
}
