import net.panayotov.util.Lines

val CARD_ORDER = mutableMapOf(
    'J' to 1,
    '2' to 2,
    '3' to 3,
    '4' to 4,
    '5' to 5,
    '6' to 6,
    '7' to 7,
    '8' to 8,
    '9' to 9,
    'T' to 10,
    'Q' to 11,
    'K' to 12,
    'A' to 13,
)

enum class HandScore(val rank: Int) {
    HIGH_CARD(1),
    ONE_PAIR(2),
    TWO_PAIR(3),
    THREE_KIND(4),
    FULL_HOUSE(5),
    FOUR_KIND(6),
    FIVE_KIND(7),
}

fun computeHandScore(hand: String): HandScore {
    if (hand.length != 5) {
        error("Invalid hand lenght(${hand.length}): $hand")
    }

    val countMap = mutableMapOf<Char, Int>()
    for (card in CARD_ORDER.keys) {
        countMap[card] = 0
    }

    for (card in hand) {
        val count = countMap[card]
        if (count == null) {
            error("Invalid card $card in hand $hand")
        }

        countMap[card] = count + 1
    }

    val jokers = countMap['J']!!
    countMap['J'] = 0

    var pairs = 0
    var triple = false

    var maxCard: Char? = null

    for (card in countMap.keys) {
        if (maxCard == null) {
            maxCard = card
        } else if (countMap[card]!! > countMap[maxCard]!!) {
            maxCard = card
        }
    }

    if (maxCard != null) {
        val count = countMap[maxCard]!!
        countMap[maxCard] = count + jokers
    }

    for (card in countMap.keys) {
        if (countMap[card] == 5) {
            return HandScore.FIVE_KIND
        } else if (countMap[card] == 4) {
            return HandScore.FOUR_KIND
        } else if (countMap[card] == 3) {
            triple = true
        } else if (countMap[card] == 2) {
            pairs++
        }
    }

    if (triple) {
        if (pairs > 0) {
            return HandScore.FULL_HOUSE
        } else {
            return HandScore.THREE_KIND
        }
    } else if (pairs == 2) {
        return HandScore.TWO_PAIR
    } else if (pairs == 1) {
        return HandScore.ONE_PAIR
    } else {
        return HandScore.HIGH_CARD
    }
}

fun compareHands(leftHand: String, rightHand: String): Int {
    var compare = computeHandScore(leftHand).rank - computeHandScore(rightHand).rank

    if (compare != 0) {
        return compare
    }

    for (i in 0..4) {
        compare = CARD_ORDER[leftHand[i]]!! - CARD_ORDER[rightHand[i]]!!
        if (compare != 0) {
            return compare
        }
    }

    return compare
}

data class Bid(val hand: String, val amount: Int)

fun readBids(): List<Bid> {
    val bids = mutableListOf<Bid>()

    for (line in Lines) {
        val parts = line.split(" ")
        bids.add(Bid(parts[0], parts[1].toInt()))
    }

    return bids.toList()
}

fun main() {
    val bids = readBids().sortedWith { l: Bid, r: Bid ->
        compareHands(l.hand, r.hand)
    }

    var score = 0
    for (i in 0..bids.size - 1) {
        score += bids[i].amount * (i + 1)
    }

    println("Answer = $score")
}
