
import kotlin.collections.ArrayDeque

data class RockShape(val xLength: Long, val yLength: Long, val points: List<Pair<Long, Long>>) {
    operator fun plus(offset: Pair<Long, Long>): RockShape {
        return RockShape(
            xLength, yLength,
            points.map {
                Pair(it.first + offset.first, it.second + offset.second)
            }
        )
    }
}

const val ROCK_COUNT = 2022
val UP = Pair(0L, 1L)
val DOWN = Pair(0L, -1L)
val LEFT = Pair(-1L, 0L)
val RIGHT = Pair(1L, 0L)

val shapes = arrayListOf<RockShape>(
    RockShape(4, 1, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0))),
    RockShape(3, 3, listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(0, 1), Pair(2, 1))),
    RockShape(3, 3, listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1), Pair(2, 2))),
    RockShape(1, 4, listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3))),
    RockShape(2, 2, listOf(Pair(0, 0), Pair(0, 1), Pair(1, 0), Pair(1, 1)))
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

    private fun adjust(point: Pair<Long, Long>): Pair<Long, Long> {
        val (x, y) = point
        return Pair(x - 1, y - level)
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

    var rockCount = 0
    var ventIt = vents.iterator()
    for (i in 1..ROCK_COUNT) {
        var rock = pile.addRock(shapes[rockCount])
        rockCount++
        rockCount %= shapes.size

        var done = false
        while (!done) {
            if (!ventIt.hasNext()) {
                ventIt = vents.iterator()
            }

            // move per vent
            var newRock = rock + ventIt.next()
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
                done = true
            }
        }
    }

    print("${pile.getMaxLevel()}\n")
}
