import net.panayotov.util.Point
import net.panayotov.util.Scanners

fun area(lhs: Point, rhs: Point): Long = Math.abs(lhs.x - rhs.x + 1) * Math.abs(lhs.y - rhs.y + 1)

fun main() {
    val points = mutableListOf<Point>()

    for (line in Scanners) {
        line.useDelimiter(",")
        val x = line.nextLong()
        val y = line.nextLong()

        points.add(Point(x, y))
    }

    var maxArea: Long? = null

    for (i in 0..<points.size) {
        for (j in i + 1..<points.size) {
            val area = area(points[i], points[j])

            maxArea =
                maxArea?.let {
                    if (it > area) {
                        it
                    } else {
                        area
                    }
                } ?: area
        }
    }

    println("Answer = $maxArea")
}
