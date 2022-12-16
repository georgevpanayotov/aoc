
import net.panayotov.util.Scanners

const val TUNING_FACTOR = 4000000

infix fun Pair<Long, Long>.manhattan(other: Pair<Long, Long>) =
    Math.abs(first - other.first) + Math.abs(second - other.second)

fun Pair<Long, Long>.tuningFrequency(): Long = first * TUNING_FACTOR + second

fun findMax(beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>): Pair<Long, Long> {
    var maxX: Long? = null
    var maxY: Long? = null

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

fun findMin(beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>): Pair<Long, Long> {
    var minX: Long? = null
    var minY: Long? = null

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

fun isPossible(currentPoint: Pair<Long, Long>, beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>, beaconSet: Set<Pair<Long, Long>>): Boolean {
    if (beaconSet.contains(currentPoint)) {
        return false
    }

    if (beaconMap.containsKey(currentPoint)) {
        return false
    }

    for (entry in beaconMap.entries) {
        val sensor = entry.key
        val beacon = entry.value

        val distance = sensor manhattan beacon

        if (sensor manhattan currentPoint <= distance) {
            return false
        }
    }

    return true
}

fun part1(row: Long, beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>, beaconSet: Set<Pair<Long, Long>>) {
    val max = findMax(beaconMap)
    val min = findMin(beaconMap)
    var score = 0

    for (x in min.first..max.first) {
        if (!isPossible(Pair(x, row), beaconMap, beaconSet)) {
            score++
        }
    }

    print("$score\n")
}

fun part2(max: Long, beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>, beaconSet: Set<Pair<Long, Long>>) {
    for (x in 0..max) {
        var y = 0L
        while (y <= max) {
            val currentPoint = Pair(x, y)

            if (beaconSet.contains(currentPoint)) {
                y++
                continue
            }

            if (beaconMap.containsKey(currentPoint)) {
                y++
                continue
            }

            var possible = true
            var maxNewY: Long? = null
            for (entry in beaconMap.entries) {
                val sensor = entry.key
                val beacon = entry.value

                val distance = sensor manhattan beacon

                if (sensor manhattan currentPoint <= distance) {
                    if (y < sensor.second) {
                        val newY = 2 * sensor.second - y
                        if (maxNewY == null || newY > maxNewY) {
                            maxNewY = newY
                        }
                    }

                    possible = false
                }
            }

            if (possible) {
                print("${currentPoint.tuningFrequency()}\n")
                return
            }

            if (maxNewY != null) {
                y = maxNewY
            } else {
                y++
            }
        }
    }
}

fun part2_take2(max: Long, beaconMap: Map<Pair<Long, Long>, Pair<Long, Long>>, beaconSet: Set<Pair<Long, Long>>) {
    for (entry in beaconMap.entries) {
        val sensor = entry.key
        val beacon = entry.value

        val targetDistance = (sensor manhattan beacon) + 1

        val minX = (sensor.first - targetDistance).coerceIn(0, max)
        val maxX = (sensor.first + targetDistance).coerceIn(0, max)

        for (x in minX..maxX) {
            val bottomY = (sensor.second - targetDistance + Math.abs(x - sensor.first)).coerceIn(0, max)
            val topY = (sensor.second + targetDistance - Math.abs(x - sensor.first)).coerceIn(0, max)

            val bottom = Pair(x, bottomY)
            val top = Pair(x, topY)
            if (isPossible(bottom, beaconMap, beaconSet)) {
                print("${bottom.tuningFrequency()}\n")
                return
            }
            if (isPossible(top, beaconMap, beaconSet)) {
                print("${top.tuningFrequency()}\n")
                return
            }
        }

    }
}

fun main() {
    val row = Scanners.next().nextLong()
    val max = Scanners.next().nextLong()
    val beaconMap = mutableMapOf<Pair<Long, Long>, Pair<Long, Long>>()
    val beaconSet = mutableSetOf<Pair<Long, Long>>()

    for (line in Scanners) {
        line.next()
        line.next()
        var xStr = line.next()
        var yStr = line.next()
        xStr = xStr.substring(0, xStr.length - 1)
        yStr = yStr.substring(0, yStr.length - 1)

        val sensorX = xStr.split('=')[1].toLong()
        val sensorY = yStr.split('=')[1].toLong()

        line.next()
        line.next()
        line.next()
        line.next()

        xStr = line.next()
        yStr = line.next()
        xStr = xStr.substring(0, xStr.length - 1)

        val beacon = Pair(xStr.split('=')[1].toLong(), yStr.split('=')[1].toLong())

        beaconMap[Pair(sensorX, sensorY)] = beacon
        beaconSet.add(beacon)
    }

    part1(row, beaconMap, beaconSet)
    part2_take2(max, beaconMap, beaconSet)
}
