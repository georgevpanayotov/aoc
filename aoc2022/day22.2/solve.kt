import java.util.ArrayDeque
import net.panayotov.util.Direction
import net.panayotov.util.Grid
import net.panayotov.util.Point
import net.panayotov.util.times
import net.panayotov.util.toDirection

fun sideTraverseTransforms(faceSize: Long) =
    mapOf(
        Direction.EAST to Affine(Point(1, 0), Point(0, 1), Point(-faceSize, 0)),
        Direction.WEST to Affine(Point(1, 0), Point(0, 1), Point(faceSize, 0)),
    )

// For reading, all of the sides (next to each other) are oriented in the same direction. So no
// transform necessary.
fun sideReadTransforms(faceSize: Long) =
    mapOf(Direction.EAST to identity(), Direction.WEST to identity())

fun Point.offset() = Point(if (x >= 0) 1 else 0, if (y >= 0) 1 else 0)

data class Affine(
    private val vector1: Point,
    private val vector2: Point,
    private val vector3: Point,
) {
    // An affine transform is a linear transform + a constant offset.
    fun transform(vector: Point): Point {
        // Pre-adjust. Rotation matrices don't really work in a zero-indexed way.
        val toTransform = vector + vector.offset()

        val linearTransformed =
            Point(
                vector1.x * toTransform.x + vector2.x * toTransform.y,
                vector1.y * toTransform.x + vector2.y * toTransform.y,
            )

        // Remove the adjustment post transform. Then do the non-linear bit.
        return linearTransformed - linearTransformed.offset() + vector3
    }

    fun linear(vector: Point) =
        Point(
            vector1.x * vector.x + vector2.x * vector.y,
            vector1.y * vector.x + vector2.y * vector.y,
        )

    // Returns a linear transformation that is the inverse of this one's linear portion. Drops the
    // offset.
    fun invertLinear(): Affine {
        val a = vector1.x
        val b = vector2.x
        val c = vector1.y
        val d = vector2.y

        // NOTE: Big bug here but we ignore the determinant part of the inversion here because we
        // know we will be dealing exclusively with rotation/identity matrices which all will end up
        // with det == 1

        return Affine(Point(d, -c), Point(-b, a), Point(0, 0))
    }
}

// The faces of the cube.
enum class Face {
    FRONT,
    RIGHT,
    BACK,
    LEFT,
    TOP,
    BOTTOM,
}

fun rotateLeft(offset: Point) = Affine(Point(0, 1), Point(-1, 0), offset)

fun rotateRight(offset: Point) = Affine(Point(0, -1), Point(1, 0), offset)

fun rotate180(offset: Point) = Affine(Point(-1, 0), Point(0, -1), offset)

fun identity() = Affine(Point(1, 0), Point(0, 1), Point(0, 0))

// The direction here is relative to the current face.
// 3 maps:
// - Neighbors determines which face neighbors this one based on direction.
// - readTransforms determines the transform used for reading the face of the neighbor on that
// direction's side. Goes from the (faceSize, faceSize) coordinates being read to the coordinates in
// the same space to write.
// - transforms determines the transform in the standardized cube when leaving a face by a
// particular direction. The transformed coordinates are in the (faceSize x faceSize) space of the
// the neighbor subgrid.
class FaceConfig(
    val neighbors: Map<Direction, Face>,
    val readTransforms: Map<Direction, Affine>,
    val transforms: Map<Direction, Affine>,
)

// Stores info about the faces on input so that we can transform the answer back into the input grid
// position.
class FaceInput(
    // From 0,0 of the inputGrid
    val offset: Point,
    // A list of transforms to orient from a point in (faceSize, faceSize) to which direction it
    // faces in that space on the input grid.
    val transforms: List<Affine>,
)

// Represents a cube with a particular face size: 50 in the problem 4 in the sample. Breaks the cube
// into subGrid "faces" arranged in this standardized projection
//        ----------
//        |   top  |
// -------|--------|---------------
// | left | front  | right | back |
// -------|--------|---------------
//        | bottom |
//        ----------
// In this projection (0, 0) is always in the bottom left for each subgrid.
class Cube(val faceSize: Long) {
    var inputHeight: Int? = null
    val leftGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val frontGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val rightGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val backGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val topGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val bottomGrid = Grid(faceSize.toInt(), faceSize.toInt(), '.')
    val subGrids =
        mapOf(
            Face.LEFT to leftGrid,
            Face.FRONT to frontGrid,
            Face.RIGHT to rightGrid,
            Face.BACK to backGrid,
            Face.TOP to topGrid,
            Face.BOTTOM to bottomGrid,
        )

