package net.panayotov.util

// Min is bottom, left and max is top, right of the rectangle.
data class Rect(val min: Point, val max: Point) {
    fun area() = (max.x - min.x + 1) * (max.y - min.y + 1)

    companion object {
        // Create a rect by 2 parallel vertical segments. Segments must be:
        // 1. Vertical
        // 2. Have the same y coordinates for both of their endpoints.
        fun fromVerticals(first: Segment, second: Segment): Rect {
            first.checkVertical()
            second.checkVertical()

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
