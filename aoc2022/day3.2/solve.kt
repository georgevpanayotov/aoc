
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
    var count = 1

    var runningSet = mutableSetOf<Char>()

    while (line != null) {
        val itemSet = makeSet(line)

        if (count == 1) {
            runningSet.addAll(itemSet)
        } else {
            runningSet = runningSet.intersect(itemSet).toMutableSet()
        }

        if (count == 0) {
            if (runningSet.size != 1) {
                error("Found set of size: ${runningSet.size} expected: 1")
            }

            val item = runningSet.firstNotNullOf { item -> item }

            val pri = priority(item)
            if (pri == null) {
                error("Invalid item: $item")
            }
            score += pri

            runningSet = mutableSetOf<Char>()
        }

        line = readLine()
        count++
        count %= 3
    }

    print("$score\n")
}
