import net.panayotov.util.Scanners
import net.panayotov.util.Lines
import net.panayotov.util.findMinMax

import java.util.Scanner

data class Condition(val property: Char, val op: Char, val amount: Int) {
    fun eval(arg: Int): Boolean =
        when (op) {
            '<' -> arg < amount
            '>' -> arg > amount
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
        '<'
    } else {
        '>'
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

fun runWorkFlows(part: MachinePart, workflows: Map<String, WorkFlow>): Boolean {
    var position: String? = "in"

    while (position != "A" && position != "R") {
        for (rule in workflows[position]!!.rules) {
            val nextPos = rule.nextWorkFlow(part)
            if (nextPos != null) {
                position = nextPos
                break
            }
        }
    }

    return position == "A"
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

    val score = machineParts.filter {
        runWorkFlows(it, workflows)
    }.fold(0) { acc, part ->
        acc + part.score()
    }

    println("Answer = $score")
}
