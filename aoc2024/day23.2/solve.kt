import java.util.ArrayDeque
import net.panayotov.util.Scanners

fun MutableMap<String, MutableSet<String>>.connect(firstComp: String, secondComp: String) {
    val others = getOrPut(firstComp, ::mutableSetOf)
    others.add(secondComp)
}

fun pruneAdditions(connections: Map<String, Set<String>>, additions: Set<String>): Set<String> =
    if (additions.size == 0) {
        setOf()
    } else {
        additions.map { setOf(it) + connections[it]!! }.reduce { acc, value -> acc intersect value }
    }

fun findParties(connections: Map<String, Set<String>>): List<Set<String>> {
    val parties = mutableSetOf<Set<String>>()
    val seen = mutableSetOf<Set<String>>()

    val queue = ArrayDeque<Set<String>>()

    queue.addAll(connections.keys.map(::setOf))

    while (!queue.isEmpty()) {
        val party = queue.removeFirst()
        if (!seen.add(party)) {
            continue
        }

        // The additions is the set of all computers that are connected to every member of the party
        // but aren't in the party (yet).
        val additions =
            party
                .map { connections[it] }
                .filterNotNull()
                .reduce { acc, value -> acc intersect value } - party

        // If we didn't find any then this party can't grow anymore, toss it on the output pile.
        if (additions.size == 0) {
            parties.add(party)
        } else {
            // Otherwise, try adding each addition independently.
            for (addition in additions) {
                queue.addLast(party + setOf(addition))
            }
        }
    }

    return parties.toList()
}

fun main() {
    val connections = mutableMapOf<String, MutableSet<String>>()
    for (line in Scanners) {
        line.useDelimiter("-")

        val firstComp = line.next()
        val secondComp = line.next()
        connections.connect(firstComp, secondComp)
        connections.connect(secondComp, firstComp)
    }

    val parties = findParties(connections)

    val largestParty = parties.maxBy { it.size }

    val score = largestParty.sorted().joinToString(",")

    println("Answer = $score")
}
