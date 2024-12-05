import net.panayotov.util.Lines

data class Rule(val before: Int, val after: Int) {
    fun validate(update: List<Int>): Boolean {
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

        return (iBefore == null || iAfter == null) || iBefore < iAfter
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

fun main() {
    val rules = mutableMapOf<Int, MutableList<Rule>>()

    for (line in Lines) {
        if (line.length == 0) {
            break
        }
        val pages = line.split('|')
        if (pages.size != 2) {
            error("Wrong size for $line")
        }

        val first = pages[0].toInt()
        val second = pages[1].toInt()

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
            .filter { validate(it, rules) }
            .fold(0) { acc, update -> acc + update[update.size / 2] }

    println("Answer = $score")
}