    val configs =
        mapOf(
            Face.FRONT to makeFrontConfig(),
            Face.RIGHT to makeRightConfig(),
            Face.BACK to makeBackConfig(),
            Face.LEFT to makeLeftConfig(),
            Face.TOP to makeTopConfig(),
            Face.BOTTOM to makeBottomConfig(),
        )

    val inputs = mutableMapOf<Face, FaceInput>()

    fun start(): Position {
        var curr = Position(Face.TOP, Point(0, faceSize - 1L), Direction.EAST)

        while (subGrids[Face.TOP]!![curr.point] != '.') {
            curr = curr.next()
        }

        return curr
    }

    // Read any cube projection into the standardized projection for this class.
    fun read() {
        data class Frame(val face: Face, val origin: Point, val transforms: List<Affine>)

        val inputGrid = Grid.read(' ', "")
        inputHeight = inputGrid.height

        val queue = ArrayDeque<Frame>()

        // The origin point of the "TOP" face by convention.
        queue.add(
            Frame(Face.TOP, inputGrid.start().point - Point(0, faceSize - 1), listOf(identity()))
        )
        val visited = mutableSetOf<Face>()

        while (queue.size > 0) {
            val (face, origin, transforms) = queue.removeFirst()
            if (!visited.add(face)) {
                continue
            }

            // Save the input for this face.
            inputs[face] = FaceInput(origin, transforms)

            // Step 1: read this grid.
            val subGrid = subGrids[face]!!

            for (x in 0L..<faceSize) {
                for (y in 0L..<faceSize) {
                    val point = Point(x, y)
                    val subGridPoint =
                        transforms.fold(point) { acc, transform -> transform.transform(acc) }

                    subGrid[subGridPoint] = inputGrid[point + origin]
                }
            }

            // Step 2: enqueue neighbors
            val config = configs[face]!!
            for (direction in Direction.cardinal) {
                // Go in the direction but make it relative to the current transforms.
                val offsetVector = direction.vector
                val transformedOffsetVector =
                    faceSize * transforms[transforms.size - 1].invertLinear().linear(offsetVector)
                val newOrigin = origin + transformedOffsetVector
                val newFace = config.neighbors[direction]!!

                if (inputGrid.isOnBoard(newOrigin)) {
                    val newTransform =
                        config.readTransforms[direction]?.let { listOf(it) } ?: listOf()

                    queue.add(Frame(newFace, newOrigin, transforms + newTransform))
                }
            }
        }
    }

    fun printCube() {
        val left = Point(0, faceSize)
        val front = Point(faceSize, faceSize)
        val right = Point(2 * faceSize, faceSize)
        val back = Point(3 * faceSize, faceSize)
        val top = Point(faceSize, 2 * faceSize)
        val bottom = Point(faceSize, 0)
        val offsets =
            mapOf(
                Face.LEFT to left,
                Face.FRONT to front,
                Face.RIGHT to right,
                Face.BACK to back,
                Face.TOP to top,
                Face.BOTTOM to bottom,
            )
        val grid = Grid(4 * faceSize.toInt(), 3 * faceSize.toInt(), ' ')

        for (face in Face.entries) {
            val offset = offsets[face]
            val subGrid = subGrids[face]
            if (subGrid == null || offset == null) {
                error("")
            }

            for (x in 0..<faceSize) {
                for (y in 0..<faceSize) {
                    val point = Point(x, y)
                    grid[point + offset] = subGrid[point]
                }
            }
        }

        println(grid)
    }

