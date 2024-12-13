import java.util.Scanner
import net.panayotov.util.Point

const val ANSWER_OFFSET = 10000000000000L
val ANSWER_OFFSET_VECTOR = Point(ANSWER_OFFSET, ANSWER_OFFSET)

fun Point.computeTokens() = 3L * x + y

data class Matrix(val a: Long, val b: Long, val c: Long, val d: Long, val mod: Long) {
    constructor(a: Long, b: Long, c: Long, d: Long) : this(a, b, c, d, 1L) {}

    fun transform(vector: Point): Point? {
        val newX = vector.x * a + vector.y * b
        val newY = vector.x * c + vector.y * d
        return if (newX % mod == 0L && newY % mod == 0L) {
            Point(newX / mod, newY / mod)
        } else {
            null
        }
    }

    private fun invDet() = a * d - b * c

    fun invert() = Matrix(d, -b, -c, a, invDet())
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
    var x = scanner.next().split("=")[1].split(",")[0].toLong()
    var y = scanner.next().split("=")[1].toLong()
    return Point(x, y)
}

fun nextScanner(): Scanner {
    var line = readLine() ?: error("bad input")
    return Scanner(line)
}

fun main(args: Array<String>) {
    var done = false
    var score = 0L
    while (!done) {
        val pointA = readPoint(nextScanner())
        val pointB = readPoint(nextScanner())

        val answer =
            readAnswer(nextScanner()) +
                if (args.size > 0 && args[0] == "p2") {
                    ANSWER_OFFSET_VECTOR
                } else {
                    Point(0L, 0L)
                }

        val matrix = Matrix(pointA.x, pointB.x, pointA.y, pointB.y)
        val inverse = matrix.invert()

        var inverted = inverse.transform(answer)

        if (inverted != null) {
            score += inverted.computeTokens()
        }

        if (readLine() == null) {
            break
        }
    }

    println("Answer = $score")
}
