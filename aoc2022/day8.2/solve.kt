val NORTH = Pair(1, 0)
val SOUTH = Pair(-1, 0)
val EAST = Pair(0, 1)
val WEST = Pair(0, -1)

fun findViewingDistance(forest: Array<IntArray>, point: Pair<Int, Int>, dir: Pair<Int, Int>): Int {
    val (deltaRow, deltaCol) = dir
    var (row, column) = point
    val height = forest[row][column]

    row += deltaRow
    column += deltaCol
    var distance = 0

    while (row >= 0 && row < forest.size && column >= 0 && column < forest[0].size) {
        distance++

        if (forest[row][column] >= height) {
            break
        }

        row += deltaRow
        column += deltaCol
    }

    return distance
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
            val currScore = findViewingDistance(forest, Pair(irow, column), NORTH) * findViewingDistance(forest, Pair(irow, column), SOUTH) * findViewingDistance(forest, Pair(irow, column), EAST) * findViewingDistance(forest, Pair(irow, column), WEST)
            if (currScore > score) {
                score = currScore
            }
        }
    }

    print(score)
}
