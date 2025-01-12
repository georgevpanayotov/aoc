package net.panayotov.util

import java.util.PriorityQueue

interface GraphLike<T> {
    fun getNeighbors(node: T): List<T>

    fun getEdgeWeight(from: T, to: T): Int {
        // By default every edge has weight 1.
        return 1
    }
}

private data class State<T>(val node: T, val prev: T?, val score: Int) {
    constructor(node: T) : this(node, null, 0)

    fun next(neighbor: T, weight: Int) = State<T>(neighbor, node, score + weight)
}

fun <T> edsger(from: T, graph: GraphLike<T>) = edsger(from, graph) { false }

// Find the shortest paths from a single source to every other node in the graph. Returns a map from
// every destination to the shortest path between source and destination.
fun <T> edsger(from: T, graph: GraphLike<T>, terminate: (T) -> Boolean): Map<T, List<T>> {
    val queue = PriorityQueue<State<T>>(compareBy { it.score })
    val seen = mutableSetOf<T>()

    queue.add(State<T>(from))

    val linkedPrev = mutableMapOf<T, T>()

    while (!queue.isEmpty()) {
        val state = queue.poll()
        val (node, prev, score) = state

        if (!seen.add(node)) {
            continue
        }

        if (prev != null) {
            linkedPrev[node] = prev
        }

        for (neighbor in graph.getNeighbors(node)) {
            val weight = graph.getEdgeWeight(node, neighbor)
            queue.add(state.next(neighbor, weight))
        }

        if (terminate(node)) {
            break
        }
    }

    val paths = mutableMapOf<T, List<T>>()

    for (to in seen) {
        val path = mutableListOf<T>()
        var curr: T? = to

        while (curr != null) {
            path.add(curr)
            curr = linkedPrev[curr]
        }

        if (path[path.size - 1] == from) {
            paths[to] = path.reversed().toList()
        }
    }

    return paths.toMap()
}
