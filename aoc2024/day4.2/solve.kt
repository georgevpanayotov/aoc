import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

fun Grid<Char>.findMas(position: Point, vector: Point): Boolean {
    val masBuilder = StringBuilder()
    var newPosition = position
    for (i in 0..2) {
        if (isValid(newPosition)) {
            masBuilder.append(this[newPosition])
        }

        newPosition = newPosition + vector
    }
    return masBuilder.toString() == "MAS"
}

fun main() {
    var score = 0
    val grid = Grid.read('.')
    val ignore = mutableSetOf<Set<Point>>()

    for (x in 0L..<grid.width) {
        for (y in 0L..<grid.height) {
            val position = Point(x, y)
            for (direction in Direction.ordinal) {
                if (grid.findMas(position, direction.vector)) {
                    val xStart = position + Point(direction.vector.x * 2L, 0L)
                    val xVector = Point(-direction.vector.x, direction.vector.y)

                    val yStart = position + Point(0L, direction.vector.y * 2L)
                    val yVector = Point(direction.vector.x, -direction.vector.y)

                    if (grid.findMas(xStart, xVector) || grid.findMas(yStart, yVector)) {
                        // If we find this same X from another of its sides, then ignore it.
                        val ignoreSet =
                            setOf(
                                position,
                                position + direction.vector + direction.vector,
                                xStart,
                                xStart + xVector + xVector,
                            )

                        if (ignore.add(ignoreSet)) {
                            score++
                        }
                    }
                }
            }
        }
    }

    println("Answer = $score")
}
