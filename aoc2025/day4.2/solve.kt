import net.panayotov.util.Direction
import net.panayotov.util.Grid

fun main() {
    val grid = Grid.read('.')

    var done = false
    var score = 0

    while (!done) {
        done = true

        grid.forPoints { pt, value ->
            if (value != '@') {
                return@forPoints
            }
            var rolls = 0
            for (dir in Direction.all) {
                val newPt = pt + dir.vector
                if (grid.isValid(newPt) && grid[newPt] == '@') {
                    rolls++
                }
            }
            if (rolls < 4) {
                score++
                done = false
                grid[pt] = 'x'
            }
        }
    }

    println("Answer = $score")
}
