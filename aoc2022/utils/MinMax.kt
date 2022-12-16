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
