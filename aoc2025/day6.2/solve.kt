import net.panayotov.util.Grid

enum class Operator {
    PLUS,
    TIMES,
}

fun doOp(lhs: Long, operator: Operator, rhs: Long) =
    when (operator) {
        Operator.PLUS -> lhs + rhs
        Operator.TIMES -> lhs * rhs
    }

class Processor {
    private val operands = mutableListOf<Long>()
    private var operator: Operator? = null

    fun addOperand(op: Long) {
        operands.add(op)
    }

    fun plus() {
        operator = Operator.PLUS
    }

    fun times() {
        operator = Operator.TIMES
    }

    fun compute(level: Long) =
        operands.reduce { acc, operand ->
            operator?.let { doOp(acc, it, operand) } ?: error("Operator not found at level $level")
        }
}

fun Grid<Char>.isBlank(y: Long): Boolean {
    for (x in 0L..<width) {
        if (this[x, y] != ' ') {
            return false
        }
    }

    return true
}

fun main() {
    val grid = Grid.read(' ').transposed().flippedY()
    var score = 0L
    var processor = Processor()

    for (y in 0L..<grid.height) {
        if (grid.isBlank(y)) {
            score += processor.compute(y)
            processor = Processor()
        } else {
            val numberStr = StringBuilder()

            for (x in 0L..<grid.width) {
                val gridVal = grid[x, y]
                if (gridVal.isDigit()) {
                    numberStr.append(gridVal)
                } else if (gridVal == '+') {
                    processor.plus()
                } else if (gridVal == '*') {
                    processor.times()
                }
            }

            processor.addOperand(numberStr.toString().toLong())
        }
    }

    // Don't forget the last one! In case we don't end on a blank line.
    score += processor.compute(grid.height - 1L)

    println("Answer = $score")
}
