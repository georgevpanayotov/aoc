import java.util.Scanner
import net.panayotov.util.Scanners

data class MachineState(val registers: MutableMap<String, Long>, var ip: Int) {
    fun comboOperandValue(operand: Int): Long =
        when (operand) {
            0,
            1,
            2,
            3 -> operand.toLong()
            4 -> register("A")
            5 -> register("B")
            6 -> register("C")
            else -> error("Unexpected combo operand $operand")
        }

    fun register(name: String) =
        if (name == "A" || name == "B" || name == "C") {
            registers[name]!!
        } else {
            error("Unknown register $name")
        }
}

interface Instruction {
    fun execute(state: MachineState, program: List<Int>): Int?
}

fun pow(b: Long, e: Long): Long {
    var result = 1L
    for (i in 1..e) {
        result *= b
    }

    return result
}

fun div(state: MachineState, program: List<Int>) =
    state.register("A") / pow(2, state.comboOperandValue(program[state.ip + 1]))

data object Adv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["A"] = div(state, program)
        state.ip += 2
        return null
    }
}

data object Bdv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = div(state, program)
        state.ip += 2
        return null
    }
}

data object Cdv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["C"] = div(state, program)
        state.ip += 2
        return null
    }
}

data object Bxl : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.register("B") xor program[state.ip + 1].toLong()
        state.ip += 2
        return null
    }
}

data object Bst : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.comboOperandValue(program[state.ip + 1]) % 8
        state.ip += 2
        return null
    }
}

data object Jnz : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        if (state.register("A") != 0L) {
            state.ip = program[state.ip + 1]
        } else {
            state.ip += 2
        }
        return null
    }
}

data object Bxc : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.register("B") xor state.register("C")
        state.ip += 2
        return null
    }
}

data object Out : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        val ret = state.comboOperandValue(program[state.ip + 1]) % 8
        state.ip += 2
        return ret.toInt()
    }
}

val opCodes = mapOf(0 to Adv, 1 to Bxl, 2 to Bst, 3 to Jnz, 4 to Bxc, 5 to Out, 6 to Bdv, 7 to Cdv)

fun parseInput(): Pair<MachineState, List<Int>> {
    val state = MachineState(mutableMapOf(), 0)

    for (line in Scanners) {
        if (!line.hasNext()) {
            break
        }
        line.useDelimiter("(:| )")

        line.next() // "Register"
        val name = line.next()
        line.next() // ""
        val value = line.next()
        state.registers[name] = value.toLong()
    }

    val program = mutableListOf<Int>()
    readLine()?.let {
        val scanner = Scanner(it)

        scanner.useDelimiter("(:| |,)")
        scanner.next() // "Program"
        scanner.next() // ""

        while (scanner.hasNextInt()) {
            program.add(scanner.nextInt())
        }
    }

    return Pair(state, program)
}

fun execute(state: MachineState, program: List<Int>): List<Int> {
    val output = mutableListOf<Int>()

    while (state.ip < program.size) {
        opCodes[program[state.ip]]?.execute(state, program)?.let { output.add(it) }
    }

    return output.toList()
}

// Prints out the program as:
// <INSTRUCTION> <OPERAND>

// e.g. for the sample input:
// Adv 3
// Out 4
// Jnz 0
// Useful to analyze the input programs.
fun disassemble(program: List<Int>) {
    for (i in 0..program.size - 2 step 2) {
        println("${opCodes[program[i]]} ${program[i + 1]}")
    }
}

// Returns the value of A that makes this program be a Quine: a program that prints its own code.
//
// Solution depends on building the list from the back to front. By inspection, both the sample and
// real input operate as simple loop that trims the 3 lowest order bits from A each time then jumps
// back to start. Therefore, the earlier bits can't impact a later output so we start by figuring
// out what value for A computes the last output. Then we shift left by 3 to fill the next 3 bits
// (and at the same time we match more of the program).
fun findQuineRegister(state: MachineState, program: List<Int>): Long {
    // The sublist from programTarget..End that we want to match.
    var programTarget = program.size - 1

    // The value that we want to set to register A.
    var register = 0L

    while (programTarget >= 0) {
        val targetList = program.subList(programTarget, program.size)
        var offset = 0L
        var found = false

        register = register shl 3
        while (!found) {
            // Be careful to copy because the state is mutable so we don't want other runs to
            // interfere here.
            val newState = state.copy()
            newState.registers["A"] = register + offset

            val output = execute(newState, program)
            if (output == targetList) {
                register += offset
                found = true
            } else {
                offset++
            }
        }

        programTarget--
    }

    val verifyState = state.copy()
    verifyState.registers["A"] = register

    val output = execute(verifyState, program)
    if (output != program) {
        error("Failed to compute expected $program got $output")
    }

    return register
}

fun main() {
    val (state, program) = parseInput()
    val register = findQuineRegister(state, program)

    println("Answer = $register")
}
