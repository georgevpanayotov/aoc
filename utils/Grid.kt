package net.panayotov.util

class Grid<T>(val width: Int, val height: Int, private val default: T) {
    private val gridValues: MutableList<MutableList<T>> = mutableListOf<MutableList<T>>()

    init {
        for (y in 0..<height) {
            val row = mutableListOf<T>()
            for (x in 0..<width) {
                row.add(default)
            }

            gridValues.add(row)
        }
    }

    // Construct if the default might be a mutable object. We don't want to put the same mutable
    // object in every spot in the grid so generate each one independently.
    constructor(
        width: Int,
        height: Int,
        defaultGenerator: () -> T,
    ) : this(width, height, defaultGenerator()) {
        for (y in 0..<height) {
            for (x in 0..<width) {
                gridValues[y][x] = defaultGenerator()
            }
        }
    }

    operator fun get(point: Point): T {
        return this[point.x, point.y]
    }

    operator fun get(x: Long, y: Long): T {
        assertValid(x, y)
        val (row, col) = rowCol(x, y)
        return gridValues[row][col]
    }

    operator fun set(point: Point, value: T) {
        this[point.x, point.y] = value
    }

    operator fun set(x: Long, y: Long, value: T) {
        assertValid(x, y)
        val (row, col) = rowCol(x, y)
        gridValues[row][col] = value
    }

    fun isValid(point: Point): Boolean = isValid(point.x, point.y)

    fun isValid(x: Long, y: Long): Boolean = isXValid(x) && isYValid(y)

    // A shallow copy of the grid. If a deep copy is necessary (such as in the case of mutable
    // entries) then use the map method instead.
    fun copy(): Grid<T> {
        val copyGrid = Grid<T>(width, height, default)

        forPoints { point, value -> copyGrid[point] = value }

        return copyGrid
    }

    // A grid of the same size but each element having `transform` applied to it.
    fun <R> map(transform: (T) -> R): Grid<R> {
        val mappedGrid = Grid<R>(width, height, transform(default))

        forPoints { point, value -> mappedGrid[point] = transform(value) }

        return mappedGrid
    }

    fun forPoints(action: (point: Point, value: T) -> Unit) {
        for (x in 0L..<width) {
            for (y in 0L..<height) {
                action(Point(x, y), this[x, y])
            }
        }
    }

    fun forPoints(action: (point: Point) -> Unit) = forPoints { point, value -> action(point) }

    override fun toString(): String {
        val stringRep = StringBuilder()

        for (row in gridValues) {
            for (value in row) {
                stringRep.append(value.toString())
            }
            stringRep.append("\n")
        }

        return stringRep.toString()
    }

    companion object {
        // Reads a grid as a plaintext input from stdin. Reads all of stdin.
        fun read(default: Char): Grid<Char> = read(default, Lines.asSequence().toList())

        // Reads a grid as a plaintext input from stdin with a specified height.
        fun read(default: Char, height: Int): Grid<Char> =
            read(default, Lines.asSequence().take(height).toList())

        // Reads a grid as a plaintext input from stdin until a sentinel `stop` is reached.
        fun read(default: Char, stop: String): Grid<Char> {
            val lines = mutableListOf<String>()
            for (line in Lines) {
                if (line != stop) {
                    lines.add(line)
                } else {
                    break
                }
            }

            return read(default, lines)
        }

        // Reads a grid as a plaintext input from a list of strings.
        fun read(default: Char, lines: List<String>): Grid<Char> {
            var width: Int? = null

            for (i in 1..lines.size) {
                val line = lines[i - 1]

                if (width == null) {
                    width = line.length
                } else {
                    if (line.length != width) {
                        error(
                            "Line:$i: Width mismatch. Current width ${line.length} doesn't match earlier width $width"
                        )
                    }
                }
            }

            if (width == null) {
                // Kinda sketch but I don't want to make all of the read functions have to return
                // nulls.
                return Grid(0, 0, default)
            }

            val grid = Grid(width, lines.size, default)

            for (row in 0..<lines.size) {
                val line = lines[row]

                for (col in 0..<line.length) {
                    grid.gridValues[row][col] = line[col]
                }
            }

            return grid
        }
    }

    // The internal representation of the grid is actually in the form of [row][col] because it is
    // easier to read from stdin and convert to a string. This method converts from (x, y) to
    // [row][col]
    private fun rowCol(x: Long, y: Long): Pair<Int, Int> =
        Pair((height - y - 1L).toInt(), x.toInt())

    private fun isXValid(x: Long): Boolean = x >= 0 && x < width

    private fun isYValid(y: Long): Boolean = y >= 0 && y < height

    private fun assertValid(x: Long, y: Long) {
        if (!isXValid(x)) {
            error("$x is out of range: [0, $width)")
        }
        if (!isYValid(y)) {
            error("$y is out of range: [0, $height)")
        }
    }
}
