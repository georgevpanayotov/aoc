import java.io.PrintStream
import java.util.ArrayDeque
import net.panayotov.util.Scanners

enum class Label(val color: String) {
    INPUT("green"),
    OUTPUT("red"),
    CARRY("yellow"),
    SUM("blue"),
}

sealed interface Component {
    val output: Boolean?
    val name: String
    val inputs: List<String>
    var label: Label?

    fun receiveInput(name: String, value: Boolean)

    fun gateName() = name
}

data class Wire(override val name: String, override val output: Boolean) : Component {
    override fun receiveInput(name: String, value: Boolean) = error("Unsupported")

    override val inputs = listOf<String>()

    override var label: Label? = Label.INPUT
}

class GateImpl(
    val left: String,
    val right: String,
    private val calc: (Boolean, Boolean) -> Boolean,
) {
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

    override var label: Label? = null

    override fun gateName() = "AND_$name"
}

data class OrGate(override val name: String, val left: String, val right: String) : Component {
    private val gate = GateImpl(left, right) { a, b -> a || b }

    override fun receiveInput(name: String, value: Boolean) = gate.receiveInput(name, value)

    override val inputs = listOf(left, right)

    override val output
        get() = gate.compute()

    override var label: Label? = null

    override fun gateName() = "OR_$name"
}

data class XorGate(override val name: String, val left: String, val right: String) : Component {
    private val gate = GateImpl(left, right) { a, b -> a xor b }

    override fun receiveInput(name: String, value: Boolean) = gate.receiveInput(name, value)

    override val inputs = listOf(left, right)

    override val output
        get() = gate.compute()

    override var label: Label? = null

    override fun gateName() = "XOR_$name"
}

fun MutableMap<String, MutableList<Component>>.addInput(name: String, gate: Component) {
    val list = getOrPut(name) { mutableListOf() }

    list.add(gate)
}

class ProblemState(val components: Map<String, Component>) {
    private val inputs: Map<String, List<Component>>

    init {
        val mutableInputs = mutableMapOf<String, MutableList<Component>>()
        for (component in components.values) {

            component.inputs.forEach { mutableInputs.addInput(it, component) }
        }
        inputs = mutableInputs.mapValues { it.value.toList() }.toMap()
    }

    fun compute(): Long? {
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

        return zValue() ?: error(zErrors())
    }

    fun traverse(name: String): Set<Component> {
        val queue = ArrayDeque<String>()
        val ret = mutableSetOf<Component>()
        queue.add(name)

        while (!queue.isEmpty()) {
            val cname = queue.removeFirst()
            if (!components.contains(cname)) {
                error("missing $cname")
            }
            ret.add(components[cname]!!)

            for (component in inputs[cname] ?: listOf()) {
                queue.add(component.name)
            }
        }

        return ret.toSet()
    }

    // Splits subgraphs out that represent each bit's computation.
    fun printGraphs() {
        var totalGraph = setOf<Component>()

        for (n in 44 downTo 0) {
            val number = "$n".padStart(2, '0')

            if (!components.contains("x${number}") || !components.contains("y${number}")) {
                continue
            }

            val newGraph = (traverse("x${number}") + traverse("y${number}")) - totalGraph

            printGraph("graphs/g${number}", newGraph)

            totalGraph += newGraph
        }
    }

    private fun zValue(): Long? {
        val zs = components.values.filter { it.name[0] == 'z' }

        var score = 0L

        for (z in zs) {
            val zPlace = z.name.substring(1).toInt()
            val bit = z.output?.let { if (it) 1L else 0L }
            if (bit == null) {
                return null
            }

            score = score or (bit shl zPlace)
        }

        return score
    }

    private fun zErrors(): String {
        val nullZs = components.values.filter { it.name[0] == 'z' && it.output == null }
        if (nullZs.isEmpty()) {
            return "No problem"
        } else {
            return "Gates ${nullZs.joinToString(",")} have no value."
        }
    }

    // Print a graph based on the problem statement that this is a circuit representing an adder.
    // Adders have a few standard patterns:
    // 1. Two inputs for each bit (x## and y##)
    // 2. an XOR gate that is the "sum" bit
    // 3. an AND gate that is the "carry" bit
    // 4. Some additional gates for transmitting the carry to the next bit.
    // Prints this to a file (appendning ".dot") the DOT format recognized by GraphViz
    fun printGraph(filename: String, graph: Set<Component>) {
        PrintStream("${filename}.dot").use { file ->
            file.println("digraph {")

            file.println("    {")
            for (component in graph) {
                val color =
                    component.label?.color?.let { " style=\"filled\" fillcolor=\"$it\" " } ?: ""
                val label =
                    when (component) {
                        is Wire -> component.name
                        is XorGate -> "XOR"
                        is OrGate -> "OR"
                        is AndGate -> "AND"
                    }

                file.println("        ${component.gateName()} [label = \"${label}\" $color]")
                if (component.label == Label.OUTPUT) {
                    file.println("        ${component.name} [$color]")
                }
            }
            file.println("    }")

            for (component in graph) {
                val gate = component.gateName()

                val inputs = inputs[component.name]

                if (inputs != null) {
                    val outputs = inputs.map(Component::gateName).joinToString(" ")

                    if (component.inputs.size > 0) {
                        file.println("    $gate -> {$outputs} [label = \"${component.name}\"]")
                    } else {
                        file.println("    $gate -> {$outputs}")
                    }
                } else {
                    file.println("    $gate -> {${component.name}}")
                }
            }

            file.println("}")
        }
    }

    // Apply the output label to the output gates.
    fun labelOutputs() {
        for (n in 0..45) {
            val number = "$n".padStart(2, '0')
            components["z$number"]?.let { it.label = Label.OUTPUT }
        }
    }

    // Apply labels to the carry and sum gates.
    fun labelCarriesAndSums() {
        for (n in 0..44) {
            val number = "$n".padStart(2, '0')

            val xInput = components["x$number"]!!
            val yInput = components["y$number"]!!

            val outputs = inputs[xInput.name]!!.toSet() intersect inputs[yInput.name]!!.toSet()

            var sumFound = false
            var carryFound = false
            for (output in outputs) {
                when (output) {
                    is XorGate -> {
                        if (output.label == null) {
                            sumFound = true
                            output.label = Label.SUM
                        }
                    }
                    is AndGate -> {
                        if (output.label == null) {
                            carryFound = true
                            output.label = Label.CARRY
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

fun main() {
    var readingWires = true

    val components = mutableMapOf<String, Component>()

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
        }
    }
    var problem = ProblemState(components.toMap())

    problem.labelOutputs()
    problem.labelCarriesAndSums()
    problem.printGraphs()
    problem.printGraph("graphs/g", problem.components.values.toSet())

    // I solved here by observing the graph. The known nodes are labeled according to the `Label`
    // enum above. The trick is to follow the uncolored nodes and see where the pattern falls apart;
    // noting that the first and last bits can be expected to be different. The uncolored nodes
    // start from the first carry, then they meet the next sum at an AND then they meet the next
    // carry at an OR gate. This OR gate meets the next sum at an XOR gate which sets the output
    // bit. And the process continues.
}
