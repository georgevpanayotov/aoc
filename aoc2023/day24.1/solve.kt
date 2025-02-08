import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.times

data class HailStone(val point: Point, val vector: Point) {
    fun move(factor: Double): Pair<Double, Double> =
        Pair(
            point.x.toDouble() + factor * vector.x.toDouble(),
            point.y.toDouble() + factor * vector.y.toDouble(),
        )
}

// Finds the intersection of 2 hail stones.
fun intersection(left: HailStone, right: HailStone): Pair<Double, Double>? {
    // This function is kinda awkward because I'm not equipped for doing stuff with doubles and I
    // don't have a matrix class. This is the first AoC problem I've done that actually seems to
    // require floating point. My rational class kept overflowing so I dropped it.

    // Trivial case.
    if (left.point == right.point) {
        return Pair(left.point.x.toDouble(), left.point.y.toDouble())
    }

    //    l + a * lv = r + b * rv
    //    a * lv - b * rv = r - l
    //    | lv  -rv| |a| = r - l
    //               |b|

    // Gotta invert a matrix of the form:
    // | lv.x -rv.x |
    // | lv.y -rv.y |
    // and invert the vector rp - lp
    // The inverted matrix is
    //                 1              | -rv.y rv.x |
    // (- lv.x * rv.y +  rv.x * lv.y) | -lv.y lv.x |

    // Using long for determinant so that the 0 check is exact (not approximate).
    val determinant = left.vector.y * right.vector.x - left.vector.x * right.vector.y
    if (determinant == 0L) {
        // Parallel because the matrix is not invertible.
        return null
    }

    val vectorToInvert = right.point - left.point

    val matrixL = Point(-right.vector.y, -left.vector.y)
    val matrixR = Point(right.vector.x, left.vector.x)

    val inverted = matrixL * vectorToInvert.x + matrixR * vectorToInvert.y

    val lFactor = inverted.x.toDouble() / determinant.toDouble()
    val rFactor = inverted.y.toDouble() / determinant.toDouble()

    val (leftX, leftY) = left.move(lFactor)

    if (inverted.x.toDouble() / determinant < 0 || inverted.y.toDouble() / determinant < 0) {
        // Met in the past
        return null
    }

    return Pair(leftX, leftY)
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        error("Bad args")
    }

    val min = args[0].toDouble()
    val max = args[1].toDouble()

    val stones = mutableListOf<HailStone>()
    for (line in Scanners) {
        line.useDelimiter(" *(,|@) *")
        val x = line.nextLong()
        val y = line.nextLong()
        val z = line.nextLong()
        val vx = line.nextLong()
        val vy = line.nextLong()
        val vz = line.nextLong()
        stones.add(HailStone(Point(x, y), Point(vx, vy)))
    }

    var score = 0
    for (i in 0..<stones.size - 1) {
        for (j in i + 1..<stones.size) {
            val point = intersection(stones[i], stones[j])
            if (point == null) {
                continue
            }

            val (x, y) = point

            if (x <= max && x >= min && y >= min && y <= max) {
                score++
            }
        }
    }

    println("Answer = $score")
}
