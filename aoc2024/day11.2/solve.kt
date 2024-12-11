import net.panayotov.util.Scanners

data class Frame(val blinks: Int, val stone: String)

fun readStones(): MutableList<String> {
    val stones = mutableListOf<String>()
    for (line in Scanners) {
        while (line.hasNext()) {
            stones.add(line.next())
        }
    }

    return stones
}

fun blinkStone(stone: String) =
    if (stone == "0") {
        listOf("1")
    } else if (stone.length % 2 == 0) {
        listOf(
            stone.substring(0, stone.length / 2),
            stone.substring(stone.length / 2).toLong().toString(),
        )
    } else {
        listOf((stone.toLong() * 2024L).toString())
    }

// This version is recursive so it can do memoization.
fun blink(memo: MutableMap<Frame, Long>, blinks: Int, stones: List<String>) =
    if (stones.size == 1) {
        val stone = stones[0]
        val frame = Frame(blinks, stone)

        if (memo.contains(frame)) {
            return memo[frame]!!
        }

        val blinkedStone = blinkStone(stone)
        val result =
            if (blinks == 1) {
                blinkedStone.size.toLong()
            } else {
                blink(memo, blinks - 1, blinkedStone)
            }

        memo[frame] = result
        return result
    } else if (stones.size > 1) {
        return blink(memo, blinks, listOf(stones[0])) +
            blink(memo, blinks, stones.subList(1, stones.size))
    } else {
        return 0
    }

fun main() {
    val stones = readStones()

    val score = blink(mutableMapOf(), 75, stones)

    println("Answer = ${score}")
}
