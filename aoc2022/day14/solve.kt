import java.util.Scanner

const val START_X = 500

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

fun createGrid(rockLines: List<List<Pair<Int, Int>>>): Pair<Int, MutableList<MutableList<Char>>> {
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

    val grid = arrayListOf<MutableList<Char>>()
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
                grid[rock.second][rock.first - minX] = '#'
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

fun moveDown(grid: List<List<Char>>, pos: Pair<Int, Int>): Pair<Int, Int>? {
    var (x, y) = pos

    if (y >= grid.size - 1) {
        return null
    }

    if (x < 0 || x >= grid[y].size) {
        return null
    }

    if (grid[y + 1][x] == '.') {
        return Pair(x, y + 1)
    } else if (x == 0 || grid[y + 1][x - 1] == '.') {
        return Pair(x - 1, y + 1)
    } else if (x == grid[y].size - 1 || grid[y + 1][x + 1] == '.') {
        return Pair(x + 1, y + 1)
    } else {
        return Pair(x, y)
    }


}

fun part1(rockLines: List<List<Pair<Int, Int>>>) {
    val (minX, grid) = createGrid(rockLines)
    printGrid(grid)

    val startX = START_X - minX

    var abyss = false
    var score = 0
    while (!abyss) {
        var moved = true
        var point = Pair(startX, 0)

        while(moved) {
            val next = moveDown(grid, point)
            if (next == null) {
                abyss = true
                break
            } else if (next == point) {
                moved = false
                score++

                val (x, y) = point
                grid[y][x] = 'o'

            } else {
                point = next
            }
        }
    }
    printGrid(grid)
    print("$score\n")

}

fun main() {
    val rockLines = parseRockLines()
    part1(rockLines)

}
