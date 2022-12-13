data class Elevation(val elevation: Char)

fun ArrayList<ArrayList<Elevation>>.validPosition(pos: Pair<Int, Int>): (Pair<Int, Int>) -> Boolean = { neighbor ->
    val (row, col) = pos
    val (nRow, nCol) = neighbor

    val inGrid = nRow >= 0 && nRow < this.size && nCol >= 0 && nCol < this[row].size
    inGrid && this[nRow][nCol].elevation - this[row][col].elevation <= 1
}

fun ArrayList<ArrayList<Elevation>>.getNeighbors(pos: Pair<Int, Int>): List<Pair<Int, Int>> {
    val (row, col) = pos
    val neighborCandidates = arrayListOf<Pair<Int, Int>>(
        Pair(row + 1, col), Pair(row - 1, col),
        Pair(row, col + 1), Pair(row, col - 1)
    )

    return neighborCandidates.filter(this.validPosition(pos))
}

fun edsger(from: Pair<Int, Int>, to: Pair<Int, Int>, grid: ArrayList<ArrayList<Elevation>>): List<Pair<Int, Int>> {
    val unvisited = mutableSetOf<Pair<Int, Int>>()
    for (row in 0..grid.size - 1) {
        for (col in 0..grid[row].size - 1) {
            unvisited.add(Pair(row, col))
        }
    }

    val dist = mutableMapOf<Pair<Int, Int>, Int>()
    dist[from] = 0

    val prev = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

    while (!unvisited.isEmpty()) {
        var min: Pair<Int, Int>? = null
        for (pos in unvisited) {
            val dp = dist[pos]
            val dm = dist[min]
            if (dp != null && ((min == null) || (dm != null && dp < dm))) {
                min = pos
            }
        }
        if (min == null) {
            break
        }

        unvisited.remove(min)
        val dm = dist[min]
        if (dm == null) {
            error("There should be a dm.")
        }

        for (neighbor in grid.getNeighbors(min)) {
            if (!unvisited.contains(neighbor)) {
                continue
            }

            val alt = dm + 1
            val dn = dist[neighbor]
            if (dn == null || alt < dn) {
                prev[neighbor] = min
                dist[neighbor] = dm + 1
            }
        }
    }

    val path = mutableListOf<Pair<Int, Int>>()
    var curr: Pair<Int, Int>? = to

    while (curr != null) {
        path.add(curr)
        curr = prev[curr]
    }

    return path.reversed()
}

fun main() {
    var line = readLine()

    val grid = arrayListOf<ArrayList<Elevation>>()

    var starting: Pair<Int, Int>? = null
    var target: Pair<Int, Int>? = null

    var row = 0
    while (line != null) {

        val gridLine = arrayListOf<Elevation>()
        var col = 0
        for (point in line) {
            val elevation = if (point == 'S') {
                starting = Pair(row, col)
                Elevation('a')
            } else if (point == 'E') {
                target = Pair(row, col)
                Elevation('z')
            } else {
                Elevation(point)
            }
            gridLine.add(elevation)

            col++
        }
        grid.add(gridLine)

        line = readLine()
        row++
    }

    var path = edsger(starting!!, target!!, grid)

    print("$path length: ${path.size}\n")
}
