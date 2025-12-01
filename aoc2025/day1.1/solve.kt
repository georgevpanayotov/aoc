import net.panayotov.util.Lines

data class Instruction(val dir: String, val amount: Int) {
    fun moveDial(dial: Int) =
        if (dir == "L") {
            (dial - amount) % 100
        } else {
            (dial + amount) % 100
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
        dial = inst.moveDial(dial)
        if (dial == 0) {
            score++
        }
    }

    println("Answer = $score")
}
