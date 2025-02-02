import java.util.PriorityQueue
import java.util.Scanner
import net.panayotov.util.Point
import net.panayotov.util.Scanners

data class Brick(val min: Point, val max: Point, val lowZ: Long, val highZ: Long) {
    fun lower(z: Long) = Brick(min, max, lowZ - z, highZ - z)

    fun lower() = Brick(min, max, 0, highZ - lowZ)

    fun forPoints(action: (Point) -> Unit) {
        for (x in min.x..max.x) {
            for (y in min.y..max.y) {
                action(Point(x, y))
            }
        }
    }
}

fun readBrick(line: Scanner): Brick {
    line.useDelimiter("[,~]")
    val x1 = line.nextLong()
    val y1 = line.nextLong()
    val z1 = line.nextLong()
    val x2 = line.nextLong()
    val y2 = line.nextLong()
    val z2 = line.nextLong()

    return Brick(Point(x1, y1), Point(x2, y2), z1, z2)
}

fun makeMap(bricks: List<Brick>): Map<Point, List<Int>> {
    val map = mutableMapOf<Point, MutableList<Int>>()

    for (i in 0..<bricks.size) {
        bricks[i].forPoints { map.getOrPut(it) { mutableListOf() }.add(i) }
    }

    return map.entries.associate { Pair(it.key, it.value.toList()) }
}

// Finds bricks supported by this brick.
fun findSupported(iBrick: Int, bricks: List<Brick>, map: Map<Point, List<Int>>): List<Int> {
    val brick = bricks[iBrick]
    val supported = mutableSetOf<Int>()
    brick.forPoints {
        map[it]
            ?.filter {
                if (it == iBrick) {
                    false
                } else {
                    bricks[it].lowZ == brick.highZ + 1
                }
            }
            ?.also { supported.addAll(it) }
    }

    return supported.toList()
}

// Finds bricks supporting this brick.
fun findSupporting(iBrick: Int, bricks: List<Brick>, map: Map<Point, List<Int>>): List<Int> {
    val brick = bricks[iBrick]
    val supporting = mutableSetOf<Int>()
    brick.forPoints {
        map[it]
            ?.filter {
                if (it == iBrick) {
                    false
                } else {
                    bricks[it].highZ + 1 == brick.lowZ
                }
            }
            ?.also { supporting.addAll(it) }
    }

    return supporting.toList()
}

fun main() {
    val bricks = Scanners.asSequence().map(::readBrick).toMutableList()

    val map = makeMap(bricks)

    val queue = PriorityQueue<Int>(compareBy { bricks[it].lowZ })
    queue.addAll(0..<bricks.size)
    while (!queue.isEmpty()) {
        val iBrick = queue.poll()
        val brick = bricks[iBrick]

        // Index of the highest z that is below the lowest Z of the current brick.
        var highest: Int? = null
        var highestZ: Long? = null

        brick.forPoints {
            val others = map[it]
            if (others == null) {
                error("Inconsistent state: the map should have point $it")
            }

            for (i in others) {
                if (i == iBrick) {
                    continue
                }
                val newZ = bricks[i].highZ
                if (newZ < brick.lowZ && (highestZ?.let { newZ > it } ?: true)) {
                    highest = i
                    highestZ = newZ
                }
            }
        }

        if (highestZ != null) {
            bricks[iBrick] = brick.lower(brick.lowZ - highestZ - 1)
        } else {
            // Nothing in our way!
            bricks[iBrick] = brick.lower()
        }
    }

    var score = 0

    for (i in 0..<bricks.size) {
        val supported =
            findSupported(i, bricks, map).filter {
                // We are the only supporting brick for brick `it`.
                findSupporting(it, bricks, map) == listOf(i)
            }

        if (supported.size == 0) {
            score++
        }
    }

    println("Answer = $score")
}
