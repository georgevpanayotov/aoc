import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax
import net.panayotov.util.Grid

import java.util.Scanner

fun getDigit(line: String, start: Int): Pair<Int, Int> {
    val digit = StringBuilder()
    var i = start

    while (i < line.length && line[i].isDigit()) {
        digit.append(line[i])
        i++
    }

    return Pair(i, digit.toString().toInt())
}

fun main() {
    var score = 0
    for (line in Lines) {
        var i = 0
        while (i < line.length - 3) {
            var maybeMul = line.substring(i, i + 3)
            if (maybeMul == "mul") {
                i += 3
                if (line[i] == '(') {
                    i++
                } else {
                    i++
                    continue
                }

                val (idx1, digit1) = getDigit(line, i)
                i = idx1
                if (i >= line.length) {
                    break
                }

                if (line[i] == ',') {
                    i++
                } else {
                    i++
                    continue
                }

                val (idx2, digit2) = getDigit(line, i)
                i = idx2

                if (line[i] == ')') {
                    score += digit1 * digit2
                } else {
                    i++
                    continue
                }
            }

            i++
        }
    }

    println("Answer = $score")
}
