import net.panayotov.util.Lines

val numberMap = mapOf(
    "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7,
    "eight" to 8, "nine" to 9
)

val numbers = listOf(
    "one", "two", "three", "four", "five", "six", "seven",
    "eight", "nine"
)

fun String.isSubstringAt(index: Int, expectedSubString: String): Boolean {
    val end = index + expectedSubString.length
    if (end > this.length) {
        return false
    }

    val subString = this.substring(index, end)

    return subString == expectedSubString
}

fun String.isSubstringBefore(index: Int, expectedSubString: String): Boolean {
    val end = index + 1
    val begin = end - expectedSubString.length
    if (begin < 0) {
        return false
    }

    val subString = this.substring(begin, end)

    return subString == expectedSubString
}

fun calculateFirstNumber(line: String): Int {
    var i = 0

    while (i < line.length) {
        for (number in numbers) {
            if (line.isSubstringAt(i, number)) {
                return numberMap[number]!!
            }
        }

        if (line[i] >= '0' && line[i] <= '9') {
            return line[i].toString().toInt()
        }
        i++
    }

    error("First number not found in $line")
}

fun calculateLastNumber(line: String): Int {
    var i = line.length - 1

    while (i >= 0) {
        for (number in numbers) {
            if (line.isSubstringBefore(i, number)) {
                return numberMap[number]!!
            }
        }

        if (line[i] >= '0' && line[i] <= '9') {
            return line[i].toString().toInt()
        }
        i--
    }

    error("Last number not found in $line")
}

fun main() {
    var score = 0
    for (line in Lines) {
        val firstNumber = calculateFirstNumber(line)
        val lastNumber = calculateLastNumber(line)

        score += 10 * firstNumber + lastNumber
    }

    println(score)
}
