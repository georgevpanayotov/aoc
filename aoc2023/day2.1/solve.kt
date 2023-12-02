import net.panayotov.util.Lines

const val MAX_RED = 12
const val MAX_GREEN = 13
const val MAX_BLUE = 14

data class Pull(val reds: Int, val greens: Int, val blues: Int) {
    val isValid
        get() = reds <= MAX_RED && greens <= MAX_GREEN && blues <= MAX_BLUE
}

data class Game(val id: Int, val pulls: List<Pull>) {
    val isValid: Boolean
        get() {
            for (pull in pulls) {
                if (!pull.isValid) {
                    return false
                }
            }

            return true
        }
}

fun parsePull(serialized: String): Pull {
    val parts = serialized.split(",")
    var red = 0
    var green = 0
    var blue = 0

    for (i in 0..parts.size - 1) {
        val part = parts[i].trim().split(" ")

        val name = part[1].trim()
        val value = part[0].toInt()

        if (name == "red") {
            red = value
        } else if (name == "green") {
            green = value
        } else if (name == "blue") {
            blue = value
        }
    }

    return Pull(red, green, blue)
}

fun parseGame(line: String): Game {
    val parts = line.split(":")
    val id = parts[0].split(" ")[1].toInt()

    val pulls = parts[1].split(";").map(::parsePull)

    return Game(id, pulls)
}

fun main() {
    val games = mutableListOf<Game>()

    for (line in Lines) {
        games.add(parseGame(line))
    }

    var score = 0
    for (game in games) {
        if (game.isValid) {
            score += game.id
        }
    }

    println("Answer = $score")
}
