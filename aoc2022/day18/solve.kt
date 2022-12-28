
import net.panayotov.util.Scanners

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

fun part1(blockMap: Map<Triple<Int, Int, Int>, BlockRecord>) {
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

    var score = 0

    for (record in blockMap.values) {
        score += record.sides.size
    }

    println(score)
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

    part1(blockMap)
}
