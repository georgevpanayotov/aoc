import java.util.ArrayDeque
import java.util.Scanner
import net.panayotov.util.Lines

fun partialMatch(outer: String, offset: Int, sub: String): Boolean {
    var matches = true

    for (i in 0..<sub.length) {
        var iOuter = offset + i
        if (iOuter >= outer.length || outer[iOuter] != sub[i]) {
            matches = false
        }
    }

    return matches
}

data class State(val offset: Int, val pattern: Int)

fun canMatch(design: String, patterns: List<String>): Boolean {
    val stack = ArrayDeque<State>()
    val deadEnds = mutableSetOf<String>()
    val soFar = StringBuilder()

    for (i in 0..<patterns.size) {
        if (partialMatch(design, 0, patterns[i])) {
            stack.add(State(0, i))
            soFar.append(patterns[i])
            break
        }
    }

    while (!stack.isEmpty()) {
        var state = stack.getLast()

        var newPattern: Int? = null
        val newOffset = state.offset + patterns[state.pattern].length

        for (i in 0..<patterns.size) {
            if (
                partialMatch(design, newOffset, patterns[i]) &&
                    !deadEnds.contains(soFar.toString() + patterns[i])
            ) {
                newPattern = i
                break
            }
        }

        if (newPattern == null) {
            // We didn't find the new pattern, ditch at least one state from the stack and try to
            // replace it. If it can't be replaced, keep searching for ones before it that can be
            // replaced.
            deadEnds.add(soFar.toString())

            var updated = false
            while (!updated && !stack.isEmpty()) {
                state = stack.removeLast()
                soFar.deleteRange(soFar.length - patterns[state.pattern].length, soFar.length)

                for (i in state.pattern + 1..<patterns.size) {
                    if (
                        partialMatch(design, state.offset, patterns[i]) &&
                            !deadEnds.contains(soFar.toString() + patterns[i])
                    ) {
                        stack.addLast(State(state.offset, i))
                        soFar.append(patterns[i])
                        updated = true

                        // If this new state completes the design, return true.
                        if (state.offset + patterns[i].length == design.length) {
                            return true
                        }
                        break
                    }
                }

                if (!updated) {
                    deadEnds.add(soFar.toString())
                }
            }
        } else {
            // We found a new pattern, append it and see if it completes the design.
            soFar.append(patterns[newPattern])
            if (newOffset + patterns[newPattern].length == design.length) {
                return true
            } else {
                stack.addLast(State(newOffset, newPattern))
            }
        }
    }

    return false
}

fun main() {
    val patterns = mutableListOf<String>()
    val designs = mutableListOf<String>()

    val line = readLine()?.let(::Scanner)
    if (line == null) {
        error("No input")
    }

    line.useDelimiter(", ")
    while (line.hasNext()) {
        patterns.add(line.next())
    }

    readLine()

    for (line in Lines) {
        designs.add(line)
    }

    val score = designs.filter { canMatch(it, patterns) }.size

    println("Answer = $score")
}
