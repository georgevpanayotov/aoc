import java.util.ArrayDeque
import net.panayotov.util.Scanners

const val ROOT_NAME = "root"
const val HUMAN_NAME = "humn"

enum class Operator(val opName: String, val functor: (Long, Long) -> Long) {
    PLUS("+", Long::plus),
    MINUS("-", Long::minus),
    TIMES("*", Long::times),
    DIV("/", Long::div);

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

    override fun toString() = opName
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

    // This makes some big assumptions that after we resolve everything (exception `humn`) we will
    // end up only with expression that have 1 parameter and 1 constant value.
    fun resolveValue(constant: ConstantMonkey): SolvableMonkey =
        if (constant.name == leftName) {
            leftValue = constant.value
            SolvableMonkey(name, op, rightName, constant.value, false)
        } else if (constant.name == rightName) {
            rightValue = constant.value
            SolvableMonkey(name, op, leftName, constant.value, true)
        } else {
            error("$constant doesn't match $this")
        }

    fun getResult() =
        if (leftValue != null && rightValue != null) {
            ConstantMonkey(name, op.functor(leftValue ?: 0, rightValue ?: 0))
        } else {
            null
        }
}

data class SolvableMonkey(
    val name: String,
    val op: Operator,
    val parameter: String,
    val value: Long,
    // If true the expression is $parameter $op $value
    // If false the expression is $value $op $parameter
    val parameterLeft: Boolean,
) {
    fun rootValue() =
        if (parameter == HUMAN_NAME) {
            value
        } else {
            null
        }

    fun invert(otherSide: Long): Long =
        when (op) {
            Operator.PLUS -> invertPlus(otherSide)
            Operator.MINUS -> invertMinus(otherSide)
            Operator.TIMES -> invertTimes(otherSide)
            Operator.DIV -> invertDiv(otherSide)
        }

    private fun invertPlus(otherSide: Long) = otherSide - value

    private fun invertMinus(otherSide: Long) =
        if (parameterLeft) {
            // otherSide = parameter - value
            otherSide + value
        } else {
            // otherSide = value - parameter
            value - otherSide
        }

    // This inversion assumes that the integer division will yield a correct result.
    private fun invertTimes(otherSide: Long) = otherSide / value

    // This inversion assumes that the integer division will yield a correct result.
    private fun invertDiv(otherSide: Long) =
        if (parameterLeft) {
            // otherSide = parameter / value
            value * otherSide
        } else {
            // otherSide = value / parameter
            value / otherSide
        }

    override fun toString() =
        if (name == ROOT_NAME) {
            if (parameterLeft) {
                "$name: $parameter = $value"
            } else {
                "$name: $value = $parameter"
            }
        } else {
            if (parameterLeft) {
                "$name = $parameter $op $value"
            } else {
                "$name = $value $op $parameter"
            }
        }

    private fun opString() =
        if (name == ROOT_NAME) {
            "="
        } else {
            op.toString()
        }
}

fun calculate(
    constants: Set<ConstantMonkey>,
    inputs: MutableMap<String, MutableList<ExpressionMonkey>>,
): Long {
    val queue = ArrayDeque(constants)
    val expressionMap = mutableMapOf<String, SolvableMonkey>()

    while (queue.size > 0) {
        val constant = queue.removeFirst()
        if (constant.name == HUMAN_NAME) {
            continue
        }

        val expressions = inputs[constant.name]

        if (expressions == null) {
            continue
        }

        var i = 0
        while (i < expressions.size) {
            val expression = expressions[i]
            val solvable = expression.resolveValue(constant)

            val result = expression.getResult()

            if (result != null) {
                queue.add(result)
                expressions.removeAt(i)
                // expressionMap.remove(expression.name)
            } else {
                expressionMap[expression.name] = solvable
                i++
            }
        }
    }

    var root = expressionMap[ROOT_NAME] ?: error("Root not found in $expressionMap")
    println("Expressions to start with: ")
    println(expressionMap.values.joinToString("\n"))
    println("\nSolving: ")

    while (root.rootValue() == null) {
        val subExpression = expressionMap[root.parameter]
        if (subExpression == null) {
            error("Expression ${root.parameter} not found.")
        }

        val newValue = subExpression.invert(root.value)

        // NOTE: The root op is effectively "=" so the `parameterLeft` is valid to be true or false.
        root = SolvableMonkey(root.name, root.op, subExpression.parameter, newValue, true)

        println(root)
    }

    return root.rootValue() ?: error("Didn't solve it!")
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
