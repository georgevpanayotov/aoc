
fun main() {
    var line = readLine()
    val signal = ArrayDeque<Char>()
    val signalMap = mutableMapOf<Char, Int>()

    while (line != null) {

        signal.clear()
        signalMap.clear()
        var score = 0
        for (x in line) {
            signal.addLast(x)
            signalMap.incr(x)

            if (signal.size > 14) {
                val first = signal.removeFirst()
                signalMap.decr(first)
            }
            score++

            var singleOnly = true
            for (count in signalMap.values) {
                if (count > 1) {
                    singleOnly = false
                }
            }

            if (singleOnly && signal.size == 14) {
                break
            }
            // print("${signal} - ${signalMap}\n")
        }
        print("$score\n")


        line = readLine()
    }
}

fun MutableMap<Char, Int>.incr(element: Char) {
    var prevValue = 0
    if (!this.containsKey(element)) {
        put(element, prevValue)
    } else {
        prevValue = this.get(element)!!
    }

    put(element, prevValue + 1)
}

fun MutableMap<Char, Int>.decr(element: Char) {
    var prevValue = 0
    if (!this.containsKey(element)) {
        put(element, prevValue)
    } else {
        prevValue = this.get(element)!!
    }

    put(element, prevValue - 1)
}
