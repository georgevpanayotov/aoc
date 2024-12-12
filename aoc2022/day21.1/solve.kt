import java.util.ArrayDeque
import net.panayotov.util.Scanners

enum class Operator(val functor: (Long, Long) -> Long) {
    PLUS(Long::plus),
    MINUS(Long::minus),
    TIMES(Long::times),
    DIV(Long::div);

    companion object {
        fun parse(value: String): Operator =
            when (value) {
                "+" -> PLUS
                "-" -> MINUS
                "*" -> TIMES
                "/" -> DIV
                else -> error("Invalid op $value")
            }
    }
}

data class ConstantMonkey(val name: String, val value: Long)

data class ExpressionMonkey(
    val name: String,
    val op: Operator,
    val leftName: String,
    val rightName: String,
) {
    private var leftValue: Long? = null
    private var rightValue: Long? = null

    fun resolveValue(constant: ConstantMonkey) {
        if (constant.name == leftName) {
            leftValue = constant.value
        }
        if (constant.name == rightName) {
            rightValue = constant.value
        }
    }

    fun getResult() =
        if (leftValue != null && rightValue != null) {
            ConstantMonkey(name, op.functor(leftValue ?: 0, rightValue ?: 0))
        } else {
            null
        }
}

fun calculate(
    constants: Set<ConstantMonkey>,
    inputs: MutableMap<String, MutableList<ExpressionMonkey>>,
): Long {
    val queue = ArrayDeque(constants)

    while (queue.size > 0) {
        val constant = queue.removeFirst()

        val expressions = inputs[constant.name]

        if (expressions == null) {
            continue
        }

        for (expression in expressions) {
            expression.resolveValue(constant)

            val result = expression.getResult()

            if (result != null) {
                if (result.name == "root") {
                    return result.value
                }
                queue.add(result)
            }
        }
    }

    error("No root resolved.")
}

fun main() {
    val constants = mutableSetOf<ConstantMonkey>()
    val inputs = mutableMapOf<String, MutableList<ExpressionMonkey>>()

    for (line in Scanners) {
        var name = line.next()
        name = name.substring(0, name.length - 1)

        if (line.hasNextLong()) {
            val constant = line.nextLong()
            constants.add(ConstantMonkey(name, constant))
        } else {
            val left = line.next()
            val op = line.next()
            val right = line.next()
            val monkey = ExpressionMonkey(name, Operator.parse(op), left, right)

            inputs.getOrPut(left) { mutableListOf() }.add(monkey)
            inputs.getOrPut(right) { mutableListOf() }.add(monkey)
        }
    }

    val score = calculate(constants, inputs)

    println("Answer = $score")
}
