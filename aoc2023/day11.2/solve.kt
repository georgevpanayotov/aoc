import net.panayotov.util.Lines

fun readGrid(): List<String> {
    val grid = mutableListOf<StringBuilder>()
    for (line in Lines) {
        // In part 2 we don't append we just replace blanks lines with 'M' to represent "Many"
        // values.
        if (line.toString().fold(true) { acc, value -> acc && value == '.' }) {
            val blankLine = StringBuilder()
            for (i in 1..line.length) {
                blankLine.append('M')
            }
            grid.add(StringBuilder(blankLine))
        } else {
            grid.add(StringBuilder(line))
        }
    }

    for (j in 0..grid[0].length - 1) {
        // Here, we also don't append and just replace. This allows us to not have to track a list
        // of blanks and worry about reversing.
        var blank = true
        for (i in 0..grid.size - 1) {
            if (grid[i][j] != '.' && grid[i][j] != 'M') {
                blank = false
                break
            }
        }

        if (blank) {
            for (i in 0..grid.size - 1) {
                grid[i].setCharAt(j, 'M')
            }
        }
    }

    return grid.map(StringBuilder::toString).toList()
}

fun findGalaxies(grid: List<String>): List<Pair<Int, Int>> {
    val galaxies = mutableListOf<Pair<Int, Int>>()

    for (i in 0..grid.size - 1) {
        for (j in 0..grid[i].length - 1) {
            if (grid[i][j] == '#') {
                galaxies.add(Pair(i, j))
            }
        }
    }

    return galaxies.toList()
}

fun abs(value: Int): Int = if (value > 0) value else -value

fun min(a: Int, b: Int): Int = if (a > b) b else a
fun max(a: Int, b: Int): Int = if (a > b) a else b

fun getDistance(grid: List<String>, size: Int, left: Pair<Int, Int>, right: Pair<Int, Int>): Long {
    var distance: Long = (abs(left.first - right.first) + abs(left.second - right.second)).toLong()

    // Since the distances are just following the "Manhattan" distance we can just add the number of
    // 'M' rows and 'M' columns times (size - 1). Minus 1 because there is already 1 column/row
    // there.
    for (i in min(left.first, right.first)..max(left.first, right.first)) {
        if (grid[i][0] == 'M') {
            distance += size - 1
        }
    }

    for (j in min(left.second, right.second)..max(left.second, right.second)) {
        if (grid[0][j] == 'M') {
            distance += size - 1
        }
    }

    return distance
}

fun main(args: Array<String>) {
    val grid = readGrid()
    val galaxies = findGalaxies(grid)

    var score = 0L

    for (i in 0..galaxies.size - 1) {
        for (j in i + 1..galaxies.size - 1) {
            // Take size as an arg so I don't have to keep rebuilding to run sample and the real input.
            val distance = getDistance(grid, args[0].toInt(), galaxies[i], galaxies[j])
            score += distance
        }
    }

    println("Answer = $score")
}
