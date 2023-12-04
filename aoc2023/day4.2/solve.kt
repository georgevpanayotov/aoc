import net.panayotov.util.Lines

data class Card(val winning: Set<String>, val myCard: List<String>) {
    fun check(): Int {
        var winningCount = 0

        for (number in myCard) {
            if (number.length == 0) {
                continue
            }

            if (winning.contains(number)) {
                winningCount++
            }
        }

        return winningCount
    }
}

fun main() {
    var score = 0
    val cards = mutableListOf<Card>()
    val cardCount = mutableMapOf<Int, Int>()
    var index = 0

    for (line in Lines) {
        val cardsParts = line.split(":")[1].split("|")

        val winning = cardsParts[0].trim().split(" ").toMutableSet()
        val myCard = cardsParts[1].trim().split(" ")

        winning.remove("")

        cards.add(Card(winning.toSet(), myCard))
        cardCount[index] = 1
        index++
    }

    for (i in 0..cards.size - 1) {
        val winningCount = cards[i].check()

        val count = cardCount[i]
        if (count == null) {
            continue
        }

        for (offset in 1..winningCount) {
            val offsetCount = cardCount[i + offset]
            if (offsetCount == null) {
                continue
            }

            cardCount[i + offset] = offsetCount + count
        }
    }

    for (i in 0..cardCount.keys.size - 1) {
        score += cardCount[i]?:0
    }

    println("Answer = $score")
}
