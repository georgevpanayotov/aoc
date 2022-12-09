val UP = Pair(1, 0)
val DOWN = Pair(-1, 0)
val LEFT = Pair(0, -1)
val RIGHT = Pair(0, 1)

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = Pair(this.first - other.first, this.second - other.second)

fun tailDirection(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int>? {
    val diff = head - tail
    val (row, col) = diff
    if (Math.abs(row) <= 1 && Math.abs(col) <= 1) {
        return null
    }

    val unitRow = if (row != 0) { row / Math.abs(row) } else 0
    val unitCol = if (col != 0) { col / Math.abs(col) } else 0

    return Pair(unitRow, unitCol)
}

fun printIt(head: Pair<Int, Int>, tails: Array<Pair<Int, Int>>) {
    for (row in 20 downTo 0) {
        for (col in 0..25) {
            var printed = false
            if (head == Pair(row, col)) {
                print("H")
                printed = true
            }

            for (i in 0..tails.size - 1) {
                val tail = tails[i]
                if (tail == Pair(row, col) && !printed) {
                    print("${i + 1}")
                    printed = true
                }
            }

            if (!printed) {
                print(".")
            }
        }
        print("\n")
    }
    print("\n")
}

fun main() {
    var line = readLine()
    var head = Pair(15, 11)
    var tails = Array<Pair<Int, Int>>(9) { Pair(15, 11) }

    val tailPoints = mutableSetOf<Pair<Int, Int>>()
    tailPoints.add(tails[8])

    while (line != null) {
        val scanner = java.util.Scanner(line)

        val dir = when (scanner.next()) {
            "U" -> UP
            "D" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            else -> error("")
        }

        var count = scanner.nextInt()

        while (count > 0) {
            print ("DIR $dir $count\n")
            head = head + dir

            printIt(head, tails)

            for (i in 0..tails.size - 1) {
                var tail = tails[i]
                val prevTail = if (i == 0) head else tails[i - 1]
                var tailDir = tailDirection(prevTail, tail)

                while (tailDir != null) {
                    tail += tailDir
                    tails[i] = tail
                    if (i == tails.size - 1) {
                        tailPoints.add(tail)
                    }
                    printIt(head, tails)

                    tailDir = tailDirection(prevTail, tail)
                }
            }

            count--
        }

        line = readLine()
    }
    print(tailPoints.size)
}
