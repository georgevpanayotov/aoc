import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

fun main() {
    var score = 0
    for (line in Lines) {
        val cards = line.split(":")[1].split("|")

        val winning = cards[0].trim().split(" ").toMutableSet()
        val myCard = cards[1].trim().split(" ")

        winning.remove("")

        var winningCount = 0

        for (number in myCard) {
            if (number.length == 0) {
                continue
            }

            if (winning.contains(number)) {
                winningCount++
            }
        }

        if (winningCount != 0) {
            score += 1 shl (winningCount - 1)
        }
    }

    println("Answer = $score")
}
