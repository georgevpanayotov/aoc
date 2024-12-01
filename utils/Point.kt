package net.panayotov.util

data class Point(val x: Long, val y: Long) {
    fun normalize() = Point(x.coerceIn(-1, 1), y.coerceIn(-1, 1))

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

    operator fun unaryMinus() = Point(-x, -y)

    operator fun minus(other: Point): Point = this + -other
}
