import net.panayotov.util.Scanners

fun isSafe(levels: List<Int>, ignore: Int?): Boolean {
    var lastLevel: Int? = null
    var direction: Int? = null

    for (i in 0..<levels.size) {
        if (ignore != null && i == ignore) {
            continue
        }

        val level = levels[i]
        if (lastLevel != null) {
            val diff = lastLevel - level
            val adiff = Math.abs(diff)
            val cdiff = diff.coerceIn(-1, 1)

            if (adiff < 1 || adiff > 3) {
                return false
            }

            if (direction == null) {
                direction = cdiff
            } else if (direction != cdiff) {
                return false
            }
        }

        lastLevel = level
    }

    return true
}

fun main() {
    var score = 0
    for (line in Scanners) {
        val levels = mutableListOf<Int>()
        while (line.hasNextInt()) {
            levels.add(line.nextInt())
        }

        var safe = isSafe(levels, null)

        for (i in 0..<levels.size) {
            if (isSafe(levels, i)) {
                safe = true
            }
        }

        if (safe) {
            score++
        }
    }

    println("Answer = $score")
}
