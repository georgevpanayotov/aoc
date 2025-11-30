import net.panayotov.util.Direction
import net.panayotov.util.Grid

fun main() {
    var score = 0
    val grid = Grid.read('.')

    grid.forPoints { position ->
        for (direction in Direction.all) {
            val xmasBuilder = StringBuilder()
            var newPosition = position
            for (i in 0..3) {
                if (grid.isValid(newPosition)) {
                    xmasBuilder.append(grid[newPosition])
                }

                newPosition = newPosition + direction.vector
            }

            if (xmasBuilder.toString() == "XMAS") {
                score++
            }
        }
    }

    println("Answer = $score")
}
