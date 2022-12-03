
fun priority(item: Char): Int? {
    if (item <= 'z' && item >= 'a') {
        // Lowercase item types a through z have priorities 1 through 26.

        return item.code - 'a'.code + 1
    } else if (item <= 'Z' && item >= 'A') {
        // Uppercase item types A through Z have priorities 27 through 52.

        return item.code - 'A'.code + 27
    } else {
        return null
    }
}

fun makeSet(items: String): Set<Char> {
    return buildSet<Char> {
        for (i in 0..items.length - 1) {
            add(items[i])
        }
    }
}

fun main() {
    var line = readLine()
    var score = 0
    while (line != null) {
        if (line.length % 2 != 0) {
            error("Should be an even number.")
        }

        val leftItems = line.substring(0, line.length / 2)
        val rightItems = line.substring(line.length / 2)

        val itemSet = makeSet(leftItems)

        for (i in 0..rightItems.length - 1) {
            if (itemSet.contains(rightItems[i])) {
                val pri = priority(rightItems[i])
                if (pri == null) {
                    error("Invalid priority range")
                }

                score += pri
                break
            }
        }

        line = readLine()
    }

    print("$score\n")
}
