import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

data class Valve(val name: String, val rate: Long, val neighbors: List<String>)

data class RecurrenceState(
    val position: String,
    val minutesLeft: Long,
    private val openValves: Long) {

    fun toMutable() = MutableRecurrenceState(position, minutesLeft, openValves)

    fun isOpen(valveIndex: Int): Boolean = (1L shl (valveIndex + 1)) and openValves != 0L

    companion object {
        fun create() = RecurrenceState("AA", 26, 0)

        fun create(openValves: List<String>, valves: List<Valve>): RecurrenceState {
            val recurrence = MutableRecurrenceState("AA", 26, 0)
            for (valveName in openValves) {
                recurrence.openValve(findValve(valveName, valves).first)
            }

            return recurrence.toRecurrence()
        }

    }
}

data class MutableRecurrenceState(
    var position: String,
    var minutesLeft: Long,
    private var openValves: Long) {

    fun toRecurrence() = RecurrenceState(position, minutesLeft, openValves)

    fun openValve(valveIndex: Int) {
        openValves = openValves or (1L shl (valveIndex + 1))
    }
}

// How much is released in one minute.
fun tick(valveList: List<Valve>, noPressure: Set<String>, state: RecurrenceState): Long {
    var score = 0L

    for (i in 0..<valveList.size) {
        if (state.isOpen(i) && !noPressure.contains(valveList[i].name)) {
            score += valveList[i].rate
        }
    }

    return score
}

fun readValves(): List<Valve> {
    val valves = mutableListOf<Valve>()

    for (scanner in Scanners) {
        scanner.next() // Valve

        val name = scanner.next()

        scanner.next() // has
        scanner.next() // flow

        val rate = scanner.next().split("=")[1].split(";")[0].toLong()

        scanner.next() // tunnels
        scanner.next() // lead
        scanner.next() // to
        scanner.next() // valves

        val neighbors = scanner.nextLine().split(",").map(String::trim).filter {
            it.length != 0
        }

        valves.add(Valve(name, rate, neighbors))
    }

    return valves.toList()
}

fun findValve(name: String, valves: List<Valve>): Pair<Int, Valve> {
    var index: Int? = null
    var valve: Valve? = null
    for (i in 0..<valves.size) {
        if (valves[i].name == name) {
            index = i
            valve = valves[i]
        }
    }

    if (valve == null || index == null) {
        error("Valve ${name} not found")
    }

    return Pair(index, valve)
}

typealias FlowResult = Pair<List<String>, Long>

fun findMaxFlow(cache: MutableMap<RecurrenceState, FlowResult>, valves: List<Valve>, noPressure: Set<String>, state: RecurrenceState): FlowResult {
    if (state.minutesLeft < 1) {
        return Pair(listOf<String>(), 0L)
    }

    val cached = cache[state]
    if (cached != null) {
        return cached
    }

    // A minute will pass as we do our turn.
    val ticked = tick(valves, noPressure, state)

    // The max flow if our current action is to open this valve.
    var valveIndex: Int? = null
    var valve: Valve? = null
    for (i in 0..<valves.size) {
        if (valves[i].name == state.position) {
            valveIndex = i
            valve = valves[i]
        }
    }

    if (valve == null || valveIndex == null) {
        error("Valve ${state.position} not found")
    }

    val openCurrent = if (!state.isOpen(valveIndex) && valve.rate > 0) {
        val newState = state.toMutable()
        newState.minutesLeft--
        newState.openValve(valveIndex)

        findMaxFlow(cache, valves, noPressure, newState.toRecurrence())
    } else {
        // It's already open so we can't do anything.
        Pair(listOf<String>(), 0L)
    }


    // The max flow if we choose to move.
    var move = Pair(listOf<String>(), 0L)
    val neighbors = valve.neighbors

    for (neighbor in neighbors) {
        val newState = state.toMutable()
        newState.position = neighbor
        newState.minutesLeft--

        val flow = findMaxFlow(cache, valves, noPressure, newState.toRecurrence())
        if (flow.second > move.second) {
            move = flow
        }
    }

    val result = if (openCurrent.second > move.second) {
        val newPath = openCurrent.first.toMutableList()
        newPath.add(valve.name)
        Pair(newPath.toList(), openCurrent.second + ticked)
    } else {
        Pair(move.first, move.second + ticked)
    }

    cache[state] = result
    return result
}

fun pow(base: Int, exp: Int): Int {
    var result = 1

    for(i in 1..exp) {
        result *= base
    }

    return result
}

fun main() {
    val valves = readValves()
    val score = findMaxFlow(mutableMapOf<RecurrenceState, FlowResult>(), valves, setOf<String>(), RecurrenceState.create())

    var max = 0L
    for (subset in 0..pow(2, score.first.size)) {
        val opened = mutableListOf<String>()
        for (i in 0..<score.first.size) {
            if (subset and pow(2, i) != 0) {
                opened.add(score.first[i])
            }
        }
        val flow1 = findMaxFlow(mutableMapOf<RecurrenceState, FlowResult>(), valves, opened.toSet(), RecurrenceState.create(opened, valves))
        val flow2 = findMaxFlow(mutableMapOf<RecurrenceState, FlowResult>(), valves, flow1.first.toSet(), RecurrenceState.create(flow1.first, valves))
        if (flow1.second + flow2.second > max) {
            max = flow1.second + flow2.second
        }
    }

    println("Answer = ${max}")
}
