import net.panayotov.util.Scanners
import net.panayotov.util.findMinMaxTriple

val directions = listOf(
    Triple(-1, 0, 0), Triple(0, -1, 0), Triple(0, 0, -1), Triple(1, 0, 0), Triple(0, 1, 0), Triple(0, 0, 1)
)

data class BlockRecord(val point: Triple<Int, Int, Int>) {
    val sides = ArrayList(directions)
}

operator fun Triple<Int, Int, Int>.plus(other: Triple<Int, Int, Int>): Triple<Int, Int, Int> =
    Triple(first + other.first, second + other.second, third + other.third)

operator fun Triple<Int, Int, Int>.times(factor: Int): Triple<Int, Int, Int> =
    Triple(first * factor, second * factor, third * factor)

class TraversalState(private val blockMap: Map<Triple<Int, Int, Int>, BlockRecord>) {
    private val min: Triple<Int, Int, Int>
    private val max: Triple<Int, Int, Int>

    private val unvisited = mutableSetOf<Triple<Int, Int, Int>>()
    private val pocketPoints = mutableSetOf<Triple<Int, Int, Int>>()

    init {
        val (_min, _max) = findMinMaxTriple(blockMap.keys.iterator())!!
        min = _min
        max = _max

        // Start with set of all possible points.
        for (x in min.first..max.first) {
            for (y in min.second..max.second) {
                for (z in min.third..max.third) {
                    unvisited.add(Triple(x, y, z))
                }
            }
        }

        // Remove everything from blockMap.
        for (point in blockMap.keys) {
            unvisited.remove(point)
        }
    }

    // Do a BFS starting at this point. If the full traversal from this point doesn't reach the
    // bounds (it is an air pocket) remove the sides from any block next to that air pocket.
    fun bfs(point: Triple<Int, Int, Int>) {
        val queue = kotlin.collections.ArrayDeque<Triple<Int, Int, Int>>()
        // Used to keep duplicates out of the queue. Which apparently causes a huge performance
        // slowdown on the real input.
        val queued = mutableSetOf<Triple<Int, Int, Int>>()
        var inBounds = true
        val visited = mutableSetOf<Triple<Int, Int, Int>>()

        queue.addLast(point)
        queued.add(point)

        while (!queue.isEmpty()) {
            val currentPoint = queue.removeFirst()
            visited.add(currentPoint)
            unvisited.remove(currentPoint)

            directions.map {
                // All the neighbors of this point.
                currentPoint + it
            }.filter {
                // That are open space.
                !blockMap.containsKey(it)
            }.filter {
                // And are inside the bounding box for the droplet.
                val pointInBounds = inBounds(it)
                if (!pointInBounds) {
                    inBounds = false
                }
                pointInBounds
            }.filter {
                // And haven't been visited yet.
                unvisited.contains(it)
            }.filter {
                // Or aren't already on deck to be visited.
                !queued.contains(it)
            }.forEach {
                queue.addLast(it)
                queued.add(it)
            }
        }

        if (inBounds) {
            // We didn't hit the bounds for any of these points. This means they are an internal air
            // pocket.
            visited.forEach { visitedPoint ->
                directions.forEach {
                    blockMap[visitedPoint + it]?.let { record ->
                        // We found a block next to this air pocket point, the side facing this
                        // point should be removed from consideration from being part of surface
                        // area.
                        record.sides.remove(it * -1)
                    }
                }
            }
        }
    }

    // Pick some point out of the unvisited set.
    fun pickPoint(): Triple<Int, Int, Int>? = unvisited.let {
        val iter = it.iterator()
        if (iter.hasNext()) {
            iter.next()
        } else {
            null
        }
    }

    private fun inBounds(point: Triple<Int, Int, Int>): Boolean {
        val (x, y, z) = point
        if (x < min.first || x > max.first) {
            return false
        }
        if (y < min.second || y > max.second) {
            return false
        }
        if (z < min.third || z > max.third) {
            return false
        }

        return true
    }
}

// Remove any side that directly touches another block.
fun simplify(blockMap: Map<Triple<Int, Int, Int>, BlockRecord>) {
    for (record in blockMap.values) {
        var i = 0
        while (i < record.sides.size) {
            val side = record.sides[i]
            val otherRecord = blockMap[record.point + side]
            if (otherRecord != null) {
                record.sides.removeAt(i)
                otherRecord.sides.remove(side * -1)
            } else {
                i++
            }
        }
    }
}

fun removeAirPockets(blockMap: Map<Triple<Int, Int, Int>, BlockRecord>) {
    val traversal = TraversalState(blockMap)

    var point = traversal.pickPoint()

    while (point != null) {
        traversal.bfs(point)

        point = traversal.pickPoint()
    }
}

fun computeSurfaceArea(blockMap: Map<Triple<Int, Int, Int>, BlockRecord>): Int {
    var score = 0

    for (record in blockMap.values) {
        score += record.sides.size
    }

    return score
}

fun animate(blockMap: Map<Triple<Int, Int, Int>, BlockRecord>) {
    val (min, max) = findMinMaxTriple(blockMap.keys.iterator())!!

    for (z in min.third..max.third) {
        print("\u001b[2J")

        for (y in min.second..max.second) {
            for (x in min.first..max.first) {
                val ch =
                    if (blockMap.containsKey(Triple(x, y, z))) {
                        '#'
                    } else {
                        ' '
                    }
                print(ch)
            }
            print("\n")
        }
        Thread.sleep(200)
    }
}

fun main() {
    val blockMap = mutableMapOf<Triple<Int, Int, Int>, BlockRecord>()
    for (line in Scanners) {
        line.useDelimiter(",")
        val x = line.nextInt()
        val y = line.nextInt()
        val z = line.nextInt()
        val point = Triple(x, y, z)
        blockMap[point] = BlockRecord(point)
    }

    animate(blockMap)

    simplify(blockMap)

    print("Part 1: ${computeSurfaceArea(blockMap)}\n")

    removeAirPockets(blockMap)

    print("Part 2: ${computeSurfaceArea(blockMap)}\n")
}
