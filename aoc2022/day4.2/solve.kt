data class Range(val min: Int, val max: Int) {

    fun overlaps(other: Range): Boolean =
        containsPoint(other.min) || containsPoint(other.max)

    fun containsPoint(point: Int): Boolean = min <= point && max >= point

    companion object {
        fun parse(assignment: String): Range {
            val numbers = assignment.split("-")
            if (numbers.size != 2) {
                error("Expected valid range, got: $assignment")
            }

            return Range(numbers[0].toInt(), numbers[1].toInt())
        }
    }
}

fun main() {
    var line = readLine()
    var score = 0
    while (line != null) {

        val assignments = line.split(",")
        if (assignments.size != 2) {
            error("expected 2 assignemnts, got: $line")
        }

        val range1 = Range.parse(assignments[0])
        val range2 = Range.parse(assignments[1])

        if (range1.overlaps(range2) || range2.overlaps(range1)) {
            score++
        }

        line = readLine()
    }
    print(score)
}
