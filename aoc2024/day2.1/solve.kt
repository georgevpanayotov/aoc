import net.panayotov.util.Scanners

fun main() {
    var score = 0
    for (line in Scanners) {
        var lastLevel: Int? = null
        var safe = true
        var direction: Int? = null

        while (line.hasNextInt()) {
            val level = line.nextInt()
            if (lastLevel != null) {
                val diff = lastLevel - level
                val adiff = Math.abs(diff)
                val cdiff = diff.coerceIn(-1, 1)

                if (adiff < 1 || adiff > 3) {
                    safe = false
                    break
                }

                if (direction == null) {
                    direction = cdiff
                } else if (direction != cdiff) {
                    safe = false
                    break
                }
            }

            lastLevel = level
        }

        if (safe) {
            score++
        }
    }

    println("Answer = $score")
}
