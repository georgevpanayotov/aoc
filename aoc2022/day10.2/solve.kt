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
    var cycle = 0
    var x = 1

    for (instruction in instructions) {
        for (i in 1..instruction.cycles()) {
            val pixel = cycle % 40

            if (pixel == 0) {
                print("\n")
            }
            if (x >= pixel - 1 && x <= pixel + 1) {
                print("#")
            } else {
                print(".")
            }

            cycle++
        }
        x = instruction.compute(x)
    }

    print(score)
}
