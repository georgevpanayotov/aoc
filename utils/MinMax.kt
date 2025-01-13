package net.panayotov.util

fun findMinMax(pairs: Iterator<Pair<Long, Long>>): Pair<Pair<Long, Long>, Pair<Long, Long>>? {
    var minX: Long? = null
    var minY: Long? = null
    var maxX: Long? = null
    var maxY: Long? = null

    for (pair in pairs) {
        if (minX == null || pair.first < minX) {
            minX = pair.first
        }
        if (minY == null || pair.second < minY) {
            minY = pair.second
        }
        if (maxX == null || pair.first > maxX) {
            maxX = pair.first
        }
        if (maxY == null || pair.second > maxY) {
            maxY = pair.second
        }
    }
    if (minX == null || minY == null || maxX == null || maxY == null) {
        return null
    }

    return Pair(Pair(minX, minY), Pair(maxX, maxY))

}

fun findMinMaxTriple(triples: Iterator<Triple<Int, Int, Int>>): Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>? {
    var minX: Int? = null
    var minY: Int? = null
    var minZ: Int? = null
    var maxX: Int? = null
    var maxY: Int? = null
    var maxZ: Int? = null

    for (triple in triples) {
        if (minX == null || triple.first < minX) {
            minX = triple.first
        }
        if (minY == null || triple.second < minY) {
            minY = triple.second
        }
        if (minZ == null || triple.third < minZ) {
            minZ = triple.third
        }
        if (maxX == null || triple.first > maxX) {
            maxX = triple.first
        }
        if (maxY == null || triple.second > maxY) {
            maxY = triple.second
        }
        if (maxZ == null || triple.third > maxZ) {
            maxZ = triple.third
        }
    }
    if (minX == null || minY == null || minZ == null || maxX == null || maxY == null || maxZ == null) {
        return null
    }

    return Pair(Triple(minX, minY, minZ), Triple(maxX, maxY, maxZ))

}

fun findMinMaxPoint(points: Iterable<Point>): Pair<Point, Point>? {
    val minX = points.map { it.x }.min()
    val minY = points.map { it.y }.min()
    val maxX = points.map { it.x }.max()
    val maxY = points.map { it.y }.max()

    return Pair(Point(minX, minY), Point(maxX, maxY))
}
