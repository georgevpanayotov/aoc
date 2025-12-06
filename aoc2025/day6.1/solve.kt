import net.panayotov.util.Scanners

class Processor {
    private val operands = mutableListOf<Long>()

    fun addOperand(op: Long) {
        operands.add(op)
    }

    fun plus() = operands.reduce { acc, it -> acc + it }

    fun times() = operands.reduce { acc, it -> acc * it }
}

fun main() {
    var problems = mutableListOf<Processor>()
    var firstLine = true
    var score = 0L

    for (line in Scanners) {
        if (line.hasNextLong()) {
            var i = 0
            while (line.hasNextLong()) {
                if (firstLine) {
                    problems.add(Processor())
                }

                problems[i].addOperand(line.nextLong())

                i++
            }
        } else {
            var i = 0
            while (line.hasNext()) {
                val operator = line.next()
                if (operator == "+") {
                    score += problems[i].plus()
                } else {
                    score += problems[i].times()
                }

                i++
            }
        }

        firstLine = false
    }

    println("Answer = $score")
}
