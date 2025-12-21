package net.panayotov.util

import java.util.ArrayDeque
import kotlin.math.abs

// A bounded shape. pre-condition: boundary must consist of only vertical and horizontal segments.
// The last point and the first point must form a segment as well and this must create an enclosed
// space.
class BoundedShape(val boundary: List<Point>) {
    // The smallest rect that contains all points in the shape.
    val boundingRect: Rect

    // Covers all of the inner space contained by the boundary. Does NOT include the boundary.
    val innerRects: List<Rect>

    init {
        val (min, max) = findMinMaxPoint(boundary)!!
        boundingRect = Rect(min, max)
        innerRects = computeInner(boundingRect, boundary)
    }

    fun area(): Long {
        var area = 0L
        for (rect in innerRects) {
            area += rect.area()
        }

        for (i in 0..<boundary.size) {
            val vector =
                if (i == boundary.size - 1) {
                    boundary[0] - boundary[i]
                } else {
                    boundary[i + 1] - boundary[i]
                }

            // Only 1 of these is non-zero
            area += abs(vector.x + vector.y)
        }

        return area
    }
}

// Computes the inner rects of the bounded shape.
private fun computeInner(boundingRect: Rect, boundary: List<Point>): List<Rect> {
    val innerRects = mutableListOf<Rect>()
    val vertical = sortedVerticalSegments(boundary)

    val (firstSegment, firstVector) = findStartingSegment(boundingRect, boundary)

    var inwardVector = firstVector
    val queue = ArrayDeque<Segment>()

    for (i in 0..<boundary.size) {
        val iFrom = (firstSegment + i) % boundary.size
        val iTo = (firstSegment + i + 1) % boundary.size

        // Arbitrarily choose just one direction. We just fill in area going east from external
        // walls. This avoid double counting.
        if (inwardVector == Direction.EAST.vector) {
            queue.addLast(Segment(boundary[iFrom], boundary[iTo]))
        }

        val iNext = (firstSegment + i + 2) % boundary.size
        val currVector = (boundary[iTo] - boundary[iFrom]).normalize()
        val nextVector = (boundary[iNext] - boundary[iTo]).normalize()

        if (currVector.rotateLeft() == nextVector) {
            inwardVector = inwardVector.rotateLeft()
        } else if (currVector.rotateRight() == nextVector) {
            inwardVector = inwardVector.rotateRight()
        }
    }

    while (queue.size > 0) {
        val segment = queue.removeFirst()
        val (rect, remainder) = findRect(boundary, vertical, segment)
        if (rect != null) {
            innerRects.add(rect)
        }

        queue.addAll(remainder)
    }

    return innerRects.toList()
}

internal fun sortedVerticalSegments(boundary: List<Point>): List<Segment> {
    val vertical = mutableListOf<Segment>()

    for (i in 0..<boundary.size) {
        val from = boundary[i]
        val to = boundary[(i + 1) % boundary.size]

        val segment = Segment(from, to)

        if (segment.vertical()) {
            vertical.add(segment)
        }
    }

    return vertical.sortedBy { it.from.x }
}

// A starting segment is one that is on the bounding rect (i.e. we can tell it's on the outside so
// we can figure out which direction is in).
private fun findStartingSegment(rect: Rect, boundary: List<Point>): Pair<Int, Point> {
    for (i in 0..<boundary.size - 1) {
        val from = boundary[i]
        val to = boundary[i + 1]

        val inwardDirection =
            if (from.x == to.x) {
                if (from.x == rect.min.x) {
                    // On the left boundary.
                    Direction.EAST
                } else if (from.x == rect.max.x) {
                    // On the right boundary.
                    Direction.WEST
                } else {
                    null
                }
            } else if (from.y == to.y) {
                if (from.y == rect.min.y) {
                    // On the bottom boundary.
                    Direction.NORTH
                } else if (from.y == rect.max.y) {
                    // On the top boundary.
                    Direction.SOUTH
                } else {
                    null
                }
            } else {
                null
            }

        if (inwardDirection != null) {
            return Pair(i, inwardDirection.vector)
        }
    }

    error("No outer segment found.")
}

