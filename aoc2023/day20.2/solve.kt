import kotlin.collections.ArrayDeque
import net.panayotov.util.Lines

enum class PulseLevel {
    LOW,
    HIGH,
}

data class Pulse(val level: PulseLevel, val input: String, val output: String)

enum class ModuleType {
    FLIP_FLOP,
    CONJUNCTION,
}

data class ModuleSet(val broadcast: List<String>, val modules: Map<String, Module>)

data class Module(val type: ModuleType, val name: String, val outputs: List<String>)

interface ModuleState {
    fun receiveInput(name: String, level: PulseLevel): Pair<ModuleState, PulseLevel?>
}

data class FlipFlopState(val name: String, val on: Boolean) : ModuleState {
    override fun receiveInput(name: String, level: PulseLevel): Pair<ModuleState, PulseLevel?> =
        if (level == PulseLevel.LOW) {
            val newState = copy(on = !on)
            val newLevel =
                if (on) {
                    PulseLevel.LOW
                } else {
                    PulseLevel.HIGH
                }

            Pair(newState, newLevel)
        } else {
            Pair(this, null)
        }

    companion object {
        fun create(module: Module) = FlipFlopState(module.name, false)
    }
}

data class ConjunctionState(val name: String, val inputStates: Map<String, PulseLevel>) :
    ModuleState {
    override fun receiveInput(name: String, level: PulseLevel): Pair<ModuleState, PulseLevel?> {
        val newInputStates = inputStates.toMutableMap()
        newInputStates[name] = level
        val allHigh =
            newInputStates.values.fold(true) { acc, pulseLevel ->
                acc && pulseLevel == PulseLevel.HIGH
            }

        return Pair(
            copy(inputStates = newInputStates),
            if (allHigh) {
                PulseLevel.LOW
            } else {
                PulseLevel.HIGH
            },
        )
    }

    companion object {
        fun create(module: Module, inputs: List<String>) =
            ConjunctionState(module.name, inputs.associate { Pair(it, PulseLevel.LOW) })
    }
}

fun readModules(): ModuleSet {
    var broadcast: List<String>? = null
    val modules = mutableMapOf<String, Module>()

    for (line in Lines) {
        val parts = line.split("->")
        if (parts[0].trim() == "broadcaster") {
            broadcast = parts[1].split(",").map(String::trim).filter { it.length > 0 }
        } else {
            val type =
                if (parts[0][0] == '%') {
                    ModuleType.FLIP_FLOP
                } else {
                    ModuleType.CONJUNCTION
                }

            val name = parts[0].trim().substring(1)

            val module = Module(type, name, parts[1].split(",").map(String::trim))
            modules[name] = module
        }
    }

    if (broadcast == null) {
        error("\"broadcaster\" not found.")
    }

    return ModuleSet(broadcast, modules.toMap())
}

fun createState(module: Module, modules: Collection<Module>) =
    if (module.type == ModuleType.FLIP_FLOP) {
        FlipFlopState.create(module)
    } else {
        val inputNames = modules.filter { it.outputs.contains(module.name) }.map { it.name }
        ConjunctionState.create(module, inputNames)
    }

fun computePushes(target: String, moduleSet: ModuleSet): Long {
    var buttonCount = 0L
    var targetLow = false

    val states =
        moduleSet.modules.values
            .associate {
                val state = createState(it, moduleSet.modules.values)
                Pair(it.name, state)
            }
            .toMutableMap()

    while (!targetLow) {
        val pulseQueue = ArrayDeque<Pulse>()
        buttonCount++
        for (name in moduleSet.broadcast) {
            pulseQueue.addLast(Pulse(PulseLevel.LOW, "broadcast", name))
        }

        while (pulseQueue.size > 0) {
            val pulse = pulseQueue.removeFirst()
            if (pulse.level == PulseLevel.LOW && pulse.output == target) {
                targetLow = true
            }

            val state = states[pulse.output]
            if (state == null) {
                continue
            }

            val (newState, newLevel) = state.receiveInput(pulse.input, pulse.level)

            states[pulse.output] = newState

            if (newLevel != null) {
                for (newOutput in moduleSet.modules[pulse.output]!!.outputs) {
                    pulseQueue.addLast(Pulse(newLevel, pulse.output, newOutput))
                }
            }
        }
    }

    return buttonCount
}

fun main() {
    val moduleSet = readModules()

    // This makes a lot of assumptions based on the input code. It doesn't seem like there is a
    // generic solution to this problem. It could maybe be generalized to any input from AOC but I'm
    // just solving this for myself. Here, we pluck four end modules for the four disjoint
    // subgraphs. When each of these get a low signal they will transmit a high to the conjunction
    // just before 'rx'.
    val targets = listOf("th", "sv", "gh", "ch")

    // `computePushes` tells us how many pushes it takes for a target module to receive a low pulse.
    // Another big assumption here is that the lengths will end up being relatively prime so lcm(a,
    // b, c, d) = a * b * c * d
    val score = targets.map { computePushes(it, moduleSet) }.reduce { acc, value -> acc * value }

    println("Answer = $score")
}
