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

    if (row == 0) {
        return Pair(0, col / Math.abs(col))
    } else if (col == 0) {
        return Pair(row / Math.abs(row), 0)
    } else {
        return Pair(row / Math.abs(row), col / Math.abs(col))
    }
}

fun printIt(head: Pair<Int, Int>, tail: Pair<Int, Int>) {
    for (row in 4 downTo 0) {
        for (col in 0..5) {
            if (head == Pair(row, col)) {
                print("H")
            } else if (tail == Pair(row, col)) {
                print("T")
            } else {
                print(".")
            }
        }
        print("\n")
    }
    print("\n")
}

fun main() {
    var line = readLine()
    var head = Pair(0, 0)
    var tail = Pair(0, 0)

    val tailPoints = mutableSetOf<Pair<Int, Int>>()
    tailPoints.add(tail)

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
            head = head + dir

            printIt(head, tail)

            var tailDir = tailDirection(head, tail)
            while (tailDir != null) {
                tail += tailDir
                tailPoints.add(tail)
                printIt(head, tail)

                tailDir = tailDirection(head, tail)
            }

            count--
        }

        line = readLine()
    }
    print(tailPoints.size)
}
