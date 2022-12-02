
enum class Hand(val score: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    companion object {
        fun forScore(score: Int): Hand? =
            // And seven years ago.
            when (score) {
                1 -> ROCK
                2 -> PAPER
                3 -> SCISSORS
                else -> null
            }
    }
}

fun main() {
    var line = readLine()
    var score = 0
    while (line != null) {
        val oppHand = convert(line[0])
        if (oppHand == null) {
            error("Failed parsing: $line")
        }

        val myHand = convertStrategy(line[2], oppHand)

        if (myHand == null) {
            error("Failed parsing: $line")
        }

        score += compute(oppHand, myHand)

        line = readLine()
    }
    print(score)
}

fun convert(play: Char): Hand? =
    when (play) {
        'A' -> Hand.ROCK
        'B' -> Hand.PAPER
        'C' -> Hand.SCISSORS
        else -> null
    }

fun convertStrategy(myPlay: Char, oppHand: Hand): Hand? =
    // X means you need to lose
    // Y means you need to end the round in a draw
    // and Z means you need to win
    when (myPlay) {
        'X' -> Hand.forScore((oppHand.score + 1) % 3 + 1)
        'Y' -> oppHand
        'Z' -> Hand.forScore((oppHand.score) % 3 + 1)
        else -> null
    }

fun compute(oppHand: Hand, myHand: Hand): Int {
    var diff = oppHand.score - myHand.score

    diff += 3
    diff %= 3

    return myHand.score + when (diff) {
        0 -> 3
        1 -> 0
        2 -> 6
        else -> error("Unexpected score: $diff")
    }
}
