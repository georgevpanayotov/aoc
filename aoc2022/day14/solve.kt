import java.util.Scanner

object Lines : Iterator<Scanner> {
    private var line: Scanner? = null
    override fun next(): Scanner {
        ensureLine()
        val ret = line ?: error("No next line")
        line = null

        return ret
    }

    override fun hasNext(): Boolean {
        ensureLine()
        return line != null
    }

    private fun ensureLine() {
        if (line == null) {
            line = readLine()?.let(::Scanner)
        }
    }
}

fun parseRocks(line: Scanner): List<Pair<Int, Int>> {
    val rocks = arrayListOf<Pair<Int, Int>>()

    while (line.hasNext()) {
        val pointStr = line.next().split(",")
        rocks.add(Pair(pointStr[0].toInt(), pointStr[1].toInt()))

        if (line.hasNext()) {
            // "->"
            line.next()
        }
    }

    return rocks
}

fun parseRockLines(): List<List<Pair<Int, Int>>> {
    val rockLines = arrayListOf<List<Pair<Int, Int>>>()

    for (line in Lines) {
        rockLines.add(parseRocks(line))
    }

    return rockLines
}

operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first - other.first, second - other.second)

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first + other.first, second + other.second)

fun Pair<Int, Int>.unit(): Pair<Int, Int> =
    Pair(first.coerceIn(-1, 1), second.coerceIn(-1, 1))

fun createGrid(rockLines: List<List<Pair<Int, Int>>>): Pair<Int, List<List<Char>>> {
    var minX: Int? = null
    var minY: Int? = null

    var maxX: Int? = null
    var maxY: Int? = null

    for (rockLine in rockLines) {
        for (rock in rockLine) {
            val (x, y) = rock

            if (minX == null || x < minX) {
                minX = x
            }

            if (minY == null || y < minY) {
                minY = y
            }

            if (maxX == null || x > maxX) {
                maxX = x
            }

            if (maxY == null || y > maxY) {
                maxY = y
            }
        }
    }

    val xRange = maxX!! - minX!! + 1
    val yRange = maxY!! + 1

    val grid = arrayListOf<ArrayList<Char>>()
    for (y in 1..yRange) {
        val gridLine = arrayListOf<Char>()
        for (x in 1..xRange) {
            gridLine.add('.')
        }

        grid.add(gridLine)
    }

    for (rockLine in rockLines) {
        var lastRock: Pair<Int, Int>? = null

        for (rock in rockLine) {
            if (lastRock != null) {
                val diff = (rock - lastRock).unit()
                var iRock = lastRock

                while (iRock != rock) {
                    grid[iRock.second][iRock.first - minX] = '#'
                    iRock += diff
                }
            }
            lastRock = rock
        }
    }

    return Pair(minX, grid)
}

fun printGrid(grid: List<List<Char>>) {
    for (y in 0..grid.size - 1) {
        for (x in 0..grid[y].size - 1) {
            print("${grid[y][x]}")
        }

        print("\n")
    }
}

fun part1(rockLines: List<List<Pair<Int, Int>>>) {
    val (minX, grid) = createGrid(rockLines)
    print("$minX\n")
    printGrid(grid)
}

fun main() {
    val rockLines = parseRockLines()
    part1(rockLines)

}
