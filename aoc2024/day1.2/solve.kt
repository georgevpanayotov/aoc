import net.panayotov.util.Scanners

fun main() {
    val leftList = mutableListOf<Int>()
    val rightMap = mutableMapOf<Int, Int>()

    for (line in Scanners) {
        leftList.add(line.nextInt())
        val rightNumber = line.nextInt()

        val count = rightMap.getOrPut(rightNumber) { 0 }
        rightMap.put(rightNumber, count + 1)
    }

    var score = 0

    for (i in 0..<leftList.size) {
        val leftNumber = leftList[i]
        val count = rightMap[leftNumber]
        if (count != null) {
            score += leftNumber * count
        }
    }

    println("Answer = $score")
}