// Finds a rect given the current segment in the boundary. Assumes that the segment is vertical and
// the interior of the space is to the east.
internal fun findRect(
    boundary: List<Point>,
    vertical: List<Segment>,
    boundSegment: Segment,
): Pair<Rect?, List<Segment>> {
    val inwardVector = Direction.EAST.vector
    val outerCoord = boundSegment.from.x
    var curr: Int = findSegment(vertical, outerCoord)

    val segmentVector = (boundSegment.to - boundSegment.from).normalize()

    val (prevInterference, nextInterference) = findInterference(boundary, boundSegment)
    val fromVector =
        if (prevInterference) {
            segmentVector
        } else {
            Point(0, 0)
        }
    val toVector =
        if (nextInterference) {
            -segmentVector
        } else {
            Point(0, 0)
        }

    var rectSegment =
        Segment(
            boundSegment.from + fromVector + inwardVector,
            boundSegment.to + toVector + inwardVector,
        )

    while (curr < vertical.size) {
        val currSegment = vertical[curr]
        val x = currSegment.from.x
        if (outerCoord != x) {
            val vector = abs(x - rectSegment.from.x) * inwardVector

            var rectSegment2 = Segment(rectSegment.from + vector, rectSegment.to + vector)

            val overlap = overlap(currSegment, rectSegment2)?.denormalizeTo(rectSegment2)

            if (overlap != null) {
                val remainders = mutableListOf<Segment>()

                if (overlap.from != rectSegment2.from) {
                    remainders.add(
                        Segment(
                            boundSegment.from + fromVector,
                            Point(boundSegment.to.x, overlap.from.y - segmentVector.y),
                        )
                    )
                }

                if (overlap.to != rectSegment2.to) {
                    remainders.add(
                        Segment(
                            Point(boundSegment.from.x, overlap.to.y + segmentVector.y),
                            boundSegment.to + toVector,
                        )
                    )
                }

                val rect =
                    if (rectSegment2.from.x > rectSegment.from.x) {
                        rectSegment =
                            Segment(
                                Point(rectSegment.from.x, overlap.from.y),
                                Point(rectSegment.to.x, overlap.to.y),
                            )
                        var rectSegment2 =
                            Segment(
                                Point(rectSegment2.from.x - 1, overlap.from.y),
                                Point(rectSegment2.to.x - 1, overlap.to.y),
                            )
                        Rect.fromVerticals(rectSegment, rectSegment2)
                    } else {
                        null
                    }

                return Pair(rect, remainders.toList())
            }
        }

        curr++
    }

    error("No blocker for $rectSegment")
}

// Finds a vertical segment in the list of sorted vertical segments.
private fun findSegment(vertical: List<Segment>, target: Long): Int {
    for (i in 0..<vertical.size) {
        if (vertical[i].from.x == target) {
            return i
        }
    }

    error("target = $target not found in $vertical.")
}

private fun findInterference(boundary: List<Point>, segment: Segment): Pair<Boolean, Boolean> {
    var iFrom = boundary.indexOf(segment.from)
    var iTo = boundary.indexOf(segment.to)

    if (iFrom < 0 || iTo < 0) {
        // In this case the segment is a subsegment of one that has already been adjusted for a
        // interference.
        return Pair(false, false)
    }

    var dir = (iTo - iFrom).coerceIn(-1, 1)
    if (abs(iTo - iFrom) > 1) {
        // We are looping around the end of the boundary to the start. This means that the direction
        // is the opposite of what we find here.
        dir = -dir
    }

    val iPrev = (iFrom - dir + boundary.size) % boundary.size
    val iNext = (iTo + dir) % boundary.size

    val prevInterference = (boundary[iPrev] - boundary[iFrom]).toDirection() == Direction.EAST
    val nextInterference = (boundary[iNext] - boundary[iTo]).toDirection() == Direction.EAST
    return Pair(prevInterference, nextInterference)
}

// Like in other places, we assume that these are both vertical.
private fun overlap(left: Segment, right: Segment): Segment? {
    if (left.from.x != right.from.x) {
        return null
    }

    val normalLeft = left.normalizeVertical()
    val normalRight = right.normalizeVertical()

    val leftPair = Pair(normalLeft.from.y, normalLeft.to.y)
    val fromSplit = leftPair.split(normalRight.from.y)
    val toSplit = leftPair.split(normalRight.to.y)

    return if (fromSplit == Split.BEFORE && toSplit == Split.AFTER) {
        left
    } else if (fromSplit == Split.INSIDE && toSplit == Split.INSIDE) {
        right
    } else if (fromSplit == Split.BEFORE && toSplit == Split.INSIDE) {
        Segment(normalLeft.from, normalRight.to)
    } else if (fromSplit == Split.INSIDE && toSplit == Split.AFTER) {
        Segment(normalRight.from, normalLeft.to)
    } else {
        null
    }
}

private enum class Split {
    BEFORE,
    INSIDE,
    AFTER,
}

// Determines how the other segment splits this one.
private fun Pair<Long, Long>.split(other: Long): Split {
    return if (other >= first && other <= second) {
        Split.INSIDE
    } else if (other < first) {
        Split.BEFORE
    } else {
        Split.AFTER
    }
}
