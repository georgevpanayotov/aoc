import net.panayotov.util.Lines

fun findMaxDigit(bank: String, low: Int, high: Int): Int {
    var iMaxDigit: Int? = null

    for (i in low..high) {
        if (iMaxDigit == null || bank[i] > bank[iMaxDigit]) {
            iMaxDigit = i
        }
    }

    if (iMaxDigit == null) {
        error("Empty bank")
    }

    return iMaxDigit
}

fun findMaxJoltage(bank: String): Long {
    var digits = StringBuilder()
    var remaining = 12
    var lastI = -1

    while (digits.length < 12) {
        val i = findMaxDigit(bank, lastI + 1, bank.length - remaining)
        lastI = i

        remaining--

        digits.append(bank[i])
    }

    return digits.toString().toLong()
}

fun main() {
    var score = 0L

    for (line in Lines) {
        score += findMaxJoltage(line)
    }

    println("Answer = $score")
}
