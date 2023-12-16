import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax
import net.panayotov.util.GraphLike
import net.panayotov.util.edsger

import java.util.Scanner

data class Valve(val name: String, val rate: Long, val neighbors: List<String>)

class ValveGraph(val valves: List<Valve>) : GraphLike<Valve> {
    override fun getNeighbors(node: Valve): List<Valve> {
        return node.neighbors.map(this::getIndex).map {
            valves[it]
        }
    }

    override fun getNodes(): List<Valve> {
        return valves
    }

    fun getIndex(name: String): Int {
        for (i in 0..<valves.size) {
            if (valves[i].name == name) {
                return i
            }
        }

        error("Unknown valve $name")
    }

    fun getIndex(valve: Valve): Int {
        return getIndex(valve.name)
    }
}

data class RecurrenceState(
    val position: String,
    val destination: String?,
    val elephantPosition: String,
    val elephantDestination: String?,
    val minutesLeft: Long,
    private val openValves: Long) {

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

    fun tick() = modified {
        minutesLeft--
    }

    fun potentialValvesCount(valves: List<Valve>): Int {
        var count = 0
        for (i in 0..<valves.size) {
            if (!isOpen(i) && valves[i].rate > 0) {
                count++
            }
        }

        return count
    }

    // Create a new recurrence based on this one with custom modifications.
    fun modified(modifier: MutableRecurrenceState.() -> Unit): RecurrenceState {
        val mutable = toMutable()
        mutable.modifier()
        return mutable.toRecurrence()
    }

    private fun toMutable() =
        MutableRecurrenceState(position, destination, elephantPosition,elephantDestination,
                               minutesLeft, openValves)

    companion object {
        fun create() = RecurrenceState("AA", null, "AA", null, 26, 0)
    }
}

data class MutableRecurrenceState(
    var position: String,
    var destination: String?,
    var elephantPosition: String,
    var elephantDestination: String?,
    var minutesLeft: Long,
    private var openValves: Long) {

    fun toRecurrence() =
        RecurrenceState(position, destination, elephantPosition, elephantDestination, minutesLeft,
                        openValves)

    fun openValve(valveIndex: Int) {
        if (toRecurrence().isOpen(valveIndex)) {
            error("Valve $valveIndex already open")
        }

        openValves = openValves or (1L shl (valveIndex + 1))
    }
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

fun findMaxFlow(cache: MutableMap<RecurrenceState, Long>, valves: List<Valve>, allPaths: AllPaths,
                state: RecurrenceState): Long {
    if (state.minutesLeft < 1) {
        return 0
    }

    val cached = cache[state]
    if (cached != null) {
        return cached
    }

    // A minute will pass as we do our turn.
    val pressure = releasePressure(valves, state)

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

    var nextState = state.tick()
    var humanMoved = false
    var elephantMoved = false

    if (nextState.destination != null) {
        if (nextState.destination == nextState.position) {
            nextState = nextState.modified {
                openValve(valveIndex)
                destination = null
            }
        } else {
            nextState = nextState.modified {
                val path = allPaths[Pair(valve, findValve(destination!!, valves).second)]

                if (path == null) {
                    error("Path not found for $position to $destination")
                } else if (path.size < 2) {
                    error("Invalid path for $position to $destination")
                }

                position = path[1].name
            }
        }
        humanMoved = true
    }

    if (nextState.elephantDestination != null) {
        if (nextState.elephantDestination == nextState.elephantPosition) {
            nextState = nextState.modified {
                openValve(elephantValveIndex)
                elephantDestination = null
            }
        } else {
            nextState = nextState.modified {
                val path = allPaths[Pair(elephantValve, findValve(elephantDestination!!, valves).second)]

                if (path == null) {
                    error("Path not found for $elephantPosition to $elephantDestination")
                } else if (path.size < 2) {
                    error("Invalid path for $elephantPosition to $elephantDestination")
                }

                elephantPosition = path[1].name
            }
        }
        elephantMoved = true
    }

    val result = pressure + if (humanMoved && elephantMoved) {
        // Both have already moved so no choices to make.
        findMaxFlow(cache, valves, allPaths, nextState)
    } else {
        var max = 0L
        val potentialCount = nextState.potentialValvesCount(valves)

        if (humanMoved) {
            for (i in 0..<valves.size) {
                if (nextState.isOpen(i) || valves[i].rate == 0L) {
                    continue
                }

                if (nextState.destination == valves[i].name) {
                    continue
                }

                val flow = findMaxFlow(cache, valves, allPaths, nextState.modified {
                    elephantDestination = valves[i].name
                })
                if (flow > max) {
                    max = flow
                }
            }
        } else if (elephantMoved) {
            for (i in 0..<valves.size) {
                if (nextState.isOpen(i) || valves[i].rate == 0L) {
                    continue
                }

                if (nextState.elephantDestination == valves[i].name) {
                    continue
                }

                val flow = findMaxFlow(cache, valves, allPaths, nextState.modified {
                    destination = valves[i].name
                })
                if (flow > max) {
                    max = flow
                }
            }
        } else if (potentialCount >= 2) {
            for (i in 0..<valves.size) {
                if (nextState.isOpen(i) || valves[i].rate == 0L) {
                    continue
                }
                for (j in 0..<valves.size) {
                    if(nextState.isOpen(j) || valves[j].rate == 0L) {
                        continue
                    }

                    if (i == j) {
                        continue
                    }

                    val flow = findMaxFlow(cache, valves, allPaths, nextState.modified {
                        destination = valves[i].name
                        elephantDestination = valves[j].name
                    })
                    if (flow > max) {
                        max = flow
                    }
                }
            }
        } else {
            for (i in 0..<valves.size) {
                if(nextState.isOpen(i) || valves[i].rate == 0L) {
                    continue
                }
                val human = findMaxFlow(cache, valves, allPaths, nextState.modified {
                    destination = valves[i].name
                })

                val elephant = findMaxFlow(cache, valves, allPaths, nextState.modified {
                    elephantDestination = valves[i].name
                })
                max = if (human > elephant) {
                    human
                } else {
                    elephant
                }
            }
        }

        max
    }

    cache[state] = result
    return result
}

typealias AllPaths = Map<Pair<Valve, Valve>, List<Valve>>

// All pairs shortest paths.
fun findAllPaths(valves: List<Valve>): AllPaths {
    val graph = ValveGraph(valves)
    val paths = mutableMapOf<Pair<Valve, Valve>, List<Valve>>()

    for (valve in valves) {
        val valvePaths = edsger(valve, graph)
        for (destValve in valvePaths.keys) {
            paths[Pair(valve, destValve)] = valvePaths[destValve]!!
        }
    }

    return paths.toMap()

}

fun main() {
    val valves = readValves()
    val allPaths = findAllPaths(valves)

    val score = findMaxFlow(mutableMapOf<RecurrenceState, Long>(), valves, allPaths,
                            RecurrenceState.create())
    println("Answer = $score")
}
