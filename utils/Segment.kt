package net.panayotov.util

data class Segment(val from: Point, val to: Point) {
    fun vertical() = from.x == to.x

    fun horizontal() = from.y == to.y

    // Makes it so that this (vertical) segment is pointing up.
    fun normalizeVertical(): Segment {
        checkVertical()

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

    fun checkVertical() {
        if (!vertical()) {
            error("$this must be vertical")
        }
    }
}
