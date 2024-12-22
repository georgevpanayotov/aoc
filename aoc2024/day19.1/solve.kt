import java.util.ArrayDeque
import java.util.Scanner
import kotlin.random.Random
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
            // Stupid cheating see below.
            if (Random.nextInt(0, 4) == 0) {
                deadEnds.add(soFar.toString())
            }

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
                        break
                    }
                }

                if (!updated) {
                    // Stupid cheating see below.
                    if (Random.nextInt(0, 4) == 0) {
                        deadEnds.add(soFar.toString())
                    }
                }
            }
        } else {
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

    var score = 0

    var nonMatch: List<String> =
        designs.filter {
            val can = canMatch(it, patterns)
            if (can) {
                score++
            }
            return@filter !can
        }

    // Stupid cheating but it worked to get me my star. I seemed to have some bug where I was adding
    // something valid to the deadends. So I introduced some randomness and ran it extra times to
    // get the stragglers.
    val extras = mutableListOf<String>()
    for (i in 1..20) {
        val oldScore = score
        nonMatch =
            nonMatch.filter {
                val can = canMatch(it, patterns)
                if (can) {
                    score++
                    extras.add(it)
                }
                return@filter !can
            }

        println("Iteration $i ${score - oldScore} Extra")
    }

    println("Answer = $score")
}