    private fun makeFrontConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.TOP,
                Direction.EAST to Face.RIGHT,
                Direction.SOUTH to Face.BOTTOM,
                Direction.WEST to Face.LEFT,
            ),
            sideReadTransforms(faceSize) +
                mapOf(Direction.NORTH to identity(), Direction.SOUTH to identity()),
            sideTraverseTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to Affine(Point(1, 0), Point(0, 1), Point(0, -faceSize)),
                    Direction.SOUTH to Affine(Point(1, 0), Point(0, 1), Point(0, faceSize)),
                ),
        )

    private fun makeRightConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.TOP,
                Direction.EAST to Face.BACK,
                Direction.SOUTH to Face.BOTTOM,
                Direction.WEST to Face.FRONT,
            ),
            sideReadTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotateLeft(Point(faceSize, 0)),
                    Direction.SOUTH to rotateRight(Point(0, faceSize)),
                ),
            sideTraverseTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotateLeft(Point(2 * faceSize, 0)),
                    Direction.SOUTH to rotateRight(Point(faceSize, faceSize)),
                ),
        )

    private fun makeBackConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.TOP,
                Direction.EAST to Face.LEFT,
                Direction.SOUTH to Face.BOTTOM,
                Direction.WEST to Face.RIGHT,
            ),
            sideReadTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotate180(Point(faceSize, faceSize)),
                    Direction.SOUTH to rotate180(Point(faceSize, faceSize)),
                ),
            sideTraverseTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotate180(Point(faceSize, 2 * faceSize)),
                    Direction.SOUTH to rotate180(Point(faceSize, 0)),
                ),
        )

    private fun makeLeftConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.TOP,
                Direction.EAST to Face.FRONT,
                Direction.SOUTH to Face.BOTTOM,
                Direction.WEST to Face.BACK,
            ),
            sideReadTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotateRight(Point(0, faceSize)),
                    Direction.SOUTH to rotateLeft(Point(faceSize, 0)),
                ),
            sideTraverseTransforms(faceSize) +
                mapOf(
                    Direction.NORTH to rotateRight(Point(-faceSize, faceSize)),
                    Direction.SOUTH to rotateLeft(Point(0, 0)),
                ),
        )

    private fun makeTopConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.BACK,
                Direction.EAST to Face.RIGHT,
                Direction.SOUTH to Face.FRONT,
                Direction.WEST to Face.LEFT,
            ),
            mapOf(
                Direction.NORTH to rotate180(Point(faceSize, faceSize)),
                Direction.EAST to rotateRight(Point(0, faceSize)),
                Direction.SOUTH to identity(),
                Direction.WEST to rotateLeft(Point(faceSize, 0)),
            ),
            mapOf(
                Direction.NORTH to rotate180(Point(faceSize, 2 * faceSize)),
                Direction.EAST to rotateRight(Point(0, 2 * faceSize)),
                Direction.SOUTH to Affine(Point(1, 0), Point(0, 1), Point(0, faceSize)),
                Direction.WEST to rotateLeft(Point(faceSize, faceSize)),
            ),
        )

    private fun makeBottomConfig() =
        FaceConfig(
            mapOf(
                Direction.NORTH to Face.FRONT,
                Direction.EAST to Face.RIGHT,
                Direction.SOUTH to Face.BACK,
                Direction.WEST to Face.LEFT,
            ),
            mapOf(
                Direction.NORTH to identity(),
                Direction.EAST to rotateLeft(Point(faceSize, 0)),
                Direction.SOUTH to rotate180(Point(faceSize, faceSize)),
                Direction.WEST to rotateRight(Point(0, faceSize)),
            ),
            mapOf(
                Direction.NORTH to Affine(Point(1, 0), Point(0, 1), Point(0, -faceSize)),
                Direction.EAST to rotateLeft(Point(faceSize, -faceSize)),
                Direction.SOUTH to rotate180(Point(faceSize, 0)),
                Direction.WEST to rotateRight(Point(0, 0)),
            ),
        )
}

fun Grid<Char>.isOnBoard(point: Point) = isValid(point) && this[point] != ' '

fun Grid<Char>.start(): Position {
    var curr = Position(Face.TOP, Point(0, height - 1L), Direction.EAST)

    while (this[curr.point] != '.') {
        curr = curr.next()
    }

    return curr
}

data class Position(val face: Face, val point: Point, val direction: Direction) {
    fun next() = Position(face, point + direction.vector, direction)
}

sealed interface Instruction {
    fun nextPosition(cube: Cube, position: Position): Position
}

data object TurnRight : Instruction {
    override fun nextPosition(cube: Cube, position: Position) =
        Position(
            position.face,
            position.point,
            position.direction.vector.rotateRight().toDirection()!!,
        )
}

data object TurnLeft : Instruction {
    override fun nextPosition(cube: Cube, position: Position) =
        Position(
            position.face,
            position.point,
            position.direction.vector.rotateLeft().toDirection()!!,
        )
}

