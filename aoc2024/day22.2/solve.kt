import net.panayotov.util.Lines

const val PRUNE_MODULUS = 16777216L
const val PRICE_MODULUS = 10L

fun mix(lhs: Long, rhs: Long) = lhs xor rhs

fun prune(value: Long) = value % PRUNE_MODULUS

fun price(value: Long) = value % PRICE_MODULUS

fun nextSecret(secret: Long): Long {
    var curr = secret
    curr = prune(mix(curr, curr * 64L))
    curr = prune(mix(curr, curr / 32L))
    curr = prune(mix(curr, curr * 2048L))

    return curr
}

data class PriceRecord(val price: Long, val change: Long)

fun computeRecords(secret: Long): List<PriceRecord> {
    val records = mutableListOf<PriceRecord>()

    var curr = secret

    var lastPrice = price(secret)
    for (i in 1..2000) {
        curr = nextSecret(curr)
        val currPrice = price(curr)
        records.add(PriceRecord(currPrice, currPrice - lastPrice))

        lastPrice = currPrice
    }

    return records.toList()
}

fun findSequences(allRecords: List<List<PriceRecord>>): Map<List<Long>, Long> {
    val sequences = mutableMapOf<List<Long>, Long>()

    for (record in allRecords) {
        val seen = mutableSetOf<List<Long>>()

        val sequenceMap = mutableMapOf<List<Long>, Long>()
        for (i in 0..record.size - 4) {
            val sequenceBuilder = mutableListOf<Long>()
            for (j in 0..3) {
                sequenceBuilder.add(record[i + j].change)
            }

            val sequence = sequenceBuilder.toList()

            if (!seen.contains(sequence)) {
                val scoreSoFar = sequences.getOrPut(sequence) { 0L }
                sequences[sequence] = scoreSoFar + record[i + 3].price
                seen.add(sequence)
            }
        }
    }

    return sequences.toMap()
}

fun solve(secrets: List<Long>): Long {
    val records = secrets.map(::computeRecords)

    var max: Long? = null
    val sequences = findSequences(records)
    return sequences.values.max()
}

fun main() {
    val secrets = mutableListOf<Long>()
    for (line in Lines) {
        secrets.add(line.toLong())
    }

    val score = solve(secrets)
    println("Answer = $score")
}
