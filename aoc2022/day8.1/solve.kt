val NORTH = Pair(1, 0)
val SOUTH = Pair(-1, 0)
val EAST = Pair(0, 1)
val WEST = Pair(0, -1)

fun findTaller(forest: Array<IntArray>, point: Pair<Int, Int>, dir: Pair<Int, Int>): Boolean {
    val (deltaRow, deltaCol) = dir
    var (row, column) = point
    val height = forest[row][column]

    row += deltaRow
    column += deltaCol

    while (row >= 0 && row < forest.size && column >= 0 && column < forest[0].size) {
        if (forest[row][column] >= height) {
            return true
        }

        row += deltaRow
        column += deltaCol
    }

    return false
}

fun main() {

    var line = readLine()
    var row = 0
    if (line == null) {
        error("Ded")
    }
    val size = line.length
    val forest = Array(size) { IntArray(size) }

    while (line != null) {

        for (column in 0..line.length - 1) {
            forest[row][column] = line[column].digitToInt()
        }

        line = readLine()
        row++
    }

    var score = 0

    for (irow in 1..size - 2) {
        for (column in 1..size - 2) {
            if (findTaller(forest, Pair(irow, column), NORTH) &&
                findTaller(forest, Pair(irow, column), SOUTH) &&
                findTaller(forest, Pair(irow, column), EAST) &&
                findTaller(forest, Pair(irow, column), WEST)
            ) {
                score++
            }
        }
    }

    print(size * size - score)
}
