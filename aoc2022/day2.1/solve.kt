
enum class Hand(val score: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3)
}

fun main() {
    var line = readLine()
    var score = 0
    while (line != null) {
        val oppHand = convert(line[0])
        val myHand = convert(line[2])

        if (oppHand == null || myHand == null) {
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
        'X' -> Hand.ROCK
        'B' -> Hand.PAPER
        'Y' -> Hand.PAPER
        'C' -> Hand.SCISSORS
        'Z' -> Hand.SCISSORS
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
