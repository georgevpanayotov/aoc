import net.panayotov.util.Lines

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

val vectors = listOf(
    Pair(-1, -1),
    Pair(-1, 0),
    Pair(-1, 1),
    Pair(0, -1),
    Pair(0, 1),
    Pair(1, -1),
    Pair(1, 0),
    Pair(1, 1)
)

fun readGrid(): List<String> {
    val grid = mutableListOf<String>()

    for (line in Lines) {
        grid.add(line)
    }

    return grid.toList()
}

fun getValue(i: Int, j: Int, grid: List<String>): Char? {
    if (i < 0 || i >= grid.size) {
        return null
    }

    val line = grid[i]
    if (j < 0 || j >= line.length) {
        return null
    }

    return line[j]
}

fun checkAdjacent(i: Int, j: Int, grid: List<String>): Boolean {
    val point = Pair(i, j)
    for (vector in vectors) {
        val (i_, j_) = point + vector
        val value = getValue(i_, j_, grid)

        if (value == null) {
            continue
        }

        if (value != '.' && (value > '9' || value < '0')) {
            return true
        }
    }

    return false
}

fun main() {
    val grid = readGrid()
    var numStart: Int? = null
    var isAdjacent = false

    var score = 0
    for (i in 0..grid.size - 1) {
        numStart = null
        isAdjacent = false

        for (j in 0..grid[i].length - 1) {
            val value = grid[i][j]
            if (value < '0' || value > '9') {
                if (isAdjacent && numStart != null) {
                    score += grid[i].substring(numStart, j).toInt()
                }
                numStart = null
                isAdjacent = false
                continue
            }

            isAdjacent = isAdjacent || checkAdjacent(i, j, grid)
            if (numStart == null) {
                numStart = j
            }
        }
        if (isAdjacent && numStart != null) {
            score += grid[i].substring(numStart, grid[i].length).toInt()
        }
    }

    println("Answer = $score")
}
