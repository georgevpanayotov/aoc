import net.panayotov.util.Lines

const val MAX_RED = 12
const val MAX_GREEN = 13
const val MAX_BLUE = 14

data class Pull(val red: Int, val green: Int, val blue: Int) {
    val isValid
        get() = red <= MAX_RED && green <= MAX_GREEN && blue <= MAX_BLUE

    val power
        get() = red * green * blue
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

    // Re-uses pull to represent the minimum number of cubes needed for this game to be valid. This
    // is computed by finding the max for each color in this game.
    val minimum: Pull
        get() {
            var maxRed = 0
            var maxGreen = 0
            var maxBlue = 0

            for (pull in pulls) {
                if (pull.red > maxRed) {
                    maxRed = pull.red
                }
                if (pull.green > maxGreen) {
                    maxGreen = pull.green
                }
                if (pull.blue > maxBlue) {
                    maxBlue = pull.blue
                }
            }

            return Pull(maxRed, maxGreen, maxBlue)
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
        score += game.minimum.power
    }

    println("Answer = $score")
}
