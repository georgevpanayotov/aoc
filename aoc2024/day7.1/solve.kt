import net.panayotov.util.Lines

data class Equation(val testValue: Long, val operands: List<Long>) {
    fun evaluate(assignments: List<Operator>): Boolean {
        var result = operands[0]
        var i = 0
        while (i < assignments.size) {
            result = assignments[i].evaluate(result, operands[i + 1])
            i++
        }

        return result == testValue
    }

    fun canBeSolved(): Boolean {
        val assignments = mutableListOf<Operator>()
        val aCount = operands.size - 1

        for (i in 0..<aCount) {
            assignments.add(Plus)
        }

        var bitMask = 0
        while (!assignments.done()) {
            for (i in 0..<aCount) {
                assignments[i] =
                    if ((bitMask shr i) % 2 == 0) {
                        Plus
                    } else {
                        Times
                    }
            }

            if (evaluate(assignments)) {
                return true
            }

            bitMask++
        }

        return false
    }
}

sealed interface Operator {
    fun evaluate(lhs: Long, rhs: Long): Long
}

object Plus : Operator {
    override fun evaluate(lhs: Long, rhs: Long) = lhs + rhs
}

object Times : Operator {
    override fun evaluate(lhs: Long, rhs: Long) = lhs * rhs
}

val OPERATORS = listOf(Plus, Times)

fun List<Operator>.done() = this.fold(true) { acc, value -> acc && (value == Times) }

fun main() {
    var score = 0L
    for (line in Lines) {
        val parts = line.split(':')
        val eq =
            Equation(
                parts[0].trim().toLong(),
                parts[1].trim().split(" ").map(String::trim).map(String::toLong),
            )
        if (eq.canBeSolved()) {
            score += eq.testValue
        }
    }

    println("Answer = $score")
}
