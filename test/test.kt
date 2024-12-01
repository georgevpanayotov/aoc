package net.panayotov.test

import net.panayotov.util.*

const val DELAY = 200L

fun clear() {
    print("\u001b[2J")
}

fun waitAndClear() {
    Thread.sleep(DELAY)
    clear()
}

fun Grid<Char>.printForDirection(category: String, original: Point, direction: Direction) {
    val newPoint = original + direction.vector
    this[newPoint] = '#'

    println("$category: $direction")
    print(this)
    waitAndClear()

    this[newPoint] = '.'
}

fun main(args: Array<String>) {

    val grid =
        if (args.size > 0) {
            if (args[0] == "stop") {
                Grid.read('.', "")
            } else if (args[0] == "limit") {
                if (args.size < 2) {
                    error("Expected arg after \"limit\"")
                }

                Grid.read('.', args[1].toInt())
            } else {
                error("Unexpected arg ${args[0]}")
            }
        } else {
            Grid.read('.')
        }

    val original = Point(2L, 2L)

    clear()
    println("Starting grid")
    print(grid)
    waitAndClear()

    for (direction in Direction.cardinal) {
        grid.printForDirection("Cardinal Directions", original, direction)
    }

    for (direction in Direction.ordinal) {
        grid.printForDirection("Ordinal Directions", original, direction)
    }

    for (direction in Direction.all) {
        grid.printForDirection("All Directions", original, direction)
    }

    val points =
        listOf(
            Point(0, 0),
            Point(5, 3),
            Point(1, 2),
            Point(-17, 22),
            Point(-6, -7),
            Point(7, 0),
            Point(12, -5),
            Point(0, -22),
            Point(-12, 0),
            Point(-16, -16),
        )

    for (point in points) {
        println("$point is ${point.toDirection()}")
    }
}
