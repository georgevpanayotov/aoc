package net.panayotov.util

enum class Direction(val vector: Point) {
    NORTH(Point(0, 1)),
    NORTHEAST(Point(1, 1)),
    EAST(Point(1, 0)),
    SOUTHEAST(Point(1, -1)),
    SOUTH(Point(0, -1)),
    SOUTHWEST(Point(-1, -1)),
    WEST(Point(-1, 0)),
    NORTHWEST(Point(-1, 1));

    companion object {
        // The cardinal directions (N, E, S, W) in clockwise order.
        val cardinal = listOf(NORTH, EAST, SOUTH, WEST)

        // The ordinal directions (NE, SE, SW, NW) in clockwise order.
        val ordinal = listOf(NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST)

        // All directions in clockwise order.
        val all = entries
    }
}

// Converts a point to a direction in a very low-res way (i.e. anything with 2 positive coordinates
// is NORTHEAST). (0, 0) is null.
fun Point.toDirection(): Direction? =
    when (normalize()) {
        Point(0, 1) -> Direction.NORTH
        Point(1, 1) -> Direction.NORTHEAST
        Point(1, 0) -> Direction.EAST
        Point(1, -1) -> Direction.SOUTHEAST
        Point(0, -1) -> Direction.SOUTH
        Point(-1, -1) -> Direction.SOUTHWEST
        Point(-1, 0) -> Direction.WEST
        Point(-1, 1) -> Direction.NORTHWEST
        else -> null
    }
