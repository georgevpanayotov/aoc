import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point

fun readPins(grid: Grid<Char>, startRow: Long, direction: Direction): List<Int> {
    for (x in 0L..<grid.width) {
        if (grid[x, startRow] != '#') {
            return listOf()
        }
    }

    val heights = mutableListOf<Int>()

    for (x in 0L..<grid.width) {
        var height = 0
        var point = Point(x, startRow)
        while (grid.isValid(point)) {
            if (grid[point] == '#') {
                height++
            } else {
                break
            }
            point = point + direction.vector
        }

        // Don't count the first line.
        heights.add(height - 1)
    }

    return heights.toList()
}

fun readKey(grid: Grid<Char>) = readPins(grid, 0L, Direction.NORTH)

fun readLock(grid: Grid<Char>) = readPins(grid, grid.height - 1L, Direction.SOUTH)

fun valid(lock: List<Int>, key: List<Int>): Boolean {
    if (lock.size != key.size) {
        error("Mismatched sizes $key and $lock")
    }

    for (i in 0..<key.size) {
        if (key[i] + lock[i] > 5) {
            return false
        }
    }

    return true
}

fun main() {
    var readingDone = false
    val keys = mutableListOf<List<Int>>()
    val locks = mutableListOf<List<Int>>()

    while (!readingDone) {
        val grid = Grid.read('.', "")
        if (grid.width != 0 && grid.height != 0) {
            val key = readKey(grid)
            val lock = readLock(grid)
            if (!key.isEmpty()) {
                keys.add(key)
            } else if (!lock.isEmpty()) {
                locks.add(lock)
            }
        } else {
            readingDone = true
        }
    }

    var score = 0
    for (i in 0..<locks.size) {
        for (j in 0..<keys.size) {
            if (valid(locks[i], keys[j])) {
                score++
            }
        }
    }

    println("Answer = $score")
}
