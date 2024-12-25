import net.panayotov.util.Scanners

fun MutableMap<String, MutableSet<String>>.connect(firstComp: String, secondComp: String) {
    val others = getOrPut(firstComp, ::mutableSetOf)
    others.add(secondComp)
}

fun findTrios(connections: Map<String, Set<String>>): List<Set<String>> {
    val trios = mutableSetOf<Set<String>>()

    for (comp in connections.keys) {
        for (otherComp in connections[comp]!!) {
            for (third in connections[comp]!! intersect connections[otherComp]!!) {
                trios.add(setOf(comp, otherComp, third))
            }
        }
    }

    return trios.toList()
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

    val trios = findTrios(connections)

    val answerTrios = trios.filter { it.filter { it[0] == 't' }.size > 0 }

    val score = answerTrios.size

    println("Answer = $score")
}
