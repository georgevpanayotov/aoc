import net.panayotov.util.Lines

fun isDone(sequence: List<Long>): Boolean = sequence.fold(true) { acc, value ->
    acc && value == 0L
}

fun doDiffs(sequence: List<Long>): List<Long> {
    val diffs = mutableListOf<Long>()
    for (i in 0..sequence.size - 2) {
        diffs.add(sequence[i + 1] - sequence[i])
    }

    return diffs.toList()
}

fun getNext(sequence: List<Long>): Long =
    if (isDone(sequence)) {
        0
    } else {
        sequence[sequence.size - 1] + getNext(doDiffs(sequence))
    }

fun main() {
    val sequences = mutableListOf<List<Long>>()

    for (line in Lines) {
        sequences.add(line.split(" ").map(String::toLong))
    }

    val score = sequences.map {
        getNext(it)
    }.reduce { acc, value ->
        acc + value
    }

    println("Answer = $score")
}
