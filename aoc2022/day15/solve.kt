
import net.panayotov.util.Scanners

infix fun Pair<Int, Int>.manhattan(other: Pair<Int, Int>) =
    Math.abs(first - other.first) + Math.abs(second - other.second)

fun findMax(beaconMap: Map<Pair<Int, Int>, Pair<Int, Int>>): Pair<Int, Int> {
    var maxX: Int? = null
    var maxY: Int? = null

    for (entry in beaconMap.entries) {
        val sensor = entry.key
        val beacon = entry.value

        val distance = sensor manhattan beacon
        val (localX, localY) = Pair(sensor.first + distance, sensor.second + distance)

        if (maxX == null || localX > maxX) {
            maxX = localX
        }

        if (maxY == null || localY > maxY) {
            maxY = localY
        }
    }

    return Pair(maxX!!, maxY!!)
}

fun findMin(beaconMap: Map<Pair<Int, Int>, Pair<Int, Int>>): Pair<Int, Int> {
    var minX: Int? = null
    var minY: Int? = null

    for (entry in beaconMap.entries) {
        val sensor = entry.key
        val beacon = entry.value

        val distance = sensor manhattan beacon
        val (localX, localY) = Pair(sensor.first - distance, sensor.second - distance)

        if (minX == null || localX < minX) {
            minX = localX
        }

        if (minY == null || localY < minY) {
            minY = localY
        }
    }

    return Pair(minX!!, minY!!)
}

fun part1(row: Int, beaconMap: Map<Pair<Int, Int>, Pair<Int, Int>>, beaconSet: Set<Pair<Int, Int>>) {
    val max = findMax(beaconMap)
    val min = findMin(beaconMap)
    var score = 0

    for (x in min.first..max.first) {
        val currentPoint = Pair(x, row)

        if (beaconSet.contains(currentPoint)) {
            continue
        }

        if (beaconMap.containsKey(currentPoint)) {
            continue
        }

        for (entry in beaconMap.entries) {
            val sensor = entry.key
            val beacon = entry.value

            val distance = sensor manhattan beacon

            if (sensor manhattan currentPoint <= distance) {
                score++
                break
            }
        }

    }

    print("$score\n")
}

fun main() {
    val row = Scanners.next().nextInt()
    val beaconMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    val beaconSet = mutableSetOf<Pair<Int, Int>>()

    for (line in Scanners) {
        line.next()
        line.next()
        var xStr = line.next()
        var yStr = line.next()
        xStr = xStr.substring(0, xStr.length - 1)
        yStr = yStr.substring(0, yStr.length - 1)

        val sensorX = xStr.split('=')[1].toInt()
        val sensorY = yStr.split('=')[1].toInt()

        line.next()
        line.next()
        line.next()
        line.next()

        xStr = line.next()
        yStr = line.next()
        xStr = xStr.substring(0, xStr.length - 1)

        val beacon = Pair(xStr.split('=')[1].toInt(), yStr.split('=')[1].toInt())

        beaconMap[Pair(sensorX, sensorY)] = beacon
        beaconSet.add(beacon)
    }

    part1(row, beaconMap, beaconSet)
}
