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

fun isValid(number: Long): Boolean {
    val str = number.toString()
    val len = str.length
    if (len % 2 == 0 && str.substring(0, len / 2) == str.substring(len / 2)) {
        return false
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
