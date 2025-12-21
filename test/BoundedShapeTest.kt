import kotlin.test.assertNotNull
import net.panayotov.util.BoundedShape
import net.panayotov.util.Point
import net.panayotov.util.Segment
import net.panayotov.util.findMinMaxPoint
import net.panayotov.util.findRect
import net.panayotov.util.sortedVerticalSegments
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@ExtendWith(TestNameLogger::class)
class Day18p2Tests {
    // Tests that a segment (in this case 3,3 -> 3,6) should not take into account the prev/next
    // segments if they are turned away from the current one.
    // ##########
    // #........#
    // #........#
    // ####.....#
    // ...#.....#
    // ...#.....#
    // ####.....#
    // #........#
    // #........#
    // ##########
    @Test
    fun prevNextSegmentsNotInterfering() {
        val boundary =
            listOf(
                Point(0L, 3L),
                Point(3L, 3L),
                Point(3L, 6L),
                Point(0L, 6L),
                Point(0L, 9L),
                Point(9L, 9L),
                Point(9L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 3), Point(3, 6)))

        assertNotNull(rect)
        assertEquals(Point(4, 3), rect.min)
        assertEquals(Point(8, 6), rect.max)

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 6), Point(3, 3)))

        assertNotNull(reverseRect)
        assertEquals(Point(4, 3), reverseRect.min)
        assertEquals(Point(8, 6), reverseRect.max)
    }

    // Tests that a segment (in this case 3,3 -> 3,6) should not take into account the next
    // segments if it is turned toward the current one.
    // .....#####
    // .....#...#
    // .....#...#
    // ...###...#
    // ...#.....#
    // ...#.....#
    // ####.....#
    // #........#
    // #........#
    // ##########
    @Test
    fun testNextSegmentInterfering() {
        val boundary =
            listOf(
                Point(0L, 3L),
                Point(3L, 3L),
                Point(3L, 6L),
                Point(5L, 6L),
                Point(5L, 9L),
                Point(9L, 9L),
                Point(9L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 3), Point(3, 6)))

        assertNotNull(rect)
        assertEquals(Point(4, 3), rect.min)
        assertEquals(Point(8, 5), rect.max)

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 6), Point(3, 3)))

        assertNotNull(reverseRect)
        assertEquals(Point(4, 3), reverseRect.min)
        assertEquals(Point(8, 5), reverseRect.max)
    }

    // Tests that a segment (in this case 3,3 -> 3,6) should take into account the prev
    // segments if it is turned toward the current one.
    // ##########
    // #........#
    // #........#
    // ####.....#
    // ...#.....#
    // ...#.....#
    // ...###...#
    // .....#...#
    // .....#...#
    // .....#####
    @Test
    fun testPrevSegmentInterfering() {
        val boundary =
            listOf(
                Point(5L, 3L),
                Point(3L, 3L),
                Point(3L, 6L),
                Point(0L, 6L),
                Point(0L, 9L),
                Point(9L, 9L),
                Point(9L, 0L),
                Point(5L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 3), Point(3, 6)))

        assertNotNull(rect)
        assertEquals(Point(4, 4), rect.min)
        assertEquals(Point(8, 6), rect.max)

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 6), Point(3, 3)))

        assertNotNull(reverseRect)
        assertEquals(Point(4, 4), reverseRect.min)
        assertEquals(Point(8, 6), reverseRect.max)
    }

    // Tests that a segment (in this case 3,3 -> 3,6) should take into account the prev/next
    // segments if they are turned toward from the current one.
    // .....#####
    // .....#...#
    // .....#...#
    // ...###...#
    // ...#.....#
    // ...#.....#
    // ...###...#
    // .....#...#
    // .....#...#
    // .....#####
    @Test
    fun prevNextSegmentsBothInterfering() {
        val boundary =
            listOf(
                Point(5L, 3L),
                Point(3L, 3L),
                Point(3L, 6L),
                Point(5L, 6L),
                Point(5L, 9L),
                Point(9L, 9L),
                Point(9L, 0L),
                Point(5L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 3), Point(3, 6)))

        assertNotNull(rect)
        assertEquals(Point(4, 4), rect.min)
        assertEquals(Point(8, 5), rect.max)

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 6), Point(3, 3)))

        assertNotNull(reverseRect)
        assertEquals(Point(4, 4), reverseRect.min)
        assertEquals(Point(8, 5), reverseRect.max)
    }

    // Testing when the segment that stops us is entirely within the current segment (3, 2) -> (3,
    // 7).
    // ##########
    // #........#
    // ####.....#
    // ...#...###
    // ...#...#..
    // ...#...#..
    // ...#...###
    // ####.....#
    // #........#
    // ##########
    @Test
    fun overlapInterior() {
        val boundary =
            listOf(
                Point(0L, 2L),
                Point(3L, 2L),
                Point(3L, 7L),
                Point(0L, 7L),
                Point(0L, 9L),
                Point(9L, 9L),
                Point(9L, 6L),
                Point(7L, 6L),
                Point(7L, 3L),
                Point(9L, 3L),
                Point(9L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 2), Point(3, 7)))

        assertNotNull(rect)
        assertEquals(Point(4, 3), rect.min)
        assertEquals(Point(6, 6), rect.max)

        assertEquals(2, remainder.size)
        assertEquals(Segment(Point(3, 2), Point(3, 2)), remainder[0])
        assertEquals(Segment(Point(3, 7), Point(3, 7)), remainder[1])

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 7), Point(3, 2)))

        assertNotNull(reverseRect)

        assertEquals(Point(4, 3), reverseRect.min)
        assertEquals(Point(6, 6), reverseRect.max)

        assertEquals(2, reverseRemainder.size)
        assertEquals(Segment(Point(3, 7), Point(3, 7)), reverseRemainder[0])
        assertEquals(Segment(Point(3, 2), Point(3, 2)), reverseRemainder[1])
    }

    // Testing when the segment that stops us only overlaps the start of the current segment (3, 2)
    // -> (3, 7).
    // ##########
    // #........#
    // ####.....#
    // ...#...###
    // ...#...#..
    // ...#...#..
    // ...#...#..
    // ####...#..
    // #......#..
    // ########..
    @Test
    fun overlapEnd() {
        val boundary =
            listOf(
                Point(0L, 2L),
                Point(3L, 2L),
                Point(3L, 7L),
                Point(0L, 7L),
                Point(0L, 9L),
                Point(9L, 9L),
                Point(9L, 6L),
                Point(7L, 6L),
                Point(7L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 2), Point(3, 7)))

        assertNotNull(rect)
        assertEquals(Point(4, 2), rect.min)
        assertEquals(Point(6, 6), rect.max)

        assertEquals(1, remainder.size)
        assertEquals(Segment(Point(3, 7), Point(3, 7)), remainder[0])

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 7), Point(3, 2)))

        assertNotNull(reverseRect)

        assertEquals(Point(4, 2), reverseRect.min)
        assertEquals(Point(6, 6), reverseRect.max)

        assertEquals(1, reverseRemainder.size)
        assertEquals(Segment(Point(3, 7), Point(3, 7)), reverseRemainder[0])
    }

    // Testing when the segment that stops us only overlaps the end of the current segment (3, 2) ->
    // (3, 7).
    // ########..
    // #......#..
    // ####...#..
    // ...#...#..
    // ...#...#..
    // ...#...#..
    // ...#...###
    // ####.....#
    // #........#
    // ##########
    @Test
    fun overlapBegin() {
        val boundary =
            listOf(
                Point(0L, 2L),
                Point(3L, 2L),
                Point(3L, 7L),
                Point(0L, 7L),
                Point(0L, 9L),
                Point(7L, 9L),
                Point(7L, 3L),
                Point(9L, 3L),
                Point(9L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(3, 2), Point(3, 7)))

        assertNotNull(rect)
        assertEquals(Point(4, 3), rect.min)
        assertEquals(Point(6, 7), rect.max)

        assertEquals(1, remainder.size)
        assertEquals(Segment(Point(3, 2), Point(3, 2)), remainder[0])

        val (reverseRect, reverseRemainder) =
            findRect(boundary, vertical, Segment(Point(3, 7), Point(3, 2)))

        assertNotNull(reverseRect)

        assertEquals(Point(4, 3), reverseRect.min)
        assertEquals(Point(6, 7), reverseRect.max)

        assertEquals(1, reverseRemainder.size)
        assertEquals(Segment(Point(3, 2), Point(3, 2)), reverseRemainder[0])
    }

    // Testing when a subsegment (of height 1) is issued.
    // ##########
    // #........#
    // #........#
    // #........#
    // #........#
    // #........#
    // #........#
    // #........#
    // #........#
    // ##########
    @Test
    fun singleRow() {
        val boundary = listOf(Point(0L, 9L), Point(9L, 9L), Point(9L, 0L), Point(0L, 0L))

        val vertical = sortedVerticalSegments(boundary)

        val (rect, remainder) = findRect(boundary, vertical, Segment(Point(0, 3), Point(0, 3)))

        assertNotNull(rect)
        assertEquals(Point(1, 3), rect.min)
        assertEquals(Point(8, 3), rect.max)

        assertEquals(0, remainder.size)
    }

    // ##########
    // #........#
    // #........#
    // ##########
    //    ##
    //    ##
    // ##########
    // #........#
    // #........#
    // ##########
    @Test
    fun weirdShape() {
        val boundary =
            listOf(
                Point(0L, 0L),
                Point(0L, 3L),
                Point(3L, 3L),
                Point(3L, 6L),
                Point(0L, 6L),
                Point(0L, 9L),
                Point(9L, 9L),
                Point(9L, 6L),
                Point(4L, 6L),
                Point(4L, 3L),
                Point(9L, 3L),
                Point(9L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (min, max) = findMinMaxPoint(boundary)!!

        assertEquals(84L, BoundedShape(boundary).area())
    }

    //  ....................
    //  ....................
    //  ....................
    //  ......###########...
    //  ......#.........#...
    //  ..#####.........#...
    //  ..#.............#...
    //  ###.............###.
    //  #.................##
    //  #..................#
    //  ####################
    @Test
    fun startVertical() {
        // NOTE: This was the test that I needed to finally find the issue blocking me.
        val boundary =
            listOf(
                Point(0L, 3L),
                Point(2L, 3L),
                Point(2L, 5L),
                Point(6L, 5L),
                Point(6L, 7L),
                Point(16L, 7L),
                Point(16L, 3L),
                Point(18L, 3L),
                Point(18L, 2L),
                Point(19L, 2L),
                Point(19L, 0L),
                Point(0L, 0L),
            )

        val vertical = sortedVerticalSegments(boundary)

        val (min, max) = findMinMaxPoint(boundary)!!

        assertEquals(131L, BoundedShape(boundary).area())
    }
}

class TestNameLogger : BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        println("==== Staring TestCase: ${context.displayName} ====")
    }

    override fun afterEach(context: ExtensionContext) {
        println("==== Ending TestCase: ${context.displayName} ====")
    }
}
