import net.panayotov.util.Lines

data class Rule(val before: Int, val after: Int) {
    // Validates if the update is satisfied by the rule.
    fun validate(update: List<Int>): Boolean {
        val (iBefore, iAfter) = findIndices(update)

        return iBefore == null || iAfter == null || iBefore < iAfter
    }

    // True if the update matches the rule (i.e. both numbers of the rule are found).
    fun fits(update: List<Int>): Boolean {
        val (iBefore, iAfter) = findIndices(update)

        return iBefore != null && iAfter != null
    }

    private fun findIndices(update: List<Int>): Pair<Int?, Int?> {
        var iBefore: Int? = null
        var iAfter: Int? = null

        for (i in 0..<update.size) {
            val page = update[i]

            if (page == before) {
                iBefore = i
            } else if (page == after) {
                iAfter = i
            }
        }

        return Pair(iBefore, iAfter)
    }
}

fun validate(update: List<Int>, rules: Map<Int, List<Rule>>): Boolean {
    for (page in update) {
        val rulesForPage = rules[page]
        if (rulesForPage == null) {
            continue
        }

        for (rule in rulesForPage) {
            if (!rule.validate(update)) {
                return false
            }
        }
    }
    return true
}

fun canPick(page: Int, remaining: List<Int>, rules: List<Rule>): Boolean {
    for (rule in rules) {
        // If this rule fits with the remaining pages and it specifies this page as "after" it means
        // this page can't yet be picked (at least 1 other page must come before).
        if (rule.fits(remaining) && rule.after == page) {
            return false
        }
    }

    return true
}

fun fixUpdate(update: List<Int>, rules: Map<Int, List<Rule>>): List<Int> {
    // The pages from the update that haven't been chosen yet. We will pick pages from here to
    // append to the fixed update.
    val remaining = update.toMutableList()
    val fixedUpdate = mutableListOf<Int>()

    while (remaining.size > 0) {
        var i = 0

        while (i < remaining.size) {
            val page = remaining[i]
            val rulesForPage = rules[page]

            if (rulesForPage == null || canPick(page, remaining, rulesForPage)) {
                fixedUpdate.add(remaining[i])
                remaining.removeAt(i)
            } else {
                i++
            }
        }
    }

    return fixedUpdate.toList()
}

fun main() {
    val rules = mutableMapOf<Int, MutableList<Rule>>()

    for (line in Lines) {
        if (line.length == 0) {
            break
        }
        val pages = line.split('|').map(String::toInt)
        if (pages.size != 2) {
            error("Wrong size for $line")
        }

        val first = pages[0]
        val second = pages[1]

        val rule = Rule(first, second)

        rules.getOrPut(first) { mutableListOf<Rule>() }.add(rule)
        rules.getOrPut(second) { mutableListOf<Rule>() }.add(rule)
    }

    val updates = mutableListOf<List<Int>>()
    for (line in Lines) {
        updates.add(line.split(',').map(String::toInt))
    }

    val score =
        updates
            .filter { !validate(it, rules) }
            .map { fixUpdate(it, rules) }
            .fold(0) { acc, update -> acc + update[update.size / 2] }

    println("Answer = $score")
}
