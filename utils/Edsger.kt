package net.panayotov.util

interface GraphLike<T> {
    fun getNeighbors(node: T): List<T>

    fun getNodes(): List<T>
}

// Find the shortest paths from a single source to every other node in the graph. Returns a map from
// every destination to the shortest path between source and destination.
fun <T>edsger(from: T, graph: GraphLike<T>): Map<T, List<T>> {
    val unvisited = mutableSetOf<T>()

    unvisited.addAll(graph.getNodes())

    val dist = mutableMapOf<T, Int>()
    dist[from] = 0

    val prev = mutableMapOf<T, T>()

    while (!unvisited.isEmpty()) {
        var min: T? = null
        for (pos in unvisited) {
            val dp = dist[pos]
            val dm = dist[min]
            if (dp != null && ((min == null) || (dm != null && dp < dm))) {
                min = pos
            }
        }
        if (min == null) {
            break
        }

        unvisited.remove(min)
        val dm = dist[min]
        if (dm == null) {
            error("There should be a dm.")
        }

        for (neighbor in graph.getNeighbors(min)) {
            if (!unvisited.contains(neighbor)) {
                continue
            }

            val alt = dm + 1
            val dn = dist[neighbor]
            if (dn == null || alt < dn) {
                prev[neighbor] = min
                dist[neighbor] = dm + 1
            }
        }
    }

    val paths = mutableMapOf<T, List<T>>()

    for (to in graph.getNodes()) {
        val path = mutableListOf<T>()
        var curr: T? = to

        while (curr != null) {
            path.add(curr)
            curr = prev[curr]
        }

        paths[to] = path.reversed().toList()
    }

    return paths.toMap()
}
