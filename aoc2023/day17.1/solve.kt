import net.panayotov.util.Direction
import net.panayotov.util.GraphLike
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.edsger
import net.panayotov.util.toDirection

data class State(val position: Point, val direction: Direction?, val forwardCount: Int) {
    constructor(position: Point) : this(position, null, 0)

    fun neighbors() =
        if (direction == null) {
            Direction.cardinal.map { State(position + it.vector, it, 1) }
        } else {
            val leftDirection = direction.vector.rotateLeft().toDirection()!!
            val rightDirection = direction.vector.rotateRight().toDirection()!!

            val leftState = State(position + leftDirection.vector, leftDirection, 1)
            val rightState = State(position + rightDirection.vector, rightDirection, 1)

            if (forwardCount == 3) {
                listOf(leftState, rightState)
            } else {
                listOf(
                    State(position + direction.vector, direction, forwardCount + 1),
                    leftState,
                    rightState,
                )
            }
        }
}

class GridGraph(val grid: Grid<Char>) : GraphLike<State> {
    override fun getNeighbors(node: State) = node.neighbors().filter { grid.isValid(it.position) }

    override fun getEdgeWeight(from: State, to: State) = (grid[to.position] - '0').toInt()
}

fun main() {
    var score = 0
    val grid = Grid.read('0')
    val start = Point(0L, grid.height - 1L)
    val end = Point(grid.width - 1L, 0L)

    val paths = edsger(State(start), GridGraph(grid)) { it.position == end }
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

    for (i in 1..<path.size) {
        score += (grid[path[i].position] - '0').toInt()
    }

    println("Answer = $score")
}
