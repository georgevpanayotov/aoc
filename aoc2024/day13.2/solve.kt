import java.util.Scanner
import net.panayotov.util.Point

const val EPSILON = .001
const val ANSWER_OFFSET = 10000000000000L

fun Point.toVector() = Vector(x.toDouble(), y.toDouble())

fun Point.computeTokens() = 3L * x + y

data class Vector(val x: Double, val y: Double) {
    fun toPoint() = Point((x + .0001).toLong(), (y + .0001).toLong())

    fun valid(): Boolean {
        val point = toPoint()
        // Both are integers?
        return kotlin.math.abs(point.x - x) < EPSILON && kotlin.math.abs(point.y - y) < EPSILON
    }
}

data class Matrix(val a: Double, val b: Double, val c: Double, val d: Double) {
    fun transform(vector: Vector): Vector =
        Vector(vector.x * a + vector.y * b, vector.x * c + vector.y * d)

    fun invert(): Matrix? {
        val det = a * d - b * c

        return if (det < EPSILON && det > -EPSILON) null
        else {
            Matrix(1 / det * d, 1 / det * -b, 1 / det * -c, 1 / det * a)
        }
    }
}

fun readPoint(scanner: Scanner): Point {
    scanner.next()
    scanner.next()
    var x = scanner.next().split("+")[1].split(",")[0].toLong()
    var y = scanner.next().split("+")[1].toLong()
    return Point(x, y)
}

fun readAnswer(scanner: Scanner): Point {
    scanner.next()
    var x = scanner.next().split("=")[1].split(",")[0].toLong() + ANSWER_OFFSET
    var y = scanner.next().split("=")[1].toLong() + ANSWER_OFFSET
    return Point(x, y)
}

fun nextScanner(): Scanner {
    var line = readLine() ?: error("bad input")
    return Scanner(line)
}

fun main() {
    var done = false
    var score = 0L
    while (!done) {
        val pointA = readPoint(nextScanner())
        val pointB = readPoint(nextScanner())

        val answer = readAnswer(nextScanner())

        val matrix =
            Matrix(
                pointA.x.toDouble(),
                pointB.x.toDouble(),
                pointA.y.toDouble(),
                pointB.y.toDouble(),
            )
        val inverse = matrix.invert()

        if (inverse != null) {
            val invertedVector = inverse.transform(answer.toVector())
            if (invertedVector.valid()) {
                val inverted = invertedVector.toPoint()
                score += inverted.computeTokens()
            }
        } else {
            println("No inverse")
        }

        if (readLine() == null) {
            break
        }
    }

    println("Answer = $score")
}
