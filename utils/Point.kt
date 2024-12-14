package net.panayotov.util

data class Point(val x: Long, val y: Long) {
    fun normalize() = Point(x.coerceIn(-1, 1), y.coerceIn(-1, 1))

    fun rotateRight() = Point(y, -x)

    fun rotateLeft() = Point(-y, x)

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)

    operator fun unaryMinus() = Point(-x, -y)

    operator fun minus(other: Point): Point = this + -other

    operator fun times(scalar: Long): Point = Point(scalar * x, scalar * y)
}

operator fun Long.times(point: Point): Point = Point(this * point.x, this * point.y)
