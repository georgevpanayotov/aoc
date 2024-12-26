import java.util.ArrayDeque
import net.panayotov.util.Scanners

interface Component {
    val output: Boolean?
    val name: String
    val inputs: List<String>

    fun receiveInput(name: String, value: Boolean)
}

data class Wire(override val name: String, override val output: Boolean) : Component {
    override fun receiveInput(name: String, value: Boolean) = error("Unsupported")

    override val inputs = listOf<String>()
}

class GateImpl(
    val left: String,
    val right: String,
    private val calc: (Boolean, Boolean) -> Boolean,
) {
    var callback: (() -> Unit)? = null

    private var leftValue: Boolean? = null
    private var rightValue: Boolean? = null
    private var ready = false

    fun receiveInput(name: String, value: Boolean) {
        if (name == left) {
            leftValue = value
        } else if (name == right) {
            rightValue = value
        }

        if (leftValue != null && rightValue != null) {
            ready = true
        }
    }

    fun compute(): Boolean? =
        if (!ready) {
            null
        } else {
            if (leftValue == null || rightValue == null) {
                error("Left: $leftValue | Right: $rightValue")
            }

            calc(leftValue!!, rightValue!!)
        }
}

data class AndGate(override val name: String, val left: String, val right: String) : Component {
    private val gate = GateImpl(left, right) { a, b -> a && b }

    override fun receiveInput(name: String, value: Boolean) = gate.receiveInput(name, value)

    override val inputs = listOf(left, right)

    override val output
        get() = gate.compute()
}

data class OrGate(override val name: String, val left: String, val right: String) : Component {
    private val gate = GateImpl(left, right) { a, b -> a || b }

    override fun receiveInput(name: String, value: Boolean) = gate.receiveInput(name, value)

    override val inputs = listOf(left, right)

    override val output
        get() = gate.compute()
}

data class XorGate(override val name: String, val left: String, val right: String) : Component {
    private val gate = GateImpl(left, right) { a, b -> a xor b }

    override fun receiveInput(name: String, value: Boolean) = gate.receiveInput(name, value)

    override val inputs = listOf(left, right)

    override val output
        get() = gate.compute()
}

fun MutableMap<String, MutableList<Component>>.addInput(name: String, gate: Component) {
    val list = getOrPut(name) { mutableListOf() }

    list.add(gate)
}

class ProblemState(
    val components: Map<String, Component>,
    val inputs: Map<String, List<Component>>,
) {
    fun compute(): Long {
        val queue = ArrayDeque<Component>()

        queue.addAll(components.values)

        while (!queue.isEmpty()) {
            val component = queue.removeFirst()
            val output = component.output
            if (output != null) {
                for (receiver in inputs[component.name] ?: listOf()) {
                    receiver.receiveInput(component.name, output)
                }
            } else {
                // Put it back in rotation until it gets an output.
                queue.addLast(component)
            }
        }

        val zs = components.values.filter { it.name[0] == 'z' }.sortedBy { it.name }

        var score = 0L

        for (z in zs) {
            val zPlace = z.name.substring(1).toInt()
            val bit = if (z.output!!) 1L else 0L
            score = score or (bit shl zPlace)
        }

        return score
    }
}

fun main() {
    var readingWires = true

    val components = mutableMapOf<String, Component>()
    val inputs = mutableMapOf<String, MutableList<Component>>()

    for (line in Scanners) {
        if (readingWires) {
            line.useDelimiter(" *: *")
            if (!line.hasNext()) {
                readingWires = false
                continue
            } else {
                val name = line.next()
                val value = line.next().toInt().let { if (it == 1) true else false }
                components[name] = Wire(name, value)
            }
        } else {
            line.useDelimiter(" (-> )?")
            val left = line.next()
            val op = line.next()
            val right = line.next()

            val name = line.next()

            val gate =
                when (op) {
                    "AND" -> AndGate(name, left, right)
                    "OR" -> OrGate(name, left, right)
                    "XOR" -> XorGate(name, left, right)
                    else -> error("Unknown gate: $op")
                }

            components[name] = gate
            gate.inputs.forEach { inputs.addInput(it, gate) }
        }
    }

    val problem = ProblemState(components.toMap(), inputs.mapValues { it.value.toList() }.toMap())

    val score = problem.compute()

    println("Answer = $score")
}
