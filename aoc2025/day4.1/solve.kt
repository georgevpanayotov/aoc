import net.panayotov.util.Direction
import net.panayotov.util.Grid

fun main() {
    var score = 0
    val grid = Grid.read('.')

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
        }
    }

    println("Answer = $score")
}
