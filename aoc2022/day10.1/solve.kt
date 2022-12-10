val SIGNAL_CYCLES = arrayOf(20, 60, 100, 140, 180, 220)

data class Instruction(val code: String, val param: Int?) {
    fun cycles(): Int = when (code) {
        "addx" -> 2
        "noop" -> 1
        else -> error("Unknown code: $code")
    }

    fun compute(x: Int): Int =
        when (code) {
            "addx" -> if (param != null) { x + param } else error("addx needs a parameter")
            "noop" -> x
            else -> error("Unknown code: $code")
        }
}

fun main() {
    var line = readLine()
    val instructions = kotlin.collections.ArrayDeque<Instruction>()
    while (line != null) {
        val scanner = java.util.Scanner(line)

        val code = scanner.next()
        val param = if (scanner.hasNextInt()) scanner.nextInt() else null

        instructions.addLast(Instruction(code, param))

        line = readLine()
    }

    var score = 0
    var cycle = 1
    var x = 1

    for (instruction in instructions) {
        for (i in 1..instruction.cycles()) {
            for (signalCycle in SIGNAL_CYCLES) {
                if (cycle == signalCycle) {
                    score += x * signalCycle
                    break
                }
            }

            cycle++
        }
        x = instruction.compute(x)
    }

    print(score)
}
