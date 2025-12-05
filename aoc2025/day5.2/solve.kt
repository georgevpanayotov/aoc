import net.panayotov.util.Lines

sealed interface IdRange {
    fun count(): Long

    fun combineOverlapping(range: IdRange): FreshIds?
}

object BlankRange : IdRange {
    override fun count() = 0L

    override fun combineOverlapping(range: IdRange) = null
}

data class FreshIds(val low: Long, val high: Long) : IdRange {
    override fun combineOverlapping(range: IdRange) =
        when (range) {
            is FreshIds -> combineFresh(range)
            is BlankRange -> null
        }

    override fun count() = high - low + 1

    private fun combineFresh(range: FreshIds): FreshIds? =
        if (
            isFresh(range.low) || isFresh(range.high) || range.isFresh(low) || range.isFresh(high)
        ) {
            FreshIds(Math.min(range.low, low), Math.max(range.high, high))
        } else {
            null
        }

    private fun isFresh(id: Long): Boolean = id >= low && id <= high
}

fun main() {
    var score = 0L
    val ranges = mutableListOf<IdRange>()

    for (line in Lines) {
        if (line.length == 0) {
            break
        }
        val parts = line.split("-")
        ranges.add(FreshIds(parts[0].toLong(), parts[1].toLong()))
    }

    var done = false
    while (!done) {
        done = true
        for (i in 0..<ranges.size) {
            for (j in 0..<ranges.size) {
                if (i == j) {
                    continue
                }
                // Collapse overlapping ranges to avoid double counting.
                ranges[i].combineOverlapping(ranges[j])?.let {
                    ranges[i] = it
                    ranges[j] = BlankRange
                    done = false
                }
            }
        }
    }

    for (range in ranges) {
        score += range.count()
    }

    println("Answer = $score")
}
