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
        fun create() = RecurrenceState("AA", 30, 0)
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
fun tick(valveList: List<Valve>, state: RecurrenceState): Long {
    var score = 0L

    for (i in 0..<valveList.size) {
        if (state.isOpen(i)) {
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

fun findMaxFlow(cache: MutableMap<RecurrenceState, Long>, valves: List<Valve>, state: RecurrenceState): Long {
    if (state.minutesLeft < 1) {
        return 0
    }

    val cached = cache[state]
    if (cached != null) {
        return cached
    }

    // A minute will pass as we do our turn.
    val ticked = tick(valves, state)

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

        findMaxFlow(cache, valves, newState.toRecurrence())
    } else {
        // It's already open so we can't do anything.
        0
    }


    // The max flow if we choose to move.
    var move = 0L
    val neighbors = valve.neighbors

    for (neighbor in neighbors) {
        val newState = state.toMutable()
        newState.position = neighbor
        newState.minutesLeft--

        val flow = findMaxFlow(cache, valves, newState.toRecurrence())
        if (flow > move) {
            move = flow
        }
    }

    val result = ticked + if (openCurrent > move) {
        openCurrent
    } else {
        move
    }

    cache[state] = result
    return result
}

fun main() {
    val valves = readValves()
    val score = findMaxFlow(mutableMapOf<RecurrenceState, Long>(), valves, RecurrenceState.create())

    println("Answer = $score")
}
