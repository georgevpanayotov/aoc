import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun computeDistance(duration: Long, heldDuration: Long): Long {
    val remainingTime = duration - heldDuration
    val speed = heldDuration

    return speed * remainingTime
}

fun findWinners(duration: Long, recordDistance: Long): Long {
    var score = 0L

    for (heldDuration in 0..duration) {
        val distance = computeDistance(duration, heldDuration)
        if (distance > recordDistance) {
            score++
        }
    }

    return score
}

fun main() {
    val timeLine = Lines.next()
    val time = timeLine.split(":")[1].filter {
        it != ' '
    }.toLong()

    val distanceLine = Lines.next()
    val distance = distanceLine.split(":")[1].filter {
        it != ' '
    }.toLong()

    val score = findWinners(time, distance)
    println("Answer = $score")
}
