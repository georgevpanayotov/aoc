import java.util.ArrayDeque
import java.util.Scanner
import net.panayotov.util.Lines

// SolvedCount is how many were solved before this frame was added to the stack.
data class Frame(val offset: Int, val pattern: Int, val solvedCount: Long) {
    constructor(offset: Int, pattern: Int) : this(offset, pattern, 0)
}

class ProblemState(val design: String, val patterns: List<String>) {
    private val soFar = StringBuilder()
    private val cache = mutableMapOf<String, Long>()

    fun matchCount(): Long {
        val stack = ArrayDeque<Frame>()
        var solves = 0L

        findMatch(0, 0)?.let {
            stack.add(Frame(0, it))
            soFar.append(patterns[it])
        }

        var newPattern: Int? = null
        while (!stack.isEmpty()) {
            var frame = stack.getLast()

            val newOffset = frame.offset + patterns[frame.pattern].length

            val start = newPattern?.let { it + 1 } ?: 0
            newPattern = findMatch(start, newOffset)

            if (newPattern == null) {
                var updated = false

                while (!updated && !stack.isEmpty()) {
                    frame = stack.removeLast()
                    // Now that we are popping this frame we see how many new were solved since it
                    // was added to the stack. This is the number of solves we can expect whenver
                    // this prefix appears. It doesn't matter how the prefix was constructed, since
                    // we have no constraints on how patterns are appended to the prefix.
                    cache[soFar.toString()] = solves - frame.solvedCount
                    soFar.deleteRange(soFar.length - patterns[frame.pattern].length, soFar.length)

                    var start: Int? = frame.pattern + 1
                    while (start != null && start < patterns.size && !updated) {
                        val matchIdx = findMatch(start, frame.offset)

                        // If we got a match then we might need to keep iterating after that match
                        // unless we add to the stack (guarded by `updated`).
                        start = matchIdx?.let { it + 1 }

                        if (matchIdx != null) {
                            // If this new frame completes the design, count it in `solves`.
                            if (solves(frame.offset, matchIdx)) {
                                solves++
                            } else {
                                val cachedSolves = checkCache(matchIdx)
                                if (cachedSolves != null && cachedSolves > 0) {
                                    solves += cachedSolves
                                } else {
                                    updated = true
                                    stack.addLast(Frame(frame.offset, matchIdx, solves))
                                    soFar.append(patterns[matchIdx])
                                }
                            }
                        }
                    }
                }
            } else {
                // We found a new pattern, append it and see if it completes the design.
                if (solves(newOffset, newPattern)) {
                    solves++
                } else {
                    val cachedSolves = checkCache(newPattern)
                    if (cachedSolves != null && cachedSolves > 0) {
                        solves += cachedSolves
                    } else {
                        soFar.append(patterns[newPattern])
                        stack.addLast(Frame(newOffset, newPattern, solves))
                        newPattern = null
                    }
                }
            }
        }

        return solves
    }

    private fun deadEnd(pattern: Int) =
        cache[soFar.toString() + patterns[pattern]]?.let { it == 0L } ?: false

    private fun solves(offset: Int, pattern: Int) =
        offset + patterns[pattern].length == design.length

    private fun findMatch(firstPattern: Int, offset: Int): Int? {
        for (i in firstPattern..<patterns.size) {
            if (isPartialMatch(offset, patterns[i]) && !deadEnd(i)) {
                return i
            }
        }

        return null
    }

    private fun isPartialMatch(offset: Int, sub: String): Boolean {
        var matches = true

        for (i in 0..<sub.length) {
            var iDesign = offset + i
            if (iDesign >= design.length || design[iDesign] != sub[i]) {
                matches = false
            }
        }

        return matches
    }

    // Cache checking, not to be confused with check cashing. Returns the cached value if the given
    // pattern index would be added to the iteration so far.
    private fun checkCache(pattern: Int): Long? = cache[soFar.toString() + patterns[pattern]]
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

    val scores = designs.map { ProblemState(it, patterns).matchCount() }

    val score = scores.reduce { acc, value -> acc + value }

    println("Answer = $score")
}
