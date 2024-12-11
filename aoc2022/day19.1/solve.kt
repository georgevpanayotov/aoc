import java.util.Scanner
import net.panayotov.util.Scanners

const val ORE = "ore"

const val CLAY = "clay"

const val OBSIDIAN = "obsidian"

const val GEODE = "geode"

const val MAX_STEPS = 24

val ROBOT_NAME =
    mapOf(
        ORE to "ore-collecting",
        CLAY to "clay-collecting",
        OBSIDIAN to "obsidian-collecting",
        GEODE to "geode-cracking",
    )

data class Cost(val amount: Int, val resource: String)

data class Blueprint(val number: Int, val costs: Map<String, List<Cost>>) {
    // Returns any robot that could potentially be built right now. This doesn't mean that we have
    // enough resources yet but we do have enough robots that are collecting the necessary
    // resources so it will just take time to wait for them to produce.
    fun possibleToBuild(state: CollectionState): List<String> {
        val possible = mutableListOf<String>()

        for (entry in costs.entries) {
            val robot = entry.key
            val robotCosts = entry.value

            val canAfford =
                robotCosts.fold(true) { acc, value ->
                    // If we have at least 1 robot then this resource can eventually be satisfied.
                    acc && (state.robots[value.resource] ?: 0 > 0)
                }

            if (canAfford) {
                possible.add(robot)
            }
        }

        return possible.toList()
    }

    fun spend(robot: String, resources: MutableMap<String, Int>) {
        for (cost in costs[robot] ?: listOf()) {
            resources[cost.resource] = resources[cost.resource]!! - cost.amount
        }
    }

    fun canBuild(robot: String, resources: Map<String, Int>): Boolean =
        costs[robot]?.fold(true) { acc, cost ->
            acc && (resources[cost.resource] ?: 0 >= cost.amount)
        } ?: error("Why is $robot missing from ${costs.keys}?")

    fun produce(updatedRobots: MutableMap<String, Int>, updatedResources: MutableMap<String, Int>) {
        for (entry in updatedRobots) {
            val resource = entry.key

            val count = updatedResources.getOrPut(resource) { 0 }
            updatedResources[resource] = count + entry.value
        }
    }
}

data class CollectionState(
    val step: Int,
    val robots: Map<String, Int>,
    val resources: Map<String, Int>,
)

fun qualityLevel(state: CollectionState, blueprint: Blueprint): Int {
    if (state.step > MAX_STEPS) {
        return state.resources[GEODE] ?: 0
    }

    var max: Int? = null

    val possible =
        if (blueprint.canBuild(GEODE, state.resources)) {
            listOf(GEODE)
        } else {
            blueprint.possibleToBuild(state)
        }

    for (robot in possible) {
        if (robot != GEODE) {
            val X = state.robots[robot]!!
            val Y = state.resources[robot]!!
            val T = MAX_STEPS - state.step

            var maxNeeded: Int? = null

            for (robotCost in blueprint.costs.entries) {
                for (cost in robotCost.value) {
                    if (cost.resource == robot) {
                        if (maxNeeded == null || cost.amount > maxNeeded) {
                            maxNeeded = cost.amount
                        }
                    }
                }
            }

            if (maxNeeded != null && X * T + Y >= T * maxNeeded) {
                continue
            }
        }
        val updatedResources = state.resources.toMutableMap()
        val updatedRobots = state.robots.toMutableMap()
        var currStep = state.step

        var didBuild = false
        // Be careful to pass `updatedResources` to `canBuild` or this will never terminate.
        while (!didBuild && currStep <= MAX_STEPS) {

            if (blueprint.canBuild(robot, updatedResources)) {

                blueprint.produce(updatedRobots, updatedResources)

                blueprint.spend(robot, updatedResources)

                val robotCount = updatedRobots.getOrPut(robot) { 0 }
                updatedRobots[robot] = robotCount + 1

                didBuild = true
            } else {
                blueprint.produce(updatedRobots, updatedResources)
            }
            currStep++
        }

        val recur =
            qualityLevel(
                CollectionState(currStep, updatedRobots.toMap(), updatedResources.toMap()),
                blueprint,
            )

        if (max == null || recur > max) {
            max = recur
        }
    }

    if (max == null) {
        return state.resources[GEODE] ?: 0
    }

    return max
}

fun qualityLevel(blueprint: Blueprint): Int {
    val robots = mutableMapOf<String, Int>()
    robots[ORE] = 1
    robots[CLAY] = 0
    robots[OBSIDIAN] = 0
    robots[GEODE] = 0

    val resources = mutableMapOf<String, Int>()
    resources[ORE] = 0
    resources[CLAY] = 0
    resources[OBSIDIAN] = 0
    resources[GEODE] = 0

    val level = qualityLevel(CollectionState(1, robots, resources), blueprint)

    return blueprint.number * level
}

fun main() {
    val blueprints = mutableListOf<Blueprint>()

    for (scanner in Scanners) {
        scanner.useDelimiter("(\\.|:)")

        val blueprint = scanner.next()
        val blueprintScanner = Scanner(blueprint)
        blueprintScanner.next()
        val number = blueprintScanner.nextInt()

        val costs = buildMap {
            while (scanner.hasNext()) {
                val robotScanner = Scanner(scanner.next())

                robotScanner.next()
                val robot = robotScanner.next()
                robotScanner.next()
                robotScanner.next()

                val costs = buildList {
                    while (robotScanner.hasNext()) {
                        val amount = robotScanner.nextInt()
                        val resource = robotScanner.next()
                        add(Cost(amount, resource))

                        if (robotScanner.hasNext()) {
                            robotScanner.next()
                        }
                    }
                }

                put(robot, costs)
            }
        }
        blueprints.add(Blueprint(number, costs))
    }

    val score = blueprints.map(::qualityLevel).reduce { acc, value -> acc + value }

    println("Answer = $score")
}
