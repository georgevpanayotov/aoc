import net.panayotov.util.Lines

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first + other.first, second + other.second)

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first - other.first, second - other.second)

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

fun traverse(start: Pair<Int, Int>, grid: List<String>): List<Pair<Int, Int>> {
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

    return path.toList()
}

sealed interface TraversalState {
    fun nextState(value: Char): TraversalState
}

object Outside : TraversalState {
    override fun nextState(value: Char): TraversalState =
        when (value) {
            '|' -> Inside
            'F' -> InPipe(1, this)
            'L' -> InPipe(-1, this)
            else -> this
        }
}

object Inside : TraversalState {
    override fun nextState(value: Char): TraversalState =
        when (value) {
            '|' -> Outside
            'F' -> InPipe(1, this)
            'L' -> InPipe(-1, this)
            else -> this
        }
}

data class InPipe(val direction: Int, val lastState: TraversalState) : TraversalState {
    override fun nextState(value: Char): TraversalState =
        when (value) {
            '7' -> exitPipe(1)
            'J' -> exitPipe(-1)
            else -> this
        }

    private fun exitPipe(newDirection: Int): TraversalState =
        when (lastState) {
            Inside -> if (direction != newDirection) {
                Outside
            } else {
                Inside
            }
            Outside -> if (direction != newDirection) {
                Inside
            } else {
                Outside
            }
            else -> error("Not valid lastState $lastState")
        }
}

fun replaceStartPipe(grid: List<String>, start: Pair<Int, Int>, path: List<Pair<Int, Int>>): List<String> {
    // These two are adjacent to the start.
    val adjacents = listOf(path[1], path[path.size - 2])

    // What's their offset from the start.
    val vectors = adjacents.map {
        it - start
    }

    // Find the correct pipe for the start based on one that matches the vectors.
    for (pipe in PIPE_VECTORS.keys) {
        val matches = vectors.fold(true) { acc, value ->
            acc && PIPE_VECTORS[pipe]?.contains(value) ?: false
        }

        if (matches) {
            val mutableGrid = grid.toMutableList()
            val newLine = StringBuilder(grid[start.first])

            newLine.setCharAt(start.second, pipe)
            mutableGrid[start.first] = newLine.toString()

            return mutableGrid.toList()
        }
    }

    error("No match for $vectors")
}

fun main() {
    val grid = readGrid()

    val start = findStart(grid)
    var path = traverse(start, grid)

    val updatedGrid = replaceStartPipe(grid, start, path)

    var score = 0
    for (i in 0..updatedGrid.size - 1) {
        var state: TraversalState = Outside

        for (j in 0..updatedGrid[i].length - 1) {
            if (path.contains(Pair(i, j))) {
                state = state.nextState(updatedGrid[i][j])
            } else {
                if (state == Inside) {
                    score++
                }
            }
        }
    }

    println("Answer = $score")
}
