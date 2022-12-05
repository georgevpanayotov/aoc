import java.util.Scanner
import kotlin.collections.ArrayDeque

fun getIndex(stackNumber: Int): Int = 1 + (stackNumber - 1) * 4

fun parseStacks(stackText: ArrayList<String>): Map<Int, ArrayDeque<Char>> {
    val stacks = mutableMapOf<Int, ArrayDeque<Char>>()

    for (i in 1..9) {
        stacks[i] = ArrayDeque<Char>()
    }

    for (i in stackText.size - 2 downTo 0) {

        var stackNumber = 1
        var index = getIndex(stackNumber)
        val line = stackText.`get`(i)

        while (index < line.length) {
            stacks[stackNumber]?.addLast(line[index])

            stackNumber++
            index = getIndex(stackNumber)
        }
    }

    return stacks
}

fun main() {
    var line = readLine()

    val stackText = arrayListOf<String>()
    while (line != null && line.length > 0) {
        stackText.add(line)
        line = readLine()
    }

    val stacks = parseStacks(stackText)

    line = readLine()
    while (line != null) {
        val scanner = Scanner(line)
        if (scanner.next() != "move") {
            continue
        }

        val count = scanner.nextInt()

        if (scanner.next() != "from") {
            error("no from $line")
        }
        val from = scanner.nextInt()

        if (scanner.next() != "to") {
            error("no to $line")
        }
        val to = scanner.nextInt()

        for (i in 1..count) {
            stacks[from]?.removeLast()?.let { crate ->
                stacks[to]?.addLast(crate)
            }
        }
        line = readLine()
    }

    for (i in 1..9) {
        print(stacks[i]?.last())
    }
    print("\n")
}
