import net.panayotov.util.Lines

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first + other.first, second + other.second)

val PIPE_VECTORS = mapOf(
    '|' to listOf(Pair(1, 0), Pair(-1, 0)),
    '-' to listOf(Pair(0, 1), Pair(0, -1)),
    'L' to listOf(Pair(-1, 0), Pair(0, 1)),
    'J' to listOf(Pair(-1, 0), Pair(0, -1)),
    '7' to listOf(Pair(1, 0), Pair(0, -1)),
    'F' to listOf(Pair(1, 0), Pair(0, 1)),
    'S' to listOf(Pair(0, -1), Pair(0, 1), Pair(-1, 0), Pair(1, 0)),
)

fun readGrid(): List<String> {
    val grid = mutableListOf<String>()
    for (line in Lines) {
        grid.add(line)
    }

    return grid.toList()
}

fun findStart(grid: List<String>): Pair<Int, Int> {
    for (i in 0..grid.size - 1) {
        for (j in 0..grid[i].length - 1) {
            if (grid[i][j] == 'S') {
                return Pair(i, j)
            }
        }
    }

    error("Start not found.")
}

fun findAdjacents(location: Pair<Int, Int>, vectors: List<Pair<Int, Int>>): List<Pair<Int, Int>> =
    vectors.map {
        it + location
    }

fun findVectors(grid: List<String>, location: Pair<Int, Int>): List<Pair<Int, Int>> =
    getValue(grid, location)?.let {
        PIPE_VECTORS[it]
    } ?: listOf<Pair<Int, Int>>()

fun getValue(grid: List<String>, location: Pair<Int, Int>): Char? =
    if (location.first >= 0 && location.first < grid.size &&
        location.second >= 0 && location.second < grid[location.first].length
    ) {
        grid[location.first][location.second]
    } else {
        null
    }

fun traverse(start: Pair<Int, Int>, grid: List<String>): Int {
    var location = start
    var count = 0
    var path = mutableListOf<Pair<Int, Int>>(start)

    while (location != start || count == 0) {
        val nextLocation = findAdjacents(location, findVectors(grid, location)).filter {
            findAdjacents(it, findVectors(grid, it)).contains(location)
        }.filter {
            path.size < 2 || path[path.size - 2] != it
        }

        if (nextLocation.size == 0) {
            error("Wrong size. $nextLocation")
        }

        location = nextLocation[0]
        path.add(location)
        count++
    }

    return count
}

fun main() {
    val grid = readGrid()

    val start = findStart(grid)
    var score = traverse(start, grid) / 2

    println("Answer = $score")
}
