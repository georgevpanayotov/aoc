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

fun findMaxJoltage(bank: String): Int {
    val i = findMaxDigit(bank, 0, bank.length - 2)
    val j = findMaxDigit(bank, i + 1, bank.length - 1)

    return "${bank[i]}${bank[j]}".toInt()
}

fun main() {
    var score = 0

    for (line in Lines) {
        score += findMaxJoltage(line)
    }

    println("Answer = $score")
}
