import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

data class Valve(val name: String, val rate: Long, val neighbors: List<String>)

enum class ActionSubject {
    HUMAN,
    ELEPHANT
}

sealed interface Action {
    val valve: Valve
    val index: Int
}

data class MoveAction(override val valve: Valve, override val index: Int, val nextValveName: String, val subject: ActionSubject): Action

data class OpenValveAction(override val valve: Valve, override val index: Int): Action


data class RecurrenceState(
    val position: String,
    val elephantPosition: String,
    val minutesLeft: Long,
    private val openValves: Long) {

    fun toMutable() = MutableRecurrenceState(position, elephantPosition, minutesLeft, openValves)

    fun isOpen(valveIndex: Int): Boolean = (1L shl (valveIndex + 1)) and openValves != 0L

    fun allOpen(valves: List<Valve>): Boolean {
        for (i in 0..<valves.size) {
            if (valves[i].rate > 0 && !isOpen(i)) {
                return false
            }
        }

        return true
    }

    fun allPressure(pressure: Long): Long = minutesLeft * pressure

    fun tick(): RecurrenceState = RecurrenceState(position, elephantPosition, minutesLeft - 1, openValves)

    companion object {
        fun create() = RecurrenceState("AA", "AA", 26, 0)
    }
}

data class MutableRecurrenceState(
    var position: String,
    var elephantPosition: String,
    var minutesLeft: Long,
    private var openValves: Long) {

    fun toRecurrence() = RecurrenceState(position, elephantPosition, minutesLeft, openValves)

    fun openValve(valveIndex: Int) {
        openValves = openValves or (1L shl (valveIndex + 1))
    }
}

// How much is released in one minute.
fun releasePressure(valveList: List<Valve>, state: RecurrenceState): Long {
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
    val pressure = releasePressure(valves, state)

    // TODO: What TODO? Still too much mem usage even with memoization + this early return that was hinted at.
    if (state.allOpen(valves)) {
        val allPressure = state.allPressure(pressure)
        cache[state] = allPressure
        return allPressure
    }

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

    var elephantValveIndex: Int? = null
    var elephantValve: Valve? = null
    for (i in 0..<valves.size) {
        if (valves[i].name == state.elephantPosition) {
            elephantValveIndex = i
            elephantValve = valves[i]
        }
    }

    if (elephantValve == null || elephantValveIndex == null) {
        error("Valve ${state.elephantPosition} not found")
    }

    val humanActions = getActions(valve, valveIndex, ActionSubject.HUMAN)
    val elephantActions = getActions(elephantValve, elephantValveIndex, ActionSubject.ELEPHANT)

    val tickedState = state.tick()

    var max = 0L

    for (action in humanActions) {
        for (elephantAction in elephantActions) {
            doAction(tickedState, action)?.let {
                doAction(it, elephantAction)
            }?.let {
                if (it.position != it.elephantPosition) {
                    val flow = findMaxFlow(cache, valves, it)
                    if (flow > max) {
                        max = flow
                    }
                }
            }
        }
    }

    val result = pressure + max
    cache[state] = result
    return result
}

fun doAction(state: RecurrenceState, action: Action): RecurrenceState? {
    var newState: MutableRecurrenceState? = state.toMutable()

    when (action) {
        is OpenValveAction -> if (!state.isOpen(action.index) && action.valve.rate > 0) {
            newState!!.openValve(action.index)
        } else {
            newState = null
        }
        is MoveAction -> when (action.subject) {
            ActionSubject.HUMAN -> newState!!.position = action.nextValveName
            ActionSubject.ELEPHANT -> newState!!.elephantPosition = action.nextValveName
        }
    }

    return newState?.toRecurrence()
}

fun getActions(valve: Valve, index: Int, subject: ActionSubject): List<Action> {
    var actions = mutableListOf<Action>()

    actions.add(OpenValveAction(valve, index))

    for (neighbor in valve.neighbors) {
        actions.add(MoveAction(valve, index, neighbor, subject))
    }

    return actions.toList()
}

fun main() {
    val valves = readValves()
    val score = findMaxFlow(mutableMapOf<RecurrenceState, Long>(), valves, RecurrenceState.create())

    println("Answer = $score")
}
