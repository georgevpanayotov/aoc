import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun hash(step: String): Int {
    var hash = 0
    for (ch in step) {
        hash += ch.code
        hash *= 17
        hash %= 256
    }

    return hash
}

fun main() {
    val steps = Lines.next().split(",")

    val score = steps.map(::hash).reduce { acc, value -> acc + value }
    println("Answer = $score")
}
