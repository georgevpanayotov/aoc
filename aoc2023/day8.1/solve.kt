import net.panayotov.util.Lines
import java.util.Scanner

data class DesertMap(
    val directions: String,
    val adjacency: Map<String, Map<Char, String>>
)

fun readMap(): DesertMap {
    val directions = Lines.next()
    Lines.next()

    val adjacency = mutableMapOf<String, Map<Char, String>>()
    for (line in Lines) {
        val scanner = Scanner(line)

        val from = scanner.next()

        // "="
        scanner.next()

        val left = scanner.next().substring(1, 4)
        val right = scanner.next().substring(0, 3)

        adjacency[from] = mapOf('L' to left, 'R' to right)
    }

    return DesertMap(directions, adjacency.toMap())
}

fun main() {
    val map = readMap()

    var spot = 0
    var score = 0
    var location = "AAA"

    while (location != "ZZZ") {
        val direction = map.directions[spot]
        location = map.adjacency[location]?.get(direction)!!

        score++
        spot++
        spot = spot % map.directions.length
    }

    println("Answer = $score")
}
