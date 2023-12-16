import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

data class Beam(val position: Pair<Int, Int>, val direction: Pair<Int, Int>) {
    companion object {
        fun create(): Beam = Beam(Pair(0, 0), Pair(0, 1))
    }

    fun step() = modified {
        position = Pair(position.first + direction.first,
                        position.second + direction.second)
    }

    fun valid(grid: List<String>): Boolean =
        position.first >= 0 && position.first < grid.size && position.second >= 0 &&
        position.second < grid[position.first].length

    fun handleSplitter(splitter: Pair<Int, Int>): Pair<Beam, Beam?> =
        if (splitter.first * direction.first + splitter.second * direction.second == 0) {
            Pair(modified {
                direction = splitter
            }, modified {
                direction = Pair(-splitter.first, -splitter.second)
            })
        } else {
            Pair(this, null)
        }

    fun handleMirror(mirror: Char) = modified {
        if (mirror == '/') {
            direction = Pair(-direction.second, -direction.first)
        } else if (mirror == '\\') {
            direction = Pair(direction.second, direction.first)
        }
    }

    fun modified(action: MutableBeam.() -> Unit):Beam {
        val mutable = toMutable()
        mutable.action()
        return mutable.toBeam()
    }

    private fun toMutable() = MutableBeam(position, direction)
}

data class MutableBeam(var position: Pair<Int, Int>, var direction: Pair<Int, Int>) {
    fun toBeam() = Beam(position, direction)
}

fun readGrid(): List<String> {
    var grid = mutableListOf<String>()

    for (line in Lines) {
        grid.add(line)
    }

    return grid.toList()
}

fun computeBeams(grid: List<String>): Int {
    // How many beams in each cell of the grid.
    val beamGrid = grid.map {
        it.map {
            0
        }.toMutableList()
    }

    var currentBeams = mutableListOf(Beam.create())
    val cycleSet = mutableSetOf<Beam>()

    while (currentBeams.size > 0) {
        var beam: Beam? = currentBeams.removeAt(0)

        while (beam != null) {

            var splitBeam: Beam? = null

            beamGrid[beam.position.first][beam.position.second]++

            when (grid[beam.position.first][beam.position.second]) {
                '|' -> {
                    val beams = beam.handleSplitter(Pair(1, 0))
                    beam = beams.first
                    splitBeam = beams.second
                }
                '-' -> {
                    val beams = beam.handleSplitter(Pair(0, 1))
                    beam = beams.first
                    splitBeam = beams.second
                }
                '/' -> beam = beam.handleMirror('/')
                '\\' -> beam = beam.handleMirror('\\')
                else -> Unit
            }

            beam = beam.step()
            if (cycleSet.contains(beam)) {
                break
            }

            cycleSet.add(beam)

            if (splitBeam != null) {
                splitBeam = splitBeam.step()
                if (splitBeam.valid(grid)) {
                    currentBeams.add(splitBeam)
                }
            }

            if (!beam.valid(grid)) {
                beam = null
            }
        }
    }

    return beamGrid.fold(0) { rowAcc, row ->
        rowAcc + row.fold(0) { acc, value ->
            acc + if (value > 0) 1 else 0
        }
    }
}

fun main() {
    val grid = readGrid()
    val score = computeBeams(grid)

    println("Answer = $score")
}
