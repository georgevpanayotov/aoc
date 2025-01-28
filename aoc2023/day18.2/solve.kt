import java.util.ArrayDeque
import java.util.Scanner
import kotlin.math.abs
import net.panayotov.util.Direction
import net.panayotov.util.Point
import net.panayotov.util.Scanners
import net.panayotov.util.findMinMaxPoint
import net.panayotov.util.times
import net.panayotov.util.toDirection

data class Instruction(val direction: Direction, val length: Int) {
    fun totalVector() = length.toLong() * direction.vector

    override fun toString() = "$direction $length"
}

fun parseDirection(dirChr: Char) =
    when (dirChr) {
        '3' -> Direction.NORTH
        '0' -> Direction.EAST
        '1' -> Direction.SOUTH
        '2' -> Direction.WEST
        else -> error("Unknown direction $dirChr")
    }

fun hexChar(hexChr: Char) =
    when (hexChr.lowercaseChar()) {
        'a' -> 10
        'b' -> 11
        'c' -> 12
        'd' -> 13
        'e' -> 14
        'f' -> 15
        else ->
            if (hexChr >= '0' && hexChr <= '9') {
                (hexChr - '0').toInt()
            } else {
                error("Unknown hex digit $hexChr")
            }
    }

fun parseHex(hexStr: String): Int {
    var place = 1
    var hex = 0

    for (i in hexStr.length - 1 downTo 0) {
        hex += hexChar(hexStr[i]) * place
        place *= 16
    }

    return hex
}

fun enumerateBoundary(instructions: List<Instruction>): List<Point> {
    var point = Point(0L, 0L)
    val points = mutableListOf<Point>()

    for (instruction in instructions) {
        point = point + instruction.totalVector()
        points.add(point)
    }

    return points.toList()
}

fun checkVertical(segment: Segment) {
    if (!segment.vertical()) {
        error("$segment must be vertical")
    }
}

// Min is bottom, left and max is top, right of the bounding rectangle.
data class Rect(val min: Point, val max: Point) {
    fun area() = (max.x - min.x + 1) * (max.y - min.y + 1)

    companion object {
        // Create a rect by 2 parallel vertical segments. Segments must be:
        // 1. Vertical
        // 2. Have the same y coordinates for both of their endpoints.
        fun fromVerticals(first: Segment, second: Segment): Rect {
            checkVertical(first)
            checkVertical(second)

            val normalFirst = first.normalizeVertical()
            val normalSecond = second.normalizeVertical()

            if (
                normalFirst.from.y != normalSecond.from.y || normalFirst.to.y != normalSecond.to.y
            ) {
                error("Must form a rectangle. $first $second")
            }

            val points =
                listOf(first.from, first.to, second.from, second.to).sortedWith { lhs, rhs ->
                    if (lhs.x == rhs.x) {
                        (lhs.y - rhs.y).toInt()
                    } else {
                        (lhs.x - rhs.x).toInt()
                    }
                }

            // In this sorting scheme the first will be bottom left. The second, top right.
            return Rect(points[0], points[3])
        }
    }
}

data class Segment(val from: Point, val to: Point) {
    fun vertical() = from.x == to.x

    fun horizontal() = from.y == to.y

    // Makes it so that this (vertical) segment is pointing up.
    fun normalizeVertical(): Segment {
        if (!vertical()) {
            error("Must be vertical")
        }

        return if (from.y < to.y) {
            this
        } else {
            Segment(to, from)
        }
    }

    // Returns a segment that takes the same space as `this` and is in the same direction as
    // `other`. `other` and `this` must be oriented in the same or opposite direction.
    fun denormalizeTo(other: Segment): Segment {
        if (from == to) {
            return this
        }

        val vector = (to - from).normalize()
        val otherVector = (other.to - other.from).normalize()

        return if (vector == otherVector) {
            this
        } else if (vector == -otherVector) {
            Segment(to, from)
        } else {
            error("$this and $other must be oriented in the same direction.")
        }
    }
}

fun findStartingSegment(rect: Rect, boundary: List<Point>): Pair<Int, Point> {
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

fun sortedVerticalSegments(boundary: List<Point>): List<Segment> {
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

enum class Split {
    BEFORE,
    INSIDE,
    AFTER,
}

fun Pair<Long, Long>.split(other: Long): Split {
    return if (other >= first && other <= second) {
        Split.INSIDE
    } else if (other < first) {
        Split.BEFORE
    } else {
        Split.AFTER
    }
}

// Like in other places, we assume that these are both vertical.
fun overlap(left: Segment, right: Segment): Segment? {
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

fun findSegment(vertical: List<Segment>, target: Long): Int {
    for (i in 0..<vertical.size) {
        if (vertical[i].from.x == target) {
            return i
        }
    }

    error("target = $target not found in $vertical.")
}

fun findInterference(boundary: List<Point>, segment: Segment): Pair<Boolean, Boolean> {
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

// Finds a rect given the current segment in the boundary. Assumes that the segment is vertical and
// the interior of the space is to the east.
fun findRect(
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

fun computeArea(boundingRect: Rect, boundary: List<Point>): Long {
    var area = 0L
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
            area += rect.area()
        }

        queue.addAll(remainder)
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

fun parseP1(line: Scanner): Instruction {
    val dirStr = line.next()
    val direction =
        when (dirStr) {
            "U" -> Direction.NORTH
            "R" -> Direction.EAST
            "D" -> Direction.SOUTH
            "L" -> Direction.WEST
            else -> error("Unknown direction $dirStr")
        }
    val length = line.next().toInt()
    val colorLine = line.next()
    val color = colorLine.substring(2, colorLine.length - 1)

    return Instruction(direction, length)
}

fun parseP2(line: Scanner): Instruction {
    line.next()
    line.next()
    val colorLine = line.next()
    val color = colorLine.substring(2, colorLine.length - 1)
    val direction = parseDirection(color[color.length - 1])
    val length = parseHex(color.substring(0, color.length - 1))
    return Instruction(direction, length)
}

fun main() {
    val instructions = mutableListOf<Instruction>()

    for (line in Scanners) {
        instructions.add(parseP2(line))
    }

    val boundary = enumerateBoundary(instructions)
    val (min, max) = findMinMaxPoint(boundary)!!

    val score = computeArea(Rect(min, max), boundary)
    println("Answer = $score")
}
