import net.panayotov.util.Lines

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

// Index: the index of the next spring to figure out
// Prefix: how many DAMAGED in a row there are leading up to `index`
// rangeIndex: the index of the next range to satisfy
data class RecurrenceState(val prefix: Int, val index: Int, val rangeIndex: Int) {
    fun toMutable() = MutableRecurrenceState(prefix, index, rangeIndex)
    companion object {
        fun start() = RecurrenceState(0, 0, 0)
    }
}

data class MutableRecurrenceState(var prefix: Int, var index: Int, var rangeIndex: Int) {
    fun toRecurrence(): RecurrenceState = RecurrenceState(prefix, index, rangeIndex)
}

fun MutableMap<RecurrenceState, Long>.save(state: RecurrenceState, result: Long): Long {
    this[state] = result
    return result
}

data class SpringRow(val springData: List<SpringState>, val ranges: List<Int>) {
    fun checkDone(state: RecurrenceState): Long? {
        if (state.rangeIndex >= ranges.size) {
            // We've satisfied all of the ranges. That means we just need to make sure there are no
            // more DAMAGED after here and we can conclude this is solved.
            for (i in state.index..<springData.size) {
                if (springData[i] == SpringState.DAMAGED) {
                    return 0
                }
            }
            return 1
        }

        if (state.index >= springData.size) {
            // We've gone through the whole row. Because of the prior statement, we know that we
            // HAVEN'T satisfied all of the previous ranges. Our only hope is that we are at the
            // very last range and the prefix is the right size to satisfy it completely.
            if (state.rangeIndex == ranges.size - 1 &&
                state.prefix == ranges[state.rangeIndex]) {
                return 1
            }

            return 0
        }

        return null
    }

    fun canEndRange(state: RecurrenceState): Boolean = state.prefix == ranges[state.rangeIndex]

    fun countArrangements(cache: MutableMap<RecurrenceState, Long>, state: RecurrenceState): Long {
        val cached = cache[state]
        if (cached != null) {
            return cached
        }

        checkDone(state)?.let {
            return cache.save(state, it)
        }

        val newState = state.toMutable()

        while (newState.index < springData.size && springData[newState.index] != SpringState.UNKNOWN) {
            if (springData[newState.index] == SpringState.DAMAGED) {
                // Still DAMAGED so continue (or start) the current range.
                newState.prefix++
            } else if (newState.prefix != 0) {
                // New operational after a range of DAMAGED.
                if (canEndRange(newState.toRecurrence())) {
                    // We can successfully close this range so just move on to the next range.
                    newState.rangeIndex++
                } else {
                    // This is an invalid attempt (i.e. we are have a damaged range that doesn't
                    // match the expect range size).
                    return cache.save(state, 0)
                }
                newState.prefix = 0
            }
            newState.index++

            // After every step forward, check if we're done.
            checkDone(newState.toRecurrence())?.let {
                return cache.save(state, it)
            }
        }

        // We're at UNKNOWN, make two recursive calls:
        // 1. supposing this were to be marked DAMAGED.
        // 2. supposing this were to be marked OPERATIONAL.
        val damageState = newState.copy()
        damageState.index++

        // For damaged, also grow the curent range by 1.
        damageState.prefix++

        val damageRecur = countArrangements(cache, damageState.toRecurrence())

        val operState = newState.copy()
        operState.index++

        // For operational, check if we would close a range here.
        val operRecur = if (newState.prefix != 0 && !canEndRange(newState.toRecurrence())) {
            // Can't count it if we close a wrong sized range.
            0
        } else if (newState.prefix != 0 && canEndRange(newState.toRecurrence())) {
            // Closing the range so starting at prefix 0 to build the next range.
            operState.prefix = 0
            // Also move to the next expected range.
            operState.rangeIndex++

            countArrangements(cache, operState.toRecurrence())
        } else {
            // There was no range so just move forward.
            countArrangements(cache, operState.toRecurrence())
        }

        return cache.save(state, damageRecur + operRecur)
    }

    fun countUnknown() = springData.filter({ it == SpringState.UNKNOWN }).size
}

fun readRows(): List<SpringRow> {
    val rows = mutableListOf<SpringRow>()

    for (line in Lines) {
        val parts = line.split(" ")

        val springData = parts[0].map(::parse)
        val ranges = parts[1].split(",").filter({ it.length != 0 }).map(String::toInt)

        val bigSpringData = springData.toMutableList()
        val bigRanges = ranges.toMutableList()

        for (i in 1..4) {
            bigSpringData.add(SpringState.UNKNOWN)
            bigSpringData.addAll(springData)
            bigRanges.addAll(ranges)
        }

        rows.add(SpringRow(bigSpringData, bigRanges))
    }

    return rows.toList()
}

fun main() {
    var score = 0L
    val rows = readRows()

    for (row in rows) {
        val cache = mutableMapOf<RecurrenceState, Long>()
        val arrangements = row.countArrangements(cache, RecurrenceState.start())
        score += arrangements
    }

    println("Answer = $score")
}
