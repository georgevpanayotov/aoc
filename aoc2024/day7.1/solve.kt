import net.panayotov.util.Lines

// `operators` is the list of possible operators
data class Equation(val testValue: Long, val operands: List<Long>, val operators: List<Operator>) {
    fun canBeSolved(): Boolean {
        val assignments = mutableListOf<Operator>()
        val aCount = operands.size - 1

        for (i in 0..<aCount) {
            assignments.add(Plus)
        }

        var bitMask = 0
        while (!done(assignments)) {
            var working = bitMask
            for (i in 0..<aCount) {
                assignments[i] = operators[working % operators.size]
                working /= operators.size
            }

            if (evaluate(assignments)) {
                return true
            }

            bitMask++
        }

        return false
    }

    private fun done(assignments: List<Operator>): Boolean =
        assignments.fold(true) { acc, value -> acc && (value == operators[operators.size - 1]) }

    private fun evaluate(assignments: List<Operator>): Boolean {
        var result = operands[0]
        var i = 0
        while (i < assignments.size) {
            result = assignments[i].evaluate(result, operands[i + 1])
            i++
        }

        return result == testValue
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

object Concat : Operator {
    override fun evaluate(lhs: Long, rhs: Long) = "${lhs.toString()}${rhs.toString()}".toLong()
}

val P1_OPERATORS = listOf(Plus, Times)
val P2_OPERATORS = listOf(Plus, Times, Concat)

fun main(args: Array<String>) {
    val operators =
        if (args.size > 0 && args[0] == "p2") {
            P2_OPERATORS
        } else {
            P1_OPERATORS
        }
    var score = 0L
    for (line in Lines) {
        val parts = line.split(':')
        val eq =
            Equation(
                parts[0].trim().toLong(),
                parts[1].trim().split(" ").map(String::trim).map(String::toLong),
                operators,
            )
        if (eq.canBeSolved()) {
            score += eq.testValue
        }
    }

    println("Answer = $score")
}
