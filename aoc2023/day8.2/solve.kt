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

fun gcd(a: Long, b: Long): Long =
    if (a == 0L) {
        b
    } else if (b == 0L) {
        a
    } else if (a > b) {
        gcd(a % b, b)
    } else {
        gcd(b % a, a)
    }

fun abs(a: Long): Long = if (a < 0) -a else a

fun lcm(a: Long, b: Long): Long = abs(a * b) / gcd(a, b)

fun findLength(map: DesertMap, start: String): Long {
    var location = start
    var spot = 0
    var length = 0L

    while (location[2] != 'Z') {
        val direction = map.directions[spot]

        location = map.adjacency[location]?.get(direction)!!

        length++
        spot++
        spot = spot % map.directions.length
    }

    return length
}

fun main() {
    val map = readMap()

    val locations = map.adjacency.keys.filter { it[2] == 'A' }
    val lengths = locations.map { findLength(map, it) }
    val lcm = lengths.reduce { acc, value -> lcm(acc, value) }

    println("Answer = $lcm")
}
