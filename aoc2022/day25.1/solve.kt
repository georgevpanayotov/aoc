import kotlin.math.abs
import net.panayotov.util.Lines

val SNAFU_DIGITS = mapOf('2' to 2L, '1' to 1L, '0' to 0L, '-' to -1L, '=' to -2L)

fun fromSnafu(snafu: String): Long {
    var place = 1L
    var result = 0L

    for (index in snafu.length - 1 downTo 0) {
        result += SNAFU_DIGITS[snafu[index]]!! * place

        place *= 5L
    }

    return result
}

fun toSnafu(number: Long): String {
    val snafuBuilder = StringBuilder()

    var running = number

    while (running > 0) {
        val b5Digit = running % 5

        val newDigit =
            when (b5Digit) {
                0L -> Pair('0', 0)
                1L -> Pair('1', 0)
                2L -> Pair('2', 0)
                3L -> Pair('=', 1)
                4L -> Pair('-', 1)
                else -> error("Dude it's mod 5")
            }

        snafuBuilder.insert(0, newDigit.first)
        running = running / 5 + newDigit.second
    }

    return snafuBuilder.toString()
}

fun main() {
    val snafus = Lines.asSequence().toList()
    val score = snafus.map(::fromSnafu).reduce { acc, it -> acc + it }

    println("Answer = ${toSnafu(score)}")
}
