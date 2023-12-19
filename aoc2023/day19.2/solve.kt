import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner
import kotlin.collections.ArrayDeque

data class Condition(val property: Char, val op: String, val amount: Int) {
    fun eval(arg: Int): Boolean =
        when (op) {
            "<" -> arg < amount
            "<=" -> arg <= amount
            ">" -> arg > amount
            ">=" -> arg >= amount
            else -> error("Unknown $op")
        }

    operator fun not(): Condition =
        when (op) {
            "<" -> Condition(property, ">=", amount)
            "<=" -> Condition(property, ">", amount)
            ">" -> Condition(property, "<=", amount)
            ">=" -> Condition(property, "<", amount)
            else -> error("Unknown $op")
        }
}

data class Rule(val condition: Condition?, val dest: String) {
    fun nextWorkFlow(part: MachinePart) = if (condition != null) {
        if (condition.eval(part.vals[condition.property]!!)) {
            dest
        } else {
            null
        }
    } else {
        dest
    }
}

data class WorkFlow(val name: String, val rules: List<Rule>)

data class MachinePart(val vals: Map<Char, Int>) {
    fun score() = vals.values.reduce { acc, value ->
        acc + value
    }
}

data class Range(val property: Char, val min: Int, val max: Int) {
    fun constrain(condition: Condition) = if (condition.property == property) {
        when (condition.op) {
            "<" -> upperBound(condition.amount - 1)
            "<=" -> upperBound(condition.amount)
            ">" -> lowerBound(condition.amount + 1)
            ">=" -> lowerBound(condition.amount)
            else -> error("Unknown ${condition.op}")
        }
    } else {
        this
    }

    // Tightens the upper bound.
    private fun upperBound(amount: Int) = if (amount < max) {
        Range(property, min, amount)
    } else {
        this
    }

    // Tightens the upper bound.
    private fun lowerBound(amount: Int) = if (amount > min) {
        Range(property, amount, max)
    } else {
        this
    }
}

fun readWorkFlow(line: String): WorkFlow {
    val scanner = Scanner(line)

    scanner.useDelimiter("[{}]")
    val name = scanner.next()

    val rules = scanner.next().split(",")

    return WorkFlow(name, rules.map(::readRule))
}

fun readRule(ruleValue: String): Rule {
    val parts = ruleValue.split(":")

    return if (parts.size > 1) {
        Rule(readCondition(parts[0]), parts[1])
    } else {
        Rule(null, parts[0])
    }
}

fun readCondition(conditionValue: String): Condition {
    val op = if (conditionValue.indexOf("<") >= 0) {
        "<"
    } else {
        ">"
    }

    val scanner = Scanner(conditionValue)
    scanner.useDelimiter(op.toString())

    return Condition(scanner.next()[0], op, scanner.next().toInt())
}

fun readPart(line: String): MachinePart {
    val vals = mutableMapOf<Char, Int>()
    val props = line.substring(1, line.length - 1).split(",")

    for (prop in props) {
        val parts = prop.split("=")
        val amount = parts[1].toInt()

        vals[parts[0][0]] = amount
    }

    return MachinePart(vals.toMap())
}

// Only used in `findPaths` but we can't have an inner typealias.
typealias PathPosition = Pair<String, Int>

fun findPaths(workflows: Map<String, WorkFlow>): List<List<Condition>> {
    // Do a DFS keeping track of "prev" as Pair(workflowName, conditionIndex). This must be a DAG
    // otherwise the problem wouldn't work (i.e. we could get stuck in a loop and never reach "A",
    // or "R").

    val allPaths = mutableListOf<List<Condition>>()
    val prev = mutableMapOf<PathPosition, PathPosition>()

    val stack = ArrayDeque<PathPosition>()
    stack.addLast(Pair("in", 0))

    while (stack.size > 0) {
        val position = stack.removeLast()

        val rules = workflows[position.first]!!.rules
        val dest = rules[position.second].dest
        if (dest == "A") {
            // Traverse back through prev negate conditions that stay in the same workflow.
            var curr: PathPosition? = position
            val path = mutableListOf<Condition>()
            var negate = false

            while (curr != null) {
                workflows[curr.first]?.rules?.get(curr.second)?.condition?.let {
                    val condition = if (negate) {
                        !it
                    } else {
                        it
                    }

                    path.add(condition)
                }

                val currWf = curr.first

                negate = prev[curr]?.let {
                    it.first == currWf
                }?: false

                curr = prev[curr]
            }

            // This path is reversed but I don't think it'll matter because all of the operations
            // are just being `AND`-ed.
            allPaths.add(path.toList())
        } else if (dest != "R") {
            val next = Pair(dest, 0)
            prev[next] = position
            stack.addLast(next)
        }

        if (position.second + 1 < rules.size) {
            val next = Pair(position.first, position.second + 1)
            prev[next] = position
            stack.addLast(next)
        }
    }

    return allPaths
}

fun getBounds(conditions: List<Condition>): List<Range> {
    val ranges = mutableListOf<Range>(
        Range('x', 1, 4000),
        Range('m', 1, 4000),
        Range('a', 1, 4000),
        Range('s', 1, 4000))

    for (i in 0..<ranges.size) {
        for (condition in conditions) {
            ranges[i] = ranges[i].constrain(condition)
        }
    }

    return ranges.toList()
}

fun getScore(bounds: List<Range>): Long = bounds.fold(1L) { acc, range ->
    acc * (range.max - range.min + 1)
}

fun main() {
    val workflows = mutableMapOf<String, WorkFlow>()
    val machineParts = mutableListOf<MachinePart>()

    for (line in Lines) {
        if (line.length == 0) {
            break
        }

        val workflow = readWorkFlow(line)
        workflows[workflow.name] = workflow
    }

    for (line in Lines) {
        if (line.length == 0) {
            break
        }

        machineParts.add(readPart(line))
    }

    val paths = findPaths(workflows)
    val score = paths.map(::getBounds).map(::getScore).reduce { acc, innerScore ->
        acc + innerScore
    }

    println("Answer = $score")
}
