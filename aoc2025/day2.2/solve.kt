fun parseRange(rangeStr: String): InputRange {
    val parts = rangeStr.split("-")

    if (parts.size != 2) {
        error("Invalid range: $rangeStr wrong number of numbers.")
    }

    val low = parts[0].toLongOrNull()
    if (low == null) {
        error("Invalid range: $rangeStr. $low isn't a valid number.")
    }

    val high = parts[1].toLongOrNull()
    if (high == null) {
        error("Invalid range: $rangeStr. $high isn't a valid number.")
    }

    return InputRange(low, high)
}

fun factors(number: Int): List<Int> {
    val ret = mutableListOf<Int>()

    for (i in 1..number / 2) {
        if (number % i == 0) {
            ret.add(i)
        }
    }

    return ret.toList()
}

fun isValid(number: Long): Boolean {
    val str = number.toString()

    // Pick only segments that evenly divide the word.
    for (segmentLength in factors(str.length)) {
        if (segmentLength == str.length) {
            // Trivial segment. Don't count it as repeating if it just covers it once. We avoid this
            // in most cases by not picking segments up to the size of the number. The only case is
            // when it is a single digit number because we consider single digit segments.
            continue
        }
        var repeated = true

        for (segment in 1..<str.length / segmentLength) {
            for (i in 0..<segmentLength) {
                // If a later segment differs from the first segment then this doesn't repeeat.
                if (str[i] != str[segmentLength * segment + i]) {
                    repeated = false
                }
            }
        }

        if (repeated) {
            return false
        }
    }

    return true
}

data class InputRange(val low: Long, val high: Long) {
    fun getInvalidNumbers(): List<Long> {
        val ret = mutableListOf<Long>()

        for (i in low..high) {
            if (!isValid(i)) {
                ret.add(i)
            }
        }

        return ret.toList()
    }
}

fun main() {
    val input = readLine()?.split(",")?.map(::parseRange)

    if (input == null) {
        error("No input")
    }

    var score = 0L
    for (range in input) {
        for (number in range.getInvalidNumbers()) {
            score += number
        }
    }

    println("Answer = $score")
}
