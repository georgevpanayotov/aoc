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
    var score = 1L
    val timeLine = Lines.next()
    val times = timeLine.split(":")[1].split(" ").filter {
        it.length > 0
    }.map {
        it.toLong()
    }

    val distanceLine = Lines.next()
    val distances = distanceLine.split(":")[1].split(" ").filter {
        it.length > 0
    }.map {
        it.toLong()
    }

    for (i in 0..times.size - 1) {
        score *= findWinners(times[i], distances[i])
    }
    println("Answer = $score")
}
