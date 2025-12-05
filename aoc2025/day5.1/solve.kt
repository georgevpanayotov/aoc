import net.panayotov.util.Lines

data class FreshIds(val low: Long, val high: Long) {
    fun isFresh(id: Long): Boolean = id >= low && id <= high
}

fun main() {
    var score = 0
    var readingRanges = true
    val ranges = mutableListOf<FreshIds>()
    val ids = mutableListOf<Long>()

    for (line in Lines) {
        if (line.length == 0) {
            readingRanges = false
        } else if (readingRanges) {
            val parts = line.split("-")
            ranges.add(FreshIds(parts[0].toLong(), parts[1].toLong()))
        } else {
            ids.add(line.toLong())
        }
    }

    for (id in ids) {
        for (range in ranges) {
            if (range.isFresh(id)) {
                score++
                // In case it falls into 2 ranges, don't double count it.
                break
            }
        }
    }

    println("Answer = $score")
}
