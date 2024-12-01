import net.panayotov.util.Scanners

fun main() {
    val leftList = mutableListOf<Int>()
    val rightList = mutableListOf<Int>()

    for (line in Scanners) {
        leftList.add(line.nextInt())
        rightList.add(line.nextInt())
    }

    leftList.sort()
    rightList.sort()

    var score = 0

    for (i in 0..<leftList.size) {
        score += Math.abs(leftList[i] - rightList[i])
    }

    println("Answer = $score")
}
