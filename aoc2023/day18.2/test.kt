import java.util.Scanner
import kotlin.test.assertNotNull
import net.panayotov.util.Point
import net.panayotov.util.findMinMaxPoint
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

    @Test
    fun p1SampleArea() {
        val input =
            listOf(
                "R 6 (#70c710)",
                "D 5 (#0dc571)",
                "L 2 (#5713f0)",
                "D 2 (#d2c081)",
                "R 2 (#59c680)",
                "D 2 (#411b91)",
                "L 5 (#8ceee2)",
                "U 2 (#caa173)",
                "L 1 (#1b58a2)",
                "U 2 (#caa171)",
                "R 2 (#7807d2)",
                "U 3 (#a77fa3)",
                "L 2 (#015232)",
                "U 2 (#7a21e3)",
            )

        val instructions = input.map(::Scanner).map(::parseP1)
        var boundary = enumerateBoundary(instructions)
        val (min, max) = findMinMaxPoint(boundary)!!
        boundary = boundary.map { it - min }

        assertEquals(62, computeArea(Rect(Point(0, 0), max - min), boundary))
    }

    @Test
    fun p1Area() {
        val input =
            listOf(
                "L 4 (#6c82d0)",
                "U 2 (#801271)",
                "L 5 (#2fcea0)",
                "U 11 (#801273)",
                "L 3 (#5b0810)",
                "D 8 (#0c0ea1)",
                "L 2 (#25bec0)",
                "D 4 (#639053)",
                "L 5 (#28db50)",
                "D 4 (#396331)",
                "L 6 (#936690)",
                "U 4 (#396333)",
                "L 5 (#4f5be0)",
                "U 7 (#639051)",
                "L 5 (#799080)",
                "U 5 (#0c0ea3)",
                "L 5 (#69d6e0)",
                "U 2 (#107d21)",
                "L 4 (#82f122)",
                "U 7 (#551401)",
                "L 7 (#82f120)",
                "U 3 (#43a1e1)",
                "L 8 (#212420)",
                "U 2 (#5fcce1)",
                "L 3 (#5d8690)",
                "U 5 (#8b76e1)",
                "L 9 (#3a3420)",
                "U 5 (#1b5501)",
                "L 9 (#191912)",
                "U 3 (#482111)",
                "L 2 (#5e1be0)",
                "U 4 (#8b7801)",
                "L 6 (#53b250)",
                "U 6 (#3b0821)",
                "L 4 (#178532)",
                "U 7 (#53fda1)",
                "L 3 (#2875e2)",
                "U 8 (#1645b1)",
                "L 2 (#71d322)",
                "U 7 (#60bdd1)",
                "L 2 (#191910)",
                "U 2 (#553ad1)",
                "L 7 (#94af32)",
                "U 3 (#116281)",
                "L 3 (#94af30)",
                "U 10 (#4b7c21)",
                "R 5 (#3f8440)",
                "U 4 (#356ea3)",
                "R 6 (#13df80)",
                "U 7 (#46dbe3)",
                "L 4 (#13df82)",
                "U 11 (#507db3)",
                "R 4 (#657e80)",
                "U 9 (#26fa81)",
                "R 7 (#4b28a0)",
                "U 3 (#8e7e81)",
                "R 3 (#2e8b60)",
                "U 6 (#174f31)",
                "R 6 (#0e9910)",
                "D 3 (#9b6b71)",
                "R 11 (#123ec0)",
                "D 5 (#5006d3)",
                "R 6 (#2c2310)",
                "D 6 (#3cfd83)",
                "R 4 (#7e56f0)",
                "D 3 (#58e933)",
                "L 4 (#413ad0)",
                "D 4 (#786ae3)",
                "L 6 (#2b21e0)",
                "D 3 (#156533)",
                "R 6 (#33e5e2)",
                "D 5 (#1e49e3)",
                "R 4 (#3eb5d2)",
                "D 3 (#25e9d3)",
                "L 4 (#29a690)",
                "D 5 (#78ad33)",
                "R 3 (#3d3340)",
                "D 3 (#2895d3)",
                "R 6 (#0bc1e0)",
                "U 3 (#0db143)",
                "R 8 (#450ca0)",
                "U 2 (#07a253)",
                "R 3 (#4d2b70)",
                "U 7 (#30ac83)",
                "L 6 (#5376e2)",
                "U 3 (#7d64a3)",
                "L 5 (#5376e0)",
                "U 4 (#00df63)",
                "R 9 (#409ae0)",
                "U 6 (#33e5b1)",
                "R 2 (#5fcc30)",
                "U 3 (#4d1741)",
                "R 7 (#2f7040)",
                "D 8 (#4d1743)",
                "R 4 (#54e460)",
                "U 6 (#5897b1)",
                "R 5 (#195bc0)",
                "U 2 (#8c7d63)",
                "R 4 (#487c00)",
                "U 3 (#277ec3)",
                "R 6 (#3c0aa2)",
                "U 4 (#638d53)",
                "R 4 (#3c0aa0)",
                "U 3 (#256eb3)",
                "L 4 (#1b2d80)",
                "U 8 (#42bca3)",
                "L 4 (#2fba30)",
                "D 8 (#4c1de3)",
                "L 6 (#4e6452)",
                "U 6 (#1cd493)",
                "L 4 (#4e6450)",
                "U 4 (#530793)",
                "L 7 (#68d220)",
                "U 2 (#0a4943)",
                "L 5 (#252560)",
                "U 3 (#03b331)",
                "L 3 (#8ead00)",
                "U 7 (#5cfb81)",
                "R 7 (#058540)",
                "U 3 (#2b5441)",
                "R 10 (#1f1790)",
                "U 3 (#128a41)",
                "R 8 (#0135f2)",
                "U 5 (#3f62d1)",
                "R 8 (#1843a2)",
                "U 3 (#25f2d1)",
                "R 4 (#5e5c90)",
                "U 6 (#3a5121)",
                "R 5 (#5e5a72)",
                "U 3 (#5d3511)",
                "R 6 (#5e5a70)",
                "U 5 (#5d9301)",
                "R 6 (#5e5c92)",
                "D 10 (#020841)",
                "R 6 (#1843a0)",
                "D 6 (#1680e1)",
                "R 6 (#0135f0)",
                "U 7 (#2174c1)",
                "R 4 (#5f43d2)",
                "U 2 (#94a1f1)",
                "L 4 (#540602)",
                "U 7 (#9b09f1)",
                "R 4 (#194b70)",
                "D 3 (#6ef6b3)",
                "R 3 (#678da0)",
                "D 8 (#337253)",
                "R 5 (#6ef800)",
                "D 8 (#060983)",
                "L 5 (#210ef0)",
                "D 4 (#2e3a73)",
                "L 3 (#1b9dd0)",
                "D 6 (#32cf93)",
                "L 10 (#3ad300)",
                "D 4 (#7e0ca1)",
                "R 3 (#3467d0)",
                "D 3 (#692a01)",
                "L 10 (#4fb770)",
                "D 5 (#2194b3)",
                "R 10 (#7c8620)",
                "D 4 (#64ec03)",
                "R 2 (#6320a0)",
                "D 2 (#500d23)",
                "R 6 (#6a9e42)",
                "D 3 (#7451a1)",
                "R 3 (#0817f2)",
                "U 7 (#7451a3)",
                "L 6 (#6cf092)",
                "U 3 (#10a8d3)",
                "R 6 (#2a6cf0)",
                "U 7 (#48fc23)",
                "R 4 (#339252)",
                "D 6 (#1da1b3)",
                "R 8 (#6135a2)",
                "D 5 (#1da1b1)",
                "R 7 (#14aba2)",
                "D 7 (#6948f3)",
                "R 4 (#3feba2)",
                "D 4 (#0d58f3)",
                "R 3 (#1b9dd2)",
                "D 5 (#1f8e93)",
                "R 7 (#39d370)",
                "U 6 (#2a2a03)",
                "R 6 (#2cda70)",
                "U 8 (#3fb023)",
                "L 6 (#43cc00)",
                "U 5 (#47fcb3)",
                "R 9 (#553d50)",
                "D 3 (#33f2d1)",
                "R 8 (#9822d0)",
                "U 8 (#33f2d3)",
                "R 7 (#34c190)",
                "D 8 (#505803)",
                "R 4 (#618d80)",
                "D 8 (#3d6eb3)",
                "L 8 (#153d00)",
                "D 7 (#2434a3)",
                "L 3 (#28df80)",
                "U 7 (#1fe683)",
                "L 8 (#4801e0)",
                "D 8 (#759953)",
                "R 3 (#1d6900)",
                "D 6 (#1ed353)",
                "R 7 (#2de280)",
                "D 5 (#371e03)",
                "R 10 (#85fe70)",
                "D 5 (#02a001)",
                "L 6 (#36eaf0)",
                "D 2 (#705bc1)",
                "L 10 (#1cbfc0)",
                "D 6 (#350221)",
                "R 9 (#495820)",
                "D 4 (#3cd261)",
                "R 7 (#48fe70)",
                "D 4 (#362461)",
                "R 9 (#925692)",
                "D 4 (#2c0e51)",
                "R 2 (#552b70)",
                "D 8 (#2d26a1)",
                "R 8 (#3c05a0)",
                "D 4 (#6efb41)",
                "R 8 (#3c05a2)",
                "D 7 (#24e861)",
                "L 5 (#5f0f20)",
                "D 2 (#1516c1)",
                "L 4 (#6f3420)",
                "D 5 (#040d01)",
                "L 5 (#44e6e0)",
                "D 2 (#3d7211)",
                "L 5 (#0a7612)",
                "D 5 (#3e93e1)",
                "R 2 (#29c122)",
                "D 3 (#2cd391)",
                "R 3 (#51d132)",
                "U 5 (#2cd393)",
                "R 10 (#2e12a2)",
                "D 5 (#6ba761)",
                "R 4 (#0fa1c0)",
                "D 5 (#50ac51)",
                "R 3 (#9123c0)",
                "U 3 (#61bfd1)",
                "R 9 (#337bf0)",
                "U 4 (#9b9743)",
                "R 6 (#516cc0)",
                "U 3 (#0d6c43)",
                "R 5 (#2c1560)",
                "U 3 (#4e6031)",
                "R 6 (#5f5732)",
                "U 9 (#1406d1)",
                "L 6 (#5f5730)",
                "U 7 (#469c81)",
                "R 5 (#0309a0)",
                "U 4 (#61bfd3)",
                "R 3 (#1f5270)",
                "U 7 (#5ce901)",
                "R 3 (#0130c0)",
                "U 3 (#1d4c21)",
                "R 7 (#47d430)",
                "U 6 (#642ca1)",
                "R 3 (#352de0)",
                "U 2 (#538061)",
                "R 5 (#3df912)",
                "D 6 (#422391)",
                "R 7 (#67a1f2)",
                "D 9 (#3147b1)",
                "R 2 (#30bde2)",
                "D 4 (#36f231)",
                "L 5 (#310a40)",
                "D 5 (#359bc1)",
                "L 4 (#6b1ed0)",
                "U 5 (#557a21)",
                "L 3 (#8645c0)",
                "D 2 (#50dca1)",
                "L 3 (#1a1372)",
                "D 7 (#2c8523)",
                "R 4 (#4d1232)",
                "D 3 (#3e0691)",
                "L 4 (#4fbd02)",
                "D 5 (#3e0693)",
                "L 5 (#06bf22)",
                "U 5 (#2c8521)",
                "L 4 (#64cd12)",
                "D 6 (#2b9071)",
                "L 2 (#4672e2)",
                "D 3 (#41fab3)",
                "R 6 (#6ff982)",
                "D 10 (#41fab1)",
                "R 6 (#0eee42)",
                "U 10 (#19a091)",
                "R 3 (#2d9a72)",
                "D 3 (#2e8801)",
                "R 5 (#6242e2)",
                "U 7 (#2e8803)",
                "R 2 (#0f8592)",
                "U 8 (#0f8d81)",
                "R 4 (#914592)",
                "D 6 (#3f2b91)",
                "R 6 (#4ebca2)",
                "U 5 (#6ac2e1)",
                "R 2 (#0cab42)",
                "U 4 (#54fb91)",
                "R 5 (#0cab40)",
                "U 4 (#4b28c1)",
                "L 5 (#414770)",
                "U 6 (#3e81d1)",
                "R 3 (#1f0792)",
                "U 2 (#1be271)",
                "R 4 (#1f0790)",
                "U 5 (#536a61)",
                "R 10 (#2bf8b2)",
                "U 4 (#1e7651)",
                "L 10 (#4f7292)",
                "U 4 (#49b9a1)",
                "R 6 (#3b8772)",
                "U 3 (#177db3)",
                "L 3 (#382f92)",
                "U 6 (#379263)",
                "L 6 (#63f042)",
                "U 5 (#268a23)",
                "L 2 (#939320)",
                "U 5 (#5fe043)",
                "R 5 (#0afc00)",
                "U 4 (#056de3)",
                "L 5 (#9e8f22)",
                "U 5 (#2a9f03)",
                "L 6 (#1e5a92)",
                "D 6 (#05a653)",
                "L 7 (#78da72)",
                "D 4 (#2a2ff1)",
                "L 2 (#04cbd2)",
                "D 6 (#265f01)",
                "L 9 (#0d6500)",
                "U 6 (#3a7761)",
                "L 6 (#6c6cc2)",
                "U 10 (#438ae1)",
                "L 5 (#6c6cc0)",
                "U 9 (#20c111)",
                "R 7 (#0d6502)",
                "U 7 (#1bdb61)",
                "R 7 (#2bac02)",
                "U 2 (#1fa311)",
                "R 6 (#3d5e80)",
                "U 2 (#8312b1)",
                "R 3 (#4db520)",
                "U 6 (#0dafb1)",
                "R 4 (#5126b0)",
                "D 3 (#3f5181)",
                "R 6 (#647410)",
                "D 9 (#3f5183)",
                "R 4 (#13ea30)",
                "U 9 (#0dafb3)",
                "R 6 (#789ab0)",
                "U 3 (#03bf41)",
                "R 3 (#4d8c10)",
                "U 9 (#08ff31)",
                "L 6 (#2fb312)",
                "U 6 (#8f02c1)",
                "L 7 (#2fb310)",
                "D 6 (#0d6881)",
                "L 10 (#414772)",
                "U 5 (#55ae31)",
                "L 3 (#43acc2)",
                "D 8 (#680c41)",
                "L 3 (#6c2212)",
                "D 7 (#680c43)",
                "L 4 (#191dd2)",
                "U 4 (#350333)",
                "L 5 (#120bc2)",
                "U 3 (#200061)",
                "R 5 (#2481f2)",
                "U 5 (#5d6201)",
                "L 4 (#67a742)",
                "U 3 (#7d6263)",
                "L 3 (#0a86e2)",
                "U 2 (#350331)",
                "L 8 (#7fc862)",
                "U 7 (#123121)",
                "R 4 (#026d32)",
                "U 9 (#9b27e1)",
                "R 7 (#04de82)",
                "D 4 (#18dbe3)",
                "R 5 (#6da282)",
                "D 3 (#7d5e43)",
                "R 8 (#00e292)",
                "D 7 (#171ee3)",
                "R 3 (#112822)",
                "U 5 (#4a9403)",
                "R 4 (#4d41c2)",
                "U 9 (#9369a3)",
                "R 7 (#571612)",
                "U 7 (#9369a1)",
                "L 8 (#0e6e82)",
                "U 4 (#1b3921)",
                "L 3 (#3b9ec2)",
                "U 5 (#1b3923)",
                "R 5 (#4c8902)",
                "U 8 (#2f0eb3)",
                "R 3 (#55e712)",
                "D 4 (#566ff3)",
                "R 4 (#27fc52)",
                "D 10 (#3102e3)",
                "R 4 (#391d52)",
                "D 5 (#6414f3)",
                "R 4 (#2fb330)",
                "D 3 (#44a733)",
                "L 4 (#2fb332)",
                "D 9 (#208aa3)",
                "R 4 (#2a4ca0)",
                "D 3 (#3d5943)",
                "L 4 (#3148d0)",
                "D 4 (#477c63)",
                "R 7 (#72d2a0)",
                "U 5 (#0e36c3)",
                "R 5 (#1bc310)",
                "U 3 (#3d7b11)",
                "L 5 (#4804c0)",
                "U 6 (#3d7b13)",
                "R 4 (#4b1df0)",
                "D 3 (#0e36c1)",
                "R 7 (#097f70)",
                "D 3 (#1646e3)",
                "R 3 (#0ed3b0)",
                "D 4 (#5f4ae3)",
                "L 6 (#0ed3b2)",
                "D 4 (#4f7ad3)",
                "L 5 (#6b2180)",
                "D 4 (#4e7b63)",
                "R 5 (#6b1582)",
                "D 5 (#2e16b3)",
                "R 6 (#6b1580)",
                "D 5 (#70ce73)",
                "R 2 (#5dc652)",
                "D 3 (#04a731)",
                "R 11 (#588482)",
                "U 6 (#2d2d81)",
                "R 3 (#55b632)",
                "U 6 (#369811)",
                "L 6 (#730eb0)",
                "D 8 (#1ef481)",
                "L 4 (#730eb2)",
                "U 8 (#53f841)",
                "L 5 (#37a662)",
                "U 4 (#407fb1)",
                "R 8 (#3d9952)",
                "U 3 (#78fa41)",
                "R 4 (#36d132)",
                "U 4 (#0b8861)",
                "R 3 (#4514f2)",
                "U 5 (#225bb1)",
                "R 4 (#0df1e2)",
                "D 3 (#3708b1)",
                "R 5 (#1a9ab2)",
                "D 7 (#6748c3)",
                "R 4 (#615ef2)",
                "D 5 (#6748c1)",
                "R 3 (#41af52)",
                "D 8 (#648171)",
                "R 8 (#142ed0)",
                "D 8 (#58af31)",
                "R 6 (#814d60)",
                "D 11 (#314a31)",
                "L 2 (#361ea0)",
                "D 7 (#45e2a1)",
                "L 6 (#562fc2)",
                "D 5 (#01ab91)",
                "L 6 (#090eb2)",
                "D 2 (#0f7491)",
                "L 4 (#28c7b2)",
                "D 9 (#8f5f01)",
                "L 6 (#3c6be2)",
                "D 6 (#2e00c3)",
                "L 3 (#4723a2)",
                "U 8 (#70d2d3)",
                "L 5 (#316352)",
                "D 8 (#400d81)",
                "L 5 (#5586b2)",
                "U 3 (#0c00e1)",
                "L 6 (#4c6a12)",
                "U 7 (#693f73)",
                "L 3 (#31a5a2)",
                "U 2 (#11b8c3)",
                "L 6 (#3e6642)",
                "U 3 (#11b8c1)",
                "L 4 (#480ee2)",
                "D 8 (#1865f3)",
                "R 3 (#265752)",
                "D 3 (#569621)",
                "R 2 (#307622)",
                "D 9 (#39b101)",
                "R 8 (#6cfcf2)",
                "D 6 (#39b103)",
                "R 2 (#04e442)",
                "D 3 (#569623)",
                "R 4 (#170482)",
                "U 10 (#6b0b43)",
                "R 4 (#3f73c2)",
                "D 10 (#128763)",
                "R 7 (#5acf92)",
                "U 3 (#8e3573)",
                "R 4 (#5acf90)",
                "D 6 (#0d0c83)",
                "R 3 (#112e32)",
                "U 6 (#783e43)",
                "R 5 (#5d8ae2)",
                "U 4 (#3e1563)",
                "R 5 (#1a9462)",
                "D 7 (#1ea523)",
                "R 7 (#83a342)",
                "D 7 (#5d7dd1)",
                "L 9 (#47c452)",
                "D 7 (#777af1)",
                "L 9 (#69b962)",
                "U 7 (#1e13f3)",
                "L 4 (#6d3142)",
                "D 8 (#490133)",
                "L 9 (#0c8442)",
                "D 4 (#59b423)",
                "L 4 (#14c4f2)",
                "D 4 (#1a36c3)",
                "L 6 (#2b45e0)",
                "D 4 (#6e7e53)",
                "L 6 (#2b45e2)",
                "D 6 (#164043)",
                "L 7 (#3b2710)",
                "D 5 (#08fc03)",
                "L 8 (#23ae40)",
                "D 4 (#165253)",
                "L 2 (#4865e0)",
                "D 4 (#6d0323)",
                "L 9 (#373450)",
                "D 6 (#2f49c1)",
                "L 4 (#7c6940)",
                "D 5 (#5d07b1)",
                "L 6 (#19d050)",
                "D 8 (#6bd2c3)",
                "R 6 (#417ff0)",
                "D 3 (#6b1e73)",
                "L 5 (#388372)",
                "D 2 (#585e83)",
                "L 3 (#6ee262)",
                "D 2 (#815e51)",
                "L 5 (#3b3162)",
                "D 8 (#815e53)",
                "L 4 (#440782)",
                "D 5 (#585e81)",
                "L 2 (#8704b2)",
                "D 7 (#28aa83)",
                "L 5 (#0885a2)",
                "D 3 (#68d7b3)",
                "L 7 (#4085c2)",
                "D 7 (#069d63)",
                "L 6 (#036b72)",
                "D 4 (#46b823)",
                "L 2 (#4ac232)",
                "D 6 (#3fa933)",
                "L 8 (#2eee82)",
                "U 5 (#131e43)",
                "L 6 (#226692)",
                "U 9 (#11ccc3)",
                "L 4 (#3f3f62)",
                "U 2 (#5ae363)",
                "L 4 (#118210)",
                "U 6 (#7cf5c3)",
                "L 5 (#599080)",
                "D 6 (#22ac01)",
                "L 3 (#2c5410)",
                "U 5 (#0a2a61)",
                "L 8 (#344c60)",
                "U 4 (#7ce411)",
                "L 6 (#3fdc50)",
                "U 7 (#498433)",
                "L 4 (#053fd0)",
                "U 4 (#603643)",
                "L 8 (#0e78b0)",
                "D 8 (#32b543)",
                "L 2 (#94adb2)",
                "D 3 (#2a0f63)",
                "L 3 (#767e12)",
                "U 4 (#08aee3)",
                "L 2 (#676092)",
                "U 9 (#3ba3b3)",
                "L 3 (#14d1f2)",
                "D 10 (#617823)",
                "L 2 (#589a62)",
                "D 3 (#2f4ae3)",
                "L 3 (#013242)",
                "U 4 (#286e03)",
                "L 5 (#9eaf10)",
                "D 12 (#3e14f3)",
                "L 4 (#4b2d20)",
                "U 12 (#407091)",
                "L 3 (#813f40)",
                "D 3 (#407093)",
                "L 4 (#2331d0)",
                "D 5 (#174123)",
                "L 7 (#0c6fb0)",
                "D 3 (#71da33)",
                "R 5 (#466df0)",
                "D 5 (#590263)",
                "R 6 (#977572)",
                "D 4 (#2db733)",
                "L 3 (#04f302)",
                "D 4 (#078a61)",
                "L 4 (#9b0d72)",
                "D 5 (#1125f1)",
                "L 12 (#1df722)",
                "D 5 (#897b61)",
                "L 3 (#0cf612)",
                "D 11 (#60f001)",
                "L 3 (#25e3c2)",
                "D 2 (#445801)",
                "L 3 (#70cb12)",
                "U 6 (#445803)",
                "L 10 (#7a6952)",
                "U 2 (#60f003)",
                "L 4 (#44c9f2)",
                "U 10 (#28c3e3)",
                "L 5 (#6fc562)",
                "U 10 (#4ecf73)",
                "L 3 (#6fc560)",
                "U 11 (#2a9863)",
                "L 5 (#51b2e2)",
                "U 4 (#786b73)",
            )

        val instructions = input.map(::Scanner).map(::parseP1)
        var boundary = enumerateBoundary(instructions)
        val (min, max) = findMinMaxPoint(boundary)!!
        boundary = boundary.map { it - min }

        assertEquals(38188, computeArea(Rect(Point(0, 0), max - min), boundary))
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

        assertEquals(84L, computeArea(Rect(min, max), boundary))
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

        assertEquals(131L, computeArea(Rect(min, max), boundary))
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