data class Move(val count: Int) : Instruction {
    override fun nextPosition(cube: Cube, position: Position): Position {
        var hitWall = false
        var curr = position
        var moves = 0

        while (!hitWall && moves < count) {
            var grid = cube.subGrids[curr.face]!!
            var next = curr.next()

            if (!grid.isOnBoard(next.point)) {
                val config = cube.configs[curr.face]!!
                val transform = config.transforms[curr.direction]!!
                val nextFace = config.neighbors[curr.direction]!!
                val nextPoint = transform.transform(next.point)
                val nextDirection = transform.linear(next.direction.vector)

                grid = cube.subGrids[nextFace]!!

                next = Position(nextFace, nextPoint, nextDirection.toDirection()!!)
            }

            if (grid[next.point] == '.') {
                curr = next
                moves++
            } else if (grid[next.point] == '#') {
                hitWall = true
            }
        }

        return curr
    }
}

fun parseInstructions(line: String): List<Instruction> {
    var number = true
    var i = 0
    val instructions = mutableListOf<Instruction>()

    while (i < line.length) {
        if (line[i].isDigit()) {
            val numberBuilder = StringBuilder()
            while (i < line.length && line[i].isDigit()) {
                numberBuilder.append(line[i])
                i++
            }

            instructions.add(Move(numberBuilder.toString().toInt()))
        } else if (line[i] == 'R') {
            instructions.add(TurnRight)
            i++
        } else if (line[i] == 'L') {
            instructions.add(TurnLeft)
            i++
        }
    }

    return instructions.toList()
}

fun followInstructions(cube: Cube, instructions: List<Instruction>): Int {
    var curr = cube.start()

    for (instruction in instructions) {
        curr = instruction.nextPosition(cube, curr)
    }

    val input = cube.inputs[curr.face]!!

    for (x in 0L..<cube.faceSize) {
        for (y in 0L..<cube.faceSize) {
            val point = Point(x, y)
            val subGridPoint =
                input.transforms.fold(point) { acc, transform -> transform.transform(acc) }
            if (subGridPoint == curr.point) {
                val inputPoint = point + input.offset
                val trueDirection =
                    input.transforms[input.transforms.size - 1]
                        .invertLinear()
                        .linear(curr.direction.vector)
                        .rotateLeft()
                        .toDirection()
                return (1000 * (cube.inputHeight!! - inputPoint.y) +
                        4 * (inputPoint.x + 1) +
                        Direction.cardinal.indexOf(trueDirection))
                    .toInt()
            }
        }
    }

    error("Not found.")
}

// I used this to zero in on the right traversal transforms.
fun test(faceSize: Long) {
    val cube = Cube(faceSize)

    val points =
        listOf(
            Pair(Point(1, 0), Direction.SOUTH),
            Pair(Point(faceSize - 1, 1), Direction.EAST),
            Pair(Point(faceSize - 2, faceSize - 1), Direction.NORTH),
            Pair(Point(0, 1), Direction.WEST),
        )

    for (face in Face.entries) {
        val config = cube.configs[face]!!
        val grid = cube.subGrids[face]!!

        for ((point, direction) in points) {
            val neighborFace = config.neighbors[direction]!!
            val neighbor = cube.subGrids[neighborFace]!!
            val transform = config.transforms[direction]!!

            val newPoint = point + direction.vector
            val transformedPoint = transform.transform(newPoint)

            grid[point] = 'A'
            if (neighbor.isValid(transformedPoint)) {
                neighbor[transformedPoint] = 'B'
            } else {
                println("ERROR $transformedPoint")
            }

            println("$face $direction")
            cube.printCube()
            println("")

            grid[point] = '.'
            if (neighbor.isValid(transformedPoint)) {
                neighbor[transformedPoint] = '.'
            }
        }
    }
}

fun main(args: Array<String>) {
    val faceSize = if (args.size > 0) args[0].toLongOrNull() ?: 50L else 50L

    val cube = Cube(faceSize)
    cube.read()

    // NOTE: Works only if you pre-process the input into a perfect grid (i.e. pad it with spaces so
    // each line is the same length).with spaces so each line is the same length).with spaces so
    // each line is the same length).with spaces so each line is the same length).
    val instructions = parseInstructions(readLine()!!)
    val score = followInstructions(cube, instructions)
    println("Answer = $score")
}
