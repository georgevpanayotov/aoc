import net.panayotov.util.Lines
import kotlin.math.pow

enum class SpringState {
    UNKNOWN,
    OPERATIONAL,
    DAMAGED,
}

fun parse(spring: Char) = when (spring) {
    '.' -> SpringState.OPERATIONAL
    '#' -> SpringState.DAMAGED
    '?' -> SpringState.UNKNOWN
    else -> error("Unrecognized spring $spring")
}

data class SpringRow(val springData: List<SpringState>, val rangeSizes: List<Int>) {
    fun tryArrangement(arrangement: Int): SpringRow {
        val newData = mutableListOf<SpringState>()

        var curr = arrangement
        var i = 0
        while (i < springData.size) {
            if (springData[i] == SpringState.UNKNOWN) {
                if (curr and 1 == 0) {
                    newData.add(SpringState.OPERATIONAL)
                } else {
                    newData.add(SpringState.DAMAGED)
                }
                curr = curr shr 1
            } else {
                newData.add(springData[i])
            }

            i++
        }

        return SpringRow(newData, rangeSizes)
    }

    fun countUnknown() = springData.filter({ it == SpringState.UNKNOWN }).size

    fun validateRanges(): Boolean {
        val observedRanges = mutableListOf<Int>()

        var currRange: Int? = null

        for (state in springData) {
            if (state == SpringState.DAMAGED) {
                currRange = (currRange ?: 0) + 1
            } else if (currRange != null) {
                observedRanges.add(currRange)
                currRange = null
            }
        }
        if (currRange != null) {
            observedRanges.add(currRange)
        }
        return rangeSizes == observedRanges
    }
}

fun readRows(): List<SpringRow> {
    val rows = mutableListOf<SpringRow>()

    for (line in Lines) {
        val parts = line.split(" ")

        val springData = parts[0].map(::parse)
        val rangeSizes = parts[1].split(",").filter({ it.length != 0 }).map(String::toInt)
        rows.add(SpringRow(springData, rangeSizes))
    }

    return rows.toList()
}

fun main() {
    var score = 0
    val rows = readRows()

    for (row in rows) {
        val unknown = row.countUnknown()
        for (i in 0..2.0.pow(unknown).toInt() - 1) {
            if (row.tryArrangement(i).validateRanges()) {
                score++
            }
        }
    }

    println("Answer = $score")
}
