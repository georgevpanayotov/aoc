import net.panayotov.util.Lines

fun Boolean.toInt() = if (this) 1 else 0

data class Instruction(val dir: String, val amount: Int) {
    // Returns the new dial position and the number of times the dial passed 0.
    fun moveDial(dial: Int): Pair<Int, Int> {
        val clicksPast0 =
            if (dir == "L") {
                (dial - (amount % 100) < 0) && dial != 0
            } else {
                dial + (amount % 100) > 100
            }
        val newDial = updateDial(dial)
        val endsAt0 = (newDial == 0)

        return Pair(updateDial(dial), clicksPast0.toInt() + (amount / 100) + endsAt0.toInt())
    }

    private fun updateDial(dial: Int): Int {
        val modAmount =
            if (dir == "L") {
                -(amount % 100)
            } else {
                (amount % 100)
            }

        return (dial + modAmount + 100) % 100
    }
}

fun readInstruction(line: String): Instruction? {
    if (line.length == 0) {
        return null
    }

    if (line[0] != 'L' && line[0] != 'R') {
        error("Invalid line: $line")
    }

    return Instruction(line[0].toString(), line.substring(1).toInt())
}

fun main() {
    val instructions = Lines.asSequence().map(::readInstruction).filterNotNull().toList()

    var score = 0
    var dial = 50

    for (inst in instructions) {
        val (newDial, passes) = inst.moveDial(dial)
        dial = newDial
        score += passes
    }

    println("Answer = $score")
}
