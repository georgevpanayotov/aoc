import net.panayotov.util.Lines

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

// Represents a number on a particular row ranging from min to max - 1.
// i.e. grid[row].substring(min, max) is the number.
data class NumberRange(val row: Int, val min: Int, val max: Int)

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

fun findAdjacentNumbers(i: Int, j: Int, grid: List<String>): List<Pair<Int, Int>> {
    val adjacentNumbers = mutableListOf<Pair<Int, Int>>()
    val point = Pair(i, j)

    for (vector in vectors) {
        val (i_, j_) = point + vector
        val value = getValue(i_, j_, grid)

        if (value == null) {
            continue
        }

        if (value <= '9' && value >= '0') {
            adjacentNumbers.add(Pair(i_, j_))
        }
    }

    return adjacentNumbers.toList()
}

fun getFullNumber(i: Int, j: Int, grid: List<String>): NumberRange {
    val line = grid[i]

    var minJ = j
    var maxJ = j

    while (minJ >= 0 && line[minJ] >= '0' && line[minJ] <= '9') {
        minJ--
    }

    while (maxJ < line.length && line[maxJ] >= '0' && line[maxJ] <= '9') {
        maxJ++
    }

    return NumberRange(i, minJ + 1, maxJ)
}

fun main() {
    val grid = readGrid()

    var score = 0
    for (i in 0..grid.size - 1) {
        for (j in 0..grid[i].length - 1) {
            val value = grid[i][j]
            if (value == '*') {
                val numberSet = mutableSetOf<NumberRange>()

                val adjacentNumbers = findAdjacentNumbers(i, j, grid)

                for (number in adjacentNumbers) {
                    val fullNumber = getFullNumber(number.first, number.second, grid)
                    numberSet.add(fullNumber)
                }

                if (numberSet.size == 2) {
                    var ratio = 1
                    for (numberRange in numberSet) {
                        ratio = ratio * grid[numberRange.row].substring(numberRange.min, numberRange.max).toInt()
                    }
                    score += ratio
                }
            }
        }
    }

    println("Answer = $score")
}
