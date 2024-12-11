import net.panayotov.util.Scanners

fun readStones(): MutableList<String> {
    val stones = mutableListOf<String>()
    for (line in Scanners) {
        while (line.hasNext()) {
            stones.add(line.next())
        }
    }

    return stones
}

fun blink(stones: MutableList<String>) {
    var i = 0
    while (i < stones.size) {
        val stone = stones[i]
        if (stone == "0") {
            stones[i] = "1"
        } else if (stone.length % 2 == 0) {
            stones[i] = stone.substring(0, stone.length / 2)
            stones.add(i + 1, stone.substring(stone.length / 2).toLong().toString())
            i++
        } else {
            stones[i] = (stone.toLong() * 2024L).toString()
        }
        i++
    }
}

fun main() {
    val stones = readStones()

    for (i in 1..25) {
        blink(stones)
    }

    println("Answer = ${stones.size}")
}
