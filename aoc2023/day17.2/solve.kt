import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger
import net.panayotov.util.times
import net.panayotov.util.toDirection

data class State(val position: Point, val direction: Direction?, val forwardCount: Int) {
    constructor(position: Point) : this(position, null, 0)

    fun neighbors() =
        if (direction == null) {
            Direction.cardinal.map { State(position + 4 * it.vector, it, 4) }
        } else {
            val leftDirection = direction.vector.rotateLeft().toDirection()!!
            val rightDirection = direction.vector.rotateRight().toDirection()!!

            // Since we need to make sure that the final position is also at least 4 steps along,
            // then we automatically take 4 steps for each turn. Otherwise, we might pick a turn
            // that gets us to the end in less than 4 steps.
            val leftState = State(position + 4 * leftDirection.vector, leftDirection, 4)
            val rightState = State(position + 4 * rightDirection.vector, rightDirection, 4)
            val forwardState = State(position + direction.vector, direction, forwardCount + 1)

            if (forwardCount < 4) {
                listOf(forwardState)
            } else if (forwardCount < 10) {
                listOf(forwardState, leftState, rightState)
            } else {
                listOf(leftState, rightState)
            }
        }
}

class GridGraph(val grid: Grid<Char>) : GraphLike<State> {
    override fun getNeighbors(node: State) = node.neighbors().filter { grid.isValid(it.position) }

    override fun getEdgeWeight(from: State, to: State): Int {
        if (to.direction == null) {
            error("Can't be going to a starting state $to.")
        }

        var point = from.position
        var weight = 0

        // Since individual states can count for multiple positions, add up all of the positions
        // between the old and the new.
        while (point != to.position) {
            point = point + to.direction.vector

            // Fencepost problem actually works in our favor here because we don't want to count the
            // starting weight. Just gotta make sure we add the weight after incrementing the
            // position.
            weight += (grid[point] - '0').toInt()
        }

        return weight
    }
}

fun main() {
    var score = 0
    val grid = Grid.read('0')
    val start = Point(0L, grid.height - 1L)
    val end = Point(grid.width - 1L, 0L)
    val graph = GridGraph(grid)

    val paths = edsger(State(start), graph) { it.position == end }
    var path: List<State>? = null

    for (state in paths.keys) {
        if (state.position == end) {
            path = paths[state]
            break
        }
    }

    if (path == null) {
        error("No path found.")
    }

    var lastState: State? = null

    for (i in 0..<path.size) {
        val state = path[i]
        if (lastState != null) {
            // Each step might include more than 1 step so use the weight utility to get the right
            // score.
            score += graph.getEdgeWeight(lastState, state)
        }
        lastState = state
    }

    println("Answer = $score")
}
