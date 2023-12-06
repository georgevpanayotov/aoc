import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun main() {
    val timeLine = Lines.next()
    val time = timeLine.split(":")[1].filter {
        it != ' '
    }.toDouble()

    val distanceLine = Lines.next()
    val distance = distanceLine.split(":")[1].filter {
        it != ' '
    }.toDouble()

    val low = time - sqrt(time * time - 4 * distance) / 2
    val high = time + sqrt(time * time - 4 * distance) / 2
    println("Answer = ${high - low}")
}
