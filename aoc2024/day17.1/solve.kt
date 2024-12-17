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

object Adv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["A"] = div(state, program)
        state.ip += 2
        return null
    }
}

object Bdv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = div(state, program)
        state.ip += 2
        return null
    }
}

object Cdv : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["C"] = div(state, program)
        state.ip += 2
        return null
    }
}

object Bxl : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.register("B") xor program[state.ip + 1].toLong()
        state.ip += 2
        return null
    }
}

object Bst : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.comboOperandValue(program[state.ip + 1]) % 8
        state.ip += 2
        return null
    }
}

object Jnz : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        if (state.register("A") != 0L) {
            state.ip = program[state.ip + 1]
        } else {
            state.ip += 2
        }
        return null
    }
}

object Bxc : Instruction {
    override fun execute(state: MachineState, program: List<Int>): Int? {
        state.registers["B"] = state.register("B") xor state.register("C")
        state.ip += 2
        return null
    }
}

object Out : Instruction {
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

fun main() {
    val (state, program) = parseInput()
    println(execute(state, program).joinToString(","))
}
