import kotlin.collections.ArrayDeque

data class PatternEntry(val id: Char, val netTransform: Pair<Long, Long>)

data class RockShape(
    val id: Char,
    val xLength: Long,
    val yLength: Long,
    val points: List<Pair<Long, Long>>
) {
    operator fun plus(offset: Pair<Long, Long>): RockShape {
        return RockShape(
            id,
            xLength, yLength,
            points.map {
                Pair(it.first + offset.first, it.second + offset.second)
            }
        )
    }

    fun bottomLeft(): Pair<Long, Long> =
        points.reduce { acc, next ->
            var (minX, minY) = acc
            val (x, y) = next

            if (x < minX) {
                minX = x
            }

            if (y < minY) {
                minY = y
            }

            return Pair(minX, minY)
        }

    fun transformFrom(other: RockShape): Pair<Long, Long> {
        val lhs = bottomLeft()
        val rhs = other.bottomLeft()

        return Pair(lhs.first - rhs.first, lhs.second - rhs.second)
    }
}

const val ROCK_COUNT = 1000000000000L
// const val ROCK_COUNT = 2022L
val UP = Pair(0L, 1L)
val DOWN = Pair(0L, -1L)
val LEFT = Pair(-1L, 0L)
val RIGHT = Pair(1L, 0L)

val shapes = arrayListOf<RockShape>(
    RockShape('0', 4, 1, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0))),
    RockShape('1', 3, 3, listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(0, 1), Pair(2, 1))),
    RockShape('2', 3, 3, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2))),
    RockShape('3', 1, 4, listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3))),
    RockShape('4', 2, 2, listOf(Pair(0, 0), Pair(0, 1), Pair(1, 0), Pair(1, 1)))
)

class Pile {
    val grid: ArrayDeque<MutableList<Char>> = ArrayDeque<MutableList<Char>>()
    // Level of the lowest row saved.
    var level: Long = 1
    var maxOccupiedLevels = arrayListOf<Long?>(null, null, null, null, null, null, null)

    init {
        addBlanks(3)
    }

    fun addBlanks(n: Long) {
        for (i in 1..n) {
            grid.add(arrayListOf('.', '.', '.', '.', '.', '.', '.'))
        }
    }

    fun getMaxLevel(): Long =
        maxOccupiedLevels.filter { it != null }.maxOfOrNull { it!! } ?: 0

    fun addRock(rock: RockShape): RockShape {
        val adjusted = rock + Pair(3, getMaxLevel() + 4)
        while (!fits(adjusted)) {
            addBlanks(1)
        }

        return adjusted
    }

    fun fits(rock: RockShape): Boolean {
        for (point in rock.points) {
            val (x, y) = adjust(point)

            if (y < 0 || y >= grid.size) {
                return false
            }

            val line = grid[y.toInt()]

            if (x < 0 || x >= line.size) {
                return false
            }

            if (grid[y.toInt()][x.toInt()] != '.') {
                return false
            }
        }

        return true
    }

    fun commit(rock: RockShape) {
        for (point in rock.points) {
            val (x, y) = adjust(point)
            grid[y.toInt()][x.toInt()] = '#'

            val currMax = maxOccupiedLevels[x.toInt()]
            if (currMax == null || point.second > currMax) {
                maxOccupiedLevels[x.toInt()] = point.second
            }
        }
    }

    override fun toString(): String {
        var str = ""
        var y = grid.size - 1
        while (y >= 0) {
            for (ch in grid[y]) {
                str += ch
            }
            str += '\n'
            y--
        }

        return str
    }

    private fun adjust(point: Pair<Long, Long>): Pair<Long, Long> {
        val (x, y) = point
        return Pair(x - 1, y - level)
    }
}

fun findPattern(rockHistory: List<PatternEntry>): Pair<Int, Int>? {
    var starting = 0

    // Brute force find a pattern. Pick each position as a start. Iterate until you find a pattern
    // from that point that repeats. If not, move to the next spot.
    while (starting < rockHistory.size) {
        var i = starting + 1
        var repeatStart: Int? = null

        while (i < rockHistory.size) {
            if (repeatStart == null) {
                if (rockHistory[i] == rockHistory[starting]) {
                    repeatStart = i
                }
            } else {
                if (rockHistory[i] != rockHistory[starting + i - repeatStart]) {
                    i = repeatStart + 1
                    repeatStart = null
                } else if (i == repeatStart + repeatStart - starting - 1) {
                    return Pair(starting, repeatStart - 1)
                }
            }

            i++
        }

        starting++
    }

    return null
}

fun printSolution(pattern: Pair<Int, Int>, heightMap: Map<Long, Long>) {
    val (start, end) = pattern
    val interval = end - start + 1L

    val prefixCount = ROCK_COUNT % interval

    val startHeight = heightMap[prefixCount]
    val intervalHeight = heightMap[end.toLong() + 1L]!! - heightMap[start.toLong()]!!

    print("Pattern starts at $start till $end for period of ${end - start + 1}\n")
    print("Height: ${heightMap[start.toLong()]} + n * ${intervalHeight}\n")

    print(
        "We start at $startHeight after $prefixCount rocks so that the remaining rocks have" +
            " a whole number of intervals.\n"
    )

    val score = startHeight!! + intervalHeight * (ROCK_COUNT - prefixCount) / interval
    print("Final Height: ${score}\n")
}

fun main() {
    val vents = readLine()?.map {
        when (it) {
            '>' -> RIGHT
            '<' -> LEFT
            else -> error("")
        }
    } ?: arrayListOf<Pair<Long, Long>>()

    val pile = Pile()

    // Keep track of where each rock was placed (relative to the previous one) for the sake of
    // pattern finding.
    var rockHistory = arrayListOf<PatternEntry>()

    // For each rock placed, keep track of how tall the stack was then.
    val heightMap = mutableMapOf<Long, Long>()

    var iRock = 0
    var iVent = 0
    var prevRock: RockShape? = null

    for (i in 1..ROCK_COUNT) {
        var rock = pile.addRock(shapes[iRock])
        iRock++
        iRock %= shapes.size

        var done = false
        while (!done) {
            // move per vent
            var newRock = rock + vents[iVent]
            iVent++
            iVent %= vents.size

            // check if fits
            if (pile.fits(newRock)) {
                rock = newRock
            }

            // move down
            newRock = rock + DOWN
            if (pile.fits(newRock)) {
                rock = newRock
            } else {
                // The old rock NOT the new one because it didn't fit.
                pile.commit(rock)

                val transform = if (prevRock != null) {
                    rock.transformFrom(prevRock)
                } else {
                    rock.bottomLeft()
                }

                rockHistory.add(PatternEntry(rock.id, transform))
                heightMap[i] = pile.getMaxLevel()

                prevRock = rock

                findPattern(rockHistory)?.let {
                    printSolution(it, heightMap)
                    return
                }

                done = true
            }
        }
    }

    // Backstop in case we don't find a pattern. I think the real input for part 1 doesn't go far
    // enough for a pattern to repeat.
    print("Final Height: ${pile.getMaxLevel()}\n")
}
